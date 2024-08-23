package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

public class ProgramsViewHolders extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView programName;
    public TextView ratingBar, skillLevel;

    public ProgramsViewHolders(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.course_popular_image_card);
        programName = itemView.findViewById(R.id.course_name_popular_card);
        ratingBar = itemView.findViewById(R.id.course_rating_popular_card);
        skillLevel = itemView.findViewById(R.id.course_skill_level_popular_card);

    }
}
