package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion6Activity extends AppCompatActivity {

    private View optHigh_q6, optMed_q6, optLow_q6;
    private ImageView ivHighTick_q6, ivMedTick_q6, ivLowTick_q6;
    private TextView btnNext_q6, btnBack_q6;
    private ProgressBar progressBar_q6;
    private TextView tvStep_q6;

    // selection
    private String fuelPriority = "high"; // default

    // previous answers (optional)
    private String vehicleFromPrev;
    private String rideWithPrev;
    private int budgetFromPrev = -1;
    private String usagePrev;
    private int distancePrev = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_question_6);

        // read incoming extras if any
        Intent inc = getIntent();
        if (inc != null) {
            vehicleFromPrev = inc.getStringExtra("vehicle_type");
            rideWithPrev = inc.getStringExtra("ride_with");
            usagePrev = inc.getStringExtra("usage");
            budgetFromPrev = inc.getIntExtra("budget", -1);
            distancePrev = inc.getIntExtra("distance_km", -1);
        }

        // top icons
        ImageView ivMenu = findViewById(R.id.ivMenu_q6);
        ImageView ivBell = findViewById(R.id.ivBell_q6);
        ImageView btnClose = findViewById(R.id.btnClose_q6);

        if (ivMenu != null) ivMenu.setOnClickListener(v -> startActivity(new Intent(AiQuestion6Activity.this, MenuActivity.class)));
        if (ivBell != null) ivBell.setOnClickListener(v -> Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        if (btnClose != null) btnClose.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion6Activity.this, HomeActivity.class);
            i.putExtra("step", 2);
            startActivity(i);
        });

        // find views
        optHigh_q6 = findViewById(R.id.optHigh_q6);
        optMed_q6 = findViewById(R.id.optMed_q6);
        optLow_q6 = findViewById(R.id.optLow_q6);

        ivHighTick_q6 = findViewById(R.id.ivHighTick_q6);
        ivMedTick_q6 = findViewById(R.id.ivMedTick_q6);
        ivLowTick_q6 = findViewById(R.id.ivLowTick_q6);

        btnNext_q6 = findViewById(R.id.btnNext_q6);
        btnBack_q6 = findViewById(R.id.btnBack_q6);

        progressBar_q6 = findViewById(R.id.progressBar_q6);
        tvStep_q6 = findViewById(R.id.tvStep_q6);

        // check required IDs are present
        String missing = "";
        if (optHigh_q6 == null) missing += " optHigh_q6";
        if (optMed_q6 == null) missing += " optMed_q6";
        if (optLow_q6 == null) missing += " optLow_q6";
        if (ivHighTick_q6 == null) missing += " ivHighTick_q6";
        if (ivMedTick_q6 == null) missing += " ivMedTick_q6";
        if (ivLowTick_q6 == null) missing += " ivLowTick_q6";
        if (btnNext_q6 == null) missing += " btnNext_q6";
        if (btnBack_q6 == null) missing += " btnBack_q6";
        if (progressBar_q6 == null) missing += " progressBar_q6";
        if (tvStep_q6 == null) missing += " tvStep_q6";

        if (!missing.isEmpty()) {
            Toast.makeText(this, "Missing IDs:" + missing, Toast.LENGTH_LONG).show();
            return;
        }

        // progress / step
        tvStep_q6.setText("6/9");
        progressBar_q6.setMax(9);
        progressBar_q6.setProgress(6);

        // default selection
        selectHigh();

        // click handlers
        optHigh_q6.setOnClickListener(v -> selectHigh());
        optMed_q6.setOnClickListener(v -> selectMed());
        optLow_q6.setOnClickListener(v -> selectLow());

        btnBack_q6.setOnClickListener(v -> finish());

        btnNext_q6.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion6Activity.this, AiQuestion7Activity.class);

            // pass selection
            i.putExtra("fuel_priority", fuelPriority);

            // pass previous answers forward
            if (vehicleFromPrev != null) i.putExtra("vehicle_type", vehicleFromPrev);
            if (rideWithPrev != null) i.putExtra("ride_with", rideWithPrev);
            if (usagePrev != null) i.putExtra("usage", usagePrev);
            if (distancePrev > 0) i.putExtra("distance_km", distancePrev);
            if (budgetFromPrev > 0) i.putExtra("budget", budgetFromPrev);

            startActivity(i);
            // forward slide animation (make sure anim files exist)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void selectHigh() {
        fuelPriority = "high";
        optHigh_q6.setBackgroundResource(R.drawable.option_selected_bg);
        optMed_q6.setBackgroundResource(R.drawable.option_bg);
        optLow_q6.setBackgroundResource(R.drawable.option_bg);
        ivHighTick_q6.setVisibility(View.VISIBLE);
        ivMedTick_q6.setVisibility(View.INVISIBLE);
        ivLowTick_q6.setVisibility(View.INVISIBLE);
    }

    private void selectMed() {
        fuelPriority = "medium";
        optMed_q6.setBackgroundResource(R.drawable.option_selected_bg);
        optHigh_q6.setBackgroundResource(R.drawable.option_bg);
        optLow_q6.setBackgroundResource(R.drawable.option_bg);
        ivMedTick_q6.setVisibility(View.VISIBLE);
        ivHighTick_q6.setVisibility(View.INVISIBLE);
        ivLowTick_q6.setVisibility(View.INVISIBLE);
    }

    private void selectLow() {
        fuelPriority = "low";
        optLow_q6.setBackgroundResource(R.drawable.option_selected_bg);
        optHigh_q6.setBackgroundResource(R.drawable.option_bg);
        optMed_q6.setBackgroundResource(R.drawable.option_bg);
        ivLowTick_q6.setVisibility(View.VISIBLE);
        ivHighTick_q6.setVisibility(View.INVISIBLE);
        ivMedTick_q6.setVisibility(View.INVISIBLE);
    }
}

