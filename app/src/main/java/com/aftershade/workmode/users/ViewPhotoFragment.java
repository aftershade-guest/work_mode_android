package com.aftershade.workmode.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.R;
import com.squareup.picasso.Picasso;

public class ViewPhotoFragment extends Fragment {

    ImageView photoView;
    ImageButton close;
    String photoUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            photoUrl = getArguments().getString("url");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_photo, container, false);

        photoView = view.findViewById(R.id.photo_view_photo);
        close = view.findViewById(R.id.close_photo_view_btn);

        Picasso.get().load(photoUrl)
                .placeholder(R.drawable.image_placeholder_icon)
                .error(R.drawable.image_error_icon)
                .into(photoView);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return view;
    }
}