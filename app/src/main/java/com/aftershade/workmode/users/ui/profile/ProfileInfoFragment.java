package com.aftershade.workmode.users.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.settings.EnterLoginInfoFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.aftershade.workmode.Databases.NetworkCheck.isNetworkConnected;
import static com.aftershade.workmode.Databases.SessionManager.KEY_FULLNAME;
import static com.aftershade.workmode.Databases.SessionManager.KEY_GENDER;
import static com.aftershade.workmode.Databases.SessionManager.KEY_PHONENUMBER;
import static com.aftershade.workmode.Databases.SessionManager.KEY_SESSIONEMAIL;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.users.BottomNav.nameText;
import static com.aftershade.workmode.users.ui.profile.ProfileFragment.REQUEST_READ_STORAGE;
import static com.aftershade.workmode.users.ui.profile.ProfileFragment.hasREAD_STORAGE;

public class ProfileInfoFragment extends Fragment {

    TextInputEditText fullNameEditText, bioEditText;
    TextView emailTextV, phoneTextV;
    CircleImageView profileImage;
    Button saveChangesBtn;
    Uri profileUri;
    RadioButton maleRb, femaleRb, otherRb;

    private static final int PROFILE_CODE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);
        nameText.setValue("Edit Profile");

        emailTextV = view.findViewById(R.id.email_profile_textView);
        phoneTextV = view.findViewById(R.id.phone_profile_textview);
        profileImage = view.findViewById(R.id.profile_image_profile);
        fullNameEditText = view.findViewById(R.id.fullname_profile_edit_txt);
        bioEditText = view.findViewById(R.id.bio_profile_edit_txt);
        saveChangesBtn = view.findViewById(R.id.save_changes_profile);
        maleRb = view.findViewById(R.id.male_rb_profile_info);
        femaleRb = view.findViewById(R.id.female_rb_profile_info);
        otherRb = view.findViewById(R.id.other_rb_profile_info);

        fullNameEditText.addTextChangedListener(textWatcher);
        bioEditText.addTextChangedListener(textWatcher);

        emailTextV.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                    new EnterLoginInfoFragment("changeEmail")).addToBackStack("").commit();
            nameText.setValue("Change Email");
        });

        phoneTextV.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                    new EnterLoginInfoFragment("changePhone")).addToBackStack("").commit();
            nameText.setValue("Change Phone");
        });

        profileImage.setOnClickListener(v -> {
            getImagePermission();
            if (hasREAD_STORAGE) {
                saveChangesBtn.setEnabled(true);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(intent, PROFILE_CODE);
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!isNetworkConnected) {
            Toast.makeText(getContext(), getString(R.string.no_internet_str), Toast.LENGTH_LONG).show();
            saveChangesBtn.setVisibility(View.GONE);
            fullNameEditText.setEnabled(false);
            emailTextV.setEnabled(false);
            emailTextV.setClickable(false);
            bioEditText.setEnabled(false);
            phoneTextV.setEnabled(false);
            phoneTextV.setClickable(false);
        }

        SessionManager sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);
        HashMap<String, String> userData = sessionManager.getLoginDetails();

        fullNameEditText.setText(userData.get(KEY_FULLNAME));
        emailTextV.setText(userData.get(KEY_SESSIONEMAIL));
        bioEditText.setText(userData.get(KEY_FULLNAME));
        phoneTextV.setText(userData.get(KEY_PHONENUMBER));

        String gender = userData.get(KEY_GENDER);

        if (gender.contentEquals(maleRb.getText())) {
            maleRb.setChecked(true);
        } else if (gender.contentEquals(femaleRb.getText())) {
            femaleRb.setChecked(true);
        } else if (gender.contentEquals(otherRb.getText())) {
            otherRb.setChecked(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_STORAGE) {
            hasREAD_STORAGE = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

    }

    public void getImagePermission() {

        boolean hasPermissionRead = (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermissionRead) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setMessage("To upload pictures. Allow GET_FIT to access to storage");
            builder.setPositiveButton("Continue", (dialog, which) -> {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
            });

            builder.setNegativeButton("Not Now", (dialog, which) -> {
                hasREAD_STORAGE = false;
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            hasREAD_STORAGE = true;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PROFILE_CODE) {
                if (data.getData() != null) {
                    profileUri = data.getData();
                    profileImage.setImageURI(profileUri);
                }

            }
        }

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            saveChangesBtn.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}