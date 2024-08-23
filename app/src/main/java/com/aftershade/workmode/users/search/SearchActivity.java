package com.aftershade.workmode.users.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.SearchProgramsAdapter;
import com.aftershade.workmode.HelperClasses.Adapters.SearchTrainersAdapter;
import com.aftershade.workmode.HelperClasses.Listeners.ProgramClickListener;
import com.aftershade.workmode.HelperClasses.Listeners.TrainersClickListener;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.TrainerInfoActivity;
import com.aftershade.workmode.users.Workouts_Posts_Activity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ImageButton backBtn, sortButton;
    SearchView searchView;
    RecyclerView searchProgramRecycler;
    SearchProgramsAdapter searchProgramsAdapter;
    SearchTrainersAdapter searchTrainersAdapter;
    DatabaseReference allProgramsRef, trainerRef;
    ErrorCatching errorCatching;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        allProgramsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");
        trainerRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
        errorCatching = new ErrorCatching(SearchActivity.this);

        backBtn = findViewById(R.id.back_btn_search_programs);
        searchView = findViewById(R.id.search_search_view);
        sortButton = findViewById(R.id.search_sort_filter);
        searchProgramRecycler = findViewById(R.id.search_programs_recycler);
        tabLayout = findViewById(R.id.search_activity_tab_lay);

        searchProgramRecycler.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false));
        setUpAdapter();
        setUpTrainersAdapter();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String text = (String) tab.getText();

                if (text.equals("Workouts")) {


                    searchProgramRecycler.setAdapter(searchProgramsAdapter);

                    searchProgramsAdapter.getFilter().filter(searchView.getQuery());

                } else if (text.equals("Trainers")) {

                    searchProgramRecycler.setAdapter(searchTrainersAdapter);

                    searchTrainersAdapter.getFilter().filter(searchView.getQuery());

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                submitQuery(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                submitQuery(newText);

                return false;
            }
        });

        backBtn.setOnClickListener(v -> onBackPressed());

    }

    /**
     * @param query receives query parameter from search and submits it to required adapter via getFilter
     */
    private void submitQuery(String query) {

        int selectTab = tabLayout.getSelectedTabPosition();

        if (!checkAdapter()) {
            if (selectTab == 0) {
                searchProgramRecycler.setAdapter(searchProgramsAdapter);
            } else {
                searchProgramRecycler.setAdapter(searchTrainersAdapter);
            }

        }

        if (selectTab == 0) {
            searchProgramsAdapter.getFilter().filter(query);
        } else {
            searchTrainersAdapter.getFilter().filter(query);
        }

    }

    //check if recycler view has an adapter set
    private boolean checkAdapter() {

        return searchProgramRecycler.getAdapter() != null;

    }

    //gets trainers data from database and sets up adapter using data
    private void setUpTrainersAdapter() {

        ArrayList<Instructor> instructorsArray = new ArrayList<>();

        trainerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String key = snapshot1.getKey();

                        Instructor instructor = snapshot.child(key).getValue(Instructor.class);
                        instructorsArray.add(instructor);
                    }

                    TrainersClickListener trainersClickListener = new TrainersClickListener() {
                        @Override
                        public void onClick(Instructor instructor) {
                            Intent intent = new Intent(SearchActivity.this, TrainerInfoActivity.class);
                            intent.putExtra("id", instructor.getUid());
                            startActivity(intent);
                        }
                    };

                    searchTrainersAdapter = new SearchTrainersAdapter(SearchActivity.this,
                            instructorsArray, trainersClickListener);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorCatching errorCatching = new ErrorCatching(SearchActivity.this);
                errorCatching.onError(error);
            }
        });

    }

    //gets workout data from database and sets up adapter using data
    private void setUpAdapter() {

        ArrayList<ProgramsHelperClass> programsHelperClasses = new ArrayList<>();

        allProgramsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.hasChildren()) {
                    //programsHelperClasses.clear();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String key = snapshot1.getKey();

                        ProgramsHelperClass helperClass = snapshot.child(key).getValue(ProgramsHelperClass.class);
                        programsHelperClasses.add(helperClass);

                    }

                    ProgramClickListener click_listerner = new ProgramClickListener() {
                        @Override
                        public void onClick(ProgramsHelperClass programsHelperClass) {
                            String key = programsHelperClass.getCourseName() +
                                    programsHelperClass.getTrainerId();

                            Intent intent = new Intent(SearchActivity.this, Workouts_Posts_Activity.class);
                            intent.putExtra("whereTo", "program");
                            intent.putExtra("id", programsHelperClass.getTrainerId());
                            intent.putExtra("courseName", key);
                            startActivity(intent);
                        }
                    };

                    searchProgramsAdapter = new SearchProgramsAdapter(SearchActivity.this,
                            programsHelperClasses, click_listerner);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCatching.onError(error);
            }
        });

    }

    /*private void showSortPopup(View v) {

        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.sort_programs_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                if (checkAdapter()) {
                    //sort using date time
                    if (id == R.id.sort_program_recent) {
                        searchProgramsAdapter.sortData("recent");
                    }
                    //sort by program name
                    else if (id == R.id.sort_program_a_to_z) {
                        searchProgramsAdapter.sortData("a to z");
                    } else if (id == R.id.sort_program_z_to_a) {
                        searchProgramsAdapter.sortData("z to a");
                    }
                    //sort by skill level
                    else if (id == R.id.filter_program_beginner) {
                        searchProgramsAdapter.getFilter().filter("beginner");
                    } else if (id == R.id.filter_program_intermediate) {
                        searchProgramsAdapter.getFilter().filter("intermediate");
                    } else if (id == R.id.filter_program_advanced) {
                        searchProgramsAdapter.getFilter().filter("advanced");
                    }
                    //filter with weeks
                    else if (id == R.id.filter_program_1_week) {
                        searchProgramsAdapter.filterProgramLength("Less than 1 week");
                    } else if (id == R.id.filter_program_1_to_2_week) {
                        searchProgramsAdapter.filterProgramLength("1 to 2 weeks");
                    } else if (id == R.id.filter_program_2_to_4_week) {
                        searchProgramsAdapter.filterProgramLength("2 to 4 weeks");
                    } else if (id == R.id.filter_program_4_week) {
                        searchProgramsAdapter.filterProgramLength("More than 4 weeks");
                    }
                }


                return false;
            }
        });

        popupMenu.show();

    }*/

}