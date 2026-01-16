package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    ViewFlipper viewFlipper;
    
    // Step 1: User Details
    EditText etUsername, etEmail;
    Button btnGetOtp;

    // Step 2: OTP
    EditText etOtp;
    Button btnVerifyOtp;

    // Step 3: New Password
    EditText etNewPassword, etConfirmPassword;
    Button btnChangePassword;
    
    TextView tvRemembered;

    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        api = ApiClient.getClient().create(ApiService.class);

        // Initialize Views
        ImageView btnBack = findViewById(R.id.btnBack);
        viewFlipper = findViewById(R.id.viewFlipper);
        tvRemembered = findViewById(R.id.tvRemembered);

        // Step 1
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        btnGetOtp = findViewById(R.id.btnGetOtp);

        // Step 2
        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        // Step 3
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Back Button Logic
        btnBack.setOnClickListener(v -> {
            if (viewFlipper.getDisplayedChild() > 0) {
                viewFlipper.showPrevious();
            } else {
                onBackPressed();
            }
        });

        // Remembered? Login
        tvRemembered.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // --- Step 1 Action: Get OTP ---
        btnGetOtp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter username and email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call API
            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            
            // Optional: You could verify if user exists via forgot-password check first, 
            // but sendEmailOTP just sends OTP. 
            
            btnGetOtp.setEnabled(false);
            btnGetOtp.setText("Sending...");

            api.sendEmailOTP(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    btnGetOtp.setEnabled(true);
                    btnGetOtp.setText("Get OTP");

                    if (response.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
                        viewFlipper.showNext(); // Go to Step 2
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    btnGetOtp.setEnabled(true);
                    btnGetOtp.setText("Get OTP");
                    Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // --- Step 2 Action: Verify OTP ---
        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (otp.length() < 6) {
                Toast.makeText(this, "Enter valid 6-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("otp", otp);

            btnVerifyOtp.setEnabled(false);
            btnVerifyOtp.setText("Verifying...");

            api.verifyEmailOTP(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    btnVerifyOtp.setEnabled(true);
                    btnVerifyOtp.setText("Verify OTP");

                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> resp = response.body();
                        // Check "verified" key if backend sends it as boolean
                        Object verifiedObj = resp.get("verified");
                        boolean isVerified = false;
                        if (verifiedObj instanceof Boolean) {
                            isVerified = (Boolean) verifiedObj;
                        }

                        if (isVerified) {
                            Toast.makeText(ForgotPasswordActivity.this, "Verified!", Toast.LENGTH_SHORT).show();
                            viewFlipper.showNext(); // Go to Step 3
                        } else {
                             Toast.makeText(ForgotPasswordActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    btnVerifyOtp.setEnabled(true);
                    btnVerifyOtp.setText("Verify OTP");
                    Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // --- Step 3 Action: New Password ---
        btnChangePassword.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("email", email);
            body.put("new_password", newPass);
            body.put("confirm_new_password", confirmPass);

            btnChangePassword.setEnabled(false);
            btnChangePassword.setText("Updating...");

            api.forgotPassword(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    btnChangePassword.setEnabled(true);
                    btnChangePassword.setText("Reset Password");

                    if (response.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password Reset Successful!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        // Backend error usually contains exact reason now (e.g., "Username does not match")
                        Toast.makeText(ForgotPasswordActivity.this, "Failed: Check details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    btnChangePassword.setEnabled(true);
                    btnChangePassword.setText("Reset Password");
                    Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    
    // Handle Custom Back Press (ViewFlipper)
    @Override
    public void onBackPressed() {
        if (viewFlipper.getDisplayedChild() > 0) {
            viewFlipper.showPrevious();
        } else {
            super.onBackPressed();
        }
    }
}