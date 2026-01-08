package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion2Activity extends AppCompatActivity {

    private View optMotorcycle, optScooter;
    private ImageView ivMotorcycleTick, ivScooterTick;
    private TextView tvStep, tvQuestion;
    private ProgressBar progressBar;

    // store selection
    private String selectedVehicleType = "Motorcycle"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_question_choice); // idhu un 2nd screen layout
        int budget = getIntent().getIntExtra("budget", -1);

        // Top bar icons (optional)
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ImageView ivBell = findViewById(R.id.ivBell);
        ImageView btnClose = findViewById(R.id.btnClose);

        ivMenu.setOnClickListener(v -> {
            startActivity(new Intent(AiQuestion2Activity.this, MenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ivBell.setOnClickListener(v ->
                Toast.makeText(AiQuestion2Activity.this,
                        "Notifications clicked", Toast.LENGTH_SHORT).show()
        );

        btnClose.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion2Activity.this, HomeActivity.class);
            i.putExtra("step", 2);
            startActivity(i);
        });

        // header/progress
        tvStep = findViewById(R.id.tvStep);
        tvQuestion = findViewById(R.id.tvQuestion);
        progressBar = findViewById(R.id.progressBar);

        tvStep.setText("2/9");
        progressBar.setMax(9);
        progressBar.setProgress(2);
        tvQuestion.setText("What type of vehicle?");

        // options
        optMotorcycle = findViewById(R.id.optMotorcycle);
        optScooter = findViewById(R.id.optScooter);
        ivMotorcycleTick = findViewById(R.id.ivMotorcycleTick);
        ivScooterTick = findViewById(R.id.ivScooterTick);

        // default selection
        setSelectedOption(optMotorcycle, ivMotorcycleTick, optScooter, ivScooterTick);
        selectedVehicleType = "Motorcycle";

        View.OnClickListener clickOpt = v -> {
            if (v.getId() == R.id.optMotorcycle) {
                setSelectedOption(optMotorcycle, ivMotorcycleTick, optScooter, ivScooterTick);
                selectedVehicleType = "Motorcycle";
            } else if (v.getId() == R.id.optScooter) {
                setSelectedOption(optScooter, ivScooterTick, optMotorcycle, ivMotorcycleTick);
                selectedVehicleType = "Scooter";
            }
        };

        optMotorcycle.setOnClickListener(clickOpt);
        optScooter.setOnClickListener(clickOpt);

        // back / next
        TextView btnBack = findViewById(R.id.btnBack);
        TextView btnNext = findViewById(R.id.btnNext);

        btnBack.setOnClickListener(v -> {
            // go back to previous question (Q1 activity)
            finish();
        });

        btnNext.setOnClickListener(v -> {

            Intent i = new Intent(AiQuestion2Activity.this, AiQuestion3Activity.class);

            i.putExtra("vehicle_type", selectedVehicleType);   // Q2 answer
            i.putExtra("budget", budget);                      // 🔥 Q1 answer

            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    private void setSelectedOption(View selectedCard, ImageView selectedTick, View otherCard, ImageView otherTick) {
        // set backgrounds and tick visibility
        selectedCard.setBackgroundResource(R.drawable.option_selected_bg);
        selectedTick.setVisibility(View.VISIBLE);

        otherCard.setBackgroundResource(R.drawable.option_bg);
        otherTick.setVisibility(View.INVISIBLE);
    }
}
