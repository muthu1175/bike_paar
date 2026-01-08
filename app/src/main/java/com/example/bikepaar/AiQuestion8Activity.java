package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion8Activity extends AppCompatActivity {

    private View optAny, optSport, optCruiser, optAdventure, optCommuter, optElectric;
    private ImageView ivAnyTick, ivSportTick, ivCruiserTick, ivAdventureTick, ivCommuterTick, ivElectricTick;
    private TextView btnNext, btnBack;
    private ProgressBar progressBar;
    private TextView tvStep;

    private String category = "Any Type"; // default

    // previous answers
    private String vehicleFromPrev;
    private String rideWithPrev;
    private int budgetFromPrev = -1;
    private String usagePrev;
    private int distancePrev = -1;
    private String fuelPriorityPrev;
    private String comfortPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_question_8);

        // receive previous extras
        Intent inc = getIntent();
        if (inc != null) {
            vehicleFromPrev = inc.getStringExtra("vehicle_type");
            rideWithPrev = inc.getStringExtra("ride_with");
            usagePrev = inc.getStringExtra("usage");
            budgetFromPrev = inc.getIntExtra("budget", -1);
            distancePrev = inc.getIntExtra("distance_km", -1);
            fuelPriorityPrev = inc.getStringExtra("fuel_priority");
            comfortPrev = inc.getStringExtra("comfort_pref");
        }

        // top icons
        ImageView ivMenu = findViewById(R.id.ivMenu_q8);
        ImageView ivBell = findViewById(R.id.ivBell_q8);
        ImageView btnClose = findViewById(R.id.btnClose_q8);

        if (ivMenu != null) ivMenu.setOnClickListener(v -> startActivity(new Intent(AiQuestion8Activity.this, MenuActivity.class)));
        if (ivBell != null) ivBell.setOnClickListener(v -> Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        if (btnClose != null) btnClose.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion8Activity.this, HomeActivity.class);
            i.putExtra("step", 2);
            startActivity(i);
        });

        // find option views
        optAny = findViewById(R.id.optAny);
        optSport = findViewById(R.id.optSport);
        optCruiser = findViewById(R.id.optCruiser);
        optAdventure = findViewById(R.id.optAdventure);
        optCommuter = findViewById(R.id.optCommuter);
        optElectric = findViewById(R.id.optElectric);

        ivAnyTick = findViewById(R.id.ivAnyTick);
        ivSportTick = findViewById(R.id.ivSportTick);
        ivCruiserTick = findViewById(R.id.ivCruiserTick);
        ivAdventureTick = findViewById(R.id.ivAdventureTick);
        ivCommuterTick = findViewById(R.id.ivCommuterTick);
        ivElectricTick = findViewById(R.id.ivElectricTick);

        btnNext = findViewById(R.id.btnNext_q8);
        btnBack = findViewById(R.id.btnBack_q8);

        progressBar = findViewById(R.id.progressBar_q8);
        tvStep = findViewById(R.id.tvStep_q8);

        // defensive null-check - report missing IDs
        String missing = "";
        if (optAny == null) missing += " optAny";
        if (optSport == null) missing += " optSport";
        if (optCruiser == null) missing += " optCruiser";
        if (optAdventure == null) missing += " optAdventure";
        if (optCommuter == null) missing += " optCommuter";
        if (optElectric == null) missing += " optElectric";
        if (ivAnyTick == null) missing += " ivAnyTick";
        if (ivSportTick == null) missing += " ivSportTick";
        if (ivCruiserTick == null) missing += " ivCruiserTick";
        if (ivAdventureTick == null) missing += " ivAdventureTick";
        if (ivCommuterTick == null) missing += " ivCommuterTick";
        if (ivElectricTick == null) missing += " ivElectricTick";
        if (btnNext == null) missing += " btnNext_q8";
        if (btnBack == null) missing += " btnBack_q8";
        if (progressBar == null) missing += " progressBar_q8";
        if (tvStep == null) missing += " tvStep_q8";

        if (!missing.isEmpty()) {
            Toast.makeText(this, "Missing IDs:" + missing, Toast.LENGTH_LONG).show();
            return;
        }

        // progress
        tvStep.setText("8/9");
        progressBar.setMax(9);
        progressBar.setProgress(8);

        // default selection
        selectCategoryAny();

        // clicks to choose
        optAny.setOnClickListener(v -> selectCategoryAny());
        optSport.setOnClickListener(v -> selectCategory("Sport bike", ivSportTick, optSport));
        optCruiser.setOnClickListener(v -> selectCategory("Cruiser", ivCruiserTick, optCruiser));
        optAdventure.setOnClickListener(v -> selectCategory("Adventure", ivAdventureTick, optAdventure));
        optCommuter.setOnClickListener(v -> selectCategory("Commuter", ivCommuterTick, optCommuter));
        optElectric.setOnClickListener(v -> selectCategory("Electric", ivElectricTick, optElectric));

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion8Activity.this, AiQuestion9Activity.class);

            // current selection
            i.putExtra("bike_category", category);

            if (vehicleFromPrev != null) i.putExtra("vehicle_type", vehicleFromPrev);
            if (rideWithPrev != null) i.putExtra("ride_with", rideWithPrev);
            if (usagePrev != null) i.putExtra("usage", usagePrev);
            if (comfortPrev != null) i.putExtra("comfort_pref", comfortPrev);
            if (fuelPriorityPrev != null) i.putExtra("fuel_priority", fuelPriorityPrev);
            if (distancePrev > 0) i.putExtra("distance_km", distancePrev);
            if (budgetFromPrev > 0) i.putExtra("budget", budgetFromPrev);


            try {
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (Exception ex) {
                Toast.makeText(AiQuestion8Activity.this, "Next AI screen not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectCategoryAny() {
        category = "Any Type";
        // set backgrounds
        optAny.setBackgroundResource(R.drawable.option_selected_bg);
        optSport.setBackgroundResource(R.drawable.option_bg);
        optCruiser.setBackgroundResource(R.drawable.option_bg);
        optAdventure.setBackgroundResource(R.drawable.option_bg);
        optCommuter.setBackgroundResource(R.drawable.option_bg);
        optElectric.setBackgroundResource(R.drawable.option_bg);
        // ticks
        ivAnyTick.setVisibility(View.VISIBLE);
        ivSportTick.setVisibility(View.INVISIBLE);
        ivCruiserTick.setVisibility(View.INVISIBLE);
        ivAdventureTick.setVisibility(View.INVISIBLE);
        ivCommuterTick.setVisibility(View.INVISIBLE);
        ivElectricTick.setVisibility(View.INVISIBLE);
    }

    private void selectCategory(String cat, ImageView tickToShow, View selectedView) {
        category = cat;
        // reset backgrounds
        optAny.setBackgroundResource(R.drawable.option_bg);
        optSport.setBackgroundResource(R.drawable.option_bg);
        optCruiser.setBackgroundResource(R.drawable.option_bg);
        optAdventure.setBackgroundResource(R.drawable.option_bg);
        optCommuter.setBackgroundResource(R.drawable.option_bg);
        optElectric.setBackgroundResource(R.drawable.option_bg);

        // highlight selected
        selectedView.setBackgroundResource(R.drawable.option_selected_bg);

        // ticks
        ivAnyTick.setVisibility(View.INVISIBLE);
        ivSportTick.setVisibility(View.INVISIBLE);
        ivCruiserTick.setVisibility(View.INVISIBLE);
        ivAdventureTick.setVisibility(View.INVISIBLE);
        ivCommuterTick.setVisibility(View.INVISIBLE);
        ivElectricTick.setVisibility(View.INVISIBLE);

        if (tickToShow != null) tickToShow.setVisibility(View.VISIBLE);
    }
}
