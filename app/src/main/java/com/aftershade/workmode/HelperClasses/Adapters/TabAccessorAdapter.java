package com.aftershade.workmode.HelperClasses.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aftershade.workmode.users.workout_posts.PhotosFragmentFragment;
import com.aftershade.workmode.users.workout_posts.ScheduleFragment;
import com.aftershade.workmode.users.workout_posts.VideosFragment;

public class TabAccessorAdapter extends FragmentPagerAdapter {

    String courseName;
    String trainerId;

    public TabAccessorAdapter(@NonNull FragmentManager fm, String courseName, String trainerId) {
        super(fm);
        this.courseName = courseName;
        this.trainerId = trainerId;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("courseName", courseName);
        bundle.putString("id", trainerId);

        switch (position) {
            case 0:
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                scheduleFragment.setArguments(bundle);
                return scheduleFragment;
            case 1:
                PhotosFragmentFragment photosFragment = new PhotosFragmentFragment();
                photosFragment.setArguments(bundle);
                return photosFragment;
            case 2:
                VideosFragment videosFragment = new VideosFragment();
                videosFragment.setArguments(bundle);
                return videosFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Schedule";
            case 1:
                return "Photos";
            case 2:
                return "Videos";
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }


}
