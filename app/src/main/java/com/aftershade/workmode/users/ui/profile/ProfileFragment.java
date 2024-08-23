package com.aftershade.workmode.users.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.AllEventsFragment;
import com.aftershade.workmode.users.ViewPhotoFragment;
import com.aftershade.workmode.users.Workouts_Posts_Activity;
import com.aftershade.workmode.users.settings.About.AppInfoFragment;
import com.aftershade.workmode.users.settings.Help.FAQFeedbackFragment;
import com.aftershade.workmode.users.settings.SettingsFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.users.BottomNav.enableBack;
import static com.aftershade.workmode.users.BottomNav.nameText;

public class ProfileFragment extends Fragment {

    private static final int COVER_INTENT = 1;
    private static final int PROFILE_INTENT = 2;
    public static final int REQUEST_READ_STORAGE = 11;
    public static boolean hasREAD_STORAGE = false;

    private TextView shareBtn, managePrivacy, settings, activities, appInfo, faqFeedback,
            privacyPolicy, notifications, favorites;
    private TextView userEmail, userFullName;
    private CircleImageView profilePic;
    private ImageView coverPhoto;
    private Uri coverUri, profileUri;
    private Button editProfile;
    private DatabaseReference ref;
    private View root;
    private StorageReference profileCover;
    String profilePhotoUrl, coverPhotoUrl;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        profileCover = FirebaseStorage.getInstance().getReference().child("Profile Cover Images");
        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        nameText.setValue(getString(R.string.profile_));
        enableBack.setValue(false);

        initialiseField();
        updateInfo();

        shareBtn.setOnClickListener(click_Listener);
        managePrivacy.setOnClickListener(click_Listener);
        settings.setOnClickListener(click_Listener);
        activities.setOnClickListener(click_Listener);
        appInfo.setOnClickListener(click_Listener);
        notifications.setOnClickListener(click_Listener);
        privacyPolicy.setOnClickListener(click_Listener);
        faqFeedback.setOnClickListener(click_Listener);
        editProfile.setOnClickListener(click_Listener);
        favorites.setOnClickListener(click_Listener);

        coverPhoto.setOnClickListener((v) -> {

            CharSequence[] options = new CharSequence[]{
                    getString(R.string.view_cover), getString(R.string.modify_cover), getString(R.string.remove_cover)
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        viewPhoto("coverPhoto");
                    } else if (which == 1) {
                        getImagePermission();
                        if (hasREAD_STORAGE) {
                            getPickImageIntent(COVER_INTENT);
                        }

                    } else if (which == 2) {
                        delete("coverPhoto");
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();


        });

        profilePic.setOnClickListener(v -> {
            CharSequence[] options = new CharSequence[]{
                    getString(R.string.view_profile), getString(R.string.modify_profile), getString(R.string.remove_prof)
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        viewPhoto("profilePhoto");
                    } else if (which == 1) {
                        getImagePermission();
                        if (hasREAD_STORAGE) {
                            getPickImageIntent(PROFILE_INTENT);
                        }

                    } else if (which == 2) {
                        delete("profilePhoto");
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        return root;
    }

    private void viewPhoto(String where) {

        Bundle bundle = new Bundle();

        if (where.equals("profilePhoto")) {
            bundle.putString("url", profilePhotoUrl);
        } else if (where.equals("coverPhoto")) {
            bundle.putString("url", coverPhotoUrl);
        }

        getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                ViewPhotoFragment.class, bundle).addToBackStack("").commit();
        enableBack.setValue(true);
        nameText.setValue("View Photo");
    }

    private void delete(String where) {
        final StorageReference filePath = profileCover.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(where + ".jpg");

        ErrorCatching errorCatching = new ErrorCatching(getContext());

        filePath.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Images").child(where).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Photo successfully removed.", Toast.LENGTH_SHORT).show();
                            } else {
                                errorCatching.onException(task.getException());
                            }
                        }
                    });
                } else {
                    errorCatching.onException(task.getException());
                }

            }
        });
    }

    private void uploadCoverPhoto(Uri coverUri_, String where) {

        final StorageReference filePath = profileCover.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(where + ".jpg");
        final UploadTask uploadTask = filePath.putFile(coverUri_);
        ErrorCatching errorCatching = new ErrorCatching(getContext());


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorCatching.onException(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String url = task.getResult().toString();


                            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Images").child(where).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Photo uploaded", Toast.LENGTH_SHORT).show();
                                    } else {
                                        errorCatching.onException(task.getException());
                                    }
                                }
                            });

                        }
                    }
                });

            }


        });


    }

    private void initialiseField() {

        activities = root.findViewById(R.id.activities_sett_btn);
        appInfo = root.findViewById(R.id.app_info_btn);
        faqFeedback = root.findViewById(R.id.faq_feedback_btn);
        privacyPolicy = root.findViewById(R.id.privacy_policy_btn);
        notifications = root.findViewById(R.id.notification_sett_btn);
        shareBtn = root.findViewById(R.id.invite_friends_sett_btn);
        managePrivacy = root.findViewById(R.id.manage_data_privacy_sett_btn);
        favorites = root.findViewById(R.id.favorites_sett_btn);
        profilePic = root.findViewById(R.id.user_profile_image_settings);
        coverPhoto = root.findViewById(R.id.user_cover_image_settings);
        settings = root.findViewById(R.id.settings_btn);
        editProfile = root.findViewById(R.id.editProfileBtn);
        userEmail = root.findViewById(R.id.user_email_settings);
        userFullName = root.findViewById(R.id.user_fullname_settings);

    }

    View.OnClickListener click_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == activities.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new AllEventsFragment()).addToBackStack("").commit();
                enableBack.setValue(true);
                nameText.setValue("Activities");
            } else if (v.getId() == managePrivacy.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new ManagePrivacyFragment()).addToBackStack("").commit();
                enableBack.setValue(true);
            } else if (v.getId() == notifications.getId()) {

            } else if (v.getId() == privacyPolicy.getId()) {

            } else if (v.getId() == faqFeedback.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new FAQFeedbackFragment()).addToBackStack("").commit();
                enableBack.setValue(true);
            } else if (v.getId() == appInfo.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new AppInfoFragment()).addToBackStack("").commit();
                enableBack.setValue(true);
            } else if (v.getId() == settings.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new SettingsFragment()).addToBackStack("").commit();
                enableBack.setValue(true);
            } else if (v.getId() == editProfile.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new ProfileInfoFragment()).addToBackStack("").commit();
                enableBack.setValue(true);
            } else if (v.getId() == favorites.getId()) {

                Intent intent = new Intent(getContext(), Workouts_Posts_Activity.class);
                intent.putExtra("whereTo", "favorite");
                startActivity(intent);
            } else if (v.getId() == shareBtn.getId()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Hey. I just found this awesome app.");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share"));
            }

        }
    };

    private void updateInfo() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ErrorCatching catching = new ErrorCatching(getContext());

        SessionManager sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);

        userFullName.setText(sessionManager.getFullname());
        userEmail.setText(sessionManager.getEmail());

        ref.child(uid).child("Images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    profilePhotoUrl = snapshot.child("profilePhoto").getValue().toString();
                    coverPhotoUrl = snapshot.child("coverPhoto").getValue().toString();

                    Picasso.get().load(coverPhotoUrl)
                            .placeholder(R.drawable.image_placeholder_icon)
                            .error(R.drawable.image_error_icon)
                            .into(coverPhoto);

                    Picasso.get().load(profilePhotoUrl)
                            .placeholder(R.drawable.image_placeholder_icon)
                            .error(R.drawable.image_error_icon)
                            .into(profilePic);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                catching.onError(error);
            }
        });

    }

    /**
     * Checks if read permissions have been granted, if true sets {@value hasREAD_STORAGE} to true
     * if false requests read permissions
     * hasReadStorage is a static boolean for the app
     */
    public void getImagePermission() {

        boolean hasPermissionRead = (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermissionRead) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setMessage(R.string.upload_pic_msg);
            builder.setPositiveButton(R.string.continue_st, (dialog, which) -> {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
            });

            builder.setNegativeButton(R.string.not_now_st, (dialog, which) -> {
                hasREAD_STORAGE = false;
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            hasREAD_STORAGE = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_STORAGE) {
            hasREAD_STORAGE = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

    }


    /**
     * @param requestCode Sets intent for picking images from gallery. It receives request code as a parameter and starts
     *                    intent for that request code.
     *                    Request code is an integer
     */
    private void getPickImageIntent(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == COVER_INTENT) {
                if (data.getData() != null) {
                    coverUri = data.getData();
                    coverPhoto.setImageURI(coverUri);
                    uploadCoverPhoto(data.getData(), "coverPhoto");
                }
            } else if (requestCode == PROFILE_INTENT) {
                if (data.getData() != null) {
                    profileUri = data.getData();
                    profilePic.setImageURI(profileUri);
                    uploadCoverPhoto(data.getData(), "profilePhoto");
                }
            }
        }

    }
}