package com.example.msl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    SharedPreferences sharedPreferences, sharedPreferencesLogin;
    static final String SHARE_PREFERENCES_STRING = "new";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        constraintLayout=findViewById(R.id.splashScreenConstraintLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(500);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
        //Open application => splash screen first
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    //duration
                    //open LoginActivity after 3 sec
                    sleep(3000);
                }
                catch (Exception exception){
                    exception.printStackTrace();
                }
                finally {
                    //share pref for onBoarding screen stories
                    sharedPreferences=getSharedPreferences(SHARE_PREFERENCES_STRING,MODE_PRIVATE);
                    boolean isNewUser = sharedPreferences.getBoolean("newUser", true);
                    sharedPreferencesLogin=getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
                    Boolean session = sharedPreferencesLogin.getBoolean("session",false);
                    if(isNewUser) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("newUser", false);
                        editor.apply();
                        Intent intent = new Intent(SplashActivity.this, OnBoardingStories.class);
                        finish();
                    }
                    else
                    {
                        if(!session) {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }
            }
        };
        thread.start();


    }

}