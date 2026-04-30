package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion3Activity extends AppCompatActivity {

    private View optSolo_q3, optFamily_q3;
    private ImageView ivSoloTick_q3, ivFamilyTick_q3;
    private TextView btnNext_q3, btnBack_q3;
    private ProgressBar progressBar_card;
    private TextView tvStep_card;
    private TextView tvPreviewBudget_q3;

    private String rideType = "Solo Ride"; // default
    private String vehicleFromPrev = null;
    private int budgetFromPrev = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvPreviewBudget_q3 = findViewById(R.id.tvPreviewBudget_q3);


        try {
            setContentView(R.layout.activity_ai_question_3);
        } catch (Exception e) {
            // If setContentView fails (layout inflate error) show toast & exit
            Toast.makeText(this, "Layout inflate error: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        // read extras passed from Q2
        Intent incoming = getIntent();
        if (incoming != null) {
            vehicleFromPrev = incoming.getStringExtra("vehicle_type");
            budgetFromPrev = incoming.getIntExtra("budget", -1);
        }

// 🔥 ADD EXACTLY HERE
        tvPreviewBudget_q3 = findViewById(R.id.tvPreviewBudget_q3);

        if (tvPreviewBudget_q3 != null && budgetFromPrev > 0) {
            tvPreviewBudget_q3.setText("Budget: ₹ " + budgetFromPrev);
        }


        // TOP BAR icons (if present)
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ImageView ivBell = findViewById(R.id.ivBell);
        ImageView btnClose_card = findViewById(R.id.btnClose_card);

        if (ivMenu != null) {
            ivMenu.setOnClickListener(v ->
                    startActivity(new Intent(AiQuestion3Activity.this, MenuActivity.class))
            );
        }
        if (ivBell != null) {
            ivBell.setOnClickListener(v -> {
                Intent intent = new Intent(AiQuestion3Activity.this, NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (btnClose_card != null) {
            btnClose_card.setOnClickListener(v -> {
                Intent i = new Intent(AiQuestion3Activity.this, HomeActivity.class);
                i.putExtra("step", 2);
                startActivity(i);
            });
        }

        // FIND main views (use defensive null-checks)
        optSolo_q3 = findViewById(R.id.optSolo_q3);
        optFamily_q3 = findViewById(R.id.optFamily_q3);

        ivSoloTick_q3 = findViewById(R.id.ivSoloTick_q3);
        ivFamilyTick_q3 = findViewById(R.id.ivFamilyTick_q3);

        btnNext_q3 = findViewById(R.id.btnNext_q3);
        btnBack_q3 = findViewById(R.id.btnBack_q3);

        progressBar_card = findViewById(R.id.progressBar_card);
        tvStep_card = findViewById(R.id.tvStep_card);



        // quick defensive checks
        String missing = "";
        if (optSolo_q3 == null) missing += " optSolo_q3";
        if (optFamily_q3 == null) missing += " optFamily_q3";
        if (ivSoloTick_q3 == null) missing += " ivSoloTick_q3";
        if (ivFamilyTick_q3 == null) missing += " ivFamilyTick_q3";
        if (btnNext_q3 == null) missing += " btnNext_q3";
        if (btnBack_q3 == null) missing += " btnBack_q3";
        if (progressBar_card == null) missing += " progressBar_card";
        if (tvStep_card == null) missing += " tvStep_card";

        if (!missing.isEmpty()) {
            Toast.makeText(this, "Missing IDs:" + missing, Toast.LENGTH_LONG).show();
            // don't continue further to avoid NPEs — but keep activity visible so you can debug
            return;
        }

        // set progress and step
        tvStep_card.setText("3/9");
        progressBar_card.setMax(9);
        progressBar_card.setProgress(3);


        // default selection (solo)
        selectSolo();

        // option clicks
        optSolo_q3.setOnClickListener(v -> selectSolo());
        optFamily_q3.setOnClickListener(v -> selectFamily());

        // back -> finish (return to Q2)
        btnBack_q3.setOnClickListener(v -> finish());

        // next -> go to Q4 (if exists). If Q4 doesn't exist yet, show a toast.
        btnNext_q3.setOnClickListener(v -> {
            // pass along collected info
            Intent i = new Intent(AiQuestion3Activity.this, AiQuestion4Activity.class);
            i.putExtra("ride_with", rideType);
            if (vehicleFromPrev != null) i.putExtra("vehicle_type", vehicleFromPrev);
            if (budgetFromPrev > 0) i.putExtra("budget", budgetFromPrev);
            try {
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (Exception ex) {
                // If AiQuestion4Activity not found, show toast so you know next step
                Toast.makeText(AiQuestion3Activity.this, "Next screen not available yet", Toast.LENGTH_SHORT).show();
            }
        });

        // done
    }

    private void selectSolo() {
        rideType = "Solo Ride";
        if (optSolo_q3 != null) optSolo_q3.setBackgroundResource(R.drawable.option_selected_bg);
        if (optFamily_q3 != null) optFamily_q3.setBackgroundResource(R.drawable.option_bg);
        if (ivSoloTick_q3 != null) ivSoloTick_q3.setVisibility(View.VISIBLE);
        if (ivFamilyTick_q3 != null) ivFamilyTick_q3.setVisibility(View.INVISIBLE);
    }

    private void selectFamily() {
        rideType = "Family";
        if (optFamily_q3 != null) optFamily_q3.setBackgroundResource(R.drawable.option_selected_bg);
        if (optSolo_q3 != null) optSolo_q3.setBackgroundResource(R.drawable.option_bg);
        if (ivFamilyTick_q3 != null) ivFamilyTick_q3.setVisibility(View.VISIBLE);
        if (ivSoloTick_q3 != null) ivSoloTick_q3.setVisibility(View.INVISIBLE);
    }
}
