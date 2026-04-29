package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText etSignupEmail, etUsername, etSignupPassword, etConfirmPassword;
    Button btnCreateAccount;
    TextView tvLogin, tvVerificationStatus;
    ImageView btnBack, imgVerifiedTick;
    ImageView ivSignupPasswordToggle, ivConfirmPasswordToggle;
    Button btnSendOTP, btnVerifyOTP; // Changed button names
    LinearLayout otpContainer;
    EditText etOTP;
    boolean isEmailVerified = false;
    ProgressBar progressBarSignup; // Added ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etSignupEmail = findViewById(R.id.etSignupEmail);
        etUsername = findViewById(R.id.etUsername);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvLogin = findViewById(R.id.tvLogin);
        btnBack = findViewById(R.id.btnBack);

        // Init new views
        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        otpContainer = findViewById(R.id.otpContainer);
        etOTP = findViewById(R.id.etOTP);
        imgVerifiedTick = findViewById(R.id.imgVerifiedTick);
        tvVerificationStatus = findViewById(R.id.tvVerificationStatus);
        progressBarSignup = findViewById(R.id.progressBarSignup); // Init ProgressBar
        ivSignupPasswordToggle = findViewById(R.id.ivSignupPasswordToggle);
        ivConfirmPasswordToggle = findViewById(R.id.ivConfirmPasswordToggle);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        ivSignupPasswordToggle.setOnClickListener(v -> {
            if (etSignupPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etSignupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivSignupPasswordToggle.setImageResource(R.drawable.ic_visibility);
            } else {
                etSignupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivSignupPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            }
            etSignupPassword.setSelection(etSignupPassword.getText().length());
        });

        ivConfirmPasswordToggle.setOnClickListener(v -> {
            if (etConfirmPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility);
            } else {
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        // SEND OTP Logic... (Unchanged)
        btnSendOTP.setOnClickListener(v -> {
             // ... existing logic ...
            String email = etSignupEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignupActivity.this, "Please enter email first", Toast.LENGTH_SHORT).show();
                return;
            }
            
            btnSendOTP.setEnabled(false);
            btnSendOTP.setText("Sending...");

            Map<String, String> body = new HashMap<>();
            body.put("email", email);

            api.sendEmailOTP(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "OTP Sent! Check your email.", Toast.LENGTH_LONG).show();
                        btnSendOTP.setText("Resend OTP");
                        btnSendOTP.setEnabled(true);
                        otpContainer.setVisibility(android.view.View.VISIBLE); // Show OTP input
                        tvVerificationStatus.setText("OTP Sent");
                    } else {
                        Toast.makeText(SignupActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                        btnSendOTP.setEnabled(true);
                        btnSendOTP.setText("Send OTP");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSendOTP.setEnabled(true);
                    btnSendOTP.setText("Send OTP");
                }
            });
        });

        // VERIFY OTP Logic... (Unchanged)
        btnVerifyOTP.setOnClickListener(v -> {
            String email = etSignupEmail.getText().toString().trim();
            String otp = etOTP.getText().toString().trim();

            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(SignupActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("otp", otp);

            api.verifyEmailOTP(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Boolean verified = (Boolean) response.body().get("verified");
                        if (verified != null && verified) {
                            // SUCCESS!
                            isEmailVerified = true;
                            imgVerifiedTick.setVisibility(android.view.View.VISIBLE); // Show tick
                            otpContainer.setVisibility(android.view.View.GONE); // Hide OTP input
                            btnSendOTP.setVisibility(android.view.View.GONE); // Hide Send button
                            tvVerificationStatus.setText("Verified");
                            tvVerificationStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                            etSignupEmail.setEnabled(false); // Lock email
                        } else {
                            Toast.makeText(SignupActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCreateAccount.setOnClickListener(v -> {
            if (!validateInputs()) return;
            
            if (!isEmailVerified) {
                Toast.makeText(SignupActivity.this, "Please verify your email first!", Toast.LENGTH_SHORT).show();
                return;
            }

            // SHOW LOADING
            btnCreateAccount.setEnabled(false);
            btnCreateAccount.setText("");
            progressBarSignup.setVisibility(android.view.View.VISIBLE);

            Map<String, String> body = new HashMap<>();
            body.put("username", etUsername.getText().toString().trim());
            body.put("email", etSignupEmail.getText().toString().trim());
            body.put("password", etSignupPassword.getText().toString().trim());
            body.put("confirm_password",
                    etConfirmPassword.getText().toString().trim());

            api.signup(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    
                    // HIDE LOADING
                    btnCreateAccount.setEnabled(true);
                    btnCreateAccount.setText("Sign Up");
                    progressBarSignup.setVisibility(android.view.View.GONE);

                    if (response.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                        
                        // Pass email back to LoginActivity
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.putExtra("email", etSignupEmail.getText().toString().trim());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        // TRY TO PARSE ERROR BODY
                        String errorMsg = "Signup failed";
                        try {
                            if (response.errorBody() != null) {
                                String errorJson = response.errorBody().string();
                                // Simple manual parsing or use Gson if clear structure
                                // For now just showing the raw error if small, or generic
                                if(errorJson.contains("Email already exists")) {
                                    errorMsg = "Email already registered. Please Login.";
                                } else if (errorJson.contains("verify your email")) {
                                    errorMsg = "Please verify your email first.";
                                } else {
                                     // Strip JSON braces for cleaner look if possible
                                     errorMsg = errorJson.replace("{","").replace("}","").replace("\"","");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    // HIDE LOADING
                    btnCreateAccount.setEnabled(true);
                    btnCreateAccount.setText("Sign Up");
                    progressBarSignup.setVisibility(android.view.View.GONE);

                    Toast.makeText(SignupActivity.this, "Server error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        tvLogin.setOnClickListener(v -> {
            onBackPressed(); // Or finish() to go back to LoginActivity
        });

    }

    private boolean validateInputs() {
        return !TextUtils.isEmpty(etSignupEmail.getText())
                && !TextUtils.isEmpty(etUsername.getText())
                && !TextUtils.isEmpty(etSignupPassword.getText())
                && etSignupPassword.getText().toString()
                .equals(etConfirmPassword.getText().toString());
    }
}
