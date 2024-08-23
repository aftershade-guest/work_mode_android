package com.aftershade.workmode.users.workout_posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.ScheduleRecyclerAdapter;
import com.aftershade.workmode.HelperClasses.Models.ScheduleHelper;
import com.aftershade.workmode.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    RecyclerView scheduleRecycler;
    DatabaseReference courseRef;
    String currentUserId;
    String courseName, trainerId;
    ArrayList<ScheduleHelper> helperArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            courseName = getArguments().getString("courseName");
            trainerId = getArguments().getString("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        courseRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs")
                .child(courseName).child("schedule");
        helperArrayList = new ArrayList<>();

        scheduleRecycler = view.findViewById(R.id.schedule_recycler_fragment_schedule);
        scheduleRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        loadRecyclerContent();

        return view;
    }

    private void loadRecyclerContent() {

        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.hasChildren()) {
                    helperArrayList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        helperArrayList.add(new ScheduleHelper(snap.getKey(), snap.getValue().toString()));
                    }

                    ScheduleRecyclerAdapter adapter = new ScheduleRecyclerAdapter(helperArrayList);
                    scheduleRecycler.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorCatching errorCatching = new ErrorCatching(getContext());
                errorCatching.onError(error);
            }
        });

    }


}