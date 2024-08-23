package com.aftershade.workmode.users.workout_posts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Models.FavoritesModel;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.HelperClasses.ViewHolders.AllProgramsHolder;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.Workouts_Posts_Activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FavoritesFragment extends Fragment {

    RecyclerView favoriteRecycler;
    DatabaseReference favRef, programRef;
    String currentUserId;
    FirebaseRecyclerAdapter<FavoritesModel, AllProgramsHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(currentUserId);
        programRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");

        favoriteRecycler = view.findViewById(R.id.favorites_recycler_view);
        favoriteRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        loadContent();

        return view;
    }

    private void loadContent() {

        FirebaseRecyclerOptions<FavoritesModel> options = new
                FirebaseRecyclerOptions.Builder<FavoritesModel>()
                .setQuery(favRef, FavoritesModel.class).build();

        adapter = new FirebaseRecyclerAdapter<FavoritesModel, AllProgramsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllProgramsHolder allProgramsHolder, int i,
                                            @NonNull FavoritesModel favoritesModel) {

                String key = favoritesModel.getCourseName();
                programRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            ProgramsHelperClass helperClass = snapshot.getValue(ProgramsHelperClass.class);

                            allProgramsHolder.courseNameText.setText(helperClass.getCourseName());
                            allProgramsHolder.ratingBar.setRating((float) helperClass.getRating());
                            allProgramsHolder.descText.setText(helperClass.getDescription());
                            allProgramsHolder.skillLevel.setText(helperClass.getSkillLevel());

                            if (snapshot.hasChild("Images")) {
                                for (DataSnapshot snap : snapshot.child("Images").getChildren()) {

                                    Picasso.get().load(snap.getValue().toString())
                                            .placeholder(R.drawable.image_placeholder_icon)
                                            .error(R.drawable.image_error_icon)
                                            .into(allProgramsHolder.imageView);

                                    break;

                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ErrorCatching errorCatching = new ErrorCatching(getContext());
                        errorCatching.onError(error);

                    }
                });

                allProgramsHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), Workouts_Posts_Activity.class);

                        intent.putExtra("id", favoritesModel.getTrainerId());
                        intent.putExtra("courseName", favoritesModel.getCourseName());
                        intent.putExtra("whereTo", "program");

                        startActivity(intent);
                    }
                });

                allProgramsHolder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = favoritesModel.getCourseName() + favoritesModel.getTrainerId();
                        FirebaseDatabase.getInstance().getReference().child("Program_Notifications")
                                .child(id).child(currentUserId).removeValue();
                        favRef.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Removed from watchlist", Toast.LENGTH_SHORT).show();
                                } else {
                                    ErrorCatching errorCatching = new ErrorCatching(getContext());
                                    errorCatching.onException(task.getException());
                                }
                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public AllProgramsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_program_card, parent, false);
                return new AllProgramsHolder(view);
            }
        };

        favoriteRecycler.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}