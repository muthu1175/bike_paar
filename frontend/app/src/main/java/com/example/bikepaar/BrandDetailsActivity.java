package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandDetailsActivity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private ImageView brandLogo;
    private TextView brandName;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    private BikeAdapter adapter;
    private List<Bike> bikeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_details);

        // Get brand data from intent
        Intent intent = getIntent();
        String selectedBrand = intent.getStringExtra("BRAND_NAME");
        int brandLogoRes = intent.getIntExtra("BRAND_LOGO", R.drawable.brand_honda);

        // Initialize views
        initViews();

        // Set brand info in header
        if (selectedBrand != null) {
            brandName.setText(selectedBrand);
        }
        brandLogo.setImageResource(brandLogoRes);

        // Fetch Data
        if (selectedBrand != null && !selectedBrand.isEmpty()) {
            fetchBrandBikes(selectedBrand);
        } else {
             Toast.makeText(this, "Invalid Brand", Toast.LENGTH_SHORT).show();
        }

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void initViews() {
        brandLogo = findViewById(R.id.brandLogo);
        brandName = findViewById(R.id.brandName);
        recyclerView = findViewById(R.id.recyclerViewBrandBikes);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Setup empty adapter initially
        adapter = new BikeAdapter(this, bikeList, this, true); // Use budget design for list view
        recyclerView.setAdapter(adapter);

        // AI button
        findViewById(R.id.aiButton).setOnClickListener(v -> {
            Intent i = new Intent(BrandDetailsActivity.this, AiQuestionActivity.class);
            i.putExtra("step", 1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        View notificationBtn = findViewById(R.id.notificationButton);
        if (notificationBtn != null) {
            notificationBtn.setOnClickListener(v -> {
                Intent intent = new Intent(BrandDetailsActivity.this, NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        
        // Hide unused views from old layout just in case, though they should be gone from XML
        // Not needed as XML was updated
    }

    private void fetchBrandBikes(String brand) {
        progressBar.setVisibility(View.VISIBLE);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String rawToken = prefs.getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;

        apiService.getBrandBikes(token, brand).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    bikeList.clear();
                    bikeList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    if (bikeList.isEmpty()) {
                        Toast.makeText(BrandDetailsActivity.this, "No bikes found for " + brand, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BrandDetailsActivity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BrandDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewDetailsClick(Bike bike) {
         // Adapter handles navigation implicitly in its bind method, 
         // BUT if we used the constructor with listener, we can handle extras logic here if needed.
         // However, BikeAdapter already has the logic to open MotorcycleDetailsActivity with extras.
         // So we can leave this empty or add logging.
    }

    @Override
    public void onFavoriteClick(Bike bike, boolean isFavorite) {
         // Optional: Handle favorite click feedback
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent i = new Intent(BrandDetailsActivity.this, HomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_search) {
                Intent i = new Intent(BrandDetailsActivity.this, SearchActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_fav) {
                Intent i = new Intent(BrandDetailsActivity.this, FavouriteActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(BrandDetailsActivity.this, ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}