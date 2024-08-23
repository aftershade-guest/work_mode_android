package com.aftershade.workmode.users.ui.home.EventsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aftershade.workmode.R;


public class MonthlyEventsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly_events, container, false);
    }

    public void monthEvents() {
        try {

            // ArrayList<EventsModel> eventsModels = eventsDatabase.getMonthEvents();
            //activitiesRecycler.setAdapter(new TodayEventAdapter(getContext(), eventsModels));
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}