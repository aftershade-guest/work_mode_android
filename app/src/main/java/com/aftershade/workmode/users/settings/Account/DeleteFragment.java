package com.aftershade.workmode.users.settings.Account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aftershade.workmode.Common.LoginSignUp.LoginSignUpActivity;
import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.aftershade.workmode.Databases.SessionManager.SESSION_REMEMBERME;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;

public class DeleteFragment extends Fragment {

    Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete, container, false);

        nextBtn = view.findViewById(R.id.delete_acc_frag_next_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete Account");
                builder.setMessage("You are about to delete your account. This can't be undone, would you still like to continue?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                }).setNegativeButton("No", null);
            }
        });

        return view;
    }

    private void deleteAccount() {

        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    SessionManager sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);
                    sessionManager.logooutUserFromSession();
                    sessionManager = new SessionManager(getContext(), SESSION_REMEMBERME);
                    sessionManager.logooutUserFromSession();
                    Intent intent = new Intent(getContext(), LoginSignUpActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    ErrorCatching errorCatching = new ErrorCatching(getContext());
                    errorCatching.onException(task.getException());
                }
            }
        });

    }
}