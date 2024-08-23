package com.aftershade.workmode.users.ui.home;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.HelperClasses.Events.EventsController;
import com.aftershade.workmode.HelperClasses.Events.EventsDatabase;
import com.aftershade.workmode.HelperClasses.Events.EventsModel;
import com.aftershade.workmode.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AddEventFragment extends Fragment {

    private EditText titleEdit, locationEdit, durationEdit, descEdit, inviteeEdit;
    private TextView startDate, startTime, endDate, endTime;
    private AppCompatSpinner calendarSpinner, repeatSpinner;
    private DatePicker startDatePicker, endDatePicker;
    private TimePicker startTimePicker, endTimePicker;
    private RelativeLayout startLayout, endLayout;
    private LinearLayout startPickerLayout, endPickerLayout;
    private MaterialButton cancelBtn, saveBtn;
    View view;
    ContentResolver cr;
    String[] calNames;
    int[] calIds;
    long eventId = 0;
    EventsDatabase eventsDatabase;
    String cMonth, cYear, cDay;
    String hour, min;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_event, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getLong("eventId");
        }

        initFields();
        eventsDatabase = new EventsDatabase(getContext());

        cr = requireContext().getContentResolver();
        loadCalendars();

        startLayout.setOnClickListener(dateLayoutListener);
        endLayout.setOnClickListener(dateLayoutListener);

        initDatesAndTimes();

        startDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {

            setDates(year, monthOfYear, dayOfMonth);

            startDate.setText(cYear + "-" + cMonth + "-" + cDay);
        });

        endDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {

            setDates(year, monthOfYear, dayOfMonth);

            endDate.setText(cYear + "-" + cMonth + "-" + cDay);
        });

        startTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {

            setTimes(hourOfDay, minute);

            startTime.setText(hour + ":" + min);
        });

        endTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {

            setTimes(hourOfDay, minute);

            endTime.setText(hour + ":" + min);
        });

        initSpinners();

        saveBtn.setOnClickListener(saveBtnListener);
        cancelBtn.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }

    private void initSpinners() {

        ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, calNames);
        calendarSpinner.setAdapter(stringArrayAdapter);
        calendarSpinner.setSelection(0);

        String[] stringsRepeat = new String[]{getString(R.string.dont_repeat),
                getString(R.string.evry_day),
                getString(R.string.every_week),
                getString(R.string.every_month)};

        repeatSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, stringsRepeat));
        repeatSpinner.setSelection(0);

    }

    private void initDatesAndTimes() {

        setDates(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth());
        startDate.setText(String.format("%s-%s-%s", cYear, cMonth, cDay));

        setDates(endDatePicker.getYear(), endDatePicker.getMonth(), endDatePicker.getDayOfMonth());
        endDate.setText(String.format("%s-%s-%s", cYear, cMonth, cDay));

        setTimes(startTimePicker.getHour(), startTimePicker.getMinute());
        startTime.setText(String.format("%s:%s", hour, min));

        setTimes(endTimePicker.getHour(), endTimePicker.getMinute());
        endTime.setText(String.format("%s:%s", hour, min));

    }

    private void setDates(int year, int month, int day) {

        month = month + 1;

        if (month < 10) {
            cMonth = "0" + month;
        } else {
            cMonth = String.valueOf(month);
        }

        if (day < 10) {
            cDay = "0" + day;
        } else {
            cDay = String.valueOf(day);
        }

        cYear = String.valueOf(year);

    }

    private void setTimes(int hourOfDay, int minute) {

        if (hourOfDay < 10) {
            hour = "0" + hourOfDay;
        } else {
            hour = String.valueOf(hourOfDay);
        }

        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = String.valueOf(minute);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (eventId != 0) {
            setFields();
        }

    }

    private void loadCalendars() {
        Cursor cursor;

        cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"),
                new String[]{"_id", "calendar_displayName"}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            calNames = new String[cursor.getCount()];
            calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void initFields() {
        titleEdit = view.findViewById(R.id.add_event_title);
        locationEdit = view.findViewById(R.id.add_event_location);
        durationEdit = view.findViewById(R.id.add_event_count);
        descEdit = view.findViewById(R.id.add_event_description);
        inviteeEdit = view.findViewById(R.id.add_event_invitee);

        startDate = view.findViewById(R.id.add_event_start_date_textview);
        startTime = view.findViewById(R.id.add_event_start_time_textview);
        endDate = view.findViewById(R.id.add_event_end_date_textview);
        endTime = view.findViewById(R.id.add_event_end_time_textview);

        calendarSpinner = view.findViewById(R.id.add_event_calendar_spinner);
        repeatSpinner = view.findViewById(R.id.add_event_repeat_spinner);

        startDatePicker = view.findViewById(R.id.add_event_start_date_picker);
        endDatePicker = view.findViewById(R.id.add_event_end_date_picker);
        startTimePicker = view.findViewById(R.id.add_event_start_time_picker);
        endTimePicker = view.findViewById(R.id.add_event_end_time_picker);

        startLayout = view.findViewById(R.id.add_event_start_dt_layout);
        endLayout = view.findViewById(R.id.add_event_end_dt_layout);
        startPickerLayout = view.findViewById(R.id.add_event_start_picker_layout);
        endPickerLayout = view.findViewById(R.id.add_event_end_picker_layout);

        cancelBtn = view.findViewById(R.id.add_event_cancel_btn);
        saveBtn = view.findViewById(R.id.add_event_save_btn);
    }

    View.OnClickListener dateLayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == startLayout.getId()) {
                if (startPickerLayout.getVisibility() == View.GONE) {
                    startPickerLayout.setVisibility(View.VISIBLE);
                } else {
                    startPickerLayout.setVisibility(View.GONE);
                }
                endPickerLayout.setVisibility(View.GONE);
            } else if (v.getId() == endLayout.getId()) {

                if (endPickerLayout.getVisibility() == View.GONE) {
                    endPickerLayout.setVisibility(View.VISIBLE);
                } else {
                    endPickerLayout.setVisibility(View.GONE);
                }

                startPickerLayout.setVisibility(View.GONE);
            }

        }
    };

    View.OnClickListener saveBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title = titleEdit.getText().toString();
            String startDate_ = startDate.getText().toString();
            String endDate_ = endDate.getText().toString();
            String startTime_ = startTime.getText().toString();
            String endTime_ = endTime.getText().toString();
            String location = locationEdit.getText().toString();

            int calendarId = calIds[calendarSpinner.getSelectedItemPosition()];
            String repeat = repeatSpinner.getSelectedItem().toString();
            String duration = durationEdit.getText().toString();
            String descr = descEdit.getText().toString();
            String invitee = inviteeEdit.getText().toString();

            try {

                if (repeat.equals(getString(R.string.dont_repeat)) && !duration.isEmpty()) {
                    Toast.makeText(getContext(), "Please change dont't repeat", Toast.LENGTH_SHORT).show();
                    repeatSpinner.requestFocus();
                    return;
                } else if (!repeat.equals(getString(R.string.dont_repeat)) && duration.equals("0")) {
                    Toast.makeText(getContext(), "Duration cannot be zero", Toast.LENGTH_SHORT).show();
                    durationEdit.requestFocus();
                    return;
                } else if (Integer.parseInt(duration) > 99) {
                    Toast.makeText(getContext(), "Cannot be greater than 99", Toast.LENGTH_SHORT).show();
                    durationEdit.requestFocus();
                    return;
                }

                String rrule = "";
                String freq = "";

                if (title.isEmpty()) {
                    title = "My Event";
                }

                EventsController eventsController = new EventsController(getContext());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                final long startDateL = sdf.parse(startDate_ + " " + startTime_ + ":00").getTime();
                final long endDateL = sdf.parse(endDate_ + " " + endTime_ + ":00").getTime();

                if (repeat.equals(getString(R.string.evry_day))) {
                    freq = "FREQ=DAILY";
                } else if (repeat.equals(getString(R.string.every_week))) {
                    freq = "FREQ=WEEKLY";
                } else if (repeat.equals(getString(R.string.every_month))) {
                    freq = "FREQ=MONTHLY";
                }
                rrule = freq + ";COUNT=" + duration;

                if (eventId != 0) {
                    int i = eventsController.updateEvent(eventId, calendarId, title, startDateL, endDateL, location, rrule, descr);

                    if (i < 1) {
                        Toast.makeText(getContext(), "An error occurred while saving please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean isUpdated = eventsDatabase.updateEvent(eventId, title, startDate_, endDate_, startTime_, endTime_, freq, duration, descr);

                    if (isUpdated) {
                        Toast.makeText(getContext(), "Event successfully updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "An error occurred while updating please try again", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    long event_id = eventsController.addEvent(calendarId, title, startDateL, endDateL, location, rrule, descr, invitee);

                    if (event_id < 1) {
                        Toast.makeText(getContext(), "An error occurred while saving please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long insert_ = eventsDatabase.insertEvent(event_id, calendarId, title, startDate_, endDate_,
                            startTime_, endTime_, freq, duration, descr);

                    if (insert_ != -1) {
                        Toast.makeText(getContext(), "Activity successfully created.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "An error occurred while saving please try again.", Toast.LENGTH_SHORT).show();
                    }
                    getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                            new HomeFragment()).commit();
                }

                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new HomeFragment()).commit();

            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage() + "\n" + e.getCause(), Toast.LENGTH_SHORT).show();
            }


        }
    };

    private void setFields() {
        ArrayList<EventsModel> eventsModels = eventsDatabase.getSpecificEvent(eventId);

        saveBtn.setText(R.string.update);

        titleEdit.setText(eventsModels.get(0).getTitle());
        startDate.setText(eventsModels.get(0).getDtstart());
        endDate.setText(eventsModels.get(0).getDtend());
        startTime.setText(eventsModels.get(0).getTimeStart());
        endTime.setText(eventsModels.get(0).getTimeEnd());
        locationEdit.setText(eventsModels.get(0).getTitle());
        durationEdit.setText(eventsModels.get(0).getCount());
        descEdit.setText(eventsModels.get(0).getDescription());

        String[] startTime = eventsModels.get(0).getTimeStart().split(":");
        String[] endTime = eventsModels.get(0).getTimeEnd().split(":");
        String[] startDate = eventsModels.get(0).getDtstart().split("-");
        String[] endDate = eventsModels.get(0).getDtend().split("-");


        String repeat = eventsModels.get(0).getFrequency();
        if (repeat.contains("DAILY")) {
            repeatSpinner.setSelection(1);
        } else if (repeat.contains("WEEKLY")) {
            repeatSpinner.setSelection(2);
        } else if (repeat.contains("MONTHLY")) {
            repeatSpinner.setSelection(3);
        }

        int pos = 0;
        for (int i = 0; i < calIds.length; i++) {
            if (calIds[i] == eventsModels.get(0).getCalendar_id()) {
                pos = i;
                break;
            }
        }
        calendarSpinner.setSelection(pos);

        startTimePicker.setHour(Integer.parseInt(startTime[0]));
        startTimePicker.setMinute(Integer.parseInt(startTime[1]));
        endTimePicker.setHour(Integer.parseInt(endTime[0]));
        endTimePicker.setMinute(Integer.parseInt(endTime[1]));

    }
}