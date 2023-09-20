package com.example.android.myvideoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        /* move from SplashScreenActivity to AllowAccessActivity after a delay */
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(SplashScreenActivity.this, AllowAccessActivity.class));
                startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
                finish();  // avoid coming back to Splash Screen when user presses back button
            }
        }, 500);
    }
}