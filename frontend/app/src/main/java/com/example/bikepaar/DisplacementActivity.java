package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DisplacementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displacement);

        // Set up the back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up category click listeners
        setupCategoryClickListeners();
    }

    private void setupCategoryClickListeners() {
        // Below 100cc
        LinearLayout categoryBelow100 = findViewById(R.id.categoryBelow100);
        categoryBelow100.setOnClickListener(v -> {
            handleCategorySelection("Below 100cc", "Commuter & Efficiency");
        });

        // 100cc - 150cc
        LinearLayout category100to150 = findViewById(R.id.category100to150);
        category100to150.setOnClickListener(v -> {
            handleCategorySelection("100cc - 150cc", "Standard Street");
        });

        // 150cc - 200cc
        LinearLayout category150to200 = findViewById(R.id.category150to200);
        category150to200.setOnClickListener(v -> {
            handleCategorySelection("150cc - 200cc", "Entry Sport");
        });

        // 200cc - 350cc
        LinearLayout category200to350 = findViewById(R.id.category200to350);
        category200to350.setOnClickListener(v -> {
            handleCategorySelection("200cc - 350cc", "Performance");
        });

        // 350cc - 500cc
        LinearLayout category350to500 = findViewById(R.id.category350to500);
        category350to500.setOnClickListener(v -> {
            handleCategorySelection("350cc - 500cc", "Mid-Range Cruisers");
        });

        // 500cc - 750cc
        LinearLayout category500to750 = findViewById(R.id.category500to750);
        category500to750.setOnClickListener(v -> {
            handleCategorySelection("500cc - 750cc", "Touring & Adventure");
        });

        // 750cc - 1000cc
        LinearLayout category750to1000 = findViewById(R.id.category750to1000);
        category750to1000.setOnClickListener(v -> {
            handleCategorySelection("750cc - 1000cc", "Super Sport");
        });

        // 1000cc & Above
        LinearLayout category1000Above = findViewById(R.id.category1000Above);
        category1000Above.setOnClickListener(v -> {
            handleCategorySelection("1000cc & Above", "Premium & Custom");
        });

        // AI Assistant Card
        LinearLayout aiAssistantCard = findViewById(R.id.aiAssistantCard);
        aiAssistantCard.setOnClickListener(v -> {
            Intent intent = new Intent(DisplacementActivity.this, AiQuestionActivity.class);
            intent.putExtra("step", 1);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    // In DisplacementActivity.java, update the handleCategorySelection method:

    private void handleCategorySelection(String displacement, String category) {
        // Show selection toast
        Toast.makeText(this, "Selected: " + displacement, Toast.LENGTH_SHORT).show();

        // Navigate based on displacement category
        if (displacement.equals("Below 100cc")) {
            Intent intent = new Intent(DisplacementActivity.this, Below100ccActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (displacement.equals("100cc - 150cc")) {
            Intent intent = new Intent(DisplacementActivity.this, Cc100to150Activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (displacement.equals("150cc - 200cc")) {
            Intent intent = new Intent(DisplacementActivity.this, Cc150to200Activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (displacement.equals("200cc - 350cc")) {
            Intent intent = new Intent(DisplacementActivity.this, Cc200to350Activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (displacement.equals("350cc - 500cc")) {
            Intent intent = new Intent(DisplacementActivity.this, Cc350to500Activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (displacement.equals("500cc - 750cc")) {
            Intent intent = new Intent(DisplacementActivity.this, Cc500to750Activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (displacement.equals("750cc - 1000cc")) {
            Intent intent = new Intent(DisplacementActivity.this, Cc750to1000Activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (displacement.equals("1000cc & Above")) {
            Intent intent = new Intent(DisplacementActivity.this, Cc1000AboveActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            // For other categories, navigate to AllMotorcyclesActivity with filter
            Intent intent = new Intent(DisplacementActivity.this, AllMotorcyclesActivity.class);
            intent.putExtra("displacement", displacement);
            intent.putExtra("category", category);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}