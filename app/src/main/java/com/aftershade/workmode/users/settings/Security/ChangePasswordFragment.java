package com.aftershade.workmode.users.settings.Security;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import static com.aftershade.workmode.Databases.SessionManager.SESSION_REMEMBERME;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateConfirmPassword;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validatePassword;
import static com.aftershade.workmode.users.BottomNav.nameText;

public class ChangePasswordFragment extends Fragment {

    String oldPassword, newPassword, email, confirmPassword;
    TextInputEditText newPasswordEditTxt, confirmPasswordEditTxt;
    Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        nameText.setValue(getString(R.string.password_));

        if (getArguments() != null) {
            oldPassword = getArguments().getString("password");
            email = getArguments().getString("email");
        }

        newPasswordEditTxt = view.findViewById(R.id.new_password_edit_txt);
        confirmPasswordEditTxt = view.findViewById(R.id.confirm_password_edit_txt);
        nextBtn = view.findViewById(R.id.change_password_next_btn);

        nextBtn.setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            newPassword = newPasswordEditTxt.getText().toString();
            confirmPassword = confirmPasswordEditTxt.getText().toString();

            if (!checkPass(newPassword) || !checkConfirmPass(newPassword, confirmPassword)) {
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            ErrorCatching errorCatching = new ErrorCatching(getContext());

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password has been changed successfully." +
                                            "\nPlease use your new password to log in.", Toast.LENGTH_SHORT).show();

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
                    } else {
                        errorCatching.onException(task.getException());
                    }
                }
            });
        }
    };

    private boolean checkPass(String pass) {

        String result_ = validatePassword(pass);

        if (result_.equals(ERR_EMPTY)) {
            newPasswordEditTxt.setError(getString(R.string.password_empty_err));
            newPasswordEditTxt.requestFocus();
            return false;
        } else if (result_.equals(NOT_MATCH)) {
            newPasswordEditTxt.setError(getString(R.string.password_match_err));
            newPasswordEditTxt.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private boolean checkConfirmPass(String pass, String confirmPass) {

        String result_ = validateConfirmPassword(pass, confirmPass);

        if (result_.equals(ERR_EMPTY)) {
            confirmPasswordEditTxt.setError(getString(R.string.password_empty_err));
            confirmPasswordEditTxt.requestFocus();
            return false;
        } else if (result_.equals(NOT_MATCH)) {
            confirmPasswordEditTxt.setError(getString(R.string.match_confirm_password_err));
            confirmPasswordEditTxt.requestFocus();
            return false;
        } else {
            return true;
        }

    }
}