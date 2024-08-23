package com.aftershade.workmode.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.AllTrainersAdapter;
import com.aftershade.workmode.HelperClasses.Adapters.RecommendedProgramsAdapter;
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

import java.util.ArrayList;

public class AllTrainersActivity extends AppCompatActivity {

    private ImageView backBtn, searchBtn;
    private RecyclerView instructorsRecycler, recommendedRecycler;
    private DatabaseReference trainerRef, allProgramsRef;
    private AllTrainersAdapter trainersAdapter;
    private RecommendedProgramsAdapter recommendedProgramsAdapter;
    private ErrorCatching errorCatching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trainers);

        allProgramsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");
        trainerRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
        errorCatching = new ErrorCatching(AllTrainersActivity.this);

        backBtn = findViewById(R.id.back_btn_all_trainers);
        instructorsRecycler = findViewById(R.id.all_trainers_recycler);
        searchBtn = findViewById(R.id.all_trainer_search_btn);
        recommendedRecycler = findViewById(R.id.recommended_programs_recycler);

        backBtn.setOnClickListener(v -> super.onBackPressed());

        searchBtn.setOnClickListener(v -> {
            startActivity(new Intent(AllTrainersActivity.this, SearchActivity.class));
        });

        recommendedRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        instructorsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        setUpAdapter();

        setUpRecommendedAdapter();

    }

    private void setUpRecommendedAdapter() {

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

                            Intent intent = new Intent(AllTrainersActivity.this, Workouts_Posts_Activity.class);
                            intent.putExtra("whereTo", "program");
                            intent.putExtra("id", programsHelperClass.getTrainerId());
                            intent.putExtra("courseName", key);
                            startActivity(intent);
                        }
                    };

                    recommendedProgramsAdapter = new RecommendedProgramsAdapter(
                            AllTrainersActivity.this, programsHelperClasses, click_listerner);

                    recommendedRecycler.setAdapter(recommendedProgramsAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCatching.onError(error);
            }
        });

    }

    private void setUpAdapter() {

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

                    trainersAdapter = new AllTrainersAdapter(AllTrainersActivity.this,
                            instructorsArray, trainersClickListener);

                    instructorsRecycler.setAdapter(trainersAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCatching.onError(error);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}