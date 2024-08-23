package com.aftershade.workmode.HelperClasses.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostImagesRecyclerAdapter extends RecyclerView.Adapter<PostImagesRecyclerAdapter.PostImagesViewHolder> {

    ArrayList<String> strings;

    public PostImagesRecyclerAdapter(ArrayList<String> strings) {
        this.strings = strings;
    }

    @NonNull
    @Override
    public PostImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_image_recycler_card, parent, false);
        return new PostImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostImagesViewHolder holder, int position) {
        Picasso.get().load(strings.get(position))
                .resize(1024, 720)
                .placeholder(R.drawable.image_placeholder_icon)
                .error(R.drawable.image_error_icon)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public static class PostImagesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public PostImagesViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.home_image_card);

        }
    }

}
