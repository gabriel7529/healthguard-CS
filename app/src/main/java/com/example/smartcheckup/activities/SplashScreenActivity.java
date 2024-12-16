package com.example.smartcheckup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;


import com.example.smartcheckup.R;
import com.example.smartcheckup.databinding.ActivitySplashScreenBinding;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;


public class SplashScreenActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivitySplashScreenBinding binding;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreenActivity.this, DetecFallActivity.class);
                startActivity(intent);

            }
        },3000);

    }
}