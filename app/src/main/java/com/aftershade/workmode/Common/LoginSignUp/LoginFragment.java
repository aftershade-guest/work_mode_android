package com.aftershade.workmode.Common.LoginSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.HelperClasses.Models.User;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.BottomNav;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.aftershade.workmode.Databases.SessionManager.KEY_SESSIONEMAIL;
import static com.aftershade.workmode.Databases.SessionManager.KEY_SESSIONPASSWORD;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_REMEMBERME;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateEmail;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validatePassword;

public class LoginFragment extends Fragment {

    TextInputEditText emailEditTxt, passwordEditTxt;
    AppCompatButton loginBtn;
    TextView signUpTxt;
    MaterialCheckBox rememberMeChk;
    String email, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditTxt = view.findViewById(R.id.login_email_edit_txt);
        passwordEditTxt = view.findViewById(R.id.login_password_edit_txt);
        rememberMeChk = view.findViewById(R.id.remember_me_chk);
        loginBtn = view.findViewById(R.id.login_btn);
        signUpTxt = view.findViewById(R.id.sign_up_login_link);

        loginBtn.setOnClickListener(loginClick);

        checkRememberME();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getActivity().startActivity(new Intent(getActivity(), BottomNav.class));
            getActivity().finish();
        }

        signUpTxt.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signupFragment);
        });

        rememberMeChk.setOnClickListener(v -> {
            if (!rememberMeChk.isChecked()) {
                SessionManager sessionManager = new SessionManager(getContext(), SESSION_REMEMBERME);
                if (sessionManager.checkRememberMe()) {
                    sessionManager.logooutUserFromSession();
                }
            }
        });

        return view;
    }

    private void checkRememberME() {
        SessionManager sessionManager = new SessionManager(getContext(), SESSION_REMEMBERME);

        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberDetails = sessionManager.getRememberMeDetailsFromSession();
            emailEditTxt.setText(rememberDetails.get(KEY_SESSIONEMAIL));
            passwordEditTxt.setText(rememberDetails.get(KEY_SESSIONPASSWORD));

            rememberMeChk.setChecked(true);
        }
    }

    View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            email = emailEditTxt.getText().toString().trim();
            password = passwordEditTxt.getText().toString();

            if (!validate_Email(email) || !validate_Password(password)) {
                return;
            }


            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {

                                                    User user = snapshot.getValue(User.class);

                                                    SessionManager sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);

                                                    if (!sessionManager.checkLogin()) {
                                                        sessionManager.createLoginSession(user.getFullName(), user.getEmail(), user.getPhoneNumber(),
                                                                password, user.getDateOfBirth(), user.getGender());
                                                    }

                                                    if (rememberMeChk.isChecked()) {
                                                        sessionManager = new SessionManager(getContext(), SESSION_REMEMBERME);
                                                        sessionManager.createRememberMeSession(user.getEmail(), password);
                                                    }

                                                    Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_SHORT).show();

                                                    getActivity().startActivity(new Intent(getActivity(), BottomNav.class));
                                                    getActivity().finish();

                                                } else {
                                                    Toast.makeText(getActivity(), "No such user.\nPlease creaate an account.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                ErrorCatching errorCatching = new ErrorCatching(getContext());
                                                errorCatching.onError(error);
                                            }
                                        });

                            } else {
                                ErrorCatching errorCatching = new ErrorCatching(getContext());
                                errorCatching.onException(task.getException());
                            }

                        }
                    });


        }
    };

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

    private boolean validate_Password(String password) {
        String returns_ = validatePassword(password);

        if (returns_.equals(ERR_EMPTY)) {
            passwordEditTxt.setError(getString(R.string.provide_email_err));
            passwordEditTxt.requestFocus();
            return false;
        } else if (returns_.equals(NOT_MATCH)) {
            passwordEditTxt.setError(getString(R.string.email_not_match));
            passwordEditTxt.requestFocus();
            return false;
        } else {
            return true;
        }

    }
}