package com.aftershade.workmode.users.settings.Privacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aftershade.workmode.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class PrivacyFragment extends Fragment {

    SwitchMaterial autoDownloadSwitch;
    TextView blockContacts, mutedAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        autoDownloadSwitch = view.findViewById(R.id.privacy_auto_download_switch);

        //gets shared preferences and sets switch switch checked status
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AutoDownloadMedia", Context.MODE_PRIVATE);
        boolean autoDownload = sharedPreferences.getBoolean("autoDownload", false);
        autoDownloadSwitch.setChecked(autoDownload);

        //listens to switch clicks and sets shared preferences values
        autoDownloadSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoDownloadSwitch.isChecked()) {
                    sharedPreferences.edit().putBoolean("autoDownload", true).apply();
                } else if (!autoDownloadSwitch.isChecked()) {
                    sharedPreferences.edit().putBoolean("autoDownload", false).apply();
                }
            }
        });

        return view;
    }
}