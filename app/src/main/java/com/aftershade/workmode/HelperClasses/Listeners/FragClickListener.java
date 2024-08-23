package com.aftershade.workmode.HelperClasses.Listeners;

import android.view.View;

import androidx.fragment.app.FragmentActivity;

public interface FragClickListener {

    void onClick(FragmentActivity fragmentActivity, View v, int position, boolean isLongClick);

}
