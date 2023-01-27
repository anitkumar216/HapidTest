package com.totoinfotech.hapidtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.totoinfotech.hapidtest.Api.ApiClient;
import com.totoinfotech.hapidtest.Interface.ApiInterface;
import com.totoinfotech.hapidtest.Model.PojoUploadApiResponse;
import com.totoinfotech.hapidtest.databinding.ActivityMainBinding;

import java.io.File;
import java.text.MessageFormat;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final static int SELECT_PICTURE = 1;
    Uri selectedImageURI;

    ApiInterface apiInterface;

    private LocationRequest locationRequest;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if (getIntent() != null) {
            String phone = getIntent().getStringExtra("PHONE");
            setViews(phone);
        }

        setOnClickEvents();
    }

    private void setViews(String phone) {
        binding.etPhone.setText(phone);
    }

    private void setOnClickEvents() {
        binding.ibBack.setOnClickListener(view -> onBackPressed());

        binding.ivProfilePhoto.setOnClickListener( view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        });

        binding.tvCreateProfile.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        });

        binding.rlLocationBtn.setOnClickListener(view -> {
            getCurrentLocation();
        });

        binding.btnSubmit.setOnClickListener(view -> {
            hitUploadApi(selectedImageURI, binding.etFirstName.getText().toString(),
                    binding.etLastname.getText().toString(),
                    binding.etPhone.getText().toString(),
                    binding.etAddress.getText().toString());
        });
    }

    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        binding.etAddress.setText(MessageFormat.format("{0}, {1}", latitude, longitude));
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }


    }

    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {

            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(MainActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {

                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Device does not have location
                        break;
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    private void hitUploadApi(Uri selectedImageURI, String firstName, String lastName, String phone, String address) {
        if (isValidate()) {
            String imagePath = selectedImageURI.getPath();
            MultipartBody.Part fileImage = null;
            if (imagePath != null && !imagePath.isEmpty()) {
                File photo_new = new File(imagePath);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), photo_new);
                fileImage = MultipartBody.Part.createFormData("photo", photo_new.getName(), mFile);
            }

            RequestBody firstNameBody = RequestBody.create(MediaType.parse("text/plain"), firstName);
            RequestBody lastNameBody = RequestBody.create(MediaType.parse("text/plain"), lastName);
            RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);
            RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);


            Call<PojoUploadApiResponse> call = apiInterface.uploadData(fileImage, firstNameBody, lastNameBody, phoneBody, addressBody);

            call.enqueue(new Callback<PojoUploadApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<PojoUploadApiResponse> call, @NonNull Response<PojoUploadApiResponse> response) {
                    if (response.body() != null) {
                        Toast.makeText(MainActivity.this, "Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PojoUploadApiResponse> call, @NonNull Throwable t) {

                    Toast.makeText(MainActivity.this, "Api Not Responding", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*private void getApiTest() {
        Call<PojoUploadApiResponse> call = apiInterface.getResponse();

        call.enqueue(new Callback<PojoUploadApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<PojoUploadApiResponse> call, @NonNull Response<PojoUploadApiResponse> response) {
                if (response.body() != null) {
                    Toast.makeText(MainActivity.this, "Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PojoUploadApiResponse> call, @NonNull Throwable t) {

                Toast.makeText(MainActivity.this, "Api Not Responding", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private boolean isValidate() {
        boolean flag = true;
        if (binding.etFirstName.getText().toString().trim().equals("")) {
            binding.etFirstName.setError("Mandatory Field");
            flag = false;
        } else if (binding.etLastname.getText().toString().trim().equals("")) {
            binding.etLastname.setError("Mandatory Field");
            flag = false;
        } else if (binding.etPhone.getText().toString().trim().equals("")) {
            binding.etPhone.setError("Mandatory Field");
            flag = false;
        } else if (binding.etAddress.getText().toString().trim().equals("")) {
            binding.etAddress.setError("Mandatory Field");
        } else if (selectedImageURI == null) {
            Toast.makeText(this, "Please select your profile photo", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                selectedImageURI = data.getData();

                Glide.with(MainActivity.this)
                        .load(selectedImageURI)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.ivProfilePhoto);
            }
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_entry, R.anim.back_exit);
        finish();
    }
}