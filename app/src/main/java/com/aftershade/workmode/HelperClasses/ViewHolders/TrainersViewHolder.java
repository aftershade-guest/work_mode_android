package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

public class TrainersViewHolder extends RecyclerView.ViewHolder {

    public TextView fullName, type;
    public RatingBar rating;
    public ImageView profilePic;

    public TrainersViewHolder(@NonNull View itemView) {
        super(itemView);

        fullName = itemView.findViewById(R.id.trainer_name_card);
        type = itemView.findViewById(R.id.trainer_type_card);
        rating = itemView.findViewById(R.id.rating_bar_card_trainer);
        profilePic = itemView.findViewById(R.id.image_trainers_card);

    }

}
