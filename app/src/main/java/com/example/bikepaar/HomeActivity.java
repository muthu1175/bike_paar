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

        ivBell.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this,
                        " No Notification", Toast.LENGTH_SHORT).show());

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
            Intent i = new Intent(HomeActivity.this, AiQuestionActivity.class);
            i.putExtra("step", 1);  // 1/9 question
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
