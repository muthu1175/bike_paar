package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion4Activity extends AppCompatActivity {

    private View optDaily_q4, optAdventure_q4, optSports_q4;
    private ImageView ivDailyTick_q4, ivAdventureTick_q4, ivSportsTick_q4;
    private TextView btnNext_q4, btnBack_q4;
    private ProgressBar progressBar_card_q4;
    private TextView tvStep_card_q4;

    private String usage = "Daily use"; // default
    private String vehicleFromPrev = null;
    private String rideWithPrev = null;
    private int budgetFromPrev = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_ai_question_4);
        } catch (Exception e) {
            Toast.makeText(this, "Layout inflate error: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        // read extras passed from previous screens (if any)
        Intent incoming = getIntent();
        if (incoming != null) {
            vehicleFromPrev = incoming.getStringExtra("vehicle_type");
            rideWithPrev = incoming.getStringExtra("ride_with");
            budgetFromPrev = incoming.getIntExtra("budget", -1);
        }

        // top bar icons
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ImageView ivBell = findViewById(R.id.ivBell);
        ImageView btnClose = findViewById(R.id.btnClose_card_q4);

        if (ivMenu != null) {
            ivMenu.setOnClickListener(v ->
                    startActivity(new Intent(AiQuestion4Activity.this, MenuActivity.class))
            );
        }
        if (ivBell != null) {
            ivBell.setOnClickListener(v -> {
                Intent intent = new Intent(AiQuestion4Activity.this, NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (btnClose != null) btnClose.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion4Activity.this, HomeActivity.class);
            i.putExtra("step", 2);
            startActivity(i);
        });

        // find views
        optDaily_q4 = findViewById(R.id.optDaily_q4);
        optAdventure_q4 = findViewById(R.id.optAdventure_q4);
        optSports_q4 = findViewById(R.id.optSports_q4);

        ivDailyTick_q4 = findViewById(R.id.ivDailyTick_q4);
        ivAdventureTick_q4 = findViewById(R.id.ivAdventureTick_q4);
        ivSportsTick_q4 = findViewById(R.id.ivSportsTick_q4);

        btnNext_q4 = findViewById(R.id.btnNext_q4);
        btnBack_q4 = findViewById(R.id.btnBack_q4);

        progressBar_card_q4 = findViewById(R.id.progressBar_card_q4);
        tvStep_card_q4 = findViewById(R.id.tvStep_card_q4);

        // defensive missing-id check
        String missing = "";
        if (optDaily_q4 == null) missing += " optDaily_q4";
        if (optAdventure_q4 == null) missing += " optAdventure_q4";
        if (optSports_q4 == null) missing += " optSports_q4";
        if (ivDailyTick_q4 == null) missing += " ivDailyTick_q4";
        if (ivAdventureTick_q4 == null) missing += " ivAdventureTick_q4";
        if (ivSportsTick_q4 == null) missing += " ivSportsTick_q4";
        if (btnNext_q4 == null) missing += " btnNext_q4";
        if (btnBack_q4 == null) missing += " btnBack_q4";
        if (progressBar_card_q4 == null) missing += " progressBar_card_q4";
        if (tvStep_card_q4 == null) missing += " tvStep_card_q4";

        if (!missing.isEmpty()) {
            Toast.makeText(this, "Missing IDs:" + missing, Toast.LENGTH_LONG).show();
            return;
        }

        // set progress state
        tvStep_card_q4.setText("4/9");
        progressBar_card_q4.setMax(9);
        progressBar_card_q4.setProgress(4);

        // default selection
        selectDaily();

        // clicks
        optDaily_q4.setOnClickListener(v -> selectDaily());
        optAdventure_q4.setOnClickListener(v -> selectAdventure());
        optSports_q4.setOnClickListener(v -> selectSports());

        btnBack_q4.setOnClickListener(v -> finish());

        btnNext_q4.setOnClickListener(v -> {
            // prepare Intent to Q5
            Intent i = new Intent(AiQuestion4Activity.this, AiQuestion5Activity.class);
            i.putExtra("primary_usage", usage);
            i.putExtra("vehicle_type", vehicleFromPrev);
            i.putExtra("ride_with", rideWithPrev);
            i.putExtra("budget", budgetFromPrev);
            startActivity(i);


            // forward animation (optional) — requires res/anim/slide_in_right & slide_out_left
            // if you don't have anim files, remove the next line
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    private void selectDaily() {
        usage = "Daily use";
        if (optDaily_q4 != null) optDaily_q4.setBackgroundResource(R.drawable.option_selected_bg);
        if (optAdventure_q4 != null) optAdventure_q4.setBackgroundResource(R.drawable.option_bg);
        if (optSports_q4 != null) optSports_q4.setBackgroundResource(R.drawable.option_bg);

        if (ivDailyTick_q4 != null) ivDailyTick_q4.setVisibility(View.VISIBLE);
        if (ivAdventureTick_q4 != null) ivAdventureTick_q4.setVisibility(View.INVISIBLE);
        if (ivSportsTick_q4 != null) ivSportsTick_q4.setVisibility(View.INVISIBLE);
    }

    private void selectAdventure() {
        usage = "Adventure";
        if (optAdventure_q4 != null) optAdventure_q4.setBackgroundResource(R.drawable.option_selected_bg);
        if (optDaily_q4 != null) optDaily_q4.setBackgroundResource(R.drawable.option_bg);
        if (optSports_q4 != null) optSports_q4.setBackgroundResource(R.drawable.option_bg);

        if (ivAdventureTick_q4 != null) ivAdventureTick_q4.setVisibility(View.VISIBLE);
        if (ivDailyTick_q4 != null) ivDailyTick_q4.setVisibility(View.INVISIBLE);
        if (ivSportsTick_q4 != null) ivSportsTick_q4.setVisibility(View.INVISIBLE);
    }

    private void selectSports() {
        usage = "Sports";
        if (optSports_q4 != null) optSports_q4.setBackgroundResource(R.drawable.option_selected_bg);
        if (optDaily_q4 != null) optDaily_q4.setBackgroundResource(R.drawable.option_bg);
        if (optAdventure_q4 != null) optAdventure_q4.setBackgroundResource(R.drawable.option_bg);

        if (ivSportsTick_q4 != null) ivSportsTick_q4.setVisibility(View.VISIBLE);
        if (ivDailyTick_q4 != null) ivDailyTick_q4.setVisibility(View.INVISIBLE);
        if (ivAdventureTick_q4 != null) ivAdventureTick_q4.setVisibility(View.INVISIBLE);
    }
}
