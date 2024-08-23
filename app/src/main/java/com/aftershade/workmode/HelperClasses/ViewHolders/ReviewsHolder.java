package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

public class ReviewsHolder extends RecyclerView.ViewHolder {
    public TextView userName, dateText, descText;
    public RatingBar ratingBar;

    public ReviewsHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.review_by_txt_card);
        dateText = itemView.findViewById(R.id.date_reviewed_card);
        descText = itemView.findViewById(R.id.reviewed_desc_card);
        ratingBar = itemView.findViewById(R.id.user_rating_card);

    }
}
