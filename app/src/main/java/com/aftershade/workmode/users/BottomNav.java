package com.aftershade.workmode.users;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.Databases.NetworkCheck;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.HelperClasses.Models.User;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ui.chats.ChatsFragment;
import com.aftershade.workmode.users.ui.home.HomeFragment;
import com.aftershade.workmode.users.ui.profile.ProfileFragment;
import com.aftershade.workmode.users.ui.trainers.TrainersFragment;
import com.aftershade.workmode.users.workout_posts.FavoritesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;

public class BottomNav extends AppCompatActivity {

    View customActionBar;
    ImageView backBtn;
    TextView actionBarText;
    public static MutableLiveData<String> nameText;
    public static MutableLiveData<Boolean> enableBack;
    DatabaseReference ref;
    NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        networkCheck = new NetworkCheck(getApplicationContext());
        networkCheck.regiserNetworkCallback();

        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        BottomNavigationView navView = findViewById(R.id.nav_view);
        customActionBar = findViewById(R.id.action_bar_custom);

        actionBarText = customActionBar.findViewById(R.id.actionBarText);
        backBtn = customActionBar.findViewById(R.id.action_bar_back);

        nameText = new MutableLiveData<>();
        enableBack = new MutableLiveData<>();

        nameText.setValue(getString(R.string.my_activity_));
        nameText.observe(this, s -> actionBarText.setText(nameText.getValue()));

        enableBack.setValue(false);

        enableBack.observe(this, s -> {

            if (s) {
                backBtn.setVisibility(View.VISIBLE);
            } else {
                backBtn.setVisibility(View.GONE);
            }

        });
        customActionBar.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction().replace(R.id.container_userdash, new HomeFragment()).commit();
        enableBack.setValue(false);
        navView.setSelectedItemId(R.id.navigation_home);

        backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_favorites) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_userdash, new FavoritesFragment()).commit();
                customActionBar.setVisibility(View.GONE);
                nameText.setValue(getString(R.string.favorites));
                enableBack.setValue(false);
            } else if (itemId == R.id.navigation_trainers) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_userdash, new TrainersFragment()).commit();
                customActionBar.setVisibility(View.GONE);
                nameText.setValue(getString(R.string.trainers));
                enableBack.setValue(false);
            } else if (itemId == R.id.navigation_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_userdash, new HomeFragment()).commit();
                customActionBar.setVisibility(View.GONE);
                nameText.setValue(getString(R.string.my_activity_));
                enableBack.setValue(false);
            } else if (itemId == R.id.navigation_chats) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_userdash, new ChatsFragment()).commit();
                customActionBar.setVisibility(View.GONE);
                nameText.setValue(getString(R.string.chats_));
                enableBack.setValue(false);
            } else if (itemId == R.id.navigation_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_userdash, new ProfileFragment()).commit();
                customActionBar.setVisibility(View.GONE);
                enableBack.setValue(false);
            }

            return true;
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ErrorCatching catching = new ErrorCatching(getApplicationContext());

        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    SessionManager manager = new SessionManager(getApplicationContext(), SESSION_USERSESSION);
                    manager.updateLoginSession(user.getFullName(), user.getEmail(),
                            user.getPhoneNumber(), user.getDateOfBirth(), user.getGender());

                } else {
                    Toast.makeText(BottomNav.this, "Error occurred loading user data.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                catching.onError(error);
            }
        });
    }
}