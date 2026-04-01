package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion9Activity extends AppCompatActivity {

    private View optBeginner, optIntermediate, optExpert;
    private ImageView ivBeginnerTick, ivIntermediateTick, ivExpertTick;
    private TextView btnBack9, btnAiSuggest9;
    private ProgressBar progressBar9;
    private TextView tvStep9;

    // selection
    private String experience = "Beginner"; // default

    // optional previous answers passed in
    private String vehicleType, rideWith, usage;
    private int budget = -1;
    private int distanceKm = -1;
    private String fuelPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_question_9);

        // read previous extras (if any)
        Intent inc = getIntent();
        if (inc != null) {
            vehicleType = inc.getStringExtra("vehicle_type");
            rideWith = inc.getStringExtra("ride_with");
            usage = inc.getStringExtra("usage");
            budget = inc.getIntExtra("budget", -1);
            distanceKm = inc.getIntExtra("distance_km", -1);
            fuelPriority = inc.getStringExtra("fuel_priority"); // if any
        }

        // topbar
        ImageView ivMenu = findViewById(R.id.ivMenu_q9);
        ImageView ivBell = findViewById(R.id.ivBell_q9);
        ImageView btnClose = findViewById(R.id.btnClose_q9);

        if (ivMenu != null) ivMenu.setOnClickListener(v -> {
            startActivity(new Intent(AiQuestion9Activity.this, MenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        if (ivBell != null) ivBell.setOnClickListener(v ->
                Toast.makeText(AiQuestion9Activity.this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        if (btnClose != null) btnClose.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion9Activity.this, HomeActivity.class);
            i.putExtra("step", 2);
            startActivity(i);
        });

        // find views (including the BACK button that was missing earlier)
        optBeginner = findViewById(R.id.optBeginner_q9);
        optIntermediate = findViewById(R.id.optIntermediate_q9);
        optExpert = findViewById(R.id.optExpert_q9);

        ivBeginnerTick = findViewById(R.id.ivBeginnerTick_q9);
        ivIntermediateTick = findViewById(R.id.ivIntermediateTick_q9);
        ivExpertTick = findViewById(R.id.ivExpertTick_q9);

        btnAiSuggest9 = findViewById(R.id.btnAiSuggest_q9);
        btnBack9 = findViewById(R.id.btnBack_q9);

        progressBar9 = findViewById(R.id.progressBar_q9);
        tvStep9 = findViewById(R.id.tvStep_q9);

        // defensive null-check (helpful for debugging missing ids)
        String missing = "";
        if (optBeginner == null) missing += " optBeginner_q9";
        if (optIntermediate == null) missing += " optIntermediate_q9";
        if (optExpert == null) missing += " optExpert_q9";
        if (ivBeginnerTick == null) missing += " ivBeginnerTick_q9";
        if (ivIntermediateTick == null) missing += " ivIntermediateTick_q9";
        if (ivExpertTick == null) missing += " ivExpertTick_q9";
        if (btnAiSuggest9 == null) missing += " btnAiSuggest_q9";
        if (btnBack9 == null) missing += " btnBack_q9";
        if (progressBar9 == null) missing += " progressBar_q9";
        if (tvStep9 == null) missing += " tvStep_q9";

        if (!missing.isEmpty()) {
            Toast.makeText(this, "Missing IDs:" + missing, Toast.LENGTH_LONG).show();
            return;
        }

        // progress
        tvStep9.setText("9/9");
        progressBar9.setMax(9);
        progressBar9.setProgress(9);

        // default selection (also sets experience)
        setSelected(optBeginner, ivBeginnerTick, optIntermediate, ivIntermediateTick, optExpert, ivExpertTick, "Beginner");

        // clicks (each sets selected UI + experience)
        optBeginner.setOnClickListener(v ->
                setSelected(optBeginner, ivBeginnerTick, optIntermediate, ivIntermediateTick, optExpert, ivExpertTick, "Beginner"));
        optIntermediate.setOnClickListener(v ->
                setSelected(optIntermediate, ivIntermediateTick, optBeginner, ivBeginnerTick, optExpert, ivExpertTick, "Intermediate"));
        optExpert.setOnClickListener(v ->
                setSelected(optExpert, ivExpertTick, optBeginner, ivBeginnerTick, optIntermediate, ivIntermediateTick, "Expert"));

        btnBack9.setOnClickListener(v -> finish());

        btnAiSuggest9.setOnClickListener(v -> {
            // final experience already set on selection, but double-check
            if (ivBeginnerTick.getVisibility() == View.VISIBLE) experience = "Beginner";
            else if (ivIntermediateTick.getVisibility() == View.VISIBLE) experience = "Intermediate";
            else if (ivExpertTick.getVisibility() == View.VISIBLE) experience = "Expert";

            // Start result screen
            Intent i = new Intent(AiQuestion9Activity.this, AiResultActivity.class);
            i.putExtra("vehicle_type", vehicleType);
            i.putExtra("ride_with", rideWith);
            i.putExtra("usage", usage);
            i.putExtra("budget", budget);
            i.putExtra("distance_km", distanceKm);
            i.putExtra("fuel_priority", fuelPriority);
            i.putExtra("experience", experience);

            try {
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (Exception ex) {
                Toast.makeText(AiQuestion9Activity.this, "AI result screen not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Update UI and selection value
     */
    private void setSelected(View selCard, ImageView selTick,
                             View other1, ImageView otherTick1,
                             View other2, ImageView otherTick2,
                             String experienceValue) {

        selCard.setBackgroundResource(R.drawable.option_selected_bg);
        selTick.setVisibility(View.VISIBLE);

        other1.setBackgroundResource(R.drawable.option_bg);
        otherTick1.setVisibility(View.INVISIBLE);

        other2.setBackgroundResource(R.drawable.option_bg);
        otherTick2.setVisibility(View.INVISIBLE);

        // set the selection value
        experience = experienceValue;
    }
}
