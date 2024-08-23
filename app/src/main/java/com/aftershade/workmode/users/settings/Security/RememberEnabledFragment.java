package com.aftershade.workmode.users.settings.Security;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.R;

import static com.aftershade.workmode.Databases.SessionManager.SESSION_REMEMBERME;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.users.BottomNav.nameText;

public class RememberEnabledFragment extends Fragment {

    SwitchCompat rememberMe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remember_enabled, container, false);
        nameText.setValue(getString(R.string.remember_me_));

        rememberMe = view.findViewById(R.id.remember_me_switch);

        SessionManager sessionManager = new SessionManager(getContext(), SESSION_REMEMBERME);
        rememberMe.setChecked(sessionManager.checkRememberMe());

        rememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rememberMe.isChecked()) {
                    SessionManager sessionManagerLogin = new SessionManager(getContext(), SESSION_USERSESSION);
                    sessionManager.createRememberMeSession(sessionManagerLogin.getEmail(), sessionManagerLogin.getPassword());
                } else if (!rememberMe.isChecked()){
                    sessionManager.logooutUserFromSession();
                }
            }
        });

        return view;
    }
}