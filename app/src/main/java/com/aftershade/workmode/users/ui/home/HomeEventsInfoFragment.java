package com.aftershade.workmode.users.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aftershade.workmode.HelperClasses.Events.EventsDatabase;
import com.aftershade.workmode.HelperClasses.Models.ProgramOfflineModel;
import com.aftershade.workmode.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HomeEventsInfoFragment extends BottomSheetDialogFragment {

    Long eventId;
    EventsDatabase eventsDatabase;
    TextView programName, skillLevel, timeSpan, programType, workoutDays, todayWorkout;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getLong("event_id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_events_info, container, false);

        initFields();

        eventsDatabase = new EventsDatabase(getContext());

        ProgramOfflineModel model = eventsDatabase.getEvent(eventId);

        programName.setText(model.getTitle());
        skillLevel.setText(model.getSkillLevel());
        timeSpan.setText(model.getTimeSpan());
        programType.setText(model.getProgramType());
        workoutDays.setText(model.getExerciseDays());

        switch (model.getDayCount()) {
            case 1:
                workoutDays.setText(model.getDay01());
                break;
            case 2:
                workoutDays.setText(model.getDay02());
                break;
            case 3:
                workoutDays.setText(model.getDay03());
                break;
            case 4:
                workoutDays.setText(model.getDay04());
                break;
            case 5:
                workoutDays.setText(model.getDay05());
                break;
            case 6:
                workoutDays.setText(model.getDay06());
                break;
            case 7:
                workoutDays.setText(model.getDay07());
                break;
        }

        return view;
    }

    private void initFields() {

        programName = view.findViewById(R.id.program_name_events_info_bottom_sheet);
        skillLevel = view.findViewById(R.id.skill_level_events_info_bottom_sheet);
        timeSpan = view.findViewById(R.id.time_span_events_info_bottom_sheet);
        programType = view.findViewById(R.id.program_type_events_info_bottom_sheet);
        workoutDays = view.findViewById(R.id.workout_days_events_info_bottom_sheet);
        todayWorkout = view.findViewById(R.id.today_workout_events_info_bottom_sheet);

    }
}