package com.aftershade.workmode.Common.LoginSignUp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aftershade.workmode.Databases.SignUpSession;
import com.aftershade.workmode.HelperClasses.Validators.ModelValidator;
import com.aftershade.workmode.R;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;

public class SignUp3rdFragment extends Fragment {

    AppCompatButton nextBtn;
    TextInputEditText phoneEditText;
    CountryCodePicker countryCodePicker;
    String phoneNumber;
    TextView cancelReg;
    SignUpSession signUpSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up3rd, container, false);
        signUpSession = new SignUpSession(getContext());
        String whatToDo = "";

        nextBtn = view.findViewById(R.id.signup_next_btn3);
        phoneEditText = view.findViewById(R.id.signup_phone);
        countryCodePicker = view.findViewById(R.id.signup_code_picker);
        cancelReg = view.findViewById(R.id.signup_cancel);

        if (getArguments() != null) {
            whatToDo = getArguments().getString("whatToDo");
        }

        if (!whatToDo.isEmpty() && whatToDo.equals("load")) {
            String phone = signUpSession.getThirdPart();
            phoneEditText.setText(phone);
        }

        cancelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("You are about to cancel your registration. Would you like us to save your progress?");
                builder.setNegativeButton("No, don't save", (dialog, which) -> {

                    signUpSession.clearProgress();
                    Navigation.findNavController(v).navigate(R.id.action_signUp3rdFragment_to_loginFragment);
                }).setPositiveButton("Yes, save", (dialog, which) -> {

                    String phone = " ";
                    if (!TextUtils.isEmpty(phoneEditText.getText())) {
                        phone = phoneEditText.getText().toString();
                    }
                    signUpSession.addThirdPart(phone);

                    Navigation.findNavController(v).navigate(R.id.action_signUp3rdFragment_to_loginFragment);

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        nextBtn.setOnClickListener(nextClickListener);

        return view;
    }

    View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            phoneNumber = phoneEditText.getText().toString();

            if (!checkPhone()) {
                return;
            }

            final String phone = countryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumber;

            SignUpSession signUpSession = new SignUpSession(getContext());
            signUpSession.addThirdPart(phone);

            Toast.makeText(getContext(), phone, Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putString("whatToDo", "signUp");

            Navigation.findNavController(v).navigate(R.id.action_signUp3rdFragment_to_verifyOTPFragment, bundle);

        }
    };

    private boolean checkPhone() {

        String result_ = ModelValidator.validatePhone(phoneNumber);

        if (result_.equals(ERR_EMPTY)) {
            phoneEditText.setError("Please provide a phone number");
            phoneEditText.requestFocus();
            return false;
        } else if (result_.equals(NOT_MATCH)) {
            phoneEditText.setError("Please provide a valid phone");
            phoneEditText.requestFocus();
            return false;
        } else {
            phoneNumber = result_;
            return true;
        }

    }


}