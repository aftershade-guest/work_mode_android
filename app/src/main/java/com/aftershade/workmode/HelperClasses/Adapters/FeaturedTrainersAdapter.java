package com.aftershade.workmode.HelperClasses.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Listeners.TrainersClickListener;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeaturedTrainersAdapter extends RecyclerView.Adapter<FeaturedTrainersAdapter.FeaturedTrainerViewHolder> {

    Context context;
    ArrayList<Instructor> instructorArrayList;
    TrainersClickListener trainersClickListener;
    DatabaseReference allTrainersRef;

    public FeaturedTrainersAdapter(Context context, ArrayList<Instructor> instructors, TrainersClickListener trainersClickListener) {
        this.context = context;
        this.instructorArrayList = instructors;
        this.trainersClickListener = trainersClickListener;
        allTrainersRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
    }

    @NonNull
    @Override
    public FeaturedTrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_trainers_round_card,
                parent, false);

        return new FeaturedTrainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedTrainerViewHolder trainerViewHolder, int position) {

        Instructor instructor = instructorArrayList.get(position);

        trainerViewHolder.fullName.setText(instructor.getFullName());
        trainerViewHolder.type.setText(instructor.getTrainerType());
        trainerViewHolder.rating.setText("0");

        allTrainersRef.child(instructor.getUid()).child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            Picasso.get()
                                    .load(snapshot.child("profilePhoto").getValue(String.class))
                                    .placeholder(R.drawable.image_placeholder_icon)
                                    .error(R.drawable.image_error_icon)
                                    .resize(1024, 720)
                                    .into(trainerViewHolder.profileImage);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ErrorCatching errorCatching = new ErrorCatching(context);
                        errorCatching.onError(error);
                    }
                });

        trainerViewHolder.itemView.setOnClickListener(v -> {

            trainersClickListener.onClick(instructorArrayList.get(position));

        });

    }

    @Override
    public int getItemCount() {
        return instructorArrayList.size();
    }

    public static class FeaturedTrainerViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView fullName;
        TextView rating, type;

        public FeaturedTrainerViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.featured_trainer_profile_pic);
            type = itemView.findViewById(R.id.featured_trainer_type);
            rating = itemView.findViewById(R.id.featured_trainer_rating);
            fullName = itemView.findViewById(R.id.featured_trainer_full_name);

        }
    }

}
