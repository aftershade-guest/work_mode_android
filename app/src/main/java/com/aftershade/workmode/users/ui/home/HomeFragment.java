package com.aftershade.workmode.users.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.HelperClasses.Events.EventsDatabase;
import com.aftershade.workmode.HelperClasses.Listeners.HomeEventsClickListener;
import com.aftershade.workmode.HelperClasses.Listeners.StepsCallback;
import com.aftershade.workmode.HelperClasses.Models.ProgramOfflineModel;
import com.aftershade.workmode.Notifications.GeneralHelper;
import com.aftershade.workmode.R;
import com.aftershade.workmode.Services.StepDetector;
import com.aftershade.workmode.users.ui.home.Adapters.TodayEventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver;
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter;
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.aftershade.workmode.Databases.NetworkCheck.isNetworkConnected;
import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;

public class HomeFragment extends Fragment implements StepsCallback {

    TextView dateToday, stepsNumber, caloriesNumber, distanceCovered, userName;
    CircleImageView profileImage;
    SingleRowCalendar singleRowCalendar;
    ImageButton addEvent;
    RecyclerView todayTaskRecycler;
    Date todayDate;
    public static final int REQUEST_WRITE_CALENDAR = 12;
    public static final int REQUEST_READ_CALENDAR = 13;
    public static boolean hasWriteCalendar = false;
    public static boolean hasReadCalendar = false;
    EventsDatabase eventsDatabase;
    View root;

    private DatabaseReference userRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        dateToday = root.findViewById(R.id.my_activity_today);
        addEvent = root.findViewById(R.id.add_event_home_btn);
        todayTaskRecycler = root.findViewById(R.id.todays_tasks_recycler_view);
        stepsNumber = root.findViewById(R.id.steps_number_home);
        caloriesNumber = root.findViewById(R.id.calories_number_home);
        singleRowCalendar = root.findViewById(R.id.single_row_calendar_home);
        distanceCovered = root.findViewById(R.id.distance_covered_steps_home);
        userName = root.findViewById(R.id.user_name_text_home);
        profileImage = root.findViewById(R.id.profile_image_picture_home);

        getUserData();

        //get today's date, formats and displays it
        todayDate = new Date();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        dateToday.setText(dateFormat.format(todayDate));

        setCheckPreferences();

        //sets up single row calendar
        setUpCalendar();

        //Starts step counter service
        Intent intent = new Intent(getContext(), StepDetector.class);
        requireActivity().startService(intent);
        StepDetector.register(this);

        //instance of events db class
        eventsDatabase = new EventsDatabase(getContext());


        //sets layout manager of tasks recycler view
        todayTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //gets today's task, creates adapter and sets adapter of today task recycler
        getTodaysTasks();

        //sets click listener of add event button
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopUpMenu(v);

            }
        });

        return root;
    }

    private void showPopUpMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.menu_add_home);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.running_session_menu_item) {

                    Toast.makeText(getContext(), "Running session clicked", Toast.LENGTH_SHORT).show();

                }

                return false;
            }
        });

        popupMenu.show();

    }

    private void getUserData() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ErrorCatching catching = new ErrorCatching(getContext());

        SessionManager sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);

        String fullName = sessionManager.getFullname();

        if (fullName.contains(" ")) {
            userName.setText(sessionManager.getFullname().substring(0, sessionManager.getFullname().indexOf(" ")));
        } else {
            userName.setText(fullName);
        }

        userRef.child(uid).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String profilePhotoUrl = snapshot.child("profilePhoto").getValue().toString();

                    Picasso.get().load(profilePhotoUrl)
                            .resize(1024, 720)
                            .placeholder(R.drawable.image_placeholder_icon)
                            .error(R.drawable.image_error_icon)
                            .into(profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                catching.onError(error);
            }
        });

    }

    private void getTodaysTasks() {

        ArrayList<ProgramOfflineModel> eventsModels = eventsDatabase.getProgramTodayEvents();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("taskDates", Context.MODE_PRIVATE);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MMM-dd");

        for (int i = 0; i < eventsModels.size(); i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            String todayWeekDay = sdf.format(todayDate);

            if (!eventsModels.get(i).getExerciseDays().contains(todayWeekDay)) {
                eventsModels.remove(i);
            }
        }

        for (int i = 0; i < eventsModels.size(); i++) {

            if (!sharedPreferences.getString("previousDate", "").equals(sdf1.format(todayDate))) {
                sharedPreferences.edit().putString("previousDate", sdf1.format(todayDate)).apply();

                int maxDays = eventsModels.get(i).getMaxDays();
                int dayCount = eventsModels.get(i).getDayCount();

                if (dayCount < maxDays) {
                    eventsDatabase.updateProgramDayCount(eventsModels.get(i).getEventId(), eventsModels.get(i).getDayCount() + 1);
                } else if (dayCount == maxDays) {
                    eventsDatabase.updateProgramDayCount(eventsModels.get(i).getEventId(), 1);
                }

            }
        }

        HomeEventsClickListener homeEventsClickListener = new HomeEventsClickListener() {
            @Override
            public void onClick(ProgramOfflineModel programOfflineModel) {

                HomeEventsInfoFragment fragment = new HomeEventsInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("event_id", programOfflineModel.getEventId());
                //bundle.put("key", programOfflineModel);
                fragment.setArguments(bundle);
                fragment.show(getParentFragmentManager(), "");


            }
        };


        TodayEventAdapter todayEventAdapter = new TodayEventAdapter(getContext(), eventsModels, homeEventsClickListener);
        todayTaskRecycler.setAdapter(todayEventAdapter);

    }

    private void setCheckPreferences() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("taskDates", Context.MODE_PRIVATE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");

        if (!sharedPreferences.getBoolean("isSavedToday", false)) {
            sharedPreferences.edit().putBoolean("isSavedToday", true).apply();
            sharedPreferences.edit().putString("previousDate", sdf.format(todayDate)).apply();
        }


    }

    /**
     * checks if there is currently internet and displays a toast if there isn't
     */
    public void showToast() {

        if (!isNetworkConnected) {
            Toast.makeText(getContext(), getString(R.string.no_internet_str), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        showToast();
    }

    /**
     * checks read and write permissions of calendar
     */
    private void getWriteCalendarPerm() {

        boolean hasPermissionRead = (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermissionWrite = (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermissionRead) {
            requestReadPerms();
        } else {
            hasReadCalendar = true;
        }

        if (!hasPermissionWrite) {
            requestWritePerms();
        } else hasWriteCalendar = true;

    }

    /**
     * request calendar read permission
     */
    private void requestReadPerms() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("To add calendar events. We need permission.");
        builder.setPositiveButton(R.string.continue_st, (dialog, which) -> {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_READ_CALENDAR);

        });

        builder.setNegativeButton(R.string.not_now_st, (dialog, which) -> {
            hasReadCalendar = false;
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * request calendar write permission
     */
    private void requestWritePerms() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("To add calendar events. We need permission.");
        builder.setPositiveButton(R.string.continue_st, (dialog, which) -> {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_WRITE_CALENDAR);
        });

        builder.setNegativeButton(R.string.not_now_st, (dialog, which) -> {
            hasWriteCalendar = false;
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * sets up single row calendar
     */
    private void setUpCalendar() {

        try {

            singleRowCalendar.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

            CalendarViewManager calendarViewManager = new CalendarViewManager() {
                @Override
                public int setCalendarViewResourceId(int i, @NotNull Date date, boolean b) {

                    if (b) {
                        return R.layout.single_row_calendar_selected;
                    } else {
                        return R.layout.single_row_calendar_deselected;
                    }

                }

                @Override
                public void bindDataToCalendarView(@NotNull SingleRowCalendarAdapter.CalendarViewHolder calendarViewHolder,
                                                   @NotNull Date date, int i, boolean b) {

                    SimpleDateFormat sdf = new SimpleDateFormat("EEE dd");
                    String[] todate = sdf.format(date).split(" ");

                    if (sdf.format(todayDate).equals(sdf.format(date))) {
                        singleRowCalendar.select(i);
                    }

                    if (b) {
                        ((TextView) calendarViewHolder.itemView
                                .findViewById(R.id.single_row_day_selected)).setText(todate[0]);
                        ((TextView) calendarViewHolder.itemView
                                .findViewById(R.id.single_row_date_no_selected)).setText(todate[1]);
                    } else {
                        ((TextView) calendarViewHolder.itemView
                                .findViewById(R.id.single_row_day_deselected)).setText(todate[0]);
                        ((TextView) calendarViewHolder.itemView
                                .findViewById(R.id.single_row_date_no_deselected)).setText(todate[1]);
                    }

                }
            };

            CalendarSelectionManager calendarSelectionManager = new CalendarSelectionManager() {
                @Override
                public boolean canBeItemSelected(int i, @NotNull Date date) {
                    return true;
                }
            };

            CalendarChangesObserver calendarChangesObserver = new CalendarChangesObserver() {
                @Override
                public void whenWeekMonthYearChanged(@NotNull String s, @NotNull String s1,
                                                     @NotNull String s2, @NotNull String s3, @NotNull Date date) {

                }

                @Override
                public void whenSelectionChanged(boolean b, int i, @NotNull Date date) {

                }

                @Override
                public void whenCalendarScrolled(int i, int i1) {

                }

                @Override
                public void whenSelectionRestored() {

                }

                @Override
                public void whenSelectionRefreshed() {

                }
            };

            singleRowCalendar.setCalendarChangesObserver(calendarChangesObserver);
            singleRowCalendar.setCalendarViewManager(calendarViewManager);
            singleRowCalendar.setCalendarSelectionManager(calendarSelectionManager);
            singleRowCalendar.init();
            //singleRowCalendar.setDates(dateList);


        } catch (Exception pe) {
            Toast.makeText(getContext(), "Oh. It seems we made a mistake. Please report the problem if it persists.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_CALENDAR) {
            hasWriteCalendar = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        } else if (requestCode == REQUEST_READ_CALENDAR) {
            hasWriteCalendar = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

    }

    @Override
    public void subscribeSteps(int steps) {
        stepsNumber.setText(steps);
        caloriesNumber.setText(GeneralHelper.getCalories(steps));
        distanceCovered.setText(String.format("%s km", GeneralHelper.getDistanceCovered(steps)));
    }
}