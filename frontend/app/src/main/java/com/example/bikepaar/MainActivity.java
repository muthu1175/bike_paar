package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000; // 3 seconds
    private Handler handler;

    private final Runnable goNext = new Runnable() {
        @Override
        public void run() {
            decideNextScreen();   // 🔥 UPDATED
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // SPLASH SCREEN LAYOUT

        TextView tvGetStarted = findViewById(R.id.tvGetStarted);
        ProgressBar progressBar = findViewById(R.id.progressLoading);

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(goNext, SPLASH_DELAY);

        // If user clicks "Get Started" → skip timer
        tvGetStarted.setOnClickListener(v -> {
            handler.removeCallbacks(goNext);
            decideNextScreen();   // 🔥 UPDATED
        });
    }

    // 🔥 NEW METHOD (ONLY ADDITION)
    private void decideNextScreen() {

        SharedPreferences sp =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String token = sp.getString("TOKEN", "");

        Intent i;
        if (token != null && !token.isEmpty()) {
            // ✅ Already logged in
            i = new Intent(MainActivity.this, HomeActivity.class);
        } else {
            // ❌ Not logged in
            i = new Intent(MainActivity.this, LoginActivity.class);
        }

        android.os.Bundle options = android.app.ActivityOptions.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(i, options);
        finish(); // user cannot come back to splash
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(goNext);
    }
}
