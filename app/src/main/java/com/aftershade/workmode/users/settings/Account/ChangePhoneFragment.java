package com.aftershade.workmode.users.settings.Account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Common.LoginSignUp.VerifyOTPFragment;
import com.aftershade.workmode.HelperClasses.Validators.ModelValidator;
import com.aftershade.workmode.R;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.ERR_EMPTY;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;

public class ChangePhoneFragment extends Fragment {

    TextInputEditText phoneEditText, newPhoneEditText;
    String phone, newPhone;
    CountryCodePicker countryCodePicker;
    Button nextBtn;
    String email = "";
    String password = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_change_phone, container, false);

        if (getArguments() != null) {
            email = getArguments().getString("email");
            password = getArguments().getString("password");
        }

        phoneEditText = view.findViewById(R.id.change_phone_edit_txt);
        newPhoneEditText = view.findViewById(R.id.new_change_phone_edit_txt);
        countryCodePicker = view.findViewById(R.id.change_phone_code_picker);
        nextBtn = view.findViewById(R.id.change_phone_next_btn);

        nextBtn.setOnClickListener(onClickListener);

        Toast.makeText(getContext(), email + "\n" + password, Toast.LENGTH_SHORT).show();

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            phone = phoneEditText.getText().toString();
            newPhone = newPhoneEditText.getText().toString();

            if (!checkPhone() || !checkNewPhone()) {
                return;
            } else if (phone.equals(newPhone)) {
                return;
            }

            phone = countryCodePicker.getSelectedCountryCodeWithPlus() + phone;
            newPhone = countryCodePicker.getSelectedCountryCodeWithPlus() + newPhone;

            Bundle bundle = new Bundle();

            bundle.putString("whatToDo", "changePhone");
            bundle.putString("newPhone", newPhone);
            bundle.putString("email", email);
            bundle.putString("password", password);

            Toast.makeText(getContext(), "Phone: " + phone + "\nNewPhone: " + newPhone, Toast.LENGTH_SHORT).show();

            getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                    VerifyOTPFragment.class, bundle).addToBackStack("").commit();

        }
    };

    private boolean checkPhone() {

        String result_ = ModelValidator.validatePhone(phone);

        if (result_.equals(ERR_EMPTY)) {
            phoneEditText.setError("Please provide a phone number");
            phoneEditText.requestFocus();
            return false;
        } else if (result_.equals(NOT_MATCH)) {
            phoneEditText.setError("Please provide a valid phone");
            phoneEditText.requestFocus();
            return false;
        } else {
            phone = result_;
            return true;
        }

    }

    private boolean checkNewPhone() {

        String result_ = ModelValidator.validatePhone(newPhone);

        if (result_.equals(ERR_EMPTY)) {
            phoneEditText.setError("Please provide a phone number");
            phoneEditText.requestFocus();
            return false;
        } else if (result_.equals(NOT_MATCH)) {
            phoneEditText.setError("Please provide a valid phone");
            phoneEditText.requestFocus();
            return false;
        } else {
            newPhone = result_;
            return true;
        }

    }
}