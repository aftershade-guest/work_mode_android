package com.aftershade.workmode.Common.OnBoarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aftershade.workmode.Common.LoginSignUp.LoginSignUpActivity;
import com.aftershade.workmode.HelperClasses.SliderAdapter;
import com.aftershade.workmode.R;

public class OnBoardingFragment extends Fragment {

    Button skip, getStarted;
    LinearLayout dotsLayout;
    TextView[] dots;
    ImageView nextBtn;
    ViewPager viewPager;
    SliderAdapter sliderAdapter;
    SharedPreferences onBoardingScreenSP;

    int currentPos;

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            currentPos = position;

            if (position == 0) {
                getStarted.setVisibility(View.INVISIBLE);

            } else if (position == 1) {
                getStarted.setVisibility(View.INVISIBLE);

            } else if (position == 2) {
                getStarted.setVisibility(View.INVISIBLE);
            } else if (position == 3){
                getStarted.setVisibility(View.INVISIBLE);
            } else {
                getStarted.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_boarding, container, false);

        onBoardingScreenSP = getActivity().getSharedPreferences("onBoardingScreen", Context.MODE_PRIVATE);

        skip = view.findViewById(R.id.skipBtn);
        getStarted = view.findViewById(R.id.get_started_btn);
        dotsLayout = view.findViewById(R.id.dots);
        nextBtn = view.findViewById(R.id.on_boarding_next);
        viewPager = view.findViewById(R.id.slider_onboarding);

        sliderAdapter = new SliderAdapter(getContext());

        viewPager.setAdapter(sliderAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        addDots(0);

        skip.setOnClickListener(v -> {

            SharedPreferences.Editor editor = onBoardingScreenSP.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            getActivity().startActivity(new Intent(getActivity(), LoginSignUpActivity.class));
            getActivity().finish();

            /*getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.splash_screen_container,
                    new AcceptPrivacyFragment()).commit();*/
        });

        nextBtn.setOnClickListener(v -> {
            viewPager.setCurrentItem(currentPos + 1);
        });

        getStarted.setOnClickListener(v -> {

            SharedPreferences.Editor editor = onBoardingScreenSP.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            getActivity().startActivity(new Intent(getActivity(), LoginSignUpActivity.class));
            getActivity().finish();

            /*getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_splash,
                    new AcceptPrivacyFragment()).commit();*/
        });


        return view;
    }

    private void addDots(int position) {
        dots = new TextView[5];

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);

        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.black));
        }

    }
}