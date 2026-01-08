package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MenuActivity extends AppCompatActivity {

    private LinearLayout rowNewBikes, rowReviews, rowCompare, rowNews, rowSettings;
    private CardView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        rowNewBikes = findViewById(R.id.rowNewBikes);
        rowReviews = findViewById(R.id.rowReviews);
        rowCompare = findViewById(R.id.rowCompare);
        rowNews = findViewById(R.id.rowNews);
        rowSettings = findViewById(R.id.rowSettings);

        // Back button with animation
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                onBackPressed();
            }
        });

        // Menu item click listeners with animations
        rowNewBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);

                Intent intent = new Intent(MenuActivity.this, RecentLaunchesActivity.class);
                startActivity(intent);
                // Animation for forward navigation
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        rowReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);

                // Navigate to ReviewsActivity
                Intent intent = new Intent(MenuActivity.this, ReviewsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        rowCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);

                // Navigate to CompareActivity
                Intent intent = new Intent(MenuActivity.this, CompareActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        rowNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);

                // Navigate to NewsActivity
                Intent intent = new Intent(MenuActivity.this, NewsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        rowSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                // Navigate to SettingsActivity
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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