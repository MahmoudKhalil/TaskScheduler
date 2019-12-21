package com.example.taskschedulerproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_SCREEN_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, ListsActivity.class);
                startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
