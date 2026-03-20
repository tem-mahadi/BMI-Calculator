package com.temmahadi.bmicalculator.BdApps_Backend;

import android.content.Intent;
import android.os.Bundle;
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

public class OTPActivity extends AppCompatActivity {

    private EditText etOTP;
    private Button btnSubmitOTP;
    private String mobileNumber;
    private String referenceNo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        
        // Enable ActionBar back button (only if it exists)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Close the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    etOTP = findViewById(R.id.editTextOTP);
    btnSubmitOTP = findViewById(R.id.buttonSubmit);
    progressBar = findViewById(R.id.progressBarOTP);

        mobileNumber = getIntent().getStringExtra("mobile_number");
        referenceNo = getIntent().getStringExtra("referenceNo");

        btnSubmitOTP.setOnClickListener(view -> {
            String otp = etOTP.getText().toString().trim();
            if (!otp.isEmpty()) {
                verifyOTPWithServer(referenceNo, otp);
            } else {
                Toast.makeText(OTPActivity.this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed(); // Use the new dispatcher
        return true;
    }
    private void verifyOTPWithServer(String referenceNo, String otp) {
        progressBar.setVisibility(View.VISIBLE);

        // Add logging to see what we're sending
        android.util.Log.d("OTPActivity", "Verifying OTP: " + otp + " with RefNo: " + referenceNo);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<OTPRequest> call = apiService.verifyOTP(otp, referenceNo);

        call.enqueue(new Callback<OTPRequest>() {
            @Override
            public void onResponse(Call<OTPRequest> call, Response<OTPRequest> response) {
                // Don't hide progress bar here - let individual cases handle it
                
                // Add detailed logging
                android.util.Log.d("OTPActivity", "Response Code: " + response.code());
                android.util.Log.d("OTPActivity", "Response Message: " + response.message());
                
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String status = response.body().getsubscriptionStatus();
                        android.util.Log.d("OTPActivity", "Subscription Status: " + status);
                        
                        if ("S1000".equalsIgnoreCase(status) || "INITIAL CHARGING PENDING".equalsIgnoreCase(status) ) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OTPActivity.this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                            goToStartPage();
                        }
//                        else if ("INITIAL CHARGING PENDING".equalsIgnoreCase(status)) {
//                            // Keep progress bar visible during charging
//                            Toast.makeText(OTPActivity.this, "OTP Verified! Processing subscription...", Toast.LENGTH_SHORT).show();
//                            pollOTPStatus(referenceNo, 0);
//                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OTPActivity.this, "OTP Failed - Status: " + status, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        android.util.Log.e("OTPActivity", "Response body is null");
                        Toast.makeText(OTPActivity.this, "Server returned empty response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    // Log the error details
                    android.util.Log.e("OTPActivity", "Response not successful: " + response.code());
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("OTPActivity", "Error Body: " + errorBody);
                            
                            // Show the actual error from server
                            if (errorBody.contains("cURL error") || errorBody.contains("Invalid JSON")) {
                                Toast.makeText(OTPActivity.this, "Server error: " + errorBody, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(OTPActivity.this, "OTP Verification Failed: " + errorBody, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(OTPActivity.this, "OTP Verification Failed - Code: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("OTPActivity", "Error reading error body: " + e.getMessage());
                        Toast.makeText(OTPActivity.this, "OTP Verification Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OTPRequest> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                android.util.Log.e("OTPActivity", "Network Error: " + t.getMessage());
                Toast.makeText(OTPActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pollOTPStatus(String referenceNo, int attempt) {
        if (attempt >= 10) { // Stop after ~30s
            progressBar.setVisibility(View.GONE);
            
            // Show dialog with options
            new android.app.AlertDialog.Builder(this)
                .setTitle("Subscription Status")
                .setMessage("Your OTP was verified successfully, but the subscription is still being processed. This may take a few minutes.\n\nWhat would you like to do?")
                .setPositiveButton("Continue to App", (dialog, which) -> {
                    Toast.makeText(this, "You can use the app. Subscription will complete in background.", Toast.LENGTH_LONG).show();
                    goToStartPage();
                })
                .setNegativeButton("Wait & Try Again", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    pollOTPStatus(referenceNo, 0); // Restart polling
                })
                .setCancelable(false)
                .show();
            return;
        }

        android.util.Log.d("OTPActivity", "Polling attempt " + (attempt + 1) + "/10");

        new android.os.Handler().postDelayed(() -> {
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            String lastOtp = etOTP.getText().toString().trim();
            Call<OTPRequest> call = apiService.verifyOTP(lastOtp, referenceNo);

            call.enqueue(new Callback<OTPRequest>() {
                @Override
                public void onResponse(Call<OTPRequest> call, Response<OTPRequest> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String status = response.body().getsubscriptionStatus();
                        android.util.Log.d("OTPActivity", "Poll result: " + status);
                        
                        if ("S1000".equalsIgnoreCase(status)) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OTPActivity.this, "Subscription activated successfully!", Toast.LENGTH_SHORT).show();
                            goToStartPage();
                        } else if ("PENDING_INITIAL_CHARGING".equalsIgnoreCase(status)) {
                            // Continue polling
                            pollOTPStatus(referenceNo, attempt + 1);
                        } else {
                            // Other status - show what it is and continue polling
                            android.util.Log.d("OTPActivity", "Status still: " + status + ", continuing to poll");
                            pollOTPStatus(referenceNo, attempt + 1);
                        }
                    } else {
                        // Continue polling on error
                        pollOTPStatus(referenceNo, attempt + 1);
                    }
                }

                @Override
                public void onFailure(Call<OTPRequest> call, Throwable t) {
                    // Continue polling on network error
                    pollOTPStatus(referenceNo, attempt + 1);
                }
            });
        }, 3000);
    }


    private void goToStartPage() {
        progressBar.setVisibility(View.GONE);
        
        // Complete user login - save mobile number and set subscribed
        SubscriptionManager.completeUserLogin(OTPActivity.this, mobileNumber);
        
        Intent intent = new Intent(OTPActivity.this, startPage.class);
        startActivity(intent);
        finish();
    }
}