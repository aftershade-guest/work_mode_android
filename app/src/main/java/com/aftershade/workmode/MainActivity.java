package com.aftershade.workmode;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aftershade.workmode.Common.LoginSignUp.LoginSignUpActivity;
import com.aftershade.workmode.Common.OnBoarding.OnBoardingFragment;
import com.aftershade.workmode.HelperClasses.PrefsHelper;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

public class MainActivity extends AppCompatActivity {

    SharedPreferences onBoardingScreenSP;
    TextView textView;

    boolean called = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        File file = new File(getExternalFilesDir(null), "/Media/Documents");
        File file1 = new File(getExternalFilesDir(null), "/Media/Audio");
        File file2 = new File(getExternalFilesDir(null), "/Media/Images");
        File file3= new File(getExternalFilesDir(null), "/Media/Videos");
        File file4 = new File(getExternalFilesDir(null), "/Media/Audio/Recordings");

        file.mkdirs();
        file1.mkdirs();
        file2.mkdirs();
        file3.mkdirs();
        file4.mkdirs();

        if (!called) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            called = true;
        }

        PrefsHelper.Builder builder = new PrefsHelper.Builder();
        builder.setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        createNotificationChannel();

        textView = findViewById(R.id.splash_screen_txt);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                onBoardingScreenSP = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

                boolean isFirstTime = onBoardingScreenSP.getBoolean("firstTime", true);

                if (isFirstTime) {
                    textView.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.splash_screen_container, new OnBoardingFragment()).commit();

                } else {
                    startActivity(new Intent(getApplicationContext(), LoginSignUpActivity.class));
                    finish();
                }


            }
        }, 3000);


    }

    private void createNotificationChannel() {

        NotificationChannel notificationChannel = new
                NotificationChannel("Get_FIT_Step_Counter", "GET_FIT Steps", NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setSound(null, null);
        notificationChannel.setVibrationPattern(null);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

    }

}