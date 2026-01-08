package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class MotorcycleDetailsActivity extends AppCompatActivity {

    private TextView tvTitle, tvPrice, tvDetailsEngine, tvDetailsDescription, tvDetailsPower, tvDetailsWeight;
    private ImageView ivBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcycle_details);

        // Initialize views
        tvTitle = findViewById(R.id.tv_title);
        tvPrice = findViewById(R.id.tv_price);
        ivBike = findViewById(R.id.motorcycle_image);
        tvDetailsEngine = findViewById(R.id.tv_details_engine);
        tvDetailsDescription = findViewById(R.id.tv_details_description);
        tvDetailsPower = findViewById(R.id.tv_details_power);
        tvDetailsWeight = findViewById(R.id.tv_details_weight);

        // Get data from intent
        Intent intent = getIntent();
        String bikeEngine = "";
        if (intent != null) {
            String bikeName = intent.getStringExtra("BIKE_NAME");
            int priceVal = intent.getIntExtra("BIKE_PRICE", 0);
            String imageUrl = intent.getStringExtra("BIKE_IMAGE_URL");
            bikeEngine = intent.getStringExtra("BIKE_ENGINE");
            String bikeDescription = intent.getStringExtra("BIKE_DESCRIPTION");
            String maxPower = intent.getStringExtra("MAX_POWER");
            String kerbWeight = intent.getStringExtra("KERB_WEIGHT");

            if (bikeName != null) {
                tvTitle.setText(bikeName);
            }
            if (priceVal > 0) {
                 tvPrice.setText(String.format("₹%,d", priceVal));
            } else {
                 // Try legacy string key if int is 0
                 String priceStr = intent.getStringExtra("PRICE");
                 if (priceStr != null) tvPrice.setText(priceStr);
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.sample_bike)
                        .into(ivBike);
            }

            if (bikeEngine != null) {
                tvDetailsEngine.setText(bikeEngine);
            }

            if (bikeDescription != null) {
                tvDetailsDescription.setText(bikeDescription);
            }
            
            if (maxPower != null) tvDetailsPower.setText(maxPower);
            if (kerbWeight != null) tvDetailsWeight.setText(kerbWeight);
        }

        final String finalBikeEngine = bikeEngine;

        // Back Button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Tab Buttons
        Button tabOverview = findViewById(R.id.tab_overview);
        Button tabReviews = findViewById(R.id.tab_reviews);

        tabOverview.setOnClickListener(v -> {
            tabOverview.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tabReviews.setTextColor(getResources().getColor(android.R.color.darker_gray));
            // Stay on current page (overview)
        });

        tabReviews.setOnClickListener(v -> {
            tabReviews.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tabOverview.setTextColor(getResources().getColor(android.R.color.darker_gray));
            // Navigate to User Reviews Activity
            Intent reviewsIntent = new Intent(MotorcycleDetailsActivity.this, UserReviewsActivity.class);
            // Pass bike name for context
            reviewsIntent.putExtra("BIKE_NAME", tvTitle.getText().toString());
            startActivity(reviewsIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // NEW: View All Button - Navigate to Full Specifications
        Button btnViewAll = findViewById(R.id.btn_view_all);
        btnViewAll.setOnClickListener(v -> {
            Intent specsIntent = new Intent(MotorcycleDetailsActivity.this, FullSpecificationsActivity.class);
            // Pass bike data
            specsIntent.putExtra("BIKE_NAME", tvTitle.getText().toString());
            specsIntent.putExtra("PRICE", tvPrice.getText().toString());
            specsIntent.putExtra("BIKE_ENGINE", finalBikeEngine);
            
            if (intent != null) {
                specsIntent.putExtra("MAX_POWER", intent.getStringExtra("MAX_POWER"));
                specsIntent.putExtra("MAX_TORQUE", intent.getStringExtra("MAX_TORQUE"));
                specsIntent.putExtra("KERB_WEIGHT", intent.getStringExtra("KERB_WEIGHT"));
                specsIntent.putExtra("MILEAGE", intent.getStringExtra("MILEAGE"));
                specsIntent.putExtra("TRANSMISSION", intent.getStringExtra("TRANSMISSION"));
                specsIntent.putExtra("FUEL_TANK_CAPACITY", intent.getStringExtra("FUEL_TANK_CAPACITY"));
                specsIntent.putExtra("BRAKING_SYSTEM", intent.getStringExtra("BRAKING_SYSTEM"));
                specsIntent.putExtra("TOP_SPEED", intent.getStringExtra("TOP_SPEED"));
                specsIntent.putExtra("FRONT_BRAKE_TYPE", intent.getStringExtra("FRONT_BRAKE_TYPE"));
                specsIntent.putExtra("REAR_BRAKE_TYPE", intent.getStringExtra("REAR_BRAKE_TYPE"));
                specsIntent.putExtra("FRONT_SUSPENSION", intent.getStringExtra("FRONT_SUSPENSION"));
                specsIntent.putExtra("REAR_SUSPENSION", intent.getStringExtra("REAR_SUSPENSION"));
                specsIntent.putExtra("TYRE_TYPE", intent.getStringExtra("TYRE_TYPE"));
                specsIntent.putExtra("HEADLIGHT", intent.getStringExtra("HEADLIGHT"));
                specsIntent.putExtra("TAIL_LIGHT", intent.getStringExtra("TAIL_LIGHT"));
                specsIntent.putExtra("BATTERY_CAPACITY", intent.getStringExtra("BATTERY_CAPACITY"));
            }
            
            startActivity(specsIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}