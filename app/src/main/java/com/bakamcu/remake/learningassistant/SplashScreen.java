package com.bakamcu.remake.learningassistant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.leancloud.LCUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LCUser currentUser = LCUser.getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(SplashScreen.this, MainActivity.class);
        } else {
            intent = new Intent(SplashScreen.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}