package com.aftershade.workmode.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.Models.PostsHelperClass;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.HelperClasses.ViewHolders.PostsViewHolder;
import com.aftershade.workmode.HelperClasses.ViewHolders.ProgramsViewHolders;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.chat.ChatScreenActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainerInfoActivity extends AppCompatActivity {

    ImageView backBtn;
    CircleImageView profilePhoto;
    ImageView coverPhoto;
    private TextView trainerName, trainerType, trainerBio, trainerPhone, trainerEmail, allPosts;
    private RatingBar trainerRating;
    private FloatingActionButton chatsFb;
    RecyclerView postsRecycler, programRecycler;

    String trainerId, name;
    FirebaseRecyclerAdapter<String, ProgramsViewHolders> programAdapter;
    FirebaseRecyclerAdapter<PostsHelperClass, PostsViewHolder> postsAdapter;
    DatabaseReference ref, postsRef, programsRef;
    ErrorCatching errorCatching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_info);

        trainerId = getIntent().getStringExtra("id");

        ref = FirebaseDatabase.getInstance().getReference().child("Trainers");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        programsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");

        errorCatching = new ErrorCatching(TrainerInfoActivity.this);

        backBtn = findViewById(R.id.trainer_info_back_btn);
        trainerName = findViewById(R.id.trainer_name_info);
        trainerType = findViewById(R.id.trainer_type_info);
        trainerBio = findViewById(R.id.trainer_description_info);
        trainerPhone = findViewById(R.id.trainer_phone_contact_info);
        trainerEmail = findViewById(R.id.trainer_email_contact_info);
        trainerRating = findViewById(R.id.trainer_rating_info);
        chatsFb = findViewById(R.id.chats_fb_trainer_info);
        allPosts = findViewById(R.id.trainer_info_see_more_posts);

        profilePhoto = findViewById(R.id.trainer_info_trainer_profile_image);
        coverPhoto = findViewById(R.id.trainer_info_trainer_cover_photo);

        programRecycler = findViewById(R.id.trainer_info_trainer_programs_recycler);
        postsRecycler = findViewById(R.id.trainer_info_trainer_posts_recycler);

        programRecycler.setLayoutManager(
                new LinearLayoutManager(TrainerInfoActivity.this, RecyclerView.HORIZONTAL, false));

        postsRecycler.setLayoutManager(
                new LinearLayoutManager(TrainerInfoActivity.this, RecyclerView.HORIZONTAL, false));

        allPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainerInfoActivity.this, Workouts_Posts_Activity.class);
                intent.putExtra("id", trainerId);
                intent.putExtra("whereTo", "post");
                startActivity(intent);
            }
        });

        loadTrainerInfo();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainerInfoActivity.super.onBackPressed();
            }
        });

        chatsFb.setOnClickListener(v -> {
            Intent intent = new Intent(TrainerInfoActivity.this, ChatScreenActivity.class);
            intent.putExtra("id", trainerId);
            intent.putExtra("name", name);
            startActivity(intent);
        });

    }

    private void loadTrainerInfo() {

        if (trainerId != null) {

            ErrorCatching catching = new ErrorCatching(getApplicationContext());

            ref.child(trainerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        Instructor instructor = snapshot.getValue(Instructor.class);

                        if (snapshot.hasChild("Images")) {
                            Picasso.get()
                                    .load(snapshot.child("Images").child("profilePhoto").getValue().toString())
                                    .placeholder(R.drawable.image_placeholder_icon)
                                    .resize(1024, 720)
                                    .error(R.drawable.image_error_icon)
                                    .into(profilePhoto);

                            Picasso.get()
                                    .load(snapshot.child("Images").child("coverPhoto").getValue().toString())
                                    .placeholder(R.drawable.image_placeholder_icon)
                                    .resize(1024, 720)
                                    .error(R.drawable.image_error_icon)
                                    .into(coverPhoto);
                        }

                        name = instructor.getFullName();

                        trainerName.setText(instructor.getFullName());
                        trainerType.setText(instructor.getTrainerType());
                        trainerBio.setText(instructor.getBio());
                        trainerPhone.setText(instructor.getPhoneNumber());
                        trainerEmail.setText(instructor.getEmail());
                        trainerRating.setRating(0f);

                    } else {

                        Toast.makeText(TrainerInfoActivity.this, "It seems an error occurred while loading the information. \n" +
                                "Please try again.", Toast.LENGTH_SHORT).show();
                        TrainerInfoActivity.super.onBackPressed();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    catching.onError(error);
                }
            });

            loadWorkoutPrograms();
            loadPosts();

        } else {
            super.onBackPressed();
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadWorkoutPrograms() {

        FirebaseRecyclerOptions<String> options = new
                FirebaseRecyclerOptions.Builder<String>()
                .setQuery(ref.child(trainerId).child("programs"), new SnapshotParser<String>() {
                    @NonNull
                    @Override
                    public String parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot.getKey();
                    }
                }).build();

        programAdapter = new FirebaseRecyclerAdapter<String, ProgramsViewHolders>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProgramsViewHolders holder, int i,
                                            @NonNull String key) {

                programsRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            ProgramsHelperClass programsHelperClass = snapshot.getValue(ProgramsHelperClass.class);

                            holder.programName.setText(programsHelperClass.getCourseName());
                            holder.ratingBar.setText(String.valueOf(programsHelperClass.getRating()));
                            holder.skillLevel.setText(programsHelperClass.getSkillLevel());

                            programsRef.child(key).child("Images").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists() && snapshot.hasChildren()) {

                                        for (DataSnapshot snap : snapshot.getChildren()) {

                                            String key = snap.getKey();
                                            Picasso.get()
                                                    .load(snap.getValue().toString())
                                                    .placeholder(R.drawable.image_placeholder_icon)
                                                    .resize(1024, 720)
                                                    .error(R.drawable.image_error_icon)
                                                    .into(holder.imageView);

                                            break;

                                        }
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(TrainerInfoActivity.this, Workouts_Posts_Activity.class);
                                            intent.putExtra("whereTo", "program");
                                            intent.putExtra("id", trainerId);
                                            intent.putExtra("courseName", getRef(i).getKey());
                                            startActivity(intent);
                                        }
                                    });

                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    errorCatching.onError(error);
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorCatching.onError(error);
                    }
                });


            }

            @NonNull
            @Override
            public ProgramsViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.workout_programs_card, parent, false);

                return new ProgramsViewHolders(view);
            }
        };

        programRecycler.setAdapter(programAdapter);

    }

    private void loadPosts() {
        FirebaseRecyclerOptions<PostsHelperClass> options = new
                FirebaseRecyclerOptions.Builder<PostsHelperClass>()
                .setQuery(postsRef.child(trainerId), PostsHelperClass.class).build();

        postsAdapter = new FirebaseRecyclerAdapter<PostsHelperClass, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i,
                                            @NonNull PostsHelperClass postsHelperClass) {

                postsRef.child(trainerId)
                        .child(postsHelperClass.getPostId())
                        .child("Images").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists() && snapshot.hasChildren()) {

                            for (DataSnapshot snap : snapshot.getChildren()) {

                                Picasso.get()
                                        .load(snap.getValue().toString())
                                        .placeholder(R.drawable.image_placeholder_icon)
                                        .resize(1024, 720)
                                        .error(R.drawable.image_error_icon)
                                        .into(postsViewHolder.imageView);

                                break;

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorCatching.onError(error);
                    }
                });

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.posts_card, parent, false);
                return new PostsViewHolder(view);
            }
        };

        postsRecycler.setAdapter(postsAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        postsAdapter.startListening();
        programAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        postsAdapter.stopListening();
        programAdapter.stopListening();
    }
}