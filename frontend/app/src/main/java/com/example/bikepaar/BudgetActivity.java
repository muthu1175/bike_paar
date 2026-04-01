package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BudgetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Setup back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Setup budget option click listeners
        setupBudgetOptions();
    }

    private void setupBudgetOptions() {
        // Setup click listeners for all budget options
        int[] budgetButtonIds = {
                R.id.budgetOption1, R.id.budgetOption2, R.id.budgetOption3,
                R.id.budgetOption4, R.id.budgetOption5, R.id.budgetOption6,
                R.id.budgetOption7
        };

        String[] budgetRanges = {
                "₹30k - ₹80k",
                "₹80k - ₹1.5 Lakh",
                "₹1.5 Lakh - ₹3 Lakh",
                "₹3 Lakh - ₹5 Lakh",
                "₹5 Lakh - ₹10 Lakh",
                "₹10 Lakh - ₹30 Lakh",
                "Above ₹30 Lakh"
        };

        for (int i = 0; i < budgetButtonIds.length; i++) {
            View budgetButton = findViewById(budgetButtonIds[i]);
            final String budgetRange = budgetRanges[i];
            final int position = i;

            budgetButton.setOnClickListener(v -> {
                // Show selected message
                String message = "Selected: " + budgetRange;
                Toast.makeText(BudgetActivity.this, message, Toast.LENGTH_SHORT).show();

                if (position == 0) {
                     // 30k - 80k Specific Activity
                     Intent intent = new Intent(BudgetActivity.this, Budget30kto80kActivity.class);
                     startActivity(intent);
                } else if (position == 1) {
                     // 80k - 1.5L Specific Activity
                     Intent intent = new Intent(BudgetActivity.this, Budget80kto150kActivity.class);
                     startActivity(intent);
                } else if (position == 2) {
                     // 1.5L - 3L Specific Activity
                     Intent intent = new Intent(BudgetActivity.this, Budget150kto300kActivity.class);
                     startActivity(intent);
                } else if (position == 3) {
                     // 3L - 5L Specific Activity
                     Intent intent = new Intent(BudgetActivity.this, Budget300kto500kActivity.class);
                     startActivity(intent);
                } else if (position == 4) {
                     // 5L - 10L Specific Activity
                     Intent intent = new Intent(BudgetActivity.this, Budget5Lto10LActivity.class);
                     startActivity(intent);
                } else if (position == 5) {
                     // 10L - 30L Specific Activity
                     Intent intent = new Intent(BudgetActivity.this, Budget10Lto30LActivity.class);
                     startActivity(intent);
                } else if (position == 6) {
                     // Above 30L Specific Activity
                     Intent intent = new Intent(BudgetActivity.this, BudgetAbove30LActivity.class);
                     startActivity(intent);
                } else {
                    // Open BudgetBikesActivity for other budget ranges
                    Intent intent = new Intent(BudgetActivity.this, BudgetBikesActivity.class);
                    intent.putExtra("BUDGET_RANGE", budgetRange);
                    intent.putExtra("BUDGET_POSITION", position);
                    startActivity(intent);
                }
            });
        }
    }
}