package com.aftershade.workmode.users.ui.home.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aftershade.workmode.users.ui.home.EventsFragment.DailyEventsFragment;
import com.aftershade.workmode.users.ui.home.EventsFragment.MonthlyEventsFragment;
import com.aftershade.workmode.users.ui.home.EventsFragment.WeeklyEventsFragment;

public class EventsVPagerAdapter extends FragmentPagerAdapter {
    public EventsVPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public EventsVPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new DailyEventsFragment();
        } else if (position == 1) {
            return new WeeklyEventsFragment();
        } else if (position == 2) {
            return new MonthlyEventsFragment();
        } else {
            return new DailyEventsFragment();
        }


    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Daily";
        } else if (position == 1) {
            return "Weekly";
        } else if (position == 2) {
            return "Monthly";
        } else {
            return null;
        }
    }
}
