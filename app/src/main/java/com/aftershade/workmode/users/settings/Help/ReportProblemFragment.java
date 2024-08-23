package com.aftershade.workmode.users.settings.Help;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class ReportProblemFragment extends Fragment {

    TextInputEditText describeProblem;
    Button nextBtn;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_problem, container, false);
        nameText.setValue(getString(R.string.report_problem_));

        describeProblem = view.findViewById(R.id.describe_problem_edit_txt);
        nextBtn = view.findViewById(R.id.save_problem_);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String problem = describeProblem.getText().toString();

                if (problem.isEmpty()) {
                    progressDialog.dismiss();
                    describeProblem.setError("You have to provide a problem description.");
                    return;
                }

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("App_Problems");

                String key = reference.push().getKey();

                HashMap<String, Object> problemMap = new HashMap<>();

                problemMap.put("uid", uid);
                problemMap.put("problem", problem);
                problemMap.put("problemKey", key);
                problemMap.put("appName", "GET_FIT");
                problemMap.put("dateReported", sdf.format(date));

                reference.child("GET_FIT").child(key)
                        .updateChildren(problemMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Thank you for informing us of this problem. We will notify you by email when the problem is fixed", Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog.dismiss();
                            ErrorCatching errorCatching = new ErrorCatching(getContext());
                            errorCatching.onException(task.getException());
                        }
                    }
                });

            }
        });

        return view;
    }
}