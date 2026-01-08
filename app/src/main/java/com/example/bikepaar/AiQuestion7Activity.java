package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion7Activity extends AppCompatActivity {

    private View optHighComfort, optMediumComfort, optSporty;
    private ImageView ivHighTick, ivMedTick, ivSportTick;
    private TextView btnNext, btnBack;
    private ProgressBar progressBar;
    private TextView tvStep;

    // selection
    private String comfortPref = "high_comfort";

    // previous answers (optional)
    private String vehicleFromPrev;
    private String rideWithPrev;
    private int budgetFromPrev = -1;
    private String usagePrev;
    private int distancePrev = -1;
    private String fuelPriorityPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_question_7);

        // read incoming extras if present
        Intent inc = getIntent();
        if (inc != null) {
            vehicleFromPrev = inc.getStringExtra("vehicle_type");
            rideWithPrev = inc.getStringExtra("ride_with");
            usagePrev = inc.getStringExtra("usage");
            budgetFromPrev = inc.getIntExtra("budget", -1);
            distancePrev = inc.getIntExtra("distance_km", -1);
            fuelPriorityPrev = inc.getStringExtra("fuel_priority");
        }

        // top icons
        ImageView ivMenu = findViewById(R.id.ivMenu_q7);
        ImageView ivBell = findViewById(R.id.ivBell_q7);
        ImageView btnClose = findViewById(R.id.btnClose_q7);

        if (ivMenu != null) ivMenu.setOnClickListener(v -> startActivity(new Intent(AiQuestion7Activity.this, MenuActivity.class)));
        if (ivBell != null) ivBell.setOnClickListener(v -> Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        if (btnClose != null) btnClose.setOnClickListener(v ->{
            Intent i = new Intent(AiQuestion7Activity.this, HomeActivity.class);
            i.putExtra("step", 2);
            startActivity(i);
        });

        // find views
        optHighComfort = findViewById(R.id.optHighComfort);
        optMediumComfort = findViewById(R.id.optMediumComfort);
        optSporty = findViewById(R.id.optSporty);

        ivHighTick = findViewById(R.id.ivHighTick);
        ivMedTick = findViewById(R.id.ivMedTick);
        ivSportTick = findViewById(R.id.ivSportTick);

        btnNext = findViewById(R.id.btnNext_q7);
        btnBack = findViewById(R.id.btnBack_q7);

        progressBar = findViewById(R.id.progressBar_q7);
        tvStep = findViewById(R.id.tvStep_q7);

        // defensive check
        String missing = "";
        if (optHighComfort == null) missing += " optHighComfort";
        if (optMediumComfort == null) missing += " optMediumComfort";
        if (optSporty == null) missing += " optSporty";
        if (ivHighTick == null) missing += " ivHighTick";
        if (ivMedTick == null) missing += " ivMedTick";
        if (ivSportTick == null) missing += " ivSportTick";
        if (btnNext == null) missing += " btnNext_q7";
        if (btnBack == null) missing += " btnBack_q7";
        if (progressBar == null) missing += " progressBar_q7";
        if (tvStep == null) missing += " tvStep_q7";

        if (!missing.isEmpty()) {
            Toast.makeText(this, "Missing IDs:" + missing, Toast.LENGTH_LONG).show();
            return;
        }

        // progress / step
        tvStep.setText("7/9");
        progressBar.setMax(9);
        progressBar.setProgress(7);

        // default selection
        selectHighComfort();

        // clicks
        optHighComfort.setOnClickListener(v -> selectHighComfort());
        optMediumComfort.setOnClickListener(v -> selectMediumComfort());
        optSporty.setOnClickListener(v -> selectSporty());

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion7Activity.this, AiQuestion8Activity.class);

            i.putExtra("comfort_pref", comfortPref);

            if (vehicleFromPrev != null) i.putExtra("vehicle_type", vehicleFromPrev);
            if (rideWithPrev != null) i.putExtra("ride_with", rideWithPrev);
            if (usagePrev != null) i.putExtra("usage", usagePrev);
            if (fuelPriorityPrev != null) i.putExtra("fuel_priority", fuelPriorityPrev);
            if (distancePrev > 0) i.putExtra("distance_km", distancePrev);
            if (budgetFromPrev > 0) i.putExtra("budget", budgetFromPrev);

            startActivity(i);

            // keep animations consistent with your other screens (ensure anim exists or remove)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void selectHighComfort() {
        comfortPref = "high_comfort";
        optHighComfort.setBackgroundResource(R.drawable.option_selected_bg);
        optMediumComfort.setBackgroundResource(R.drawable.option_bg);
        optSporty.setBackgroundResource(R.drawable.option_bg);
        ivHighTick.setVisibility(View.VISIBLE);
        ivMedTick.setVisibility(View.INVISIBLE);
        ivSportTick.setVisibility(View.INVISIBLE);
    }

    private void selectMediumComfort() {
        comfortPref = "medium_comfort";
        optMediumComfort.setBackgroundResource(R.drawable.option_selected_bg);
        optHighComfort.setBackgroundResource(R.drawable.option_bg);
        optSporty.setBackgroundResource(R.drawable.option_bg);
        ivMedTick.setVisibility(View.VISIBLE);
        ivHighTick.setVisibility(View.INVISIBLE);
        ivSportTick.setVisibility(View.INVISIBLE);
    }

    private void selectSporty() {
        comfortPref = "sporty";
        optSporty.setBackgroundResource(R.drawable.option_selected_bg);
        optHighComfort.setBackgroundResource(R.drawable.option_bg);
        optMediumComfort.setBackgroundResource(R.drawable.option_bg);
        ivSportTick.setVisibility(View.VISIBLE);
        ivHighTick.setVisibility(View.INVISIBLE);
        ivMedTick.setVisibility(View.INVISIBLE);
    }
}
