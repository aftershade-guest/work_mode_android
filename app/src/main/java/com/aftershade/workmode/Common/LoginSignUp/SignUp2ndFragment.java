package com.aftershade.workmode.Common.LoginSignUp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aftershade.workmode.Databases.SignUpSession;
import com.aftershade.workmode.R;

import java.util.HashMap;

import static com.aftershade.workmode.Databases.SignUpSession.KEY_DATE_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_GENDER_S;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.NOT_MATCH;
import static com.aftershade.workmode.HelperClasses.Validators.ModelValidator.validateAge;


public class SignUp2ndFragment extends Fragment {

    AppCompatButton nextBtn;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;
    View view;
    TextView cancel;
    SignUpSession sign;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up2nd, container, false);
        String whatToDo = "";
        sign = new SignUpSession(getContext());

        nextBtn = view.findViewById(R.id.signup_next_btn2);
        radioGroup = view.findViewById(R.id.radio_group);
        datePicker = view.findViewById(R.id.signup_age_picker);
        cancel = view.findViewById(R.id.signup2nd_login_txt);


        if (getArguments() != null) {
            whatToDo = getArguments().getString("whatToDo");
        }

        if (!whatToDo.isEmpty() && whatToDo.equals("load")) {

            RadioButton radioButton;
            HashMap<String, String> hashMap = sign.getSecondPart();
            String gender = hashMap.get(KEY_GENDER_S);
            String date = hashMap.get(KEY_DATE_S);

            if (gender.equals("Male")) {
                radioButton = view.findViewById(R.id.signup_male);
                radioButton.setChecked(true);
            } else if (gender.equals("Female")) {
                radioButton = view.findViewById(R.id.signup_female);
                radioButton.setChecked(true);
            } else if (gender.equals("Other")) {
                radioButton = view.findViewById(R.id.signup_other);
                radioButton.setChecked(true);
            }

            String[] dateArr = date.split("/");

            datePicker.updateDate(Integer.parseInt(dateArr[2]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[0]));

        }

        nextBtn.setOnClickListener(nextClickListener);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpSession signUpSession = new SignUpSession(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("You are about to cancel your registration. Would you like us to save your progress?");
                builder.setNegativeButton("No, don't save", (dialog, which) -> {

                    signUpSession.clearProgress();
                    Navigation.findNavController(v).navigate(R.id.action_signUp2ndFragment_to_loginFragment);

                }).setPositiveButton("Yes, save", (dialog, which) -> {

                    String gender = "None";

                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        selectedGender = view.findViewById(radioGroup.getCheckedRadioButtonId());
                        gender = selectedGender.getText().toString();
                    }

                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int day = datePicker.getDayOfMonth();

                    final String date = day + "/" + month + "/" + year;
                    signUpSession.addSecondPart(gender, date);

                    Navigation.findNavController(v).navigate(R.id.action_signUp2ndFragment_to_loginFragment);

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }


    View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();

            if (!checkAge(year) || !checkGender()) {
                return;
            }

            selectedGender = view.findViewById(radioGroup.getCheckedRadioButtonId());


            String gender = selectedGender.getText().toString();

            final String date = day + "/" + month + "/" + year;

            SignUpSession signUpSession = new SignUpSession(getContext());

            signUpSession.addSecondPart(gender, date);

            Navigation.findNavController(v).navigate(R.id.action_signUp2ndFragment_to_signUp3rdFragment);

        }
    };

    private boolean checkAge(int year) {

        String result_ = validateAge(year);

        if (result_.equals(NOT_MATCH)) {
            Toast.makeText(getContext(), "You are not of eligible age to apply.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    private boolean checkGender() {

        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }
}