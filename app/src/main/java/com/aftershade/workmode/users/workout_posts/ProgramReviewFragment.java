package com.aftershade.workmode.users.workout_posts;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Models.User;
import com.aftershade.workmode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProgramReviewFragment extends BottomSheetDialogFragment {

    RatingBar ratingBar;
    TextInputEditText commentText;
    Button submitBtn;
    String courseName, trainerId, currentUserId;
    DatabaseReference reviewsRef;
    User user;
    ErrorCatching errorCatching;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            courseName = getArguments().getString("courseName");
            trainerId = getArguments().getString("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_program_review, container, false);

        String reviewId = courseName + trainerId;
        errorCatching = new ErrorCatching(getContext());

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reviewsRef = FirebaseDatabase.getInstance().getReference().child("Program_Reviews")
                .child(reviewId);

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(User.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorCatching.onError(error);
                    }
                });

        ratingBar = view.findViewById(R.id.program_review_rating_bar);
        commentText = view.findViewById(R.id.write_review_txt);
        submitBtn = view.findViewById(R.id.program_review_submit_review);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (ratingBar.getRating() == 0) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please set a rating", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                String date = currentDate.format(calendar.getTime());

                String rating = String.valueOf(ratingBar.getRating());
                String comment = commentText.getText().toString();

                HashMap<String, Object> map = new HashMap<>();
                map.put("courseName", courseName);
                map.put("trainerId", trainerId);
                map.put("date", date);
                map.put("rating", rating);
                map.put("reviewDesc", comment);
                map.put("userFullName", user.getFullName());
                map.put("userId", user.getUid());

                reviewsRef.child(currentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Review has been added", Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog.dismiss();
                            errorCatching.onException(task.getException());
                        }
                    }
                });

            }
        });

        return view;
    }
}