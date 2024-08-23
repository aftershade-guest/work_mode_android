package com.aftershade.workmode.users.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.settings.Account.ChangeEmailFragment;
import com.aftershade.workmode.users.settings.Account.ChangePhoneFragment;
import com.aftershade.workmode.users.settings.Account.DeleteFragment;
import com.aftershade.workmode.users.settings.Security.ChangePasswordFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateEmail;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validatePassword;

public class EnterLoginInfoFragment extends Fragment {

    String whereTo;
    Button nextBtn;
    TextInputEditText emailEditTxt, passwordEditTxt;


    public EnterLoginInfoFragment() {
    }

    public EnterLoginInfoFragment(String whereTo) {
        this.whereTo = whereTo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_login_info, container, false);
        emailEditTxt = view.findViewById(R.id.enter_log_email_edit_txt);
        passwordEditTxt = view.findViewById(R.id.enter_log_password_edit_txt);

        nextBtn = view.findViewById(R.id.enter_login_next_btn);

        nextBtn.setOnClickListener(nextBtnListner);

        return view;
    }

    View.OnClickListener nextBtnListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String email = emailEditTxt.getText().toString();
            String password = passwordEditTxt.getText().toString();

            if (!validate_Email(email) || !validate_Password(password)) {
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("password", password);

            /*ErrorCatching errorCatching = new ErrorCatching(getContext());

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                            } else {
                                errorCatching.onException(task.getException());
                            }
                        }
                    });*/

            if (whereTo.equals("changeEmail")) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        ChangeEmailFragment.class, bundle).addToBackStack("").commit();
            } else if (whereTo.equals("changePhone")) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        ChangePhoneFragment.class, bundle).addToBackStack("").commit();
            } else if (whereTo.equals("deleteAccount")) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        DeleteFragment.class, bundle).addToBackStack("").commit();
            } else if (whereTo.equals("changePassword")) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        ChangePasswordFragment.class, bundle).addToBackStack("").commit();
            }
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
            passwordEditTxt.setError(getString(R.string.password_empty_err));
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