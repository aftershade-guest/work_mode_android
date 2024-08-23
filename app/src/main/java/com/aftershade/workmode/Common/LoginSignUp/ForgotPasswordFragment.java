package com.aftershade.workmode.Common.LoginSignUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.aftershade.workmode.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateEmail;

public class ForgotPasswordFragment extends Fragment {

    TextInputEditText emailEditTxt;
    Button nextBtn;
    ImageButton backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        emailEditTxt = view.findViewById(R.id.forgot_password_email_edit_text);
        nextBtn = view.findViewById(R.id.forgot_password_continue_btn);
        backBtn = view.findViewById(R.id.forgot_password_back_btn);

        nextBtn.setOnClickListener(onClickListener);

        backBtn.setOnClickListener(v -> {
            super.getActivity().onBackPressed();
        });

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = emailEditTxt.getText().toString();

            if (!validate_Email(email)){
                return;
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
}