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

        // 🔹 NEW: Compare Your Favourite Button
        Button btnCompareFav = findViewById(R.id.btnCompareFav);
        btnCompareFav.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CompareActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

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
        
        // 🔹 5. LOAD SLIDER IMAGES (Super Bikes)
        fetchSliderImages(adapter);
    }
    
    private void fetchSliderImages(BikeSliderAdapter adapter) {
        // Fetch ALL bikes to ensure we find the specific ones requested
        android.content.SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String rawToken = prefs.getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllBikes(token).enqueue(new retrofit2.Callback<java.util.List<SportsBike>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<SportsBike>> call, retrofit2.Response<java.util.List<SportsBike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<SportsBike> allBikes = response.body();
                    java.util.List<Bike> sliderList = new java.util.ArrayList<>();
                    
                    Bike nx500 = null;
                    Bike cbr400r = null;
                    Bike gt650 = null;
                    java.util.List<Bike> others = new java.util.ArrayList<>();

                    // Find specific bikes and convert SportsBike -> Bike
                    for (SportsBike sb : allBikes) {
                        // Convert to Bike object
                        Bike b = new Bike(
                            sb.name, 
                            sb.name, 
                            sb.engine != null ? sb.engine : "", 
                            sb.mileage != null ? sb.mileage : "", 
                            sb.getPrice(), 
                            sb.imageUrl, 
                            "", // badge
                            "Motorcycle", 
                            "Sports"
                        );
                        // Access private fields via public setters or direct assignment if public?
                        // Bike fields are public.
                        b.maxPower = sb.maxPower;
                        b.maxTorque = sb.maxTorque;
                        b.kerbWeight = sb.kerbWeight;
                        b.topSpeed = sb.topSpeed;

                        String name = b.name.toLowerCase();
                        if (name.contains("nx500")) {
                            nx500 = b;
                        } else if (name.contains("cbr 400r") || name.contains("cbr 400 r")) {
                            cbr400r = b;
                        } else if (name.contains("continental gt 650") || name.contains("gt 650")) {
                            gt650 = b;
                        } else {
                            // Only add premium bikes to "Others" fallback (e.g. > 2 Lakh)
                            if (b.price > 200000) {
                                others.add(b);
                            }
                        }
                    }

                    // Constuct List: [NX500, CBR 400R, GT650, Others...]
                    
                    // 1. First: NX500
                    if (nx500 != null) sliderList.add(nx500);
                    else if (!others.isEmpty()) sliderList.add(others.remove(0)); // Fallback

                    // 2. Second: CBR 400R
                    if (cbr400r != null) sliderList.add(cbr400r);
                    else if (!others.isEmpty()) sliderList.add(others.remove(0));

                    // 3. Third: Continental GT 650
                    if (gt650 != null) sliderList.add(gt650);
                    else if (!others.isEmpty()) sliderList.add(others.remove(0));

                    // 4. Fill remaining up to 5 total from premium others
                    while (sliderList.size() < 5 && !others.isEmpty()) {
                        sliderList.add(others.remove(0));
                    }
                    
                    if (!sliderList.isEmpty()) {
                        adapter.setBikes(sliderList);
                        // Reset slider timer
                        viewPagerBikes.setCurrentItem(0, false);
                    }

                    // 🔹 POPULAR COMPARE POPULATION (Reuse allBikes)
                    populatePopularCompare(allBikes);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<java.util.List<SportsBike>> call, Throwable t) {
                // Ignore failure
            }
        });
    }

    private void populatePopularCompare(java.util.List<SportsBike> allBikes) {
        androidx.recyclerview.widget.RecyclerView rvPopularCompare = findViewById(R.id.rvPopularCompare);
         // If layout manager not set, set it. Or set in onCreate. Let's assume we set here or onCreate.
         // Better to set in onCreate, but let's do it safely here if we can't edit onCreate easily right now.
        if (rvPopularCompare.getLayoutManager() == null) {
             rvPopularCompare.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        }

        java.util.List<CompareItem> compareList = new java.util.ArrayList<>();
        
        // 1. Himalayan 450 vs Xpulse 200 4V
        Bike himalayan = findBike(allBikes, "himalayan 450");
        Bike xpulse = findBike(allBikes, "xpulse 200 4v");
        if (himalayan != null && xpulse != null) {
            compareList.add(new CompareItem(himalayan, xpulse));
        }

        // 2. MT-15 vs R15 V4
        Bike mt15 = findBike(allBikes, "mt-15");
        Bike r15 = findBike(allBikes, "r15 v4");
        if (mt15 != null && r15 != null) {
             compareList.add(new CompareItem(mt15, r15));
        }

        // 3. NS 200 vs RS 200
        Bike ns200 = findBike(allBikes, "ns200"); // or ns 200
        Bike rs200 = findBike(allBikes, "rs200");
        if (ns200 != null && rs200 != null) {
             compareList.add(new CompareItem(ns200, rs200));
        }

        PopularCompareAdapter compareAdapter = new PopularCompareAdapter(this, compareList);
        rvPopularCompare.setAdapter(compareAdapter);
    }

    private Bike findBike(java.util.List<SportsBike> sourceList, String query) {
        // Query should be lowercase
        query = query.toLowerCase();
        for (SportsBike sb : sourceList) {
            if (sb.name.toLowerCase().contains(query)) {
                 Bike b = new Bike(
                            sb.name, 
                            sb.name, 
                            sb.engine != null ? sb.engine : "", 
                            sb.mileage != null ? sb.mileage : "", 
                            sb.getPrice(), 
                            sb.imageUrl, 
                            "", // badge
                            "Motorcycle", 
                            "Sports"
                        );
                 // Copy All Specs for CompareResultsActivity
                 b.maxPower = sb.maxPower;
                 b.maxTorque = sb.maxTorque;
                 b.kerbWeight = sb.kerbWeight;
                 b.topSpeed = sb.topSpeed;
                 b.mileage = sb.mileage;
                 b.fuelTankCapacity = sb.fuelTankCapacity;
                 b.transmission = sb.transmission;
                 b.brakingSystem = sb.brakingSystem;
                 b.frontBrakeType = sb.frontBrakeType;
                 b.rearBrakeType = sb.rearBrakeType;
                 b.frontSuspension = sb.frontSuspension;
                 b.rearSuspension = sb.rearSuspension;
                 b.tyreType = sb.tyreType;
                 b.headlight = sb.headlight;
                 b.tailLight = sb.tailLight;
                 b.batteryCapacity = sb.batteryCapacity;
                 
                 // Dimensions
                 b.overallLength = sb.overallLength;
                 b.overallWidth = sb.overallWidth;
                 b.seatHeight = sb.seatHeight;
                 b.groundClearance = sb.groundClearance;
                 
                 return b;
            }
        }
        return null; // Not found
    }

    private void updateDots(int position) {
        if (dot1 == null || dot2 == null || dot3 == null) return;
        
        // Safety check if we have fewer items than dots
        // Ideally dots should be dynamic too, but for now we keep 3 dots static
        
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
