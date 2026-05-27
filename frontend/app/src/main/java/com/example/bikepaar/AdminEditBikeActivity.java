package com.example.bikepaar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditBikeActivity extends AppCompatActivity {

    private TextView chipActive, chipDiscontinued;
    private ImageView ivBikeImage;
    private Uri selectedImageUri;
    private String originalBikeName = "";

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivBikeImage.setImageURI(selectedImageUri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ivBikeImage.setImageBitmap(imageBitmap);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_bike);

        // Top Bar
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());
        
        TextView tvReset = findViewById(R.id.tvReset);
        tvReset.setOnClickListener(v -> {
            Toast.makeText(this, "Form reset to original values", Toast.LENGTH_SHORT).show();
        });

        // Status Chips setup
        chipActive = findViewById(R.id.chipActive);
        chipDiscontinued = findViewById(R.id.chipDiscontinued);

        setupStatusChips();

        // Get Bike from Intent
        Bike bike = (Bike) getIntent().getSerializableExtra("bike");

        TextInputEditText etBikeName = findViewById(R.id.etBikeName);
        TextInputEditText etBrand = findViewById(R.id.etBrand);
        TextInputEditText etPrice = findViewById(R.id.etPrice);

        String engine = "";
        String power = "";
        String torque = "";
        String topSpeed = "";
        String mileage = "";
        String transmission = "";

        String brakingSystem = "";
        String frontBrake = "";
        String rearBrake = "";
        String frontSuspension = "";
        String rearSuspension = "";
        String tyreType = "";
        String instrumentCluster = "";
        String headlight = "";
        String tailLight = "";
        String batteryCapacity = "";

        String overallLength = "";
        String overallWidth = "";
        String weight = "";
        String fuelTank = "";
        String seatHeight = "";
        String clearance = "";

        if (bike != null) {
            ivBikeImage = findViewById(R.id.ivBikeImage);
            if (bike.imageUrl != null && !bike.imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(bike.imageUrl)
                        .placeholder(R.drawable.sample_bike)
                        .into(ivBikeImage);
            } else if (bike.imageRes != 0) {
                ivBikeImage.setImageResource(bike.imageRes);
            }

            LinearLayout btnChangePhoto = findViewById(R.id.btnChangePhoto);
            btnChangePhoto.setOnClickListener(v -> showImageSourceDialog());

            etBikeName.setText(bike.name != null ? bike.name : "");
            originalBikeName = bike.name != null ? bike.name : "";
            etBrand.setText(bike.brand != null ? bike.brand : "");
            etPrice.setText(String.valueOf(bike.price));

            engine = bike.engine != null ? bike.engine : "";
            power = bike.maxPower != null ? bike.maxPower : "";
            torque = bike.maxTorque != null ? bike.maxTorque : "";
            topSpeed = bike.topSpeed != null ? bike.topSpeed : "";
            mileage = bike.mileage != null ? bike.mileage : "";
            transmission = bike.transmission != null ? bike.transmission : "";

            brakingSystem = bike.brakingSystem != null ? bike.brakingSystem : "";
            frontBrake = bike.frontBrakeType != null ? bike.frontBrakeType : "";
            rearBrake = bike.rearBrakeType != null ? bike.rearBrakeType : "";
            frontSuspension = bike.frontSuspension != null ? bike.frontSuspension : "";
            rearSuspension = bike.rearSuspension != null ? bike.rearSuspension : "";
            tyreType = bike.tyreType != null ? bike.tyreType : "";
            instrumentCluster = bike.instrumentCluster != null ? bike.instrumentCluster : "";
            headlight = bike.headlight != null ? bike.headlight : "";
            tailLight = bike.tailLight != null ? bike.tailLight : "";
            batteryCapacity = bike.batteryCapacity != null ? bike.batteryCapacity : "";

            overallLength = bike.overallLength != null ? bike.overallLength : "";
            overallWidth = bike.overallWidth != null ? bike.overallWidth : "";
            weight = bike.kerbWeight != null ? bike.kerbWeight : "";
            fuelTank = bike.fuelTankCapacity != null ? bike.fuelTankCapacity : "";
            seatHeight = bike.seatHeight != null ? bike.seatHeight : "";
            clearance = bike.groundClearance != null ? bike.groundClearance : "";
        }

        // Setup the included Spec Fields
        setupSpecField(findViewById(R.id.fldEngine), "Engine Displacement", engine);
        setupSpecField(findViewById(R.id.fldPower), "Max Power", power);
        setupSpecField(findViewById(R.id.fldTorque), "Max Torque", torque);
        setupSpecField(findViewById(R.id.fldTopSpeed), "Top Speed", topSpeed);
        setupSpecField(findViewById(R.id.fldMileage), "Mileage", mileage);
        setupSpecField(findViewById(R.id.fldTransmission), "Transmission", transmission);

        setupSpecField(findViewById(R.id.fldBrakingSystem), "Braking System", brakingSystem);
        setupSpecField(findViewById(R.id.fldFrontBrake), "Front Brake", frontBrake);
        setupSpecField(findViewById(R.id.fldRearBrake), "Rear Brake", rearBrake);
        setupSpecField(findViewById(R.id.fldFrontSuspension), "Front Suspension", frontSuspension);
        setupSpecField(findViewById(R.id.fldRearSuspension), "Rear Suspension", rearSuspension);
        setupSpecField(findViewById(R.id.fldTyreType), "Tyre Type", tyreType);
        setupSpecField(findViewById(R.id.fldInstrumentCluster), "Instrument Cluster", instrumentCluster);
        setupSpecField(findViewById(R.id.fldHeadlight), "Headlight", headlight);
        setupSpecField(findViewById(R.id.fldTailLight), "Tail Light", tailLight);
        setupSpecField(findViewById(R.id.fldBatteryCapacity), "Battery Capacity", batteryCapacity);

        setupSpecField(findViewById(R.id.fldOverallLength), "Overall Length", overallLength);
        setupSpecField(findViewById(R.id.fldOverallWidth), "Overall Width", overallWidth);
        setupSpecField(findViewById(R.id.fldWeight), "Kerb Weight", weight);
        setupSpecField(findViewById(R.id.fldFuelTank), "Fuel Tank Capacity", fuelTank);
        setupSpecField(findViewById(R.id.fldSeatHeight), "Seat Height", seatHeight);
        setupSpecField(findViewById(R.id.fldClearance), "Ground Clearance", clearance);

        // Action Buttons
        Button btnDiscard = findViewById(R.id.btnDiscard);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        btnDiscard.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> {
            updateBike();
        });
    }

    private String getSpecFieldValue(int viewId) {
        View includeView = findViewById(viewId);
        if (includeView == null) return "";
        TextInputEditText etValue = includeView.findViewById(R.id.etValue);
        return etValue != null && etValue.getText() != null ? etValue.getText().toString() : "";
    }

    private void updateBike() {
        if (originalBikeName == null || originalBikeName.isEmpty()) {
            Toast.makeText(this, "Cannot update without original bike name", Toast.LENGTH_SHORT).show();
            return;
        }

        TextInputEditText etBikeName = findViewById(R.id.etBikeName);
        TextInputEditText etBrand = findViewById(R.id.etBrand);
        TextInputEditText etPrice = findViewById(R.id.etPrice);

        String name = etBikeName.getText() != null ? etBikeName.getText().toString() : "";
        String brand = etBrand.getText() != null ? etBrand.getText().toString() : "";
        String price = etPrice.getText() != null ? etPrice.getText().toString() : "";

        if (name.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("Model", name);
        payload.put("Brand", brand);
        payload.put("Price (₹)", price);

        payload.put("Displacement (cc)", getSpecFieldValue(R.id.fldEngine));
        payload.put("Max Power", getSpecFieldValue(R.id.fldPower));
        payload.put("Max Torque", getSpecFieldValue(R.id.fldTorque));
        payload.put("Top Speed (kmph)", getSpecFieldValue(R.id.fldTopSpeed));
        payload.put("Mileage (kmpl)", getSpecFieldValue(R.id.fldMileage));
        payload.put("Transmission", getSpecFieldValue(R.id.fldTransmission));

        payload.put("Braking System", getSpecFieldValue(R.id.fldBrakingSystem));
        payload.put("Front Brake", getSpecFieldValue(R.id.fldFrontBrake));
        payload.put("Rear Brake", getSpecFieldValue(R.id.fldRearBrake));
        payload.put("Front Suspension", getSpecFieldValue(R.id.fldFrontSuspension));
        payload.put("Rear Suspension", getSpecFieldValue(R.id.fldRearSuspension));
        payload.put("Tyre Type", getSpecFieldValue(R.id.fldTyreType));
        payload.put("Instrument Cluster", getSpecFieldValue(R.id.fldInstrumentCluster));
        payload.put("Headlight", getSpecFieldValue(R.id.fldHeadlight));
        payload.put("Tail Light", getSpecFieldValue(R.id.fldTailLight));
        payload.put("Battery Capacity (Ah)", getSpecFieldValue(R.id.fldBatteryCapacity));

        payload.put("Overall Length (mm)", getSpecFieldValue(R.id.fldOverallLength));
        payload.put("Overall Width (mm)", getSpecFieldValue(R.id.fldOverallWidth));
        payload.put("Kerb Weight (kg)", getSpecFieldValue(R.id.fldWeight));
        payload.put("Fuel Tank Capacity (L)", getSpecFieldValue(R.id.fldFuelTank));
        payload.put("Seat Height (mm)", getSpecFieldValue(R.id.fldSeatHeight));
        payload.put("Ground Clearance (mm)", getSpecFieldValue(R.id.fldClearance));

        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.updateBike(originalBikeName, "Token " + token, payload).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminEditBikeActivity.this, "Bike details updated successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AdminEditBikeActivity.this, "Failed to update bike", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(AdminEditBikeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpecField(View includeView, String label, String value) {
        if (includeView == null) {
            Toast.makeText(this, "Error: Include view is null for " + label, Toast.LENGTH_SHORT).show();
            return;
        }
        TextView tvLabel = includeView.findViewById(R.id.tvLabel);
        TextInputEditText etValue = includeView.findViewById(R.id.etValue);

        if (tvLabel != null) tvLabel.setText(label);
        if (etValue != null) etValue.setText(value);
    }

    private void setupStatusChips() {
        // Initial state: Active is selected
        selectChip(chipActive, "#A03020", "#FFFFFF"); // Dark Red bg, White text
        deselectChip(chipDiscontinued, "#F6D0C3", "#A03020"); // Light peach bg, Dark Red text

        chipActive.setOnClickListener(v -> {
            selectChip(chipActive, "#A03020", "#FFFFFF");
            deselectChip(chipDiscontinued, "#F6D0C3", "#A03020");
        });

        chipDiscontinued.setOnClickListener(v -> {
            deselectChip(chipActive, "#F5E6E6", "#A03020"); // Lighter red for unselected active
            selectChip(chipDiscontinued, "#A03020", "#FFFFFF"); // Dark Red bg for selected
        });
    }

    private void selectChip(TextView chip, String bgColor, String textColor) {
        chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(bgColor)));
        chip.setTextColor(Color.parseColor(textColor));
    }

    private void deselectChip(TextView chip, String bgColor, String textColor) {
        chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(bgColor)));
        chip.setTextColor(Color.parseColor(textColor));
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            } else if (which == 1) {
                // Gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            }
        });
        builder.show();
    }
}
