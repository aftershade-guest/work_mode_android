package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

public class PostsViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.images_post_card);

    }
}
