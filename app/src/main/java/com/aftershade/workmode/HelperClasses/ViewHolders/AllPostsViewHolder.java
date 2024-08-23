package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllPostsViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView profilePic;
    public TextView userNameTop, userNameBottom, trainerType, postDate, caption;
    public RatingBar ratingBar;
    public ImageButton optionsButton;
    public RelativeLayout captionLayout;
    public RecyclerView imageRecyclerView;

    public AllPostsViewHolder(@NonNull View itemView) {
        super(itemView);

        profilePic = itemView.findViewById(R.id.post_profile_pic_card);
        imageRecyclerView = itemView.findViewById(R.id.all_post_image_card);
        userNameTop = itemView.findViewById(R.id.post_user_name_text_card);
        userNameBottom = itemView.findViewById(R.id.post_user_name_bottom_text_card);
        trainerType = itemView.findViewById(R.id.post_trainer_type_text_card);
        postDate = itemView.findViewById(R.id.post_date_text_card);
        caption = itemView.findViewById(R.id.post_caption_text_card);
        ratingBar = itemView.findViewById(R.id.post_trainer_rating_card);
        optionsButton = itemView.findViewById(R.id.post_options_btn_card);
        captionLayout = itemView.findViewById(R.id.caption_info_post_card);

    }
}
