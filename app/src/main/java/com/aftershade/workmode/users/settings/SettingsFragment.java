package com.aftershade.workmode.users.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Common.LoginSignUp.LoginSignUpActivity;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.settings.About.AboutFragment;
import com.aftershade.workmode.users.settings.Account.AccountFragment;
import com.aftershade.workmode.users.settings.Help.HelpFragment;
import com.aftershade.workmode.users.settings.Privacy.PrivacyFragment;
import com.aftershade.workmode.users.settings.Security.SecurityFragment;
import com.aftershade.workmode.users.settings.Theme.ThemeFragment;
import com.google.firebase.auth.FirebaseAuth;

import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.users.BottomNav.nameText;

public class SettingsFragment extends Fragment {

    private RelativeLayout accountLay, privacyLay, securityLay, helpLay, aboutLay, themeLay;
    TextView logOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        nameText.setValue(getString(R.string.settings_));

        accountLay = view.findViewById(R.id.account_layout_settings);
        privacyLay = view.findViewById(R.id.privacy_layout_settings);
        securityLay = view.findViewById(R.id.security_layout_settings);
        helpLay = view.findViewById(R.id.help_layout_settings);
        aboutLay = view.findViewById(R.id.about_layout_settings);
        themeLay = view.findViewById(R.id.theme_layout_settings);

        logOut = view.findViewById(R.id.logout_btn);

        accountLay.setOnClickListener(onClickListenerLayouts);
        privacyLay.setOnClickListener(onClickListenerLayouts);
        securityLay.setOnClickListener(onClickListenerLayouts);
        helpLay.setOnClickListener(onClickListenerLayouts);
        aboutLay.setOnClickListener(onClickListenerLayouts);
        themeLay.setOnClickListener(onClickListenerLayouts);

        logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SessionManager sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);
            sessionManager.logooutUserFromSession();
            getActivity().startActivity(new Intent(getActivity(), LoginSignUpActivity.class));
            getActivity().finish();
        });

        return view;
    }

    View.OnClickListener onClickListenerLayouts = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == accountLay.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new AccountFragment()).addToBackStack("").commit();
            } else if ((v.getId() == privacyLay.getId())) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new PrivacyFragment()).addToBackStack("").commit();
            } else if ((v.getId() == securityLay.getId())) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new SecurityFragment()).addToBackStack("").commit();
            } else if ((v.getId() == helpLay.getId())) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new HelpFragment()).addToBackStack("").commit();
            } else if ((v.getId() == aboutLay.getId())) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new AboutFragment()).addToBackStack("").commit();
            } else if ((v.getId() == themeLay.getId())) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new ThemeFragment()).addToBackStack("").commit();
            }
        }
    };
}