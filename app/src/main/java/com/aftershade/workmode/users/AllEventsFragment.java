package com.aftershade.workmode.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Events.EventsDatabase;
import com.aftershade.workmode.HelperClasses.Events.EventsModel;
import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ui.home.Adapters.TodayEventAdapter;
import com.aftershade.workmode.users.ui.home.AddEventFragment;

import java.util.ArrayList;

public class AllEventsFragment extends Fragment {

    RelativeLayout allEventsLayout;
    RecyclerView completeRecycler;
    EventsDatabase eventsDatabase;
    ImageView collapseIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

        eventsDatabase = new EventsDatabase(getContext());

        allEventsLayout = view.findViewById(R.id.all_events_layout);
        completeRecycler = view.findViewById(R.id.complete_events_recycler);
        collapseIcon = view.findViewById(R.id.collapse_all_events);

        allEventsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completeRecycler.getVisibility() == View.VISIBLE) {
                    completeRecycler.setVisibility(View.GONE);
                    collapseIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.arrow_right_icon));

                } else {
                    completeRecycler.setVisibility(View.VISIBLE);
                    collapseIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.drop_down_icon));
                }
            }
        });

        completeRecycler.setHasFixedSize(true);
        completeRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //inflateRecycler();

        return view;
    }

    /*private void inflateRecycler() {

        try {

            ArrayList<EventsModel> eventsModels = eventsDatabase.getAllEvents();

            TodayEventAdapter allEventAdapter = new TodayEventAdapter(getContext(), eventsModels);

            allEventAdapter.setClick_listerner(new Click_Listerner() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    long id = eventsModels.get(completeRecycler.getChildLayoutPosition(v)).getEvent_id();

                    Bundle bundle = new Bundle();

                    bundle.putLong("eventId", id);

                    getActivity().getSupportFragmentManager().beginTransaction();
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(
                            R.id.container_userdash, AddEventFragment.class, bundle).addToBackStack("").commit();

                }
            });

            completeRecycler.setAdapter(allEventAdapter);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }*/
}