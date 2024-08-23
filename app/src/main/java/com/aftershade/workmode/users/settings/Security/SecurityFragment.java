package com.aftershade.workmode.users.settings.Security;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aftershade.workmode.R;
import com.aftershade.workmode.users.settings.Account.AccountFragment;
import com.aftershade.workmode.users.settings.EnterLoginInfoFragment;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class SecurityFragment extends Fragment {

    RelativeLayout password, rememberMe, twoFactorAuth, accessData, downloadData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_security, container, false);
        nameText.setValue(getString(R.string.securuty_));

        password = view.findViewById(R.id.password_layout_security);
        rememberMe = view.findViewById(R.id.remember_layout_security);
        twoFactorAuth = view.findViewById(R.id.two_factor_auth_lay);
        accessData = view.findViewById(R.id.access_data_lay);
        downloadData = view.findViewById(R.id.download_data_lay);

        password.setOnClickListener(securityClickListener);
        rememberMe.setOnClickListener(securityClickListener);
        twoFactorAuth.setOnClickListener(securityClickListener);

        return view;
    }

    View.OnClickListener securityClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == password.getId()){
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new EnterLoginInfoFragment("changePassword")).addToBackStack("").commit();
            } else if (v.getId() == rememberMe.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new RememberEnabledFragment()).addToBackStack("").commit();
            } else if (v.getId() == twoFactorAuth.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new TwoFactorAuthFragment()).addToBackStack("").commit();
            } else if (v.getId() == accessData.getId()) {

            } else if (v.getId() == downloadData.getId()) {

            }

        }
    };
}