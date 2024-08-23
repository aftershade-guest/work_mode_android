package com.aftershade.workmode.users.chat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.BuildConfig;
import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Models.MessagesModel;
import com.aftershade.workmode.HelperClasses.ViewHolders.MessageViewHolder;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ImageViewActivity;
import com.aftershade.workmode.users.TrainerInfoActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.aftershade.workmode.users.ui.profile.ProfileFragment.REQUEST_READ_STORAGE;

public class ChatScreenActivity extends AppCompatActivity {

    private TextView chatName, lastSeen, cancelRecording, recordTime;
    private EditText chatsEditText;
    ImageButton backBtn, optionsBtn, sendMessageBtn, addFileBtn, imagesBtn, recordAudio, sendRecordBtn;
    CircleImageView profilePic;
    ImageView blinkingImage;
    String receiverId, receiverName, currentUserId;
    DatabaseReference messageRef, userRef, mutedRef;
    RecyclerView messsagesRecycler;
    FirebaseRecyclerAdapter<MessagesModel, MessageViewHolder> adapter;
    private String todayDate, previousDate, checker;
    HashMap<String, Object> messageMap;
    MediaRecorder recorder;
    LinearLayout chatMessageLayout, recordLayout;

    public final int REQUEST_CAMERA = 12;
    private static final int WRITE_STORAGE = 13;
    private static final int REQUEST_RECORD_AUDIO = 14;

    private final int PHOTO_PROGRAM_INTENT = 5;
    private final int DOCUMENT_PROGRAM_INTENT = 8;
    private final int AUDIO_PROGRAM_INTENT = 7;
    private final int CAMERA_PROGRAM_INTENT = 9;
    Boolean autoDownload;
    boolean isRecording = false;
    int second = -1, minute;
    String recordName = "";
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        receiverId = getIntent().getStringExtra("id");
        receiverName = getIntent().getStringExtra("name");

        SharedPreferences sharedPreferences = getSharedPreferences("AutoDownloadMedia", MODE_PRIVATE);
        autoDownload = sharedPreferences.getBoolean("autoDownload", false);

        messageMap = new HashMap<>();

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messageRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        userRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
        mutedRef = FirebaseDatabase.getInstance().getReference().child("Muted_Blocked_Contacts")
                .child(currentUserId);

        initFields();

        loadUserInfo();

        chatName.setOnClickListener(v -> {
            Intent intent = new Intent(ChatScreenActivity.this, TrainerInfoActivity.class);
            intent.putExtra("id", receiverId);
            startActivity(intent);
        });

        sendMessageBtn.setOnClickListener(sendMessageClickListener);

        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        imagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "image";
                requestPermission(PHOTO_PROGRAM_INTENT);
            }
        });

        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsPopUpMenu(v);
            }
        });

        loadChats();

    }

    //initializes fields and variables
    private void initFields() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        todayDate = currentDate.format(calendar.getTime());

        lastSeen = findViewById(R.id.chat_screen_last_seen);
        chatName = findViewById(R.id.trainer_name_chat);
        chatsEditText = findViewById(R.id.chats_edit_text);
        backBtn = findViewById(R.id.back_btn_chat_screen);
        optionsBtn = findViewById(R.id.chat_screen_option_btn);
        sendMessageBtn = findViewById(R.id.chats_send_message_btn);
        addFileBtn = findViewById(R.id.chats_add_files_btn);
        imagesBtn = findViewById(R.id.chats_images_btn);
        recordAudio = findViewById(R.id.chats_record_audio_btn);
        cancelRecording = findViewById(R.id.cancel_recording_chats_text);
        recordTime = findViewById(R.id.record_time_chats_text);
        sendRecordBtn = findViewById(R.id.stop_send_recording_chats_btn);
        blinkingImage = findViewById(R.id.blinking_recording_icon_chats);

        profilePic = findViewById(R.id.profile_pic_chat_screen);

        messsagesRecycler = findViewById(R.id.chat_screen_messages_recycler);

        chatMessageLayout = findViewById(R.id.messages_linear_layout);
        recordLayout = findViewById(R.id.recording_linear_layout);

        messsagesRecycler.setLayoutManager(new
                LinearLayoutManager(ChatScreenActivity.this, RecyclerView.VERTICAL, false));

        chatsEditText.addTextChangedListener(textWatcher);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ChatScreenActivity.this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {

                    chatMessageLayout.setVisibility(View.GONE);
                    recordLayout.setVisibility(View.VISIBLE);
                    startRecording();
                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(500);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.REVERSE);
                    blinkingImage.startAnimation(animation);

                } else {
                    ActivityCompat.requestPermissions(ChatScreenActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                }


            }
        });

        sendRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMessageLayout.setVisibility(View.VISIBLE);
                recordLayout.setVisibility(View.GONE);
                stopRecording();
                sendRecording();
            }
        });

        cancelRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMessageLayout.setVisibility(View.VISIBLE);
                recordLayout.setVisibility(View.GONE);
                stopRecording();
            }
        });

    }

    private void startRecording() {

        try {

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

            recordName = sdf.format(date);

            String path = "Media/Audio/Recordings/" + recordName + ".3gp";
            File file = new File(getExternalFilesDir(null), path);
            file.createNewFile();

            recorder = new MediaRecorder();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            //max duration is 30 minutes
            recorder.setMaxDuration(1_800_000);
            recorder.setOutputFile(file);

            recorder.prepare();
            recorder.start();
            showTimer();

            recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        stopRecording();
                        sendRecording();
                    }
                }
            });

            isRecording = true;

        } catch (IOException e) {
            recorder.stop();
            recorder.release();
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendRecording() {

        checker = "audio";
        String path = "Media/Audio/Recordings/" + recordName + ".3gp";
        File file = new File(getExternalFilesDir(null), path);

        Uri uri1 = FileProvider.getUriForFile(ChatScreenActivity.this,
                BuildConfig.APPLICATION_ID + ".fileprovider", file);
        showMessageDialog(uri1);

    }

    private void stopRecording() {

        countDownTimer.cancel();
        countDownTimer = null;
        second = -1;
        minute = 0;
        recorder.stop();
        recorder.release();
        isRecording = false;

    }

    private void showTimer() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                second++;
                /*Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_animation);
                recordTime.setAnimation(animation);
                recordTime.startAnimation(animation);*/
                recordTime.setText(recorderTime());
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    private String recorderTime() {

        if (second == 60) {
            minute++;
            second = 0;
        }

        if (minute == 30) {
            countDownTimer.cancel();
            minute = 0;
        }

        return String.format("%02d:%02d", minute, second);

    }

    //Shows toolbar menu
    private void showOptionsPopUpMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(ChatScreenActivity.this, v);
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.inflate(R.menu.chats_act_toolbar);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.chats_view_profile) {
                    Intent intent = new Intent(ChatScreenActivity.this, TrainerInfoActivity.class);
                    intent.putExtra("id", receiverId);
                    startActivity(intent);
                } else if (id == R.id.chats_mute) {

                    showMuteDialog();

                } else if (id == R.id.chats_report) {

                } else if (id == R.id.chats_block) {

                } else if (id == R.id.chats_clear) {

                    showClearDialog();

                }

                return true;
            }
        });
        popupMenu.show();
    }

    //show confirmation dialog when clearing chat messages
    private void showClearDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatScreenActivity.this);
        builder.setTitle("Clear Chat Messages");
        builder.setMessage("You are about to clear your chat messages, this cannot be undone. Do you want to continue?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                messageRef.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatScreenActivity.this, "Chat cleared", Toast.LENGTH_SHORT).show();
                        } else {
                            ErrorCatching errorCatching = new ErrorCatching(ChatScreenActivity.this);
                            errorCatching.onException(task.getException());
                        }
                    }
                });

            }
        }).setNegativeButton("No", null);

        builder.show();

    }

    //show dialog for choosing mute duration when muting a chat
    private void showMuteDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ChatScreenActivity.this);

        builder.setItems(R.array.mute_chat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                HashMap<String, Object> muteMap = new HashMap<>();

                muteMap.put("duration", Array.get(R.array.mute_chat, which));
                muteMap.put("id", receiverId);
                muteMap.put("stated", "muted");

                mutedRef.child(currentUserId).child(receiverId).updateChildren(muteMap);
            }
        });

        builder.show();


    }

    private void loadUserInfo() {

        if (receiverName != null) {
            chatName.setText(receiverName);
        }

        userRef.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("Images").hasChild("profilePhoto")) {
                        Picasso.get()
                                .load(snapshot.child("Images")
                                        .child("profilePhoto")
                                        .getValue().toString())
                                .fit()
                                .placeholder(R.drawable.image_placeholder_icon)
                                .error(R.drawable.image_error_icon)
                                .into(profilePic);
                    }

                    if (snapshot.hasChild("userState")) {

                        String state = snapshot.child("userState").child("state").getValue().toString();

                        if (state.equals("online")) {
                            lastSeen.setText(R.string.online);
                        } else if (state.equals("offline")) {
                            String date = snapshot.child("userState").child("date").getValue().toString();
                            String time = snapshot.child("userState").child("time").getValue().toString();

                            if (date.equals(todayDate)) {
                                lastSeen.setText(String.format("Last seen at %s", time));
                            } else {
                                lastSeen.setText(String.format("Last seen on %s at %s", date, time));
                            }

                        }

                    } else {
                        lastSeen.setText(R.string.offline);
                    }

                } else {
                    lastSeen.setText(R.string.offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorCatching errorCatching = new ErrorCatching(ChatScreenActivity.this);
                errorCatching.onError(error);


            }
        });

    }

    //Show menu when user clicks add/attach file button
    private void showPopupMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(ChatScreenActivity.this, v);
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.inflate(R.menu.chats_popup_menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.chats_menu_camera) {
                    checker = "image";
                    requestCameraPermission(CAMERA_PROGRAM_INTENT);
                } else if (id == R.id.chats_menu_image) {
                    checker = "image";
                    requestPermission(PHOTO_PROGRAM_INTENT);
                } else if (id == R.id.chats_menu_audio) {
                    checker = "audio";
                    requestPermission(AUDIO_PROGRAM_INTENT);
                } else if (id == R.id.chats_menu_file) {
                    checker = "file";
                    requestPermission(DOCUMENT_PROGRAM_INTENT);
                }

                return false;
            }
        });

        popupMenu.show();

    }

    //requests camera permission
    private void requestCameraPermission(int requestCode) {

        if (ContextCompat.checkSelfPermission(ChatScreenActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            pickContentIntent(requestCode);
        } else {
            ActivityCompat.requestPermissions(ChatScreenActivity.this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

    }

    //requests read permission
    private void requestPermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(ChatScreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            pickContentIntent(requestCode);
        } else {
            ActivityCompat.requestPermissions(ChatScreenActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
        }
    }

    /**
     * @param requestCode receives request code as parameter and sent intent type then starts activity result
     */
    private void pickContentIntent(int requestCode) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

        if (requestCode == PHOTO_PROGRAM_INTENT) {
            intent.setType("image/*");
        } else if (requestCode == DOCUMENT_PROGRAM_INTENT) {
            intent.setType("*/*");
        } else if (requestCode == AUDIO_PROGRAM_INTENT) {
            intent.setType("audio/*");
        } else if (requestCode == CAMERA_PROGRAM_INTENT) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                if (requestCode == PHOTO_PROGRAM_INTENT || requestCode == DOCUMENT_PROGRAM_INTENT
                        || requestCode == AUDIO_PROGRAM_INTENT || requestCode == CAMERA_PROGRAM_INTENT) {

                    showMessageDialog(uri);

                }
            }
        }

    }

    //download files to app storage
    private boolean downloadFile(String url, String fileName, String mimeType, String fromMessageType) {
        final boolean[] isDownloaded = {false};

        try {


            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

            String suffix = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            String path = null;

            if (fromMessageType.equals("image")) {
                path = "Media/Images/" + fileName + suffix;
            } else if (fromMessageType.equals("audio")) {
                path = "Media/Audio/" + fileName + suffix;
            } else if (fromMessageType.equals("file")) {
                path = "Media/Documents/" + fileName + suffix;
            }

            File file = new File(getExternalFilesDir(null), path);

            if (!file.exists()) {
                file.createNewFile();
            }

            reference.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        isDownloaded[0] = true;
                        Toast.makeText(ChatScreenActivity.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } else {
                        isDownloaded[0] = false;
                        ErrorCatching errorCatching = new ErrorCatching(ChatScreenActivity.this);
                        errorCatching.onException(task.getException());
                    }
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Oops. It seems we made a mistake while we were creating your file", Toast.LENGTH_SHORT).show();
            return false;
        }

        return isDownloaded[0];

    }

    //check if a particular file exists in app storage
    private boolean checkFileExists(String fileName, String mimeType, String fromMessageType) {

        String suffix = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        String path = null;

        if (fromMessageType.equals("image")) {
            path = "Media/Images/" + fileName + suffix;
        } else if (fromMessageType.equals("audio")) {
            path = "Media/Audio/" + fileName + suffix;
        } else if (fromMessageType.equals("file")) {
            path = "Media/Documents/" + fileName + suffix;
        }

        File file = new File(getExternalFilesDir(null), path);

        return file.exists();

    }

    //opens files using other apps
    private void openFile(String fileName, String mimeType, String messageType) {

        String suffix = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        String path = null;


        if (messageType.equals("image")) {
            path = "Media/Images/" + fileName + suffix;
        } else if (messageType.equals("audio")) {
            path = "Media/Audio/" + fileName + suffix;
        } else if (messageType.equals("file")) {
            path = "Media/Documents/" + fileName + suffix;
        }

        File file = new File(getExternalFilesDir(null), path);

        Uri uri1 = FileProvider.getUriForFile(ChatScreenActivity.this,
                BuildConfig.APPLICATION_ID + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri1, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

    }

    //Shows send confirmation dialog when sending files
    private void showMessageDialog(Uri uri) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatScreenActivity.this);

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();

        String fileName = cursor.getString(nameIndex);
        String mimeType = getContentResolver().getType(uri);

        cursor.close();

        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

        builder.setMessage("Send" + fileName + "to" + receiverName + "?");
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ErrorCatching errorCatching = new ErrorCatching(ChatScreenActivity.this);

                DatabaseReference fRef = messageRef.child(currentUserId).child(receiverId).push();
                String msgPushId = fRef.getKey();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                        .child("Chats").child(currentUserId).child(receiverId);
                StorageReference filePath = storageReference.child(msgPushId + extension);

                UploadTask uploadTask = filePath.putFile(uri);

                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                        messageMap.put("fileName", fileName);
                                        messageMap.put("mimeType", mimeType);

                                        saveMessage(msgPushId, url);

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
        }).setNegativeButton("Don't Send", null);

        builder.show();

    }

    //loads chats from db into recycler view
    private void loadChats() {

        FirebaseRecyclerOptions<MessagesModel> options = new
                FirebaseRecyclerOptions.Builder<MessagesModel>()
                .setQuery(messageRef.child(currentUserId).child(receiverId), MessagesModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<MessagesModel, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int i,
                                            @NonNull MessagesModel messagesModel) {

                String fromUserId = messagesModel.getFrom();
                String fromMessageType = messagesModel.getType();

                holder.dateText.setVisibility(View.GONE);
                holder.receiverConstraint.setVisibility(View.GONE);
                holder.receiverTime.setVisibility(View.GONE);

                holder.senderConstraint.setVisibility(View.GONE);
                holder.senderTime.setVisibility(View.GONE);
                holder.senderFileIcon.setVisibility(View.GONE);
                holder.receiverFileIcon.setVisibility(View.GONE);

                //sets message states to read
                messageRef.child(currentUserId).child(receiverId)
                        .child(messagesModel.getMessageId()).child("state").setValue("read");

                //Check if message date equals today's date and sets date accordingly
                if (!messagesModel.getDate().equals(todayDate)) {
                    holder.dateText.setVisibility(View.VISIBLE);
                    holder.dateText.setText(messagesModel.getDate());
                } else {
                    holder.dateText.setVisibility(View.VISIBLE);
                    holder.dateText.setText(R.string.today);
                }

                //Check if date equal previous text date and sets visibility accordingly
                if (messagesModel.getDate().equals(previousDate)) {
                    holder.dateText.setVisibility(View.GONE);
                } else {
                    holder.dateText.setVisibility(View.VISIBLE);
                }

                //Checks sender/receiver of message and sets visibility
                if (fromUserId.equals(currentUserId)) {
                    holder.senderConstraint.setVisibility(View.VISIBLE);
                    holder.senderTime.setVisibility(View.VISIBLE);
                    holder.senderTime.setText(messagesModel.getTime());

                    if (fromMessageType.equals("text")) {
                        holder.senderImage.setVisibility(View.GONE);
                        holder.senderText.setVisibility(View.VISIBLE);
                        holder.senderText.setText(messagesModel.getMessage());
                    } else if (fromMessageType.equals("image")) {
                        holder.senderImage.setVisibility(View.VISIBLE);
                        holder.senderText.setVisibility(View.GONE);

                        Picasso.get()
                                .load(messagesModel.getMessage())
                                .placeholder(R.drawable.image_placeholder_icon)
                                .error(R.drawable.image_error_icon)
                                .into(holder.senderImage);

                    } else if (fromMessageType.equals("audio")) {
                        holder.senderText.setVisibility(View.VISIBLE);
                        holder.senderImage.setVisibility(View.GONE);
                        holder.senderFileIcon.setVisibility(View.VISIBLE);

                        holder.senderText.setText(messagesModel.getFileName());
                        holder.senderFileIcon.setImageDrawable(ContextCompat
                                .getDrawable(ChatScreenActivity.this, R.drawable.music_note_icon));

                    } else if (fromMessageType.equals("file")) {
                        holder.senderText.setVisibility(View.VISIBLE);
                        holder.senderImage.setVisibility(View.GONE);
                        holder.senderFileIcon.setVisibility(View.VISIBLE);

                        holder.senderText.setText(messagesModel.getFileName());
                        holder.senderFileIcon.setImageDrawable(ContextCompat
                                .getDrawable(ChatScreenActivity.this, R.drawable.text_snippet_icon));
                    }

                    if (!fromMessageType.equals("text")) {
                        if (!(checkFileExists(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType))) {
                            holder.senderFileIcon.setVisibility(View.VISIBLE);
                            holder.senderFileIcon.setImageDrawable(ContextCompat
                                    .getDrawable(ChatScreenActivity.this, R.drawable.download_icon));
                        }
                    }

                } else {
                    holder.receiverConstraint.setVisibility(View.VISIBLE);
                    holder.receiverTime.setVisibility(View.VISIBLE);
                    holder.receiverTime.setText(messagesModel.getTime());

                    if (fromMessageType.equals("text")) {
                        holder.receiverImage.setVisibility(View.GONE);
                        holder.receiverText.setVisibility(View.VISIBLE);
                        holder.receiverText.setText(messagesModel.getMessage());
                    } else if (fromMessageType.equals("image")) {
                        holder.receiverText.setVisibility(View.GONE);
                        holder.receiverImage.setVisibility(View.VISIBLE);

                        Picasso.get()
                                .load(messagesModel.getMessage())
                                .placeholder(R.drawable.image_placeholder_icon)
                                .error(R.drawable.image_error_icon)
                                .into(holder.receiverImage);
                    } else if (fromMessageType.equals("audio")) {
                        holder.receiverText.setVisibility(View.VISIBLE);
                        holder.receiverImage.setVisibility(View.GONE);
                        holder.receiverFileIcon.setVisibility(View.VISIBLE);

                        holder.receiverText.setText(messagesModel.getFileName());
                        holder.receiverFileIcon.setImageDrawable(ContextCompat
                                .getDrawable(ChatScreenActivity.this, R.drawable.music_note_icon));
                    } else if (fromMessageType.equals("file")) {
                        holder.receiverText.setVisibility(View.VISIBLE);
                        holder.receiverImage.setVisibility(View.GONE);
                        holder.receiverFileIcon.setVisibility(View.VISIBLE);

                        holder.receiverText.setText(messagesModel.getFileName());
                        holder.receiverFileIcon.setImageDrawable(ContextCompat
                                .getDrawable(ChatScreenActivity.this, R.drawable.text_snippet_icon));
                    }

                    if (!fromMessageType.equals("text")) {
                        if (!(checkFileExists(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType))) {
                            holder.receiverFileIcon.setVisibility(View.VISIBLE);
                            holder.receiverFileIcon.setImageDrawable(ContextCompat
                                    .getDrawable(ChatScreenActivity.this, R.drawable.download_icon));
                        }
                    }

                }

                //if auto download is on or true, automatically download chat media
                if (autoDownload) {
                    if (fromMessageType.equals("image")) {

                        if (checkFileExists(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType)) {
                            Intent intent = new Intent(ChatScreenActivity.this, ImageViewActivity.class);
                            intent.putExtra("url", messagesModel.getMessage());
                            startActivity(intent);
                        } else {
                            downloadFile(messagesModel.getMessage(), messagesModel.getMessageId(),
                                    messagesModel.getMimeType(), fromMessageType);
                        }


                    } else if (fromMessageType.equals("file") || fromMessageType.equals("audio")) {

                        if (checkFileExists(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType)) {
                            openFile(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType);
                        } else {
                            if (downloadFile(messagesModel.getMessage(), messagesModel.getMessageId(),
                                    messagesModel.getMimeType(), fromMessageType)) {

                                if (fromUserId.equals(currentUserId)) {

                                    if (fromMessageType.equals("file")) {
                                        holder.senderFileIcon.setImageResource(R.drawable.text_snippet_icon);
                                    } else {
                                        holder.senderFileIcon.setImageResource(R.drawable.music_note_icon);
                                    }


                                } else {

                                    if (fromMessageType.equals("file")) {
                                        holder.receiverFileIcon.setImageResource(R.drawable.text_snippet_icon);
                                    } else {
                                        holder.receiverFileIcon.setImageResource(R.drawable.music_note_icon);
                                    }
                                }
                            } else {
                                if (fromUserId.equals(currentUserId)) {
                                    holder.senderFileIcon.setImageResource(R.drawable.download_icon);
                                } else {
                                    holder.receiverFileIcon.setImageResource(R.drawable.download_icon);
                                }
                            }


                        }

                    }
                }

                //when chat is clicked will, download if media if it does not or open it if
                // it already exists
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (fromMessageType.equals("image")) {

                            if (checkFileExists(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType)) {
                                Intent intent = new Intent(ChatScreenActivity.this, ImageViewActivity.class);
                                intent.putExtra("url", messagesModel.getMessage());
                                startActivity(intent);
                            } else {
                                downloadFile(messagesModel.getMessage(), messagesModel.getMessageId(),
                                        messagesModel.getMimeType(), fromMessageType);
                            }


                        } else if (fromMessageType.equals("file") || fromMessageType.equals("audio")) {

                            if (checkFileExists(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType)) {
                                openFile(messagesModel.getMessageId(), messagesModel.getMimeType(), fromMessageType);
                            } else {
                                if (downloadFile(messagesModel.getMessage(), messagesModel.getMessageId(),
                                        messagesModel.getMimeType(), fromMessageType)) {

                                    if (fromUserId.equals(currentUserId)) {

                                        if (fromMessageType.equals("file")) {
                                            holder.senderFileIcon.setImageResource(R.drawable.text_snippet_icon);
                                        } else {
                                            holder.senderFileIcon.setImageResource(R.drawable.music_note_icon);
                                        }


                                    } else {

                                        if (fromMessageType.equals("file")) {
                                            holder.receiverFileIcon.setImageResource(R.drawable.text_snippet_icon);
                                        } else {
                                            holder.receiverFileIcon.setImageResource(R.drawable.music_note_icon);
                                        }
                                    }
                                } else {
                                    if (fromUserId.equals(currentUserId)) {
                                        holder.senderFileIcon.setImageResource(R.drawable.download_icon);
                                    } else {
                                        holder.receiverFileIcon.setImageResource(R.drawable.download_icon);
                                    }
                                }


                            }

                        }
                    }
                });

                //Handles chat long click
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        ChatsBottomSheetFragment sheetFragment = new ChatsBottomSheetFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("messageType", fromMessageType);
                        bundle.putString("messageId", messagesModel.getMessageId());
                        bundle.putString("mimeType", messagesModel.getMimeType());
                        bundle.putString("message", messagesModel.getMessage());
                        sheetFragment.setArguments(bundle);
                        sheetFragment.show(getSupportFragmentManager(), "chats");

                        return true;
                    }
                });

                //saves chat message date for comparing
                previousDate = messagesModel.getDate();
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_messages_card, parent, false);
                return new MessageViewHolder(view);
            }
        };

        messsagesRecycler.setAdapter(adapter);

    }

    View.OnClickListener sendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String message;

            message = chatsEditText.getText().toString();

            if (message.isEmpty()) {
                Toast.makeText(ChatScreenActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            checker = "text";

            DatabaseReference userMsgRef = messageRef.child(currentUserId).child(receiverId).push();
            String msgPushId = userMsgRef.getKey();

            saveMessage(msgPushId, message);


        }
    };

    //save messages to db
    private void saveMessage(String msgPushId, String message) {

        String messageSenderRef = "/" + currentUserId + "/" + receiverId;
        String messageReceiverRef = "/" + receiverId + "/" + currentUserId;


        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        messageMap.put("message", message);
        messageMap.put("type", checker);
        messageMap.put("from", currentUserId);
        messageMap.put("to", receiverId);
        messageMap.put("messageId", msgPushId);
        messageMap.put("time", saveCurrentTime);
        messageMap.put("date", saveCurrentDate);

        Map<String, Object> messsageBodyDetails = new HashMap<>();
        messsageBodyDetails.put(messageSenderRef + "/" + msgPushId, messageMap);
        messsageBodyDetails.put(messageReceiverRef + "/" + msgPushId, messageMap);

        messageRef.updateChildren(messsageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    adapter.notifyDataSetChanged();
                } else {
                    ErrorCatching errorCatching = new ErrorCatching(ChatScreenActivity.this);
                    errorCatching.onException(task.getException());
                }
            }
        });

        if (checker.equals("text")) {
            chatsEditText.setText("");
        }

        messsagesRecycler.smoothScrollToPosition(messsagesRecycler.getAdapter().getItemCount());

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {
                sendMessageBtn.setVisibility(View.VISIBLE);
                imagesBtn.setVisibility(View.GONE);
                recordAudio.setVisibility(View.GONE);
            } else {
                sendMessageBtn.setVisibility(View.GONE);
                imagesBtn.setVisibility(View.VISIBLE);
                recordAudio.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        messsagesRecycler.smoothScrollToPosition(messsagesRecycler.getAdapter().getItemCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}