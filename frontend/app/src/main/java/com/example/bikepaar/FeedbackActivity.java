package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private CardView btnBack, btnSubmitFeedback;
    private android.widget.ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize views
        etFeedback = findViewById(R.id.etFeedback);
        btnBack = findViewById(R.id.btnBack);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);
        progressBar = findViewById(R.id.progressBar);

        // Back button click - return to SettingsActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Submit Feedback button click
        btnSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                submitFeedback();
            }
        });
    }

    private void submitFeedback() {
        String feedbackText = etFeedback.getText().toString().trim();

        if (TextUtils.isEmpty(feedbackText)) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        if (feedbackText.length() < 10) {
            Toast.makeText(this, "Feedback should be at least 10 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement API call to submit feedback to backend
        // Using Retrofit to send feedback to PHP/MySQL backend
        submitFeedbackToServer(feedbackText);
    }

    private void submitFeedbackToServer(String feedback) {
        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Please login to submit feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnSubmitFeedback.setEnabled(false);
        Toast.makeText(this, "Submitting your feedback...", Toast.LENGTH_SHORT).show();

        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("feedback", feedback);

        api.addFeedback("Token " + token, body).enqueue(new retrofit2.Callback<Map<String, String>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, String>> call, retrofit2.Response<Map<String, String>> response) {
                // Hide loading
                progressBar.setVisibility(View.GONE);
                btnSubmitFeedback.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(FeedbackActivity.this, "Thank you! Your feedback has been saved.", Toast.LENGTH_LONG).show();
                    etFeedback.setText("");
                    
                    // Return to Settings after delay
                    etFeedback.postDelayed(() -> {
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }, 2000);
                } else {
                    String errorMsg = "Submission failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(FeedbackActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, String>> call, Throwable t) {
                // Hide loading
                progressBar.setVisibility(View.GONE);
                btnSubmitFeedback.setEnabled(true);
                Toast.makeText(FeedbackActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Button click animation
    private void animateButtonClick(View v) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}