package com.temmahadi.bmicalculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.temmahadi.bmicalculator.BdApps_Backend.MobileNumberActivity;
import com.temmahadi.bmicalculator.BdApps_Backend.SubscriptionManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
//            if (SubscriptionManager.isSubscribed(this)) {
                intent = new Intent(SplashActivity.this, startPage.class);
//            } else {
//                intent = new Intent(SplashActivity.this, MobileNumberActivity.class);
//            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
