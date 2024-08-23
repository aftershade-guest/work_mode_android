package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

public class AllProgramsHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView courseNameText, descText, skillLevel, duration, durationSpan;
    public RatingBar ratingBar;
    public ImageButton favoriteBtn;

    public AllProgramsHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.main_photo_program_card);
        courseNameText = itemView.findViewById(R.id.program_name_card);
        ratingBar = itemView.findViewById(R.id.program_rating_card);
        descText = itemView.findViewById(R.id.program_description_card);
        skillLevel = itemView.findViewById(R.id.skill_level_program_card);
        favoriteBtn = itemView.findViewById(R.id.favorite_btn_program_card);
        duration = itemView.findViewById(R.id.duration_span_program_card);
        durationSpan = itemView.findViewById(R.id.duration_span_text_program_card);

    }
}
