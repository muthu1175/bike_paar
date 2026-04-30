package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class AiQuestionActivity extends AppCompatActivity {

    private static final int MIN_BUDGET = 30000;     // 30,000
    private static final int MAX_BUDGET = 3000000;   // 30,00,000

    private TextView tvStep, tvQuestion, tvSubText;
    private TextView tvMinValue, tvMaxValue, tvSelectedBudget;
    private SeekBar seekBudget;
    private ProgressBar progressBar;

    private int currentBudget = MIN_BUDGET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_question);

        // ------------ TOP BAR ------------
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ImageView ivBell = findViewById(R.id.ivBell);

        ivMenu.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestionActivity.this, MenuActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ivBell.setOnClickListener(v -> {
            Intent intent = new Intent(AiQuestionActivity.this, NotificationActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ------------ CARD VIEWS ----------
        tvStep = findViewById(R.id.tvStep);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvSubText = findViewById(R.id.tvSubText);
        tvMinValue = findViewById(R.id.tvMinValue);
        tvMaxValue = findViewById(R.id.tvMaxValue);
        tvSelectedBudget = findViewById(R.id.tvSelectedBudget);
        seekBudget = findViewById(R.id.seekBudget);
        progressBar = findViewById(R.id.progressBar);

        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
                    Intent i = new Intent(AiQuestionActivity.this, HomeActivity.class);
                    i.putExtra("step", 2);
                    startActivity(i);
                });

        // Step (1/9)
        int step = getIntent().getIntExtra("step", 1);
        tvStep.setText(step + "/9");
        progressBar.setMax(9);
        progressBar.setProgress(step);

        // Question text
        tvQuestion.setText("What’s your budget?");
        tvSubText.setText("set your maximum budget for a bike");

        // Min / Max labels
        tvMinValue.setText("30,000");
        tvMaxValue.setText("30,00,000");

        // SeekBar (0 - 100) -> map to 30,000 - 30,00,000
        seekBudget.setMax(100);
        seekBudget.setProgress(0);
        updateSelectedBudget(0);   // initial text

        seekBudget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSelectedBudget(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // NEXT button
        TextView btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestionActivity.this, AiQuestion2Activity.class);
            i.putExtra("step", 2);
            i.putExtra("budget", currentBudget); // 🔥 ADD THIS
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    private void updateSelectedBudget(int progress) {
        long range = MAX_BUDGET - MIN_BUDGET;
        currentBudget = (int) (MIN_BUDGET + (range * progress / 100f));
        tvSelectedBudget.setText(formatRupees(currentBudget));
    }

    private String formatRupees(int value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
        return "₹ " + nf.format(value);
    }
}
