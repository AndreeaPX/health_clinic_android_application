package com.example.msl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoardingStories extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout slide_dots;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsStartBtn, skipBtn, nextBtn;
    Animation anim;
    //used in the onClick for next button
    int savedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_stories);

        viewPager=findViewById(R.id.slider);
        slide_dots=findViewById(R.id.llDots);
        letsStartBtn=findViewById(R.id.btnStart);
        skipBtn=findViewById(R.id.btnSkip);
        nextBtn=findViewById(R.id.btnNext);

        //set adapter
        sliderAdapter = new SliderAdapter(getApplicationContext());
        viewPager.setAdapter(sliderAdapter);

        //default position
        createSliderDots(0);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        //skip button
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //next button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(savedPosition+1);//go to the next story
            }
        });
    }

    //create the dots
    private void createSliderDots(int position){
        dots = new TextView[3];
        slide_dots.removeAllViews(); //clean dots every time
        for(int i=0;i<dots.length;i++){
            dots[i] = new TextView(getApplicationContext());
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(40);
            slide_dots.addView(dots[i]);
        }

        if(dots.length >0){
            dots[position].setTextColor(getResources().getColor(R.color.lightBlue));
        }


    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            createSliderDots(position);
            //hide the startBtn
            savedPosition=position;
            switch (position){
                case 0:
                case 1:
                    letsStartBtn.setVisibility(View.INVISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    skipBtn.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    nextBtn.setVisibility(View.INVISIBLE);
                    skipBtn.setVisibility(View.INVISIBLE);
                    anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.lets_start_animation);
                    letsStartBtn.setAnimation(anim);
                    letsStartBtn.setVisibility(View.VISIBLE);
                    letsStartBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}