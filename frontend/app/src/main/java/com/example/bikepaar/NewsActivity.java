package com.example.bikepaar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class NewsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CardView featuredNews, news1, news2, news3, news4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        featuredNews = findViewById(R.id.featuredNews);
        news1 = findViewById(R.id.news1);
        news2 = findViewById(R.id.news2);
        news3 = findViewById(R.id.news3);
        news4 = findViewById(R.id.news4);

        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set click listeners for news items
        featuredNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle featured news click
                // Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);
                // startActivity(intent);
            }
        });

        news1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle news 1 click
            }
        });

        news2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle news 2 click
            }
        });

        news3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle news 3 click
            }
        });

        news4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle news 4 click
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
