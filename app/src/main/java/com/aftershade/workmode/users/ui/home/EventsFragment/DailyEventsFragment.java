package com.aftershade.workmode.users.ui.home.EventsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Events.EventsDatabase;
import com.aftershade.workmode.R;

public class DailyEventsFragment extends Fragment {

    RecyclerView activitiesRecycler;
    EventsDatabase eventsDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);
        eventsDatabase = new EventsDatabase(getContext());

        activitiesRecycler = view.findViewById(R.id.today_events_recycler);

        activitiesRecycler.setHasFixedSize(true);
        activitiesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //todaysEvents();

        return view;
    }

}