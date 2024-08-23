package com.aftershade.workmode.users;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.aftershade.workmode.R;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    String url;
    ImageView imageView;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        url = getIntent().getStringExtra("url");

        imageView = findViewById(R.id.image_view_activity);
        backBtn = findViewById(R.id.image_activity_back_btn);

        Picasso.get()
                .load(url)
                .placeholder(R.drawable.image_placeholder_icon)
                .error(R.drawable.image_error_icon)
                .into(imageView);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}