package com.example.bikepaar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class CompareResultsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    
    // Bike Data
    private Bike[] bikes = new Bike[3];

    // Header Views
    private ImageView[] ivBikes = new ImageView[3];
    private TextView[] tvNames = new TextView[3];
    private TextView[] tvPrices = new TextView[3];

    // Spec Views
    // Engine Views
    private TextView[] tvDisplacements = new TextView[3];
    private TextView[] tvPowers = new TextView[3];
    private TextView[] tvTorques = new TextView[3];
    private TextView[] tvMileages = new TextView[3];
    private TextView[] tvTopSpeeds = new TextView[3];
    
    // Feature Views
    private TextView[] tvBraking = new TextView[3];
    private TextView[] tvHeadlights = new TextView[3];
    private TextView[] tvConsoles = new TextView[3];
    private TextView[] tvTransmissions = new TextView[3];
    
    // Dimension Views 
    private TextView[] tvKerbWeights = new TextView[3];
    private TextView[] tvFuelTanks = new TextView[3];
    private TextView[] tvSeatHeights = new TextView[3];
    private TextView[] tvTyreTypes = new TextView[3];

    // Brakes & Suspension
    private TextView[] tvFrontBrakes = new TextView[3];
    private TextView[] tvRearBrakes = new TextView[3];
    private TextView[] tvFrontSuspensions = new TextView[3];
    private TextView[] tvRearSuspensions = new TextView[3];

    // Price Section
    private View[] layoutPrices = new View[3];
    private TextView[] tvPriceNames = new TextView[3];
    private TextView[] tvPriceValues = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_results);

        // Get Bikes from Intent
        bikes[0] = (Bike) getIntent().getSerializableExtra("BIKE_1");
        bikes[1] = (Bike) getIntent().getSerializableExtra("BIKE_2");
        bikes[2] = (Bike) getIntent().getSerializableExtra("BIKE_3");

        // Initialize Common Views
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Initialize View Arrays
        initViews();

        // Populate and Hide Columns
        populateData();
    }

    private void initViews() {
        // Headers
        ivBikes[0] = findViewById(R.id.ivBike1);
        ivBikes[1] = findViewById(R.id.ivBike2);
        ivBikes[2] = findViewById(R.id.ivBike3);

        tvNames[0] = findViewById(R.id.tvBike1Name);
        tvNames[1] = findViewById(R.id.tvBike2Name);
        tvNames[2] = findViewById(R.id.tvBike3Name);

        tvPrices[0] = findViewById(R.id.tvBike1Price);
        tvPrices[1] = findViewById(R.id.tvBike2Price);
        tvPrices[2] = findViewById(R.id.tvBike3Price);

        // Engine
        tvDisplacements[0] = findViewById(R.id.tvDisplacement1);
        tvDisplacements[1] = findViewById(R.id.tvDisplacement2);
        tvDisplacements[2] = findViewById(R.id.tvDisplacement3);

        tvPowers[0] = findViewById(R.id.tvMaxPower1);
        tvPowers[1] = findViewById(R.id.tvMaxPower2);
        tvPowers[2] = findViewById(R.id.tvMaxPower3);

        tvTorques[0] = findViewById(R.id.tvPeakTorque1);
        tvTorques[1] = findViewById(R.id.tvPeakTorque2);
        tvTorques[2] = findViewById(R.id.tvPeakTorque3);

        tvMileages[0] = findViewById(R.id.tvMileage1);
        tvMileages[1] = findViewById(R.id.tvMileage2);
        tvMileages[2] = findViewById(R.id.tvMileage3);

        tvTopSpeeds[0] = findViewById(R.id.tvTopSpeed1);
        tvTopSpeeds[1] = findViewById(R.id.tvTopSpeed2);
        tvTopSpeeds[2] = findViewById(R.id.tvTopSpeed3);
        
        // Features
        tvBraking[0] = findViewById(R.id.tvBraking1);
        tvBraking[1] = findViewById(R.id.tvBraking2);
        tvBraking[2] = findViewById(R.id.tvBraking3);
        
        tvHeadlights[0] = findViewById(R.id.tvHeadlight1);
        tvHeadlights[1] = findViewById(R.id.tvHeadlight2);
        tvHeadlights[2] = findViewById(R.id.tvHeadlight3);
        
        tvConsoles[0] = findViewById(R.id.tvConsole1);
        tvConsoles[1] = findViewById(R.id.tvConsole2);
        tvConsoles[2] = findViewById(R.id.tvConsole3);

        tvTransmissions[0] = findViewById(R.id.tvTransmission1);
        tvTransmissions[1] = findViewById(R.id.tvTransmission2);
        tvTransmissions[2] = findViewById(R.id.tvTransmission3);
        
        // Dimensions
        tvKerbWeights[0] = findViewById(R.id.tvKerbWeight1);
        tvKerbWeights[1] = findViewById(R.id.tvKerbWeight2);
        tvKerbWeights[2] = findViewById(R.id.tvKerbWeight3);
        
        tvFuelTanks[0] = findViewById(R.id.tvFuelTank1);
        tvFuelTanks[1] = findViewById(R.id.tvFuelTank2);
        tvFuelTanks[2] = findViewById(R.id.tvFuelTank3);
        
        tvSeatHeights[0] = findViewById(R.id.tvSeatHeight1);
        tvSeatHeights[1] = findViewById(R.id.tvSeatHeight2);
        tvSeatHeights[2] = findViewById(R.id.tvSeatHeight3);

        tvTyreTypes[0] = findViewById(R.id.tvTyreType1);
        tvTyreTypes[1] = findViewById(R.id.tvTyreType2);
        tvTyreTypes[2] = findViewById(R.id.tvTyreType3);

        // Brakes & Suspension
        tvFrontBrakes[0] = findViewById(R.id.tvFrontBrake1);
        tvFrontBrakes[1] = findViewById(R.id.tvFrontBrake2);
        tvFrontBrakes[2] = findViewById(R.id.tvFrontBrake3);

        tvRearBrakes[0] = findViewById(R.id.tvRearBrake1);
        tvRearBrakes[1] = findViewById(R.id.tvRearBrake2);
        tvRearBrakes[2] = findViewById(R.id.tvRearBrake3);

        tvFrontSuspensions[0] = findViewById(R.id.tvFrontSuspension1);
        tvFrontSuspensions[1] = findViewById(R.id.tvFrontSuspension2);
        tvFrontSuspensions[2] = findViewById(R.id.tvFrontSuspension3);

        tvRearSuspensions[0] = findViewById(R.id.tvRearSuspension1);
        tvRearSuspensions[1] = findViewById(R.id.tvRearSuspension2);
        tvRearSuspensions[2] = findViewById(R.id.tvRearSuspension3);

        // Price Section
        layoutPrices[0] = findViewById(R.id.layoutPrice1);
        layoutPrices[1] = findViewById(R.id.layoutPrice2);
        layoutPrices[2] = findViewById(R.id.layoutPrice3);

        tvPriceNames[0] = findViewById(R.id.tvPriceName1);
        tvPriceNames[1] = findViewById(R.id.tvPriceName2);
        tvPriceNames[2] = findViewById(R.id.tvPriceName3);

        tvPriceValues[0] = findViewById(R.id.tvPriceValue1);
        tvPriceValues[1] = findViewById(R.id.tvPriceValue2);
        tvPriceValues[2] = findViewById(R.id.tvPriceValue3);
    }

    private void populateData() {
        for (int i = 0; i < 3; i++) {
            Bike b = bikes[i];
            
            if (b == null) {
                setVisibility(i, View.GONE);
            } else {
                setVisibility(i, View.VISIBLE);
                
                // Populate Header
                tvNames[i].setText(b.name);
                tvPrices[i].setText(String.format("₹ %,d", b.price));
                
                if (b.imageUrl != null && !b.imageUrl.isEmpty()) {
                     Glide.with(this).load(b.imageUrl).placeholder(R.drawable.sample_bike).into(ivBikes[i]);
                } else {
                     ivBikes[i].setImageResource(R.drawable.sample_bike);
                }
                
                // Populate Specs
                setText(tvDisplacements[i], b.engine);
                setText(tvPowers[i], b.getMaxPower());
                setText(tvTorques[i], b.getMaxTorque());
                setText(tvMileages[i], b.getMileage());
                setText(tvTopSpeeds[i], b.getTopSpeed());
                
                setText(tvBraking[i], b.getBrakingSystem());
                setText(tvHeadlights[i], b.getHeadlight());
                setText(tvConsoles[i], "-"); 
                setText(tvTransmissions[i], b.getTransmission());

                setText(tvKerbWeights[i], b.getKerbWeight());
                setText(tvFuelTanks[i], b.getFuelTankCapacity());
                setText(tvSeatHeights[i], "-");
                setText(tvTyreTypes[i], b.getTyreType());

                setText(tvFrontBrakes[i], b.getFrontBrakeType());
                setText(tvRearBrakes[i], b.getRearBrakeType());
                setText(tvFrontSuspensions[i], b.getFrontSuspension());
                setText(tvRearSuspensions[i], b.getRearSuspension());

                // Price Section
                setText(tvPriceNames[i], b.name);
                setText(tvPriceValues[i], String.format("₹ %,d", b.price));
            }
        }
    }
    
    private void setText(TextView tv, String text) {
        if (tv == null) return;
        tv.setText((text != null && !text.isEmpty()) ? text : "-");
    }

    private void setVisibility(int index, int visibility) {
        if (ivBikes[index] != null) ivBikes[index].setVisibility(visibility);
        if (tvNames[index] != null) tvNames[index].setVisibility(visibility);
        if (tvPrices[index] != null) tvPrices[index].setVisibility(visibility);
        
        if (tvDisplacements[index] != null) tvDisplacements[index].setVisibility(visibility);
        if (tvPowers[index] != null) tvPowers[index].setVisibility(visibility);
        if (tvTorques[index] != null) tvTorques[index].setVisibility(visibility);
        if (tvMileages[index] != null) tvMileages[index].setVisibility(visibility);
        if (tvTopSpeeds[index] != null) tvTopSpeeds[index].setVisibility(visibility);
        
        if (tvBraking[index] != null) tvBraking[index].setVisibility(visibility);
        if (tvHeadlights[index] != null) tvHeadlights[index].setVisibility(visibility);
        if (tvConsoles[index] != null) tvConsoles[index].setVisibility(visibility);
        if (tvTransmissions[index] != null) tvTransmissions[index].setVisibility(visibility);
        
        if (tvKerbWeights[index] != null) tvKerbWeights[index].setVisibility(visibility);
        if (tvFuelTanks[index] != null) tvFuelTanks[index].setVisibility(visibility);
        if (tvSeatHeights[index] != null) tvSeatHeights[index].setVisibility(visibility);
        if (tvTyreTypes[index] != null) tvTyreTypes[index].setVisibility(visibility);

        if (tvFrontBrakes[index] != null) tvFrontBrakes[index].setVisibility(visibility);
        if (tvRearBrakes[index] != null) tvRearBrakes[index].setVisibility(visibility);
        if (tvFrontSuspensions[index] != null) tvFrontSuspensions[index].setVisibility(visibility);
        if (tvRearSuspensions[index] != null) tvRearSuspensions[index].setVisibility(visibility);

        if (layoutPrices[index] != null) layoutPrices[index].setVisibility(visibility);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}