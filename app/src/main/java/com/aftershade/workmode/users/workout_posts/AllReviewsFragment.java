package com.aftershade.workmode.users.workout_posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Models.ProgramReviewModel;
import com.aftershade.workmode.HelperClasses.ViewHolders.ReviewsHolder;
import com.aftershade.workmode.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllReviewsFragment extends BottomSheetDialogFragment {

    TextView titleText;
    RecyclerView reviewsRecycler;
    String trainerId, courseName;
    DatabaseReference reviewRef;
    FirebaseRecyclerAdapter<ProgramReviewModel, ReviewsHolder> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            trainerId = getArguments().getString("id");
            courseName = getArguments().getString("courseName");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_reviews, container, false);

        String reviewId = courseName + trainerId;
        reviewRef = FirebaseDatabase.getInstance().getReference().child("Program_Reviews")
                .child(reviewId);

        titleText = view.findViewById(R.id.title_all_reviews);
        reviewsRecycler = view.findViewById(R.id.reviews_recycler_all_reviews);

        titleText.setText(String.format("%s Reviews", courseName));

        reviewsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        loadReviews();

        return view;
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

    private void loadReviews() {

        FirebaseRecyclerOptions<ProgramReviewModel> options = new
                FirebaseRecyclerOptions.Builder<ProgramReviewModel>()
                .setQuery(reviewRef, ProgramReviewModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ProgramReviewModel, ReviewsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewsHolder reviewsHolder, int i,
                                            @NonNull ProgramReviewModel programReviewModel) {

                reviewsHolder.userName.setText(programReviewModel.getUserFullName());
                reviewsHolder.ratingBar.setRating(Float.parseFloat(programReviewModel.getRating()));
                reviewsHolder.dateText.setText(programReviewModel.getDate());
                reviewsHolder.descText.setText(programReviewModel.getReviewDesc());

            }

            @NonNull
            @Override
            public ReviewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reviews_card, parent, false);
                return new ReviewsHolder(view);
            }
        };

        reviewsRecycler.setAdapter(adapter);

    }
}