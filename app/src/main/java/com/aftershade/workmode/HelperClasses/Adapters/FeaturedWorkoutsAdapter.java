package com.aftershade.workmode.HelperClasses.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.HelperClasses.Listeners.ProgramClickListener;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ui.trainers.FeaturedProgramHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeaturedWorkoutsAdapter extends RecyclerView.Adapter<FeaturedProgramHolder> {

    ArrayList<Object> allPrograms;
    Context context;
    ProgramClickListener programClickListener;
    Click_Listerner click_listerner;
    DatabaseReference programRef;
    Button button;

    private final int WORKOUT_TYPE = 1;
    private final int WELCOME_TYPE = 0;

    public FeaturedWorkoutsAdapter(ArrayList<Object> allPrograms, Context context,
                                   ProgramClickListener programClickListener, Click_Listerner click_listerner) {
        this.allPrograms = allPrograms;
        this.context = context;
        programRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");
        this.programClickListener = programClickListener;
        this.click_listerner = click_listerner;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return WELCOME_TYPE;
        }

        return WORKOUT_TYPE;
    }

    @NonNull
    @Override
    public FeaturedProgramHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case WELCOME_TYPE:
                View welcomeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_welcome_card, parent, false);
                button = welcomeView.findViewById(R.id.explore_content_welcome_card_btn);
                return new FeaturedProgramHolder(welcomeView);
            case WORKOUT_TYPE:
                //fall through
            default:
                View workoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.program_featured_card,
                        parent, false);

                return new FeaturedProgramHolder(workoutView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedProgramHolder holder, int position) {

        if (position == 0) {

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click_listerner.onClick(v, position, false);
                }
            });

        } else {

            ProgramsHelperClass programsHelperClass = (ProgramsHelperClass) allPrograms.get(position);


            ((FeaturedProgramHolder) holder).courseName.setText(programsHelperClass.getCourseName());
            ((FeaturedProgramHolder) holder).skillLevel.setText(programsHelperClass.getSkillLevel());
            ((FeaturedProgramHolder) holder).rating.setRating((float) programsHelperClass.getRating());

            String key = programsHelperClass.getCourseName() + programsHelperClass.getTrainerId();

            programRef.child(key).child("Images")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Picasso.get()
                                            .load(snap.getValue().toString())
                                            .resize(1024, 720)
                                            .placeholder(R.drawable.image_placeholder_icon)
                                            .error(R.drawable.image_error_icon)
                                            .into(((FeaturedProgramHolder) holder).programPic);
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
                    programClickListener.onClick(programsHelperClass);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return allPrograms.size();
    }
}
