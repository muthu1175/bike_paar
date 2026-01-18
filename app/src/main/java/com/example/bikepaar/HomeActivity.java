package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private View searchPill;
    private ImageView aiButton;

    private ViewPager2 viewPagerBikes;
    private View dot1, dot2, dot3;

    private View rowDisplacement, rowBudget, rowRideStyle;
    private Button btnViewAllBikes;

    // 🔹 NEW: top category buttons (most popular / brand / recent launches)
    private View btnMostPopular, btnBrand, btnRecentLaunches;

    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerBikes == null) return;
            int next = (viewPagerBikes.getCurrentItem() + 1) % 3;
            viewPagerBikes.setCurrentItem(next, true);
            updateDots(next);
            sliderHandler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // top bar
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ImageView ivBell = findViewById(R.id.ivBell);

        ivMenu.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, MenuActivity.class);
            Bundle options = android.app.ActivityOptions.makeCustomAnimation(HomeActivity.this,
                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            startActivity(i, options);
        });

        ivBell.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // views
        searchPill = findViewById(R.id.searchPill);
        aiButton = findViewById(R.id.aiButton);

        viewPagerBikes = findViewById(R.id.viewPagerBikes);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        rowDisplacement = findViewById(R.id.rowDisplacement);
        rowBudget = findViewById(R.id.rowBudget);
        rowRideStyle = findViewById(R.id.rowRideStyle);
        btnViewAllBikes = findViewById(R.id.btnViewAllBikes);

        // 🔹 NEW: connect to your 3 tab layouts from XML
        btnMostPopular = findViewById(R.id.btnMostPopular);
        btnBrand = findViewById(R.id.btnBrand);
        btnRecentLaunches = findViewById(R.id.btnRecentLaunches);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);   // highlight home

        // slider adapter
        BikeSliderAdapter adapter = new BikeSliderAdapter(this);
        viewPagerBikes.setAdapter(adapter);
        viewPagerBikes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });
        updateDots(0);

        // bottom nav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // already here
                return true;
            } else if (id == R.id.nav_search) {
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }  else if (id == R.id.nav_fav) {
                Intent i = new Intent(HomeActivity.this, FavouriteActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });

        // search / AI
        // Find the search pill/view
        View searchPill = findViewById(R.id.searchPill); // or whatever your search bar ID is

        searchPill.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        aiButton = findViewById(R.id.aiButton);
        aiButton.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AiSplashActivity.class);
            // i.putExtra("step", 1); // Not needed for splash
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // discover rows
        rowDisplacement.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DisplacementActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        rowBudget.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BudgetActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        rowRideStyle.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RideStyleActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnViewAllBikes.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AllMotorcyclesActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 🔹 CLICK HANDLER for the 3 top tabs
        // In onCreate method
        btnRecentLaunches.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RecentLaunchesActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        // In onCreate method
        btnMostPopular.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MostPopularActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        // Replace the existing btnBrand click listener with this:
        btnBrand.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BrandActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        // 🔹 NEW: Welcome Animation (Wave Float + Color Change)
        android.widget.TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (tvWelcome != null) {
            // 1. Float Animation (Wave-like)
            android.animation.ObjectAnimator floatAnim = android.animation.ObjectAnimator.ofFloat(
                    tvWelcome, "translationY", 0f, 15f);
            floatAnim.setDuration(1200);
            floatAnim.setRepeatCount(android.animation.ValueAnimator.INFINITE);
            floatAnim.setRepeatMode(android.animation.ValueAnimator.REVERSE);
            floatAnim.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            floatAnim.start();

            // 2. Color Animation
            android.animation.ObjectAnimator colorAnim = android.animation.ObjectAnimator.ofInt(
                    tvWelcome, "textColor", 
                    android.graphics.Color.parseColor("#FFC65C"), // Gold
                    android.graphics.Color.parseColor("#FF5722")  // Orange
            );
            colorAnim.setEvaluator(new android.animation.ArgbEvaluator());
            colorAnim.setDuration(1200);
            colorAnim.setRepeatCount(android.animation.ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(android.animation.ValueAnimator.REVERSE);
            colorAnim.start();
        }
        
        // 🔹 3. Tagline Typewriter Animation
        final android.widget.TextView tvTagline = findViewById(R.id.tvTagline);
        if (tvTagline != null) {
            final String fullText = "bringing AI to choose the comfort bike";
            tvTagline.setText(""); // Start empty
            
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable typeWriter = new Runnable() {
                int index = 0;
                @Override
                public void run() {
                    if (index <= fullText.length()) {
                        tvTagline.setText(fullText.substring(0, index));
                        index++;
                        handler.postDelayed(this, 100); // 100ms delay per letter
                    } else {
                        // Optional: Reset and loop? Or just stop.
                        // User said "wave mari poganum", implying continuous movement or just the effect.
                        // Let's loop it with a pause.
                        index = 0;
                        handler.postDelayed(this, 3000); // Wait 3s then restart
                    }
                }
            };
            handler.post(typeWriter);
        }

        // 🔹 4. POPULAR BRANDS SECTION
        androidx.recyclerview.widget.RecyclerView rvPopularBrands = findViewById(R.id.rvPopularBrands);
        rvPopularBrands.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        
        java.util.List<BrandItem> popularBrands = new java.util.ArrayList<>();
        // Honda, TVS, Hero, Yamaha, Bajaj, Royal Enfield
        popularBrands.add(new BrandItem("Honda", R.drawable.brand_honda));
        popularBrands.add(new BrandItem("TVS", R.drawable.brand_tvs));
        popularBrands.add(new BrandItem("Hero", R.drawable.brand_hero));
        popularBrands.add(new BrandItem("Yamaha", R.drawable.brand_yamaha));
        popularBrands.add(new BrandItem("Bajaj", R.drawable.brand_bajaj));
        popularBrands.add(new BrandItem("Royal Enfield", R.drawable.brand_royal_enfield));
        
        PopularBrandsHomeAdapter brandAdapter = new PopularBrandsHomeAdapter(this, popularBrands);
        rvPopularBrands.setAdapter(brandAdapter);
    }

    private void updateDots(int position) {
        dot1.setBackgroundResource(position == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot2.setBackgroundResource(position == 1 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot3.setBackgroundResource(position == 2 ? R.drawable.dot_active : R.drawable.dot_inactive);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}
