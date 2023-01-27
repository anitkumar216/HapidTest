package com.totoinfotech.hapidtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.totoinfotech.hapidtest.databinding.ActivitySecondSplashBinding;

public class SecondSplashActivity extends AppCompatActivity {

    private ActivitySecondSplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondSplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnClickEvents();
    }

    private void setOnClickEvents() {
        binding.btnGetStarted.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.entry, R.anim.exit);
        });
    }
}