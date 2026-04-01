package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AiSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_splash);

        TextView tvAiWelcome = findViewById(R.id.tvAiWelcome);

        // Animation: Fade In + Scale Up
        tvAiWelcome.setAlpha(0f);
        tvAiWelcome.setScaleX(0.8f);
        tvAiWelcome.setScaleY(0.8f);

        tvAiWelcome.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1500)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();

        // Delay for 3 seconds then go to Question Activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(AiSplashActivity.this, AiQuestionActivity.class);
            // Pass step 1 explicitly just in case
            intent.putExtra("step", 1);
            startActivity(intent);
            finish(); // Close splash so back button doesn't return here
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 3000);
    }
}
