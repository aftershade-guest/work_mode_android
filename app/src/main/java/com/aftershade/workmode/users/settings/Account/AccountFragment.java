package com.aftershade.workmode.users.settings.Account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aftershade.workmode.R;
import com.aftershade.workmode.users.settings.EnterLoginInfoFragment;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class AccountFragment extends Fragment {

    TextView personalInfo, language, changeEmail, changePhone, deleteAcc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        nameText.setValue(getString(R.string.account_));

        personalInfo = view.findViewById(R.id.personal_info_account);
        language = view.findViewById(R.id.language_account);
        changeEmail = view.findViewById(R.id.change_email_account);
        changePhone = view.findViewById(R.id.change_phone_account);
        deleteAcc = view.findViewById(R.id.delete_account);

        //Set click listeners
        personalInfo.setOnClickListener(accountClickCListener);
        language.setOnClickListener(accountClickCListener);
        changeEmail.setOnClickListener(accountClickCListener);
        changePhone.setOnClickListener(accountClickCListener);
        deleteAcc.setOnClickListener(accountClickCListener);

        return view;
    }

    View.OnClickListener accountClickCListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == personalInfo.getId()) {

            } else if (v.getId() == language.getId()) {

            } else if (v.getId() == changeEmail.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new EnterLoginInfoFragment("changeEmail")).addToBackStack("").commit();
                nameText.setValue(getString(R.string.change_email));
            } else if (v.getId() == changePhone.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new EnterLoginInfoFragment("changePhone")).addToBackStack("").commit();
                nameText.setValue(getString(R.string.change_phone));
            } else if (v.getId() == deleteAcc.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new EnterLoginInfoFragment("deleteAccount")).addToBackStack("").commit();
                nameText.setValue(getString(R.string.delete_account));
            }

        }
    };
}