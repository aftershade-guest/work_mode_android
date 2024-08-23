package com.aftershade.workmode.users.ui.trainers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.CategoriesAdapter;
import com.aftershade.workmode.HelperClasses.Adapters.FeaturedWorkoutsAdapter;
import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.HelperClasses.Listeners.ProgramClickListener;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.HelperClasses.ViewHolders.TrainersViewHolder;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.AllProgramsActivity;
import com.aftershade.workmode.users.AllTrainersActivity;
import com.aftershade.workmode.users.TrainerInfoActivity;
import com.aftershade.workmode.users.Workouts_Posts_Activity;
import com.aftershade.workmode.users.search.SearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TrainersFragment extends Fragment {

    TextView viewAll, viewAllPrograms;
    RecyclerView popularRecyclerView, featuredRecycler, categoriesRecycler;
    DatabaseReference trainerRef, programRef;
    LinearLayout searchLayout;
    FirebaseRecyclerAdapter<Instructor, TrainersViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trainers, container, false);

        popularRecyclerView = view.findViewById(R.id.trainers_recycler_view_popular);
        featuredRecycler = view.findViewById(R.id.trainers_recycler_view_featured);
        viewAll = view.findViewById(R.id.view_all_trainers);
        viewAllPrograms = view.findViewById(R.id.view_all_programs);
        categoriesRecycler = view.findViewById(R.id.trainers_frag_categories_recycler);
        searchLayout = view.findViewById(R.id.trainers_frag_search_view_lay);

        trainerRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
        programRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");

        setUpCategories();
        featuredAddContent();
        popularAddContent();

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        viewAll.setOnClickListener(v -> startActivity(new Intent(getActivity(), AllTrainersActivity.class)));

        viewAllPrograms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AllProgramsActivity.class));
            }
        });

        return view;
    }

    private void setUpCategories() {

        categoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        String[] categories = getResources().getStringArray(R.array.wokout_categories);

        Click_Listerner click_listerner = new Click_Listerner() {
            @Override
            public void onClick(View v, int position, boolean isLongClick) {

                Intent intent = new Intent(getContext(), AllProgramsActivity.class);
                intent.putExtra("category", categories[position]);
                startActivity(intent);

            }
        };

        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(), categories, click_listerner);
        categoriesRecycler.setAdapter(categoriesAdapter);

    }

    private void featuredAddContent() {
        featuredRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        featuredRecyclerAdapter();
    }

    private void featuredRecyclerAdapter() {

        ArrayList<Object> objectArrayList = new ArrayList<>();
        String view = "welcome";
        objectArrayList.add(view);

        programRef.orderByChild("rating").limitToFirst(15).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String key = snapshot1.getKey();

                        ProgramsHelperClass helperClass = snapshot.child(key).getValue(ProgramsHelperClass.class);
                        objectArrayList.add(helperClass);
                    }

                    ProgramClickListener click_listerner = new ProgramClickListener() {
                        @Override
                        public void onClick(ProgramsHelperClass programsHelperClass) {
                            String key = programsHelperClass.getCourseName() +
                                    programsHelperClass.getTrainerId();

                            Intent intent = new Intent(getContext(), Workouts_Posts_Activity.class);
                            intent.putExtra("whereTo", "program");
                            intent.putExtra("id", programsHelperClass.getTrainerId());
                            intent.putExtra("courseName", key);
                            startActivity(intent);
                        }
                    };

                    Click_Listerner click_listerner1 = new Click_Listerner() {
                        @Override
                        public void onClick(View v, int position, boolean isLongClick) {

                            startActivity(new Intent(getContext(), AllProgramsActivity.class));

                        }
                    };

                    FeaturedWorkoutsAdapter workoutsAdapter = new
                            FeaturedWorkoutsAdapter(objectArrayList, getContext(),
                            click_listerner, click_listerner1);
                    featuredRecycler.setAdapter(workoutsAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void popularAddContent() {
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        popularRecyclerAdapter();

    }

    private void popularRecyclerAdapter() {

        FirebaseRecyclerOptions<Instructor> options = new FirebaseRecyclerOptions.Builder<Instructor>()
                .setQuery(trainerRef.orderByChild("fullName").limitToFirst(25), Instructor.class).build();

        adapter = new FirebaseRecyclerAdapter<Instructor, TrainersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TrainersViewHolder trainersViewHolder, int i,
                                            @NonNull Instructor instructor) {

                trainersViewHolder.fullName.setText(instructor.getFullName());
                trainersViewHolder.rating.setRating(2f);
                trainersViewHolder.type.setText(instructor.getTrainerType());

                trainerRef.child(instructor.getUid()).child("Images").child("profilePhoto")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {
                                    Picasso.get()
                                            .load(snapshot.getValue(String.class))
                                            .placeholder(R.drawable.image_placeholder_icon)
                                            .error(R.drawable.image_error_icon)
                                            .into(trainersViewHolder.profilePic);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                ErrorCatching errorCatching = new ErrorCatching(getContext());
                                errorCatching.onError(error);
                            }
                        });

                trainersViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), TrainerInfoActivity.class);
                    intent.putExtra("id", instructor.getUid());
                    startActivity(intent);
                });

            }

            @NonNull
            @Override
            public TrainersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainers_card,
                        parent, false);

                return new TrainersViewHolder(view);
            }
        };

        popularRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        //featuredAdapter.startListening();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //featuredAdapter.stopListening();
        adapter.stopListening();
    }
}