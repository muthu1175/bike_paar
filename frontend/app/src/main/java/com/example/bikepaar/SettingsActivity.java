package com.example.bikepaar;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchNotifications;
    private CardView btnBack;
    private LinearLayout btnHelpSupport, btnFeedback, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views - CORRECTED TYPES
        switchNotifications = findViewById(R.id.switchNotifications);
        btnBack = findViewById(R.id.btnBack);
        btnHelpSupport = findViewById(R.id.btnHelpSupport); // This is LinearLayout in XML
        btnFeedback = findViewById(R.id.btnFeedback);       // This is LinearLayout in XML
        btnLogout = findViewById(R.id.btnLogout);           // This is LinearLayout in XML

        // Back button click - return to ProfileActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add button press animation
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }
        });

        // Notification switch with animation
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Add toggle animation
                ObjectAnimator animator = ObjectAnimator.ofFloat(buttonView, "scaleX",
                        0.9f, 1.1f, 1f);
                animator.setDuration(200);
                animator.start();

                if (isChecked) {
                    Toast.makeText(SettingsActivity.this,
                            "Notifications enabled", Toast.LENGTH_SHORT).show();
                    // Save preference to SharedPreferences or backend
                } else {
                    Toast.makeText(SettingsActivity.this,
                            "Notifications disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Help & Support click
        btnHelpSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                Toast.makeText(SettingsActivity.this,
                        "coming soon..", Toast.LENGTH_SHORT).show();
                // Implement help/support functionality
            }
        });

        // Feedback click
        // Feedback click
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                Intent intent = new Intent(SettingsActivity.this, FeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Logout click
        // Logout click
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);

                // Clear user session/data
                android.content.SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                // Navigate to LoginActivity
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                Toast.makeText(SettingsActivity.this,
                        "Logged out successfully", Toast.LENGTH_SHORT).show();
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