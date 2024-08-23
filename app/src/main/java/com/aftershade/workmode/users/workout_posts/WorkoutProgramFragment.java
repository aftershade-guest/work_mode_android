package com.aftershade.workmode.users.workout_posts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Adapters.PostImagesRecyclerAdapter;
import com.aftershade.workmode.HelperClasses.Adapters.TabAccessorAdapter;
import com.aftershade.workmode.HelperClasses.Models.ProgramReviewModel;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.BottomNav;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingfisher.easyviewindicator.AnyViewIndicator;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutProgramFragment extends Fragment {

    RecyclerView imagesRecycler;
    ToggleButton favoritesToggleBtn;
    TextView programName, skillLevel, durationSpan, repetitionDays, timeTextV,
            descriptionTextV, goalTextV, overallRating, reviewBy, dateReviewed, reviewDesc,
            noReviewsText, seeMoreReviews, overallRatingText;
    RatingBar topOverallRating, bottomOverallRating, userRating;
    TabLayout tabLayout;
    ViewPager viewPager;
    RelativeLayout overallRatingLayout, reviewLayout;
    Button addReviewBtn, category1, category2, category3;

    View view;
    String courseName, trainerId;
    DatabaseReference programsRef, programReviewRef, favoritesRef;
    ErrorCatching errorCatching;
    String reviewId, currentUserId;
    AnyViewIndicator viewIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseName = getArguments().getString("courseName");
        trainerId = getArguments().getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_workout_program, container, false);


        reviewId = courseName + trainerId;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        programsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs")
                .child(courseName);
        favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites")
                .child(currentUserId);

        programReviewRef = FirebaseDatabase.getInstance().getReference().child("Program_Reviews")
                .child(reviewId);

        errorCatching = new ErrorCatching(getContext());

        initFields();

        TabAccessorAdapter accessorAdapter = new TabAccessorAdapter(getChildFragmentManager(), courseName, trainerId);

        viewPager.setAdapter(accessorAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0, true);
        tabLayout.setupWithViewPager(viewPager);

        favoritesToggleBtn.setChecked(false);
        favoritesToggleBtn.setBackgroundResource(R.drawable.favorite_border_icon);

        loadProgramInfo();

        loadReviewInfo();

        checkFavorites();

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("courseName", courseName);
                bundle.putString("id", trainerId);

                ProgramReviewFragment reviewFragment = new ProgramReviewFragment();
                reviewFragment.setArguments(bundle);
                reviewFragment.show(getParentFragmentManager(), "");
            }
        });

        seeMoreReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("courseName", courseName);
                bundle.putString("id", trainerId);

                AllReviewsFragment reviewsFragment = new AllReviewsFragment();
                reviewsFragment.setArguments(bundle);
                reviewsFragment.show(getParentFragmentManager(), "");

            }
        });

        favoritesToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favoritesToggleBtn.isChecked()) {
                    HashMap<String, Object> map = new HashMap<>();

                    map.put("trainerId", trainerId);
                    map.put("courseName", courseName);
                    map.put("userId", currentUserId);

                    FirebaseDatabase.getInstance().getReference().child("Program_Notifications")
                            .child(reviewId).child(currentUserId).updateChildren(map);

                    favoritesRef.child(reviewId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                favoritesToggleBtn.setChecked(true);
                                favoritesToggleBtn.setBackgroundResource(R.drawable.favorite_icon);
                                Toast.makeText(getContext(), "Program add to favorites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Program_Notifications")
                            .child(reviewId).child(currentUserId).removeValue();
                    favoritesRef.child(reviewId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                favoritesToggleBtn.setChecked(false);
                                favoritesToggleBtn.setBackgroundResource(R.drawable.favorite_border_icon);
                            } else {
                                errorCatching.onException(task.getException());
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    //initialises fields
    private void initFields() {
        imagesRecycler = view.findViewById(R.id.image_recycler_program_info);

        programName = view.findViewById(R.id.course_name_program_info);
        skillLevel = view.findViewById(R.id.skill_level_text_view);
        durationSpan = view.findViewById(R.id.duration_span_text_program_info);
        repetitionDays = view.findViewById(R.id.repetition_text_program_info);
        timeTextV = view.findViewById(R.id.time_text_program_info);
        descriptionTextV = view.findViewById(R.id.description_edit_program_info);
        goalTextV = view.findViewById(R.id.goal_edit_program_info);
        overallRating = view.findViewById(R.id.overall_rating_program_info);
        reviewBy = view.findViewById(R.id.review_by_txt_program_info);
        dateReviewed = view.findViewById(R.id.date_reviewed_program_info);
        reviewDesc = view.findViewById(R.id.reviewed_desc_program_info);
        noReviewsText = view.findViewById(R.id.no_reviews_text_program_info);
        seeMoreReviews = view.findViewById(R.id.see_more_reviews_program_info);
        favoritesToggleBtn = view.findViewById(R.id.favourites_btn_program_info);

        viewIndicator = view.findViewById(R.id.indicator_info_program_info);

        topOverallRating = view.findViewById(R.id.top_overall_rating_program_info);
        bottomOverallRating = view.findViewById(R.id.overall_rating_bar_2nd);
        userRating = view.findViewById(R.id.user_rating_program_info);

        category1 = view.findViewById(R.id.workout_category_one_program_info);
        category2 = view.findViewById(R.id.workout_category_two_program_info);
        category3 = view.findViewById(R.id.workout_category_three_program_info);

        tabLayout = view.findViewById(R.id.tab_layout_program_info);
        viewPager = view.findViewById(R.id.view_pager_program_info);

        overallRatingLayout = view.findViewById(R.id.overall_rating_layout_program_info);
        reviewLayout = view.findViewById(R.id.reviews_layout_program_info);
        overallRatingText = view.findViewById(R.id.overall_rating_bar_text_program_info);

        addReviewBtn = view.findViewById(R.id.add_a_review_btn_program_info);

        imagesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

    }

    //Gets program information from the database and loads in the relevant fields
    private void loadProgramInfo() {

        programsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    if (snapshot.hasChild("Images")) {

                        ArrayList<String> urlStrings = new ArrayList<>();

                        for (DataSnapshot snap : snapshot.child("Images").getChildren()) {
                            urlStrings.add(snap.getValue().toString());
                        }

                        viewIndicator.setItemCount(urlStrings.size());
                        viewIndicator.setCurrentPosition(0);

                        imagesRecycler.setAdapter(new PostImagesRecyclerAdapter(urlStrings));

                        imagesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                switch (newState) {
                                    case RecyclerView.SCROLL_STATE_DRAGGING:
                                        int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                                        viewIndicator.setCurrentPosition(pos);
                                    case RecyclerView.SCROLL_STATE_IDLE:
                                        int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                                        viewIndicator.setCurrentPosition(position);
                                }

                            }
                        });

                    }

                    ProgramsHelperClass programsHelperClass = snapshot.getValue(ProgramsHelperClass.class);

                    programName.setText(programsHelperClass.getCourseName());
                    skillLevel.setText(programsHelperClass.getSkillLevel());
                    topOverallRating.setRating((float) programsHelperClass.getRating());
                    overallRatingText.setText(String.valueOf(programsHelperClass.getRating()));
                    bottomOverallRating.setRating((float) programsHelperClass.getRating());
                    overallRating.setText(String.valueOf(programsHelperClass.getRating()));
                    durationSpan.setText(String.format("%s %s", programsHelperClass.getDuration(), programsHelperClass.getDurationSpan()));
                    repetitionDays.setText(String.format("%s to %s days", programsHelperClass.getStartDay(), programsHelperClass.getEndDay()));
                    timeTextV.setText(String.format("%s %s", programsHelperClass.getTime(), programsHelperClass.getTimeSpan()));
                    descriptionTextV.setText(programsHelperClass.getDescription());
                    goalTextV.setText(programsHelperClass.getGoal());
                    category1.setText(programsHelperClass.getType());

                } else {
                    Toast.makeText(getContext(), "Oops. It seems we made an error while loading program info, please try again.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), BottomNav.class));
                    getActivity().finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCatching.onError(error);
                startActivity(new Intent(getContext(), BottomNav.class));
            }
        });

    }

    //Gets one latest review information of the program from the database
    private void loadReviewInfo() {

        programReviewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    overallRatingLayout.setVisibility(View.VISIBLE);
                    reviewLayout.setVisibility(View.VISIBLE);
                    noReviewsText.setVisibility(View.GONE);
                    seeMoreReviews.setVisibility(View.VISIBLE);
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String key = snap.getKey();
                        ProgramReviewModel reviewModel = snapshot.child(key).getValue(ProgramReviewModel.class);

                        dateReviewed.setText(reviewModel.getDate());
                        reviewDesc.setText(reviewModel.getReviewDesc());
                        reviewDesc.setText(reviewModel.getReviewDesc());
                        userRating.setRating(Float.parseFloat(reviewModel.getRating()));
                        reviewBy.setText(reviewModel.getUserFullName());

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

    //Checks user favourites and toggles the button either on or off
    private void checkFavorites() {

        favoritesRef.child(reviewId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.hasChildren()) {
                    favoritesToggleBtn.setChecked(true);
                    favoritesToggleBtn.setBackgroundResource(R.drawable.favorite_icon);
                } else {
                    favoritesToggleBtn.setChecked(false);
                    favoritesToggleBtn.setBackgroundResource(R.drawable.favorite_border_icon);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCatching.onError(error);
            }
        });

    }
}