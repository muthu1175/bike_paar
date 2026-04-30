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

        Intent intent = getIntent();

        // Initial UI Update with Intent Data
        updateUI(intent);

        // Fetch Fresh Data from Dataset (Backend)
        if (intent != null && intent.getStringExtra("BIKE_NAME") != null) {
            fetchBikeDetails(intent.getStringExtra("BIKE_NAME"));
        }

        // Back Button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Tab Buttons
        Button tabOverview = findViewById(R.id.tab_overview);
        Button tabReviews = findViewById(R.id.tab_reviews);

        tabOverview.setOnClickListener(v -> {
            tabOverview.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tabReviews.setTextColor(getResources().getColor(android.R.color.darker_gray));
        });

        tabReviews.setOnClickListener(v -> {
            tabReviews.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tabOverview.setTextColor(getResources().getColor(android.R.color.darker_gray));
            
            try {
                // Navigate to User Reviews Activity
                Intent reviewsIntent = new Intent(MotorcycleDetailsActivity.this, UserReviewsActivity.class);
                String name = tvTitle.getText().toString();
                if (intent != null && (name.isEmpty() || name.equals("BikePaar"))) {
                     name = intent.getStringExtra("BIKE_NAME");
                }
                reviewsIntent.putExtra("BIKE_NAME", name);
                startActivity(reviewsIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (Exception e) {
                e.printStackTrace();
                android.widget.Toast.makeText(MotorcycleDetailsActivity.this, "Error opening reviews", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Intent intent) {
        if (intent == null) return;
        
        String bikeName = intent.getStringExtra("BIKE_NAME");
        int priceVal = intent.getIntExtra("BIKE_PRICE", 0);
        String imageUrl = intent.getStringExtra("BIKE_IMAGE_URL");
        String bikeEngine = intent.getStringExtra("BIKE_ENGINE");
        String bikeDescription = intent.getStringExtra("BIKE_DESCRIPTION");
        String maxPower = intent.getStringExtra("MAX_POWER");
        String kerbWeight = intent.getStringExtra("KERB_WEIGHT");

        if (bikeName != null) tvTitle.setText(bikeName);
        if (priceVal > 0) {
             tvPrice.setText(String.format("₹%,d", priceVal));
        } else {
             String priceStr = intent.getStringExtra("PRICE");
             if (priceStr != null) tvPrice.setText(priceStr);
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.sample_bike).into(ivBike);
        }

        if (bikeEngine != null) tvDetailsEngine.setText(bikeEngine);
        if (bikeDescription != null) tvDetailsDescription.setText(bikeDescription);
        if (maxPower != null) tvDetailsPower.setText(maxPower);
        if (kerbWeight != null) tvDetailsWeight.setText(kerbWeight);
        
        // Setup View All Button here if data is available in intent
        setupViewAllButton(intent, bikeEngine);
    }
    
    private void setupViewAllButton(Intent intentData, String engine) {
         Button btnViewAll = findViewById(R.id.btn_view_all);
         btnViewAll.setOnClickListener(v -> {
            Intent specsIntent = new Intent(MotorcycleDetailsActivity.this, FullSpecificationsActivity.class);
            // Pass bike data
            specsIntent.putExtra("BIKE_NAME", tvTitle.getText().toString());
            specsIntent.putExtra("PRICE", tvPrice.getText().toString());
            specsIntent.putExtra("BIKE_ENGINE", tvDetailsEngine.getText().toString()); // Use current UI text which might be updated
            
            if (intentData != null) {
                specsIntent.putExtra("BIKE_IMAGE_URL", intentData.getStringExtra("BIKE_IMAGE_URL"));
                specsIntent.putExtra("MAX_POWER", intentData.getStringExtra("MAX_POWER"));
                specsIntent.putExtra("MAX_TORQUE", intentData.getStringExtra("MAX_TORQUE"));
                specsIntent.putExtra("KERB_WEIGHT", intentData.getStringExtra("KERB_WEIGHT"));
                specsIntent.putExtra("MILEAGE", intentData.getStringExtra("MILEAGE"));
                specsIntent.putExtra("TRANSMISSION", intentData.getStringExtra("TRANSMISSION"));
                specsIntent.putExtra("FUEL_TANK_CAPACITY", intentData.getStringExtra("FUEL_TANK_CAPACITY"));
                specsIntent.putExtra("BRAKING_SYSTEM", intentData.getStringExtra("BRAKING_SYSTEM"));
                specsIntent.putExtra("TOP_SPEED", intentData.getStringExtra("TOP_SPEED"));
                specsIntent.putExtra("FRONT_BRAKE_TYPE", intentData.getStringExtra("FRONT_BRAKE_TYPE"));
                specsIntent.putExtra("REAR_BRAKE_TYPE", intentData.getStringExtra("REAR_BRAKE_TYPE"));
                specsIntent.putExtra("FRONT_SUSPENSION", intentData.getStringExtra("FRONT_SUSPENSION"));
                specsIntent.putExtra("REAR_SUSPENSION", intentData.getStringExtra("REAR_SUSPENSION"));
                specsIntent.putExtra("TYRE_TYPE", intentData.getStringExtra("TYRE_TYPE"));
                specsIntent.putExtra("HEADLIGHT", intentData.getStringExtra("HEADLIGHT"));
                specsIntent.putExtra("TAIL_LIGHT", intentData.getStringExtra("TAIL_LIGHT"));
                specsIntent.putExtra("BATTERY_CAPACITY", intentData.getStringExtra("BATTERY_CAPACITY"));
                specsIntent.putExtra("INSTRUMENT_CLUSTER", intentData.getStringExtra("INSTRUMENT_CLUSTER"));
                
                // Dimensions
                specsIntent.putExtra("OVERALL_LENGTH", intentData.getStringExtra("OVERALL_LENGTH"));
                specsIntent.putExtra("OVERALL_WIDTH", intentData.getStringExtra("OVERALL_WIDTH"));
                specsIntent.putExtra("SEAT_HEIGHT", intentData.getStringExtra("SEAT_HEIGHT"));
                specsIntent.putExtra("GROUND_CLEARANCE", intentData.getStringExtra("GROUND_CLEARANCE"));
            }
            
            // Shared Element Transition
            android.app.ActivityOptions options = android.app.ActivityOptions.makeSceneTransitionAnimation(
                    MotorcycleDetailsActivity.this,
                    ivBike,
                    "shared_bike_image"
            );
            
            startActivity(specsIntent, options.toBundle());
        });
    }

    private void fetchBikeDetails(String bikeName) {
        String token = "Token " + getSharedPreferences("USER_DATA", MODE_PRIVATE).getString("TOKEN", "");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        
        apiService.searchBikes(token, bikeName).enqueue(new retrofit2.Callback<java.util.List<Bike>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<Bike>> call, retrofit2.Response<java.util.List<Bike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Bike b : response.body()) {
                        // Fuzzy match check if needed, but search usually returns relevant results.
                        // Ideally pick the one that matches name closely.
                        if (b.name != null && b.name.toLowerCase().contains(bikeName.toLowerCase())) {
                            refreshUIWithBike(b);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<java.util.List<Bike>> call, Throwable t) {
                // Fail silently, keep showing intent data
            }
        });
    }

    private void refreshUIWithBike(Bike b) {
        // Update Main UI
        tvTitle.setText(b.name);
        tvPrice.setText(b.getFormattedPrice());
        tvDetailsEngine.setText(b.engine != null ? b.engine : "N/A");
        tvDetailsDescription.setText(b.badge != null ? b.badge : (b.vehicleType + " • " + b.usage));
        tvDetailsPower.setText(b.getMaxPower());
        tvDetailsWeight.setText(b.getKerbWeight());

        if (b.imageUrl != null && !b.imageUrl.isEmpty()) {
             Glide.with(this).load(b.imageUrl).placeholder(R.drawable.sample_bike).into(ivBike);
        }

        // Update Button Listener with new FULL data
        Intent newIntent = new Intent(); // Mock intent to hold new data
        newIntent.putExtra("BIKE_NAME", b.name);
        newIntent.putExtra("BIKE_PRICE", b.price);
        newIntent.putExtra("BIKE_IMAGE_URL", b.imageUrl);
        newIntent.putExtra("BIKE_ENGINE", b.engine);
        newIntent.putExtra("MAX_POWER", b.getMaxPower());
        newIntent.putExtra("MAX_TORQUE", b.getMaxTorque());
        newIntent.putExtra("KERB_WEIGHT", b.getKerbWeight());
        newIntent.putExtra("MILEAGE", b.getMileage());
        newIntent.putExtra("TRANSMISSION", b.getTransmission());
        newIntent.putExtra("FUEL_TANK_CAPACITY", b.getFuelTankCapacity());
        newIntent.putExtra("BRAKING_SYSTEM", b.getBrakingSystem());
        newIntent.putExtra("TOP_SPEED", b.getTopSpeed());
        newIntent.putExtra("FRONT_BRAKE_TYPE", b.getFrontBrakeType());
        newIntent.putExtra("REAR_BRAKE_TYPE", b.getRearBrakeType());
        newIntent.putExtra("FRONT_SUSPENSION", b.getFrontSuspension());
        newIntent.putExtra("REAR_SUSPENSION", b.getRearSuspension());
        newIntent.putExtra("TYRE_TYPE", b.getTyreType());
        newIntent.putExtra("HEADLIGHT", b.getHeadlight());
        newIntent.putExtra("TAIL_LIGHT", b.getTailLight());
        newIntent.putExtra("BATTERY_CAPACITY", b.getBatteryCapacity());
        newIntent.putExtra("INSTRUMENT_CLUSTER", b.getInstrumentCluster());
        newIntent.putExtra("OVERALL_LENGTH", b.getOverallLength());
        newIntent.putExtra("OVERALL_WIDTH", b.getOverallWidth());
        newIntent.putExtra("SEAT_HEIGHT", b.getSeatHeight());
        newIntent.putExtra("GROUND_CLEARANCE", b.getGroundClearance());

        setupViewAllButton(newIntent, b.engine);
    }
}