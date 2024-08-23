package com.aftershade.workmode.Common.LoginSignUp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aftershade.workmode.Databases.SignUpSession;
import com.aftershade.workmode.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import static com.aftershade.workmode.Databases.SignUpSession.KEY_EMAIL_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_FULLNAME_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_PASSWORD_S;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateConfirmPassword;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateEmail;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateFullName;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validatePassword;

public class SignupFragment extends Fragment {

    Button nextSignUpbutton;
    TextInputEditText fullnameEditTxt, emailEditTxt, passwordEditTxt, confirmPasswordEditTxt;
    TextView loginTxt;
    String fullName, email, password;

    SignUpSession sign;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        sign = new SignUpSession(getContext());

        fullnameEditTxt = view.findViewById(R.id.fullname_signup_txt);
        emailEditTxt = view.findViewById(R.id.signup_email_txt);
        passwordEditTxt = view.findViewById(R.id.signup_password_txt);
        confirmPasswordEditTxt = view.findViewById(R.id.confirm_password_txt);
        loginTxt = view.findViewById(R.id.signup_login_txt);
        nextSignUpbutton = view.findViewById(R.id.signup_next_btn1);

        nextSignUpbutton.setOnClickListener(signUpListener);

        if (sign.checkSignUp()) {
            showAlertDialog_();
        }

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignUpSession signUpSession = new SignUpSession(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("You are about to cancel your registration. Would you like us to save your progress?");
                builder.setNegativeButton("No, don't save", (dialog, which) -> {

                    signUpSession.clearProgress();
                    Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_loginFragment);

                }).setPositiveButton("Yes, save", (dialog, which) -> {

                    fullName = fullnameEditTxt.getText().toString();
                    email = emailEditTxt.getText().toString();
                    password = passwordEditTxt.getText().toString();

                    if (fullName.isEmpty()) {
                        fullName = " ";
                    }

                    if (email.isEmpty()) {
                        email = " ";
                    }

                    if (password.isEmpty()) {
                        password = " ";
                    }

                    signUpSession.addFirstPart(fullName, email, password);

                    Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_loginFragment);

                });
            }
        });

        return view;
    }

    private void showAlertDialog_() {

        String progress = sign.checkProgress();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Would you like to continue with your previous sign up progress?");

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (progress.equals("fragment1")) {

                    HashMap<String, String> hashMap = sign.getFirstPart();
                    fullnameEditTxt.setText(hashMap.get(KEY_FULLNAME_S));
                    emailEditTxt.setText(hashMap.get(KEY_EMAIL_S));
                    passwordEditTxt.setText(hashMap.get(KEY_PASSWORD_S));
                } else if (progress.equals("fragment2")) {
                    //getParentFragmentManager().beginTransaction().replace()
                    Bundle bundle = new Bundle();
                    bundle.putString("whatToDo", "load");

                    Navigation.findNavController(getView()).navigate(R.id.action_signupFragment_to_signUp2ndFragment,bundle);
                } else if (progress.equals("fragment3")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("whatToDo", "load");

                    Navigation.findNavController(getView()).navigate(R.id.action_signupFragment_to_signUp3rdFragment,bundle);
                }
            }
        });

    }

    View.OnClickListener signUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            fullName = fullnameEditTxt.getText().toString();
            email = emailEditTxt.getText().toString();
            password = passwordEditTxt.getText().toString();
            String confirmPass = confirmPasswordEditTxt.getText().toString();

            if (!checkName(fullName) || !checkEmail(email) || !checkPass(password) || !checkConfirmPass(password, confirmPass)) {
                return;
            }

            SignUpSession signUpSession = new SignUpSession(getContext());
            signUpSession.addFirstPart(fullName, email, password);

            Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_signUp2ndFragment);

        }
    };

    private boolean checkName(String name) {

        String result_ = validateFullName(name);

        if (result_.equals(ERR_EMPTY)) {
            fullnameEditTxt.setError("Please provide your full name");
            fullnameEditTxt.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkEmail(String email) {
        String result_ = validateEmail(email);

        if (result_.equals(ERR_EMPTY)) {
            emailEditTxt.setError(getString(R.string.provide_email_err));
            emailEditTxt.requestFocus();
            return false;
        } else if (result_.equals(NOT_MATCH)) {
            emailEditTxt.setError(getString(R.string.email_not_match));
            emailEditTxt.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPass(String pass) {

        String result_ = validatePassword(pass);

        if (result_.equals(ERR_EMPTY)) {
            passwordEditTxt.setError(getString(R.string.password_empty_err));
            passwordEditTxt.requestFocus();
            return false;
        } else if (result_.equals(NOT_MATCH)) {
            passwordEditTxt.setError(getString(R.string.password_match_err));
            passwordEditTxt.requestFocus();
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