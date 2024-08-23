package com.aftershade.workmode.users.settings.Account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Common.LoginSignUp.LoginSignUpActivity;
import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.aftershade.workmode.Databases.SessionManager.SESSION_REMEMBERME;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateEmail;

public class ChangeEmailFragment extends Fragment {

    TextInputEditText emailEditTxt;
    String email = "";
    String password = "";
    ErrorCatching errorCatching;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        errorCatching = new ErrorCatching(getContext());

        if (getArguments() != null) {
            email = getArguments().getString("email");
            password = getArguments().getString("password");
        }

        emailEditTxt = view.findViewById(R.id.change_email_edit_txt);


        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String emailTxt = emailEditTxt.getText().toString();

            if (!validate_Email(emailTxt)) {
                return;
            } else if (emailTxt.equals(email)) {
                Toast.makeText(getContext(), "The email entered is the same as the previous email.", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();


            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updateEmail(emailTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    updateDatabase(user.getUid());
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
    };

    private void updateDatabase(String uid) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.child(uid).child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Email changed successfully", Toast.LENGTH_SHORT).show();

                    FirebaseAuth.getInstance().signOut();
                    SessionManager sessionManager;
                    sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);
                    sessionManager.logooutUserFromSession();

                    sessionManager = new SessionManager(getContext(), SESSION_REMEMBERME);
                    sessionManager.logooutUserFromSession();

                    startActivity(new Intent(getActivity(), LoginSignUpActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    requireActivity().finish();

                } else {
                    errorCatching.onException(task.getException());
                }
            }

        });

    }

    private boolean validate_Email(String email) {

        String returns_ = validateEmail(email);

        if (returns_.equals(ERR_EMPTY)) {
            emailEditTxt.setError(getString(R.string.provide_email_err));
            emailEditTxt.requestFocus();
            return false;
        } else if (returns_.equals(NOT_MATCH)) {
            emailEditTxt.setError(getString(R.string.email_not_match));
            emailEditTxt.requestFocus();
            return false;
        } else {
            return true;
        }

    }

}