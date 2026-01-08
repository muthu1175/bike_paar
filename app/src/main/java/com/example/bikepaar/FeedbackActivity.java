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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize views
        etFeedback = findViewById(R.id.etFeedback);
        btnBack = findViewById(R.id.btnBack);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

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
        // Show loading
        Toast.makeText(this, "Submitting feedback...", Toast.LENGTH_SHORT).show();

        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Please login to submit feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("feedback", feedback);

        api.addFeedback("Token " + token, body).enqueue(new retrofit2.Callback<Map<String, String>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, String>> call, retrofit2.Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    etFeedback.setText("");
                    
                    // Return to Settings after delay
                    new android.os.Handler().postDelayed(() -> {
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }, 1500);
                } else {
                    Toast.makeText(FeedbackActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, String>> call, Throwable t) {
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