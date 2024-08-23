package com.aftershade.workmode.HelperClasses.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.HelperClasses.Models.KeyValueClass;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ViewPhotoFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageFragmentAdapter extends RecyclerView.Adapter<ImageFragmentAdapter.ImageFragmentHolder> {

    ArrayList<KeyValueClass> urlArray;
    Click_Listerner click_listerner;

    public ImageFragmentAdapter(ArrayList<KeyValueClass> urlArray, Click_Listerner click_listerner) {
        this.urlArray = urlArray;
        this.click_listerner = click_listerner;
    }

    @NonNull
    @Override
    public ImageFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_pager_card, parent, false);

        return new ImageFragmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFragmentHolder holder, int position) {

        Picasso.get()
                .load(urlArray.get(position).getValue())
                .resize(1024, 720)
                .placeholder(R.drawable.image_placeholder_icon)
                .error(R.drawable.image_error_icon)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_listerner.onClick(v, position, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return urlArray.size();
    }

    public static class ImageFragmentHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageFragmentHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images_pager_view_card);
        }
    }

}
