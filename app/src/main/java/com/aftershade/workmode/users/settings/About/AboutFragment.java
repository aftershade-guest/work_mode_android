package com.aftershade.workmode.users.settings.About;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aftershade.workmode.R;
import com.aftershade.workmode.users.settings.Account.AccountFragment;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class AboutFragment extends Fragment {

    TextView appInfo, termsOfUse, privacyPolicy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        nameText.setValue(getString(R.string.about));

        appInfo = view.findViewById(R.id.app_info_about);
        termsOfUse = view.findViewById(R.id.terms_of_use_about);
        privacyPolicy = view.findViewById(R.id.privacy_policy_about);

        appInfo.setOnClickListener(aboutClickListener);
        termsOfUse.setOnClickListener(aboutClickListener);
        privacyPolicy.setOnClickListener(aboutClickListener);

        return view;
    }

    View.OnClickListener aboutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == appInfo.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new AppInfoFragment()).addToBackStack("").commit();
                nameText.setValue(getString(R.string.app_info_));
            } else if (v.getId() == termsOfUse.getId()) {

            }  else if (v.getId() == privacyPolicy.getId()) {

            }
        }
    };
}