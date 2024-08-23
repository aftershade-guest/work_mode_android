package com.aftershade.workmode.users;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.aftershade.workmode.R;
import com.aftershade.workmode.users.workout_posts.FavoritesFragment;
import com.aftershade.workmode.users.workout_posts.PostsFragment;
import com.aftershade.workmode.users.workout_posts.SaveProgramFragment;
import com.aftershade.workmode.users.workout_posts.WorkoutProgramFragment;

public class Workouts_Posts_Activity extends AppCompatActivity {

    ImageButton backBtn, optionBtn;
    String id, courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts__posts_);

        String where = getIntent().getStringExtra("whereTo");
        id = getIntent().getStringExtra("id");
        courseName = getIntent().getStringExtra("courseName");

        backBtn = findViewById(R.id.back_btn_workout_posts);
        optionBtn = findViewById(R.id.options_workout_post);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu(v);
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("id", id);

        if (where.equals("post")) {
            optionBtn.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.workout_posts_container, PostsFragment.class, bundle).commit();
        } else if (where.equals("program")) {
            optionBtn.setVisibility(View.VISIBLE);
            bundle.putString("courseName", courseName);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.workout_posts_container, WorkoutProgramFragment.class, bundle).commit();
        } else if (where.equals("favorite")) {
            optionBtn.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.workout_posts_container, new FavoritesFragment()).commit();
        }

    }

    private void showOptionsMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(Workouts_Posts_Activity.this, v);
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.inflate(R.menu.workout_post_options);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.nav_i_want_this) {

                    showProgramDialog();
                }

                return true;
            }
        });

        popupMenu.show();

    }

    private void showProgramDialog() {

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("courseName", courseName);

        SaveProgramFragment saveProgramFragment = new SaveProgramFragment();
        saveProgramFragment.setArguments(bundle);
        saveProgramFragment.show(getSupportFragmentManager(), "");

    }
}