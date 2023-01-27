package com.totoinfotech.hapidtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.totoinfotech.hapidtest.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        goNext();
    }

    private void goNext() {
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, SecondSplashActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.entry, R.anim.exit);
            finish();
        }, 3000);
    }
}