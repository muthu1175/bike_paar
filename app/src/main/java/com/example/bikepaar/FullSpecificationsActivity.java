package com.example.bikepaar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FullSpecificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_specifications);
        // Disable transition to fix 'Mismatch' error
        overridePendingTransition(0, 0);

        // Get data from intent
        String bikeName = getIntent().getStringExtra("BIKE_NAME");
        String bikePrice = getIntent().getStringExtra("PRICE");
        String bikeEngine = getIntent().getStringExtra("BIKE_ENGINE");

        // Back Button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Set bike info if data is available
        if (bikeName != null) {
            TextView tvBikeName = findViewById(R.id.bike_name);
            tvBikeName.setText(bikeName);
        }

        if (bikePrice != null) {
            TextView tvBikePrice = findViewById(R.id.bike_price);
            tvBikePrice.setText(bikePrice);
        }

        if (bikeEngine != null) {
            TextView tvSpecEngine = findViewById(R.id.tv_spec_engine);
            tvSpecEngine.setText(bikeEngine);
        }

        // --- NEW SPECIFICATIONS ---
        setSpecText(R.id.tv_spec_power, "MAX_POWER");
        setSpecText(R.id.tv_spec_torque, "MAX_TORQUE");
        setSpecText(R.id.tv_spec_mileage, "MILEAGE");
        setSpecText(R.id.tv_spec_transmission, "TRANSMISSION");
        setSpecText(R.id.tv_spec_weight, "KERB_WEIGHT");
        setSpecText(R.id.tv_spec_fuel_tank, "FUEL_TANK_CAPACITY");
        setSpecText(R.id.tv_spec_front_brake, "FRONT_BRAKE_TYPE");
        setSpecText(R.id.tv_spec_rear_brake, "REAR_BRAKE_TYPE");
        setSpecText(R.id.tv_spec_front_suspension, "FRONT_SUSPENSION");
        setSpecText(R.id.tv_spec_rear_suspension, "REAR_SUSPENSION");
        setSpecText(R.id.tv_spec_tyre, "TYRE_TYPE");
        setSpecText(R.id.tv_spec_headlight, "HEADLIGHT");
        setSpecText(R.id.tv_spec_taillight, "TAIL_LIGHT");
        setSpecText(R.id.tv_spec_battery, "BATTERY_CAPACITY");
    }

    private void setSpecText(int textViewId, String extraKey) {
        String value = getIntent().getStringExtra(extraKey);
        if (value != null && !value.isEmpty() && !value.equals("N/A")) {
            TextView tv = findViewById(textViewId);
            if (tv != null) {
                tv.setText(value);
            }
        }
    }
}