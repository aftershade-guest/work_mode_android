package com.aftershade.workmode.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.AllProgramsAdapter;
import com.aftershade.workmode.HelperClasses.Adapters.FeaturedTrainersAdapter;
import com.aftershade.workmode.HelperClasses.Listeners.ProgramClickListener;
import com.aftershade.workmode.HelperClasses.Listeners.TrainersClickListener;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.search.SearchActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import java.util.ArrayList;

public class AllProgramsActivity extends AppCompatActivity {

    ImageButton backBtn, searchBtn;
    RecyclerView allProgramsRecycler, featuredTrainers;
    DatabaseReference allProgramsRef;
    SingleSelectToggleGroup toggleGroup;
    ErrorCatching errorCatching;
    AllProgramsAdapter allProgramsAdapter;
    FeaturedTrainersAdapter featuredTrainersAdapter;
    private DatabaseReference trainerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_programs);

        allProgramsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");
        trainerRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
        errorCatching = new ErrorCatching(AllProgramsActivity.this);

        backBtn = findViewById(R.id.back_btn_all_programs);
        allProgramsRecycler = findViewById(R.id.all_programs_recycler);
        searchBtn = findViewById(R.id.all_programs_search_btn);
        featuredTrainers = findViewById(R.id.all_programs_featured_trainers_recycler);
        toggleGroup = findViewById(R.id.categories_toggle_group_all_programs);

        featuredTrainers.setLayoutManager(new
                LinearLayoutManager(AllProgramsActivity.this, RecyclerView.HORIZONTAL, false));

        allProgramsRecycler.setLayoutManager(new
                LinearLayoutManager(AllProgramsActivity.this, RecyclerView.VERTICAL, false));

        setFeaturedAdapter();

        setUpAdapter();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllProgramsActivity.this, SearchActivity.class));
            }
        });

    }

    /**
     * loads featured content from database into arrayList and sets featured content adapter
     */
    private void setFeaturedAdapter() {

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
                            Intent intent = new Intent(getApplicationContext(), TrainerInfoActivity.class);
                            intent.putExtra("id", instructor.getUid());
                            startActivity(intent);
                        }
                    };

                    featuredTrainersAdapter = new FeaturedTrainersAdapter(AllProgramsActivity.this,
                            instructorsArray, trainersClickListener);

                    featuredTrainers.setAdapter(featuredTrainersAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorCatching errorCatching = new ErrorCatching(AllProgramsActivity.this);
                errorCatching.onError(error);
            }
        });

    }

    /**
     * loads all workout programs from database into arrayList and then sets workouts adapter
     */
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

                            Intent intent = new Intent(AllProgramsActivity.this, Workouts_Posts_Activity.class);
                            intent.putExtra("whereTo", "program");
                            intent.putExtra("id", programsHelperClass.getTrainerId());
                            intent.putExtra("courseName", key);
                            startActivity(intent);
                        }
                    };

                    allProgramsAdapter = new AllProgramsAdapter(
                            AllProgramsActivity.this, programsHelperClasses, click_listerner);

                    allProgramsRecycler.setAdapter(allProgramsAdapter);

                    toggleGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                            if (checkedId == R.id.all_programs_all_toggle_btn) {
                                allProgramsAdapter.filter("all");
                            } else if (checkedId == R.id.all_programs_aerobic_toggle_btn) {
                                allProgramsAdapter.filter("aerobic");
                            } else if (checkedId == R.id.all_programs_anaerobic_toggle_btn) {
                                allProgramsAdapter.filter("anaerobic");
                            } else if (checkedId == R.id.all_programs_calisthenics_toggle_btn) {
                                allProgramsAdapter.filter("calisthenics");
                            } else if (checkedId == R.id.all_programs_circuit_toggle_btn) {
                                allProgramsAdapter.filter("circuit");
                            } else if (checkedId == R.id.all_programs_exercises_toggle_btn) {
                                allProgramsAdapter.filter("exercise");
                            } else if (checkedId == R.id.all_programs_functional_toggle_btn) {
                                allProgramsAdapter.filter("functional");
                            } else if (checkedId == R.id.all_programs_strength_toggle_btn) {
                                allProgramsAdapter.filter("strength");
                            } else if (checkedId == R.id.all_programs_yoga_toggle_btn) {
                                allProgramsAdapter.filter("yoga");
                            }
                        }
                    });

                    if (getIntent().getStringExtra("category") != null) {
                        setCheckedButton(getIntent().getStringExtra("category"));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCatching.onError(error);
            }
        });

    }

    /**
     * @param query sets checked toggle button based on parameter received
     */
    private void setCheckedButton(String query) {

        String[] categories = getResources().getStringArray(R.array.wokout_categories);

        if (categories[0].equals(query)) {
            toggleGroup.check(R.id.all_programs_aerobic_toggle_btn);
        } else if (categories[1].equals(query)) {
            toggleGroup.check(R.id.all_programs_anaerobic_toggle_btn);
        } else if (categories[2].equals(query)) {
            toggleGroup.check(R.id.all_programs_calisthenics_toggle_btn);
        } else if (categories[3].equals(query)) {
            toggleGroup.check(R.id.all_programs_circuit_toggle_btn);
        } else if (categories[4].equals(query)) {
            toggleGroup.check(R.id.all_programs_strength_toggle_btn);
        } else if (categories[5].equals(query)) {
            toggleGroup.check(R.id.all_programs_yoga_toggle_btn);
        }

    }
}