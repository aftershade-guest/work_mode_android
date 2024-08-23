package com.aftershade.workmode.users.workout_posts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Events.EventsController;
import com.aftershade.workmode.HelperClasses.Events.EventsDatabase;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveProgramFragment extends BottomSheetDialogFragment {

    TextView dateText, timeText;
    RelativeLayout dateTimeRelative;
    LinearLayout dateTimeLinear;
    DatePicker datePicker;
    TimePicker timePicker;
    AppCompatSpinner calendarSpinner;
    Button saveProgram;
    ContentResolver cr;
    String[] calNames;
    int[] calIds;
    EventsDatabase eventsDatabase;
    EventsController eventsController;
    String cMonth, cYear, cDay, hour, min;
    View view;
    String id, courseName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getString("id");
            courseName = getArguments().getString("courseName");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_save_program, container, false);

        initFields();

        eventsDatabase = new EventsDatabase(getContext());
        eventsController = new EventsController(getContext());

        cr = requireContext().getContentResolver();
        loadCalendars();

        setDatesTimes();

        calendarSpinner.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, calNames));
        calendarSpinner.setSelection(0);

        saveProgram.setOnClickListener(saveClickListener);

        return view;
    }

    private void initFields() {
        dateText = view.findViewById(R.id.save_program_start_date_textview);
        timeText = view.findViewById(R.id.save_program_start_time_textview);
        dateTimeRelative = view.findViewById(R.id.save_program_start_dt_layout);
        dateTimeLinear = view.findViewById(R.id.save_program_start_picker_layout);
        datePicker = view.findViewById(R.id.save_program_start_date_picker);
        timePicker = view.findViewById(R.id.save_program_start_time_picker);
        calendarSpinner = view.findViewById(R.id.save_program_calendar_spinner);
        saveProgram = view.findViewById(R.id.save_program_save_btn);
    }

    private void setDatesTimes() {

        setDates(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        dateText.setText(String.format("%s-%s-%s", cYear, cMonth, cDay));

        setTimes(timePicker.getHour(), timePicker.getMinute());
        timeText.setText(String.format("%s:%s", hour, min));

        datePicker.setOnDateChangedListener((view12, year, monthOfYear, dayOfMonth) -> {
            setDates(year, monthOfYear, dayOfMonth);

            dateText.setText(String.format("%s-%s-%s", cYear, cMonth, cDay));
        });

        timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            setTimes(hourOfDay, minute);

            timeText.setText(String.format("%s:%s", hour, min));
        });

        dateTimeRelative.setOnClickListener(v -> {
            if (v.getId() == dateTimeRelative.getId()) {
                if (dateTimeLinear.getVisibility() == View.GONE) {
                    dateTimeLinear.setVisibility(View.VISIBLE);
                } else {
                    dateTimeLinear.setVisibility(View.GONE);
                }
            }
        });

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

    View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseDatabase.getInstance().getReference().child("Workout_Programs")
                    .child(courseName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        try {
                            ProgramsHelperClass helperClass = snapshot.getValue(ProgramsHelperClass.class);

                            String day01 = null, day02 = null, day03 = null, day04 = null,
                                    day05 = null, day06 = null, day07 = null;

                            if (snapshot.hasChild("schedule")) {
                                day01 = snapshot.child("schedule").child("day01").getValue().toString();
                                day02 = snapshot.child("schedule").child("day02").getValue().toString();
                                day03 = snapshot.child("schedule").child("day03").getValue().toString();
                                day04 = snapshot.child("schedule").child("day04").getValue().toString();
                                day05 = snapshot.child("schedule").child("day05").getValue().toString();
                                day06 = snapshot.child("schedule").child("day06").getValue().toString();
                                day07 = snapshot.child("schedule").child("day07").getValue().toString();
                            }

                            //LocalDate localDate = LocalDate.now();
                            //localDate.plusWeeks(Long.parseLong(helperClass.getDuration()));

                            int calendarId = calIds[calendarSpinner.getSelectedItemPosition()];

                            String dateTime = dateText.getText() + "T" + timeText.getText() + ":00";
                            LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
                            LocalDateTime endDateTime = localDateTime.plusDays(Long.parseLong(helperClass.getEndDay()));
                            LocalDateTime endProgramLocalDateTime = localDateTime.plusWeeks(Long.parseLong(helperClass.getDuration()));

                            String rrule = "";
                            String freq = "";
                            StringBuilder exerciseDays;

                            if (helperClass.getDurationSpan().equals("Days")) {
                                freq = "FREQ=DAILY";
                            } else if (helperClass.getDurationSpan().equals("Weeks")) {
                                freq = "FREQ=WEEKLY";
                            }

                            rrule = freq + ";COUNT=" + helperClass.getDuration();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String startDateStr = localDateTime.format(dateTimeFormatter);
                            String endDateStr = endDateTime.format(dateTimeFormatter);

                            String endProgramDate = endProgramLocalDateTime.format(dateTimeFormatter);
                            endProgramDate = endProgramDate.substring(0, endProgramDate.indexOf(" "));

                            String[] startDateArr = startDateStr.split(" ");
                            String[] endDateArr = endDateStr.split(" ");

                            long startDate = sdf.parse(startDateStr).getTime();
                            long endDate = sdf.parse(endDateStr).getTime();

                            Long eventId = eventsController.addEvent(calendarId,
                                    helperClass.getCourseName(), startDate, endDate, " ",
                                    rrule, helperClass.getDescription(), "");

                            String durationSpan = helperClass.getDuration() + " " + helperClass.getDurationSpan();
                            String timeSpan = helperClass.getTime() + " " + helperClass.getTimeSpan();

                            //Sets exercise days
                            DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("EEE");
                            exerciseDays = new StringBuilder(localDateTime.format(dateTimeFormatter1));

                            int endDayInt = Integer.parseInt(helperClass.getEndDay());
                            for (int i = 1; i < endDayInt; i++) {
                                LocalDateTime exerciseDayDateTime = localDateTime.plusDays(i);
                                exerciseDays.append(" ").append(exerciseDayDateTime.format(dateTimeFormatter1));
                            }

                            Toast.makeText(getContext(), exerciseDays.toString(), Toast.LENGTH_SHORT).show();

                            //inserts program data into sql db
                            long insert = eventsDatabase.insertProgramEvent(eventId, calendarId,
                                    helperClass.getCourseName(), startDateArr[0], endDateArr[0],
                                    startDateArr[1], endDateArr[1], freq, helperClass.getDuration(),
                                    helperClass.getDescription(), durationSpan, timeSpan, helperClass.getSkillLevel(),
                                    helperClass.getGoal(), helperClass.getType(), 1, endProgramDate,
                                    day01, day02, day03, day04, day05, day06, day07, exerciseDays.toString(),
                                    Integer.parseInt(helperClass.getEndDay()));

                            if (insert != -1) {
                                Toast.makeText(getContext(), "Program saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "An error occurred while saving please try again.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (ParseException e) {
                            Toast.makeText(getContext(), "Hmm. It seems we made a mistake.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            Toast.makeText(getContext(), "Error: " + e.getCause() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ErrorCatching errorCatching = new ErrorCatching(getContext());
                    errorCatching.onError(error);

                }
            });
        }
    };

}