package com.aftershade.workmode.users.workout_posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Adapters.PostImagesRecyclerAdapter;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.Models.PostsHelperClass;
import com.aftershade.workmode.HelperClasses.ViewHolders.AllPostsViewHolder;
import com.aftershade.workmode.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PostsFragment extends Fragment {

    String trainerId;
    RecyclerView postsRecycler;
    DatabaseReference postsRef, trainerRef;
    String todayDate;
    FirebaseRecyclerAdapter<PostsHelperClass, AllPostsViewHolder> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            trainerId = getArguments().getString("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        trainerRef = FirebaseDatabase.getInstance().getReference().child("Trainers").child(trainerId);
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        todayDate = currentDate.format(calendar.getTime());

        postsRecycler = view.findViewById(R.id.frag_posts_recycler_);

        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        loadPosts();

        return view;
    }

    private void loadPosts() {

        FirebaseRecyclerOptions<PostsHelperClass> options = new
                FirebaseRecyclerOptions.Builder<PostsHelperClass>()
                .setQuery(postsRef.child(trainerId), PostsHelperClass.class).build();

        adapter = new FirebaseRecyclerAdapter<PostsHelperClass, AllPostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllPostsViewHolder holder, int i,
                                            @NonNull PostsHelperClass postsHelperClass) {

                holder.caption.setText(postsHelperClass.getCaption());

                trainerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Instructor instructor = snapshot.getValue(Instructor.class);

                            holder.userNameTop.setText(instructor.getFullName());
                            holder.userNameBottom.setText(instructor.getFullName());

                            holder.trainerType.setText(instructor.getTrainerType());

                            if (snapshot.hasChild("Images")) {
                                Picasso.get()
                                        .load(snapshot.child("Images").child("profilePhoto").getValue().toString())
                                        .placeholder(R.drawable.image_placeholder_icon)
                                        .error(R.drawable.image_error_icon)
                                        .into(holder.profilePic);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (postsHelperClass.getDateCreated().equals(todayDate)) {
                    holder.postDate.setText(postsHelperClass.getTimeCreated());
                } else {
                    holder.postDate.setText(postsHelperClass.getDateCreated());
                }

                holder.ratingBar.setRating(0f);

                postsRef.child(trainerId).child(postsHelperClass.getPostId())
                        .child("Images").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChildren()) {
                            holder.imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.HORIZONTAL, false));

                            ArrayList<String> urlStrings = new ArrayList<>();
                            urlStrings.clear();

                            for (DataSnapshot snap: snapshot.getChildren()) {
                                urlStrings.add(snap.getValue().toString());
                            }

                            holder.imageRecyclerView.setAdapter(new PostImagesRecyclerAdapter(urlStrings));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public AllPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.posts_layout_card, parent, false);

                return new AllPostsViewHolder(view);
            }
        };

        postsRecycler.setAdapter(adapter);

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