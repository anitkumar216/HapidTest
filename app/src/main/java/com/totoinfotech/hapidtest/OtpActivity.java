package com.totoinfotech.hapidtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.totoinfotech.hapidtest.databinding.ActivityOtpBinding;

import java.text.MessageFormat;
import java.util.Objects;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null) {
            String phone = getIntent().getStringExtra("PHONE");
            String otp = getIntent().getStringExtra("OTP");
            setViews(phone, otp);
            setOnClickEvents(phone, otp);
        }
    }

    private void setViews(String phone, String otp) {
        binding.tvPhoneNumber.setText(MessageFormat.format("+91 {0}", phone));
    }

    private void setOnClickEvents(String phone, String otp) {

        binding.ibBack.setOnClickListener(view -> onBackPressed());

        binding.ibEditPhoneNumber.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.btnSubmit.setOnClickListener(view -> {
            if (Objects.requireNonNull(binding.pvOtp.getText()).toString().isEmpty() || binding.pvOtp.getText().toString().length() < 4) {
                Toast.makeText(this, "Please enter four digit otp", Toast.LENGTH_SHORT).show();
            } else {
                if (binding.pvOtp.getText().toString().equals(otp)) {
                    goNext(phone);
                } else {
                    Toast.makeText(this, "Please enter correct otp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goNext(String phone) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("PHONE", phone);
        startActivity(intent);
        overridePendingTransition(R.anim.entry, R.anim.exit);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_entry, R.anim.back_exit);
        finish();
    }
}