package com.aftershade.workmode.users.ui.trainers;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeaturedProgramHolder extends RecyclerView.ViewHolder{

    public TextView courseName, skillLevel;
    public RatingBar rating;
    public ImageView programPic;

    public FeaturedProgramHolder(@NonNull View itemView) {
        super(itemView);

        courseName = itemView.findViewById(R.id.featured_program_name_card);
        skillLevel = itemView.findViewById(R.id.featured_skill_level_);
        rating = itemView.findViewById(R.id.featured_rating_card);
        programPic = itemView.findViewById(R.id.featured_program_card_image);

    }

}
