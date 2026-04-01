package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuperBikesActivity extends AppCompatActivity {

    private LinearLayout bikesContainer;
    private List<Bike> bikeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_bikes);

        ImageView backButton = findViewById(R.id.ivBack);
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        bikesContainer = findViewById(R.id.bikesContainer);
        fetchData();
    }

    private void fetchData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String rawToken = getSharedPreferences("USER_DATA", MODE_PRIVATE).getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        
        apiService.getSuperBikes(token).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bikeList = response.body();
                    populateBikes(bikeList);
                } else {
                    Toast.makeText(SuperBikesActivity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(SuperBikesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateBikes(List<Bike> bikes) {
        bikesContainer.removeAllViews();
        for (Bike bike : bikes) {
            View bikeCard = getLayoutInflater().inflate(R.layout.item_super_bike, bikesContainer, false);

            ImageView bikeImage = bikeCard.findViewById(R.id.bikeImage);
            TextView engineBadge = bikeCard.findViewById(R.id.engineBadge);
            TextView bikeName = bikeCard.findViewById(R.id.bikeName);
            TextView bikeDescription = bikeCard.findViewById(R.id.bikeDescription);
            TextView bikePrice = bikeCard.findViewById(R.id.bikePrice);
            Button viewDetailsBtn = bikeCard.findViewById(R.id.viewDetailsBtn);

            if (bike.imageUrl != null && !bike.imageUrl.isEmpty()) {
                Glide.with(this).load(bike.imageUrl).placeholder(R.drawable.sample_bike).into(bikeImage);
            } else {
                 bikeImage.setImageResource(R.drawable.sample_bike);
            }
            
            if (bike.engine != null && !bike.engine.isEmpty()) engineBadge.setText(bike.engine);
            else engineBadge.setText("N/A");
            
            bikeName.setText(bike.name);
            
            if (bike.badge != null && !bike.badge.isEmpty()) bikeDescription.setText(bike.badge);
            else bikeDescription.setText(bike.vehicleType + " • " + bike.usage);
            
            bikePrice.setText(bike.getFormattedPrice());

            viewDetailsBtn.setOnClickListener(v -> {
                Intent intent = new Intent(SuperBikesActivity.this, MotorcycleDetailsActivity.class);
                intent.putExtra("BIKE_NAME", bike.name);
                intent.putExtra("BIKE_DESCRIPTION", bike.badge);
                intent.putExtra("BIKE_ENGINE", bike.engine);
                intent.putExtra("BIKE_PRICE", bike.price);
                intent.putExtra("BIKE_IMAGE_URL", bike.imageUrl);
                
                // Dynamic Specs
                intent.putExtra("MAX_POWER", bike.getMaxPower());
                intent.putExtra("MAX_TORQUE", bike.getMaxTorque());
                intent.putExtra("KERB_WEIGHT", bike.getKerbWeight());
                intent.putExtra("MILEAGE", bike.getMileage());
                intent.putExtra("TRANSMISSION", bike.getTransmission());
                intent.putExtra("FUEL_TANK_CAPACITY", bike.getFuelTankCapacity());
                intent.putExtra("BRAKING_SYSTEM", bike.getBrakingSystem());
                intent.putExtra("TOP_SPEED", bike.getTopSpeed());

                // Full Specs
                intent.putExtra("FRONT_BRAKE_TYPE", bike.getFrontBrakeType());
                intent.putExtra("REAR_BRAKE_TYPE", bike.getRearBrakeType());
                intent.putExtra("FRONT_SUSPENSION", bike.getFrontSuspension());
                intent.putExtra("REAR_SUSPENSION", bike.getRearSuspension());
                intent.putExtra("TYRE_TYPE", bike.getTyreType());
                intent.putExtra("HEADLIGHT", bike.getHeadlight());
                intent.putExtra("TAIL_LIGHT", bike.getTailLight());
                intent.putExtra("BATTERY_CAPACITY", bike.getBatteryCapacity());
                
                startActivity(intent);
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 32);
            bikeCard.setLayoutParams(params);
            bikesContainer.addView(bikeCard);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}