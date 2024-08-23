package com.aftershade.workmode.users.chat;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.aftershade.workmode.BuildConfig;
import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ChatsBottomSheetFragment extends BottomSheetDialogFragment {

    String messageType, messageId, mimeType, message;
    TextView copyTextV, shareText, downloadText, openText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            messageType = getArguments().getString("messageType");
            messageId = getArguments().getString("messageId");
            mimeType = getArguments().getString("mimeType");
            message = getArguments().getString("message");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats_bottom_sheet, container, false);

        copyTextV = view.findViewById(R.id.chats_bottom_copy);
        downloadText = view.findViewById(R.id.chats_bottom_download);
        shareText = view.findViewById(R.id.chats_bottom_share);
        openText = view.findViewById(R.id.chats_bottom_open);

        if (messageType.equals("text")) {
            openText.setVisibility(View.GONE);
            downloadText.setVisibility(View.GONE);
            shareText.setVisibility(View.GONE);
        } else {
            copyTextV.setVisibility(View.GONE);
        }

        openText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFileExists()) {
                    openFile();
                } else {
                    showMessageDialog("download");
                }
            }
        });

        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFileExists()) {
                    shareFile();
                } else {
                    showMessageDialog("download");
                }
            }
        });

        downloadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFileExists()) {
                    showMessageDialog("open");
                } else {
                    downloadFile();
                }

            }
        });

        copyTextV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("GET_FIT_TEXT", message);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getContext(), "Message copied.", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    //shows alert dialog
    private void showMessageDialog(String whatToDo) {

        String[] messageArray = new String[]{
          "File already exists. Would you like to open it instead?",
          "File has not yet been downloaded. Would you like to download it instead?"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (whatToDo.equals("open")) {
            builder.setTitle("Open File");
            builder.setMessage(messageArray[0]);
        } else {
            builder.setTitle("Download File");
            builder.setMessage(messageArray[1]);
        }

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (whatToDo.equals("open")) {
                    openFile();
                } else {
                    downloadFile();
                }
            }
        }).setNegativeButton("No", null);

        builder.show();

    }

    //shares files with other apps
    private void shareFile() {

        String path = setFilePath();

        File file = new File(requireContext().getExternalFilesDir(null), path);

        Uri uri1 = FileProvider.getUriForFile(requireContext(),
                BuildConfig.APPLICATION_ID + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri1);
        intent.setType(mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share"));

    }

    //download files to app storage
    private void downloadFile() {

        try {


            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(message);

            String path = setFilePath();

            File file = new File(getContext().getExternalFilesDir(null), path);

            if (!file.exists()) {
                file.createNewFile();
            }

            reference.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(getContext(), "File downloaded.", Toast.LENGTH_SHORT).show();

                    } else {
                        ErrorCatching errorCatching = new ErrorCatching(getContext());
                        errorCatching.onException(task.getException());
                    }
                }
            });

        } catch (IOException e) {
            Toast.makeText(getContext(), "Oops. It seems we made a mistake while we were creating your file", Toast.LENGTH_SHORT).show();
        }
    }

    //check if a particular file exists in app storage
    private boolean checkFileExists() {

        String path = setFilePath();

        File file = new File(getContext().getExternalFilesDir(null), path);

        return file.exists();

    }

    //opens files using other apps
    private void openFile() {


        String path = setFilePath();

        File file = new File(getContext().getExternalFilesDir(null), path);

        Uri uri1 = FileProvider.getUriForFile(getContext(),
                BuildConfig.APPLICATION_ID + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri1, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

    }

    //Maps file path using message type and mime type
    private String setFilePath() {

        String suffix = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        String path = null;

        if (messageType.equals("image")) {
            path = "Media/Images/" + messageId + suffix;
        } else if (messageType.equals("audio")) {
            path = "Media/Audio/" + messageId + suffix;
        } else if (messageType.equals("file")) {
            path = "Media/Documents/" + messageId + suffix;
        }

        return path;
    }
}