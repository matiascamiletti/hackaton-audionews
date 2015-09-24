package com.mobileia.audionews;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.mobileia.audionews.activity.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private final static int DURATION_SPLASH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadTimer();
    }

    private void loadTimer() {
        CountDownTimer timer = new CountDownTimer(DURATION_SPLASH*1000,DURATION_SPLASH*1000) {

            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                MainActivity.createInstance(SplashActivity.this);
                finish();
            }
        };
        timer.start();
    }
}
