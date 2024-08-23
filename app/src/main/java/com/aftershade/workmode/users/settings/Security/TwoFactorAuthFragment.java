package com.aftershade.workmode.users.settings.Security;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aftershade.workmode.R;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class TwoFactorAuthFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nameText.setValue(getString(R.string.two_factor_));
        return inflater.inflate(R.layout.fragment_two_factor_auth, container, false);
    }
}