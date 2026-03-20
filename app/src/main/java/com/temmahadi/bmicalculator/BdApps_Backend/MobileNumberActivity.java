package com.temmahadi.bmicalculator.BdApps_Backend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.temmahadi.bmicalculator.R;
import com.temmahadi.bmicalculator.startPage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberActivity extends AppCompatActivity {

    private EditText etMobileNumber;
    private Button btnSendOTP;
    private ProgressBar progressBar;
    private MobileNumberRequest mobileNumberRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);

        if (SubscriptionManager.isSubscribed(this)) {
            startActivity(new Intent(this, startPage.class));
            finish();
            return;
        }

        // Enable ActionBar back button (only if it exists)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Handle back button press (new way)
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Close the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        etMobileNumber = findViewById(R.id.etMobileNumber);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        progressBar = findViewById(R.id.progressBar);

        btnSendOTP.setOnClickListener(v -> {
            String mobileNumber = etMobileNumber.getText().toString().trim();
            if (!mobileNumber.isEmpty()) {
                if(mobileNumber.length()==11){
                    sendMobileNumberToServer(mobileNumber);
                }
                else{
                    Toast.makeText(this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle ActionBar back button press
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed(); // Use the new dispatcher
        return true;
    }

    private void sendMobileNumberToServer(String mobileNumber) {
        progressBar.setVisibility(View.VISIBLE);
        
        Log.d("MobileNumberActivity", "Sending mobile number: " + mobileNumber);
        Log.d("MobileNumberActivity", "Base URL: " + RetrofitClient.getRetrofitInstance().baseUrl());
        
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<MobileNumberRequest> call = apiService.sendMobileNumber(mobileNumber);

        call.enqueue(new Callback<MobileNumberRequest>() {
            @Override
            public void onResponse(Call<MobileNumberRequest> call, Response<MobileNumberRequest> response) {
                progressBar.setVisibility(View.GONE);
                
                Log.d("MobileNumberActivity", "Response Code: " + response.code());
                Log.d("MobileNumberActivity", "Response Message: " + response.message());
                
                if (response.isSuccessful() && response.body() != null) {
                    mobileNumberRequest = response.body();
                    Log.d("MobileNumberActivity", "Success! Reference No: " + mobileNumberRequest.getReferenceNo());
                    
                    Intent intent = new Intent(MobileNumberActivity.this, OTPActivity.class);
                    intent.putExtra("mobile_number", mobileNumber);
                    intent.putExtra("referenceNo", mobileNumberRequest.getReferenceNo());
                    startActivity(intent);
                    finish();
                } else {
                    // Log the error details
                    Log.e("MobileNumberActivity", "Response not successful");
                    Log.e("MobileNumberActivity", "Error Code: " + response.code());
                    Log.e("MobileNumberActivity", "Error Message: " + response.message());
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("MobileNumberActivity", "Error Body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("MobileNumberActivity", "Error reading error body: " + e.getMessage());
                    }
                    
                    // Show more detailed error message
                    String errorMessage = "Failed to send OTP";
                    if (response.code() == 404) {
                        errorMessage = "Server endpoint not found";
                    } else if (response.code() == 500) {
                        errorMessage = "Server error occurred";
                    } else if (response.code() >= 400) {
                        errorMessage = "Request failed: " + response.message();
                    }
                    
                    Toast.makeText(MobileNumberActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MobileNumberRequest> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("MobileNumberActivity", "Network Error: " + t.getMessage());
                Log.e("MobileNumberActivity", "Error Class: " + t.getClass().getSimpleName());
                
                String errorMessage = "Network error occurred";
                if (t instanceof java.net.ConnectException) {
                    errorMessage = "Cannot connect to server";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "Request timeout";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Server not found";
                }
                
                Toast.makeText(MobileNumberActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}