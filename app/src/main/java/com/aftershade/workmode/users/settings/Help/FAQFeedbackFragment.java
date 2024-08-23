package com.aftershade.workmode.users.settings.Help;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class FAQFeedbackFragment extends Fragment {

    RatingBar ratingBar;
    TextInputEditText commentEdit;
    Button submitBtn;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f_a_q_feedback, container, false);
        nameText.setValue(getString(R.string.faq_feedback_));

        ratingBar = view.findViewById(R.id.rating_bar_app_feedback);
        commentEdit = view.findViewById(R.id.app_feed_comment);
        submitBtn = view.findViewById(R.id.submit_feedback_btn);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                float rating = ratingBar.getRating();

                if (rating == 0) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Oops, you need to set a rating.", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveFeedback();
            }
        });

        return view;
    }

    private void saveFeedback() {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Feedback");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TimeZone.getDefault().getID()));

        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("rating", String.valueOf(ratingBar.getRating()));
        stringMap.put("comment", commentEdit.getText().toString());

        ref.child(String.valueOf(calendar.getTimeInMillis())).updateChildren(stringMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Feedback submitted. Thank you for your feedback.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    ErrorCatching errorCatching = new ErrorCatching(getContext());
                    errorCatching.onException(task.getException());
                }

            }
        });

    }
}