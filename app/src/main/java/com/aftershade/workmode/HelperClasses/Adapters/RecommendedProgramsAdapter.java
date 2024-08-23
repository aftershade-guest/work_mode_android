package com.aftershade.workmode.HelperClasses.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Listeners.ProgramClickListener;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.HelperClasses.ViewHolders.ProgramsViewHolders;
import com.aftershade.workmode.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecommendedProgramsAdapter extends RecyclerView.Adapter<ProgramsViewHolders> {

    Context context;
    ArrayList<ProgramsHelperClass> programsHelperClasses;
    ProgramClickListener programClickListener;
    DatabaseReference allProgramsRef;

    public RecommendedProgramsAdapter(Context context, ArrayList<ProgramsHelperClass> programsHelperClasses,
                                      ProgramClickListener programClickListener) {
        this.context = context;
        this.programsHelperClasses = programsHelperClasses;
        this.programClickListener = programClickListener;
        allProgramsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");
    }

    @NonNull
    @Override
    public ProgramsViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_programs_card, parent, false);
        return new ProgramsViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramsViewHolders holder, int position) {

        ProgramsHelperClass helperClass = programsHelperClasses.get(position);

        holder.programName.setText(helperClass.getCourseName());
        holder.ratingBar.setText(String.valueOf(helperClass.getRating()));
        holder.skillLevel.setText(helperClass.getSkillLevel());

        String key = helperClass.getCourseName() + helperClass.getTrainerId();

        allProgramsRef.child(key).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Picasso.get()
                                .load(snap.getValue().toString())
                                .resize(1024, 720)
                                .placeholder(R.drawable.image_placeholder_icon)
                                .error(R.drawable.image_error_icon)
                                .into(holder.imageView);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorCatching errorCatching = new ErrorCatching(context);
                errorCatching.onError(error);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programClickListener.onClick(programsHelperClasses.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return programsHelperClasses.size();
    }

    /*public static class RecommendedProgramsViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RatingBar ratingBar;
        TextView courseName;

        public RecommendedProgramsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recommended_programs_image_);
            courseName = itemView.findViewById(R.id.recommended_programs_course_name);
            ratingBar = itemView.findViewById(R.id.recommended_programs_rating);

        }
    }*/

}
