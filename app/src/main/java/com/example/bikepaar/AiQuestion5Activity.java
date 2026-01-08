package com.example.bikepaar;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AiQuestion5Activity extends AppCompatActivity {

    private SeekBar seekDistance;
    private TextView tvCurrentDistance, tvMin, tvMax;
    private TextView btnNext_q5, btnBack_q5;
    private ProgressBar progressBar_q5;
    private TextView tvStep_q5;
    private TextView tvThumbPopup;

    // prev answers (optional)
    private String vehicleFromPrev;
    private String rideWithPrev;
    private int budgetFromPrev = -1;
    private String usagePrev;
    private int distanceKm = 30; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_ai_question_5);
        } catch (Exception e) {
            Toast.makeText(this, "Layout inflate error: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        // read previous extras if present
        Intent inc = getIntent();
        if (inc != null) {
            vehicleFromPrev = inc.getStringExtra("vehicle_type");
            rideWithPrev = inc.getStringExtra("ride_with");
            usagePrev = inc.getStringExtra("usage");
            budgetFromPrev = inc.getIntExtra("budget", -1);
            distanceKm = inc.getIntExtra("distance_km", 30);
        }

        // top icons
        ImageView ivMenu = findViewById(R.id.ivMenu_q5);
        ImageView ivBell = findViewById(R.id.ivBell_q5);
        ImageView btnClose = findViewById(R.id.btnClose_q5);

        if (ivMenu != null) ivMenu.setOnClickListener(v -> startActivity(new Intent(AiQuestion5Activity.this, MenuActivity.class)));
        if (ivBell != null) ivBell.setOnClickListener(v -> Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        if (btnClose != null) btnClose.setOnClickListener(v -> {
            Intent i = new Intent(AiQuestion5Activity.this, HomeActivity.class);
            i.putExtra("step", 2);
            startActivity(i);
        });

        // find views
        seekDistance = findViewById(R.id.seekDistance);
        tvCurrentDistance = findViewById(R.id.tvCurrentDistance);
        tvMin = findViewById(R.id.tvMin);
        tvMax = findViewById(R.id.tvMax);
        btnNext_q5 = findViewById(R.id.btnNext_q5);
        btnBack_q5 = findViewById(R.id.btnBack_q5);
        progressBar_q5 = findViewById(R.id.progressBar_q5);
        tvStep_q5 = findViewById(R.id.tvStep_q5);
        tvThumbPopup = findViewById(R.id.tvThumbPopup);

        // defensive null-check
        if (seekDistance == null || tvCurrentDistance == null || btnNext_q5 == null || btnBack_q5 == null || tvThumbPopup == null) {
            Toast.makeText(this, "Missing UI elements in Q5 layout", Toast.LENGTH_LONG).show();
            return;
        }

        // configure progress label
        if (tvStep_q5 != null) tvStep_q5.setText("5/9");
        if (progressBar_q5 != null) {
            progressBar_q5.setMax(9);
            progressBar_q5.setProgress(5);
        }

        // SeekBar config
        seekDistance.setMax(250); // safe
        if (distanceKm < 30) distanceKm = 30;
        seekDistance.setProgress(distanceKm);

        tvMin.setText("30");
        tvMax.setText("250");
        updateDistanceText(distanceKm);

        // place the popup initially after layout (so widths are known)
        seekDistance.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                // position popup above current thumb
                positionPopupForProgress(seekDistance.getProgress(), false);
                seekDistance.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // slider change
        seekDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = Math.max(30, progress);
                distanceKm = value;
                updateDistanceText(value);
                positionPopupForProgress(progress, true);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnBack_q5.setOnClickListener(v -> finish());

        btnNext_q5.setOnClickListener(v -> {
            // pass collected data forward (example to AiQuestion6Activity)
            Intent i = new Intent(AiQuestion5Activity.this, AiQuestion6Activity.class);
            i.putExtra("distance_km", distanceKm);

            if (vehicleFromPrev != null) i.putExtra("vehicle_type", vehicleFromPrev);
            if (rideWithPrev != null) i.putExtra("ride_with", rideWithPrev);
            if (usagePrev != null) i.putExtra("usage", usagePrev);
            if (budgetFromPrev > 0) i.putExtra("budget", budgetFromPrev);


            try {
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (Exception ex) {
                Toast.makeText(AiQuestion5Activity.this, "Next AI screen not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDistanceText(int km) {
        if (tvCurrentDistance != null) {
            tvCurrentDistance.setText(km + " km");
        }
        if (tvThumbPopup != null) {
            tvThumbPopup.setText(km + " km");
        }
    }

    /**
     * Position the popup TextView above the SeekBar thumb.
     * If animate==true, we animate translationX for smooth movement.
     */
    private void positionPopupForProgress(int progress, boolean animate) {
        if (seekDistance == null || tvThumbPopup == null) return;

        // get SeekBar location and size
        int[] sbLocation = new int[2];
        seekDistance.getLocationOnScreen(sbLocation);
        int[] parentLocation = new int[2];
        // use parent coords (window) for relative translation
        ((View) seekDistance.getParent()).getLocationOnScreen(parentLocation);

        // compute ratio of progress (0..max)
        int max = seekDistance.getMax();
        float ratio = max > 0 ? (float) progress / (float) max : 0f;

        // available width inside the seekbar (exclude padding)
        int available = seekDistance.getWidth() - seekDistance.getPaddingLeft() - seekDistance.getPaddingRight();
        float xInside = seekDistance.getPaddingLeft() + (available * ratio);

        // convert to parent relative x (seekDistance left relative to parent)
        int sbLeftInParent = seekDistance.getLeft();
        float popupCenterX = sbLeftInParent + xInside;

        // adjust to center the popup (popup width may not be measured yet)
        final View popup = tvThumbPopup;
        int popupWidth = popup.getWidth();
        if (popupWidth == 0) {
            // width not measured yet -> measure quickly
            popup.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            popupWidth = popup.getMeasuredWidth();
        }

        float targetX = popupCenterX - (popupWidth / 2f);

        // clamp inside parent bounds (optional)
        float minX = 0f;
        float maxX = ((View) popup.getParent()).getWidth() - popupWidth;
        if (targetX < minX) targetX = minX;
        if (targetX > maxX) targetX = maxX;

        if (animate) {
            popup.animate().translationX(targetX).setDuration(80).start();
        } else {
            popup.setTranslationX(targetX);
        }
    }
}
