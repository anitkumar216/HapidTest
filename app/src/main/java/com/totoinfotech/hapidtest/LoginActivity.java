package com.totoinfotech.hapidtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.totoinfotech.hapidtest.databinding.ActivityLoginBinding;
import com.totoinfotech.hapidtest.databinding.LayoutOtpPopUpBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnClickEvents();
    }

    private void setOnClickEvents() {
        binding.ibBack.setOnClickListener(view -> onBackPressed());

        binding.btnRequestOtp.setOnClickListener(view -> {
            if (binding.etPhoneNumber.getText().toString().isEmpty() || binding.etPhoneNumber.getText().toString().length() < 10) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            } else {
                String phoneNumber = binding.etPhoneNumber.getText().toString();
                String otp = phoneNumber.substring(0,2) + phoneNumber.substring(8,10);
                otpPopUp(otp, phoneNumber);
            }
        });
    }

    private void otpPopUp(String otp, String phoneNumber) {
        final Dialog dialog = new Dialog(LoginActivity.this, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutOtpPopUpBinding layoutOtpPopUpBinding = LayoutOtpPopUpBinding.inflate(getLayoutInflater());
        dialog.setContentView(layoutOtpPopUpBinding.getRoot());
        layoutOtpPopUpBinding.tvOtp.setText(otp);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
            intent.putExtra("PHONE", phoneNumber);
            intent.putExtra("OTP", otp);
            startActivity(intent);
            overridePendingTransition(R.anim.entry, R.anim.exit);
            dialog.dismiss();
        }, 3000);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_entry, R.anim.back_exit);
        finish();
    }
}