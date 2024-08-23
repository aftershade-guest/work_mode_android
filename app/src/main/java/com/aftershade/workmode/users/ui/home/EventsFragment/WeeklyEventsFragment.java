package com.aftershade.workmode.users.ui.home.EventsFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aftershade.workmode.HelperClasses.Events.EventsModel;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ui.home.Adapters.TodayEventAdapter;

import java.util.ArrayList;

public class WeeklyEventsFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_weekly_events, container, false);
    }

    public void weekEvents() {
        try {

            //ArrayList<EventsModel> eventsModels = eventsDatabase.getWeekEvents();
            //activitiesRecycler.setAdapter(new TodayEventAdapter(getContext(), eventsModels));
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}