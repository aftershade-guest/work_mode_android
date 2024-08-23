package com.aftershade.workmode.users.settings.Theme;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.aftershade.workmode.R;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class ThemeFragment extends Fragment {

    RadioGroup themeMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        nameText.setValue("Theme");

        themeMode = view.findViewById(R.id.theme_mode_);

        themeMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });

        return view;
    }
}