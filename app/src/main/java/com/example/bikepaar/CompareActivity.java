package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

public class CompareActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnCompareNow, btnDisabledCompare;
    private CardView addBike1, addBike2, addBike3;
    private CardView compareCard1, compareCard2, compareCard3;

    // New views for selection state
    private LinearLayout layoutAddBike1, layoutAddBike2, layoutAddBike3;
    private LinearLayout layoutSelectedBike1, layoutSelectedBike2, layoutSelectedBike3;
    private ImageView ivSelectedBike1, ivSelectedBike2, ivSelectedBike3;
    private TextView tvSelectedBike1, tvSelectedBike2, tvSelectedBike3;

    private Bike[] selectedBikes = new Bike[3];
    private static final int REQ_CODE_BIKE_1 = 101;
    private static final int REQ_CODE_BIKE_2 = 102;
    private static final int REQ_CODE_BIKE_3 = 103;

    private ImageButton btnRemoveBike1, btnRemoveBike2, btnRemoveBike3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnDisabledCompare = findViewById(R.id.btnDisabledCompare);

        addBike1 = findViewById(R.id.addBike1);
        addBike2 = findViewById(R.id.addBike2);
        addBike3 = findViewById(R.id.addBike3);

        // Initialize Selection Views
        layoutAddBike1 = findViewById(R.id.layoutAddBike1);
        layoutAddBike2 = findViewById(R.id.layoutAddBike2);
        layoutAddBike3 = findViewById(R.id.layoutAddBike3);

        layoutSelectedBike1 = findViewById(R.id.layoutSelectedBike1);
        layoutSelectedBike2 = findViewById(R.id.layoutSelectedBike2);
        layoutSelectedBike3 = findViewById(R.id.layoutSelectedBike3);

        ivSelectedBike1 = findViewById(R.id.ivSelectedBike1);
        ivSelectedBike2 = findViewById(R.id.ivSelectedBike2);
        ivSelectedBike3 = findViewById(R.id.ivSelectedBike3);

        tvSelectedBike1 = findViewById(R.id.tvSelectedBike1);
        tvSelectedBike2 = findViewById(R.id.tvSelectedBike2);
        tvSelectedBike3 = findViewById(R.id.tvSelectedBike3);

        btnRemoveBike1 = findViewById(R.id.btnRemoveBike1);
        btnRemoveBike2 = findViewById(R.id.btnRemoveBike2);
        btnRemoveBike3 = findViewById(R.id.btnRemoveBike3);
//
//        compareCard1 = findViewById(R.id.compareCard1);
//        compareCard2 = findViewById(R.id.compareCard2);
//        compareCard3 = findViewById(R.id.compareCard3);
//
//        btnCompareNow = findViewById(R.id.btnCompareNow1);
//        Button btnCompareNow2 = findViewById(R.id.btnCompareNow2);
//        Button btnCompareNow3 = findViewById(R.id.btnCompareNow3);

        // Initial state check
        updateCompareButtonState();

        // Back button click
        btnBack.setOnClickListener(v -> onBackPressed());

        // Add Bike buttons
        addBike1.setOnClickListener(v -> openSearchForBike(REQ_CODE_BIKE_1));
        addBike2.setOnClickListener(v -> openSearchForBike(REQ_CODE_BIKE_2));
        addBike3.setOnClickListener(v -> openSearchForBike(REQ_CODE_BIKE_3));

        // Remove Bike buttons
        btnRemoveBike1.setOnClickListener(v -> removeBike(0));
        btnRemoveBike2.setOnClickListener(v -> removeBike(1));
        btnRemoveBike3.setOnClickListener(v -> removeBike(2));

//        // Compare Now buttons for popular compares
//        btnCompareNow.setOnClickListener(v -> navigateToResults(null, null, null)); // Placeholder bikes
//        btnCompareNow2.setOnClickListener(v -> navigateToResults(null, null, null));
//        btnCompareNow3.setOnClickListener(v -> navigateToResults(null, null, null));

        // Disabled Compare Now button
        btnDisabledCompare.setOnClickListener(v -> updateCompareButtonState());

//        // Compare cards
//        compareCard1.setOnClickListener(v -> navigateToResults(null, null, null));
//        compareCard2.setOnClickListener(v -> navigateToResults(null, null, null));
//        compareCard3.setOnClickListener(v -> navigateToResults(null, null, null));
    }

    private void navigateToResults(Bike b1, Bike b2, Bike b3) {
        Intent intent = new Intent(CompareActivity.this, CompareResultsActivity.class);
        intent.putExtra("BIKE_1", b1);
        intent.putExtra("BIKE_2", b2);
        intent.putExtra("BIKE_3", b3);
        Bundle options = android.app.ActivityOptions.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(intent, options);
    }

    private void removeBike(int index) {
        selectedBikes[index] = null;
        updateBikeSlotUI(index);
        updateCompareButtonState();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openSearchForBike(int requestCode) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("IS_SELECTION_MODE", true);
        Bundle options = android.app.ActivityOptions.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivityForResult(intent, requestCode, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bike bike = (Bike) data.getSerializableExtra("SELECTED_BIKE");
            if (bike != null) {
                if (requestCode == REQ_CODE_BIKE_1) {
                    selectedBikes[0] = bike;
                    updateBikeSlotUI(0);
                } else if (requestCode == REQ_CODE_BIKE_2) {
                    selectedBikes[1] = bike;
                    updateBikeSlotUI(1);
                } else if (requestCode == REQ_CODE_BIKE_3) {
                    selectedBikes[2] = bike;
                    updateBikeSlotUI(2);
                }
                updateCompareButtonState();
            }
        }
    }

    private void updateBikeSlotUI(int index) {
        LinearLayout layoutAdd, layoutSelected;
        ImageView iv;
        TextView tv;
        ImageButton btnRemove;
        Bike bike = selectedBikes[index];

        if (index == 0) {
            layoutAdd = layoutAddBike1;
            layoutSelected = layoutSelectedBike1;
            iv = ivSelectedBike1;
            tv = tvSelectedBike1;
            btnRemove = btnRemoveBike1;
        } else if (index == 1) {
            layoutAdd = layoutAddBike2;
            layoutSelected = layoutSelectedBike2;
            iv = ivSelectedBike2;
            tv = tvSelectedBike2;
            btnRemove = btnRemoveBike2;
        } else {
            layoutAdd = layoutAddBike3;
            layoutSelected = layoutSelectedBike3;
            iv = ivSelectedBike3;
            tv = tvSelectedBike3;
            btnRemove = btnRemoveBike3;
        }

        if (bike != null) {
            layoutAdd.setVisibility(View.GONE);
            layoutSelected.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.VISIBLE);
            tv.setText(bike.name);

            if (bike.imageUrl != null && !bike.imageUrl.isEmpty()) {
                Glide.with(this).load(bike.imageUrl)
                        .placeholder(R.drawable.sample_bike).into(iv);
            } else if (bike.imageRes != 0) {
                iv.setImageResource(bike.imageRes);
            } else {
                iv.setImageResource(R.drawable.sample_bike);
            }
        } else {
            layoutAdd.setVisibility(View.VISIBLE);
            layoutSelected.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
        }
    }

    private void updateCompareButtonState() {
        int count = 0;
        for (Bike b : selectedBikes) if (b != null) count++;

        boolean canCompare = count >= 2;

        if (canCompare) {
            btnDisabledCompare.setTextColor(android.graphics.Color.WHITE);
            btnDisabledCompare.setBackgroundColor(android.graphics.Color.parseColor("#EA580C")); // Orange
            btnDisabledCompare.setText("COMPARE NOW (" + count + ")");

            btnDisabledCompare.setOnClickListener(v -> {
                Intent intent = new Intent(CompareActivity.this, CompareResultsActivity.class);
                intent.putExtra("BIKE_1", selectedBikes[0]);
                intent.putExtra("BIKE_2", selectedBikes[1]);
                intent.putExtra("BIKE_3", selectedBikes[2]);

                Bundle options = android.app.ActivityOptions.makeCustomAnimation(CompareActivity.this,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent, options);
            });

        } else {
            btnDisabledCompare.setTextColor(android.graphics.Color.parseColor("#9CA3AF"));
            btnDisabledCompare.setBackgroundColor(android.graphics.Color.parseColor("#E5E7EB"));
            btnDisabledCompare.setText("Compare Now");
            btnDisabledCompare.setOnClickListener(v ->
                    Toast.makeText(CompareActivity.this, "Please add at least 2 bikes to compare", Toast.LENGTH_SHORT).show()
            );
        }
    }
}