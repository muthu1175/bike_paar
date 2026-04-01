package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiResultActivity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private RecyclerView recyclerView;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();
    private ImageView aiButton;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_result);

        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvAiResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use BikeAdapter initially with empty list
        bikeAdapter = new BikeAdapter(this, bikeList, this, true);
        recyclerView.setAdapter(bikeAdapter);


        // -------- collect answers from intent --------
        Intent i = getIntent();

        Map<String, Object> body = new HashMap<>();
        body.put("budget", i.getIntExtra("budget", 0));
        body.put("vehicle_type", i.getStringExtra("vehicle_type"));
        body.put("ride_with", i.getStringExtra("ride_with"));
        body.put("usage", i.getStringExtra("usage"));
        body.put("distance_km", i.getIntExtra("distance_km", 0));
        body.put("fuel_priority", i.getStringExtra("fuel_priority"));
        body.put("comfort_pref", i.getStringExtra("comfort_pref"));
        body.put("bike_category", i.getStringExtra("bike_category"));
        body.put("experience", i.getStringExtra("experience"));

        // -------- API CALL --------
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAiSuggestions("Token " + token, body)
                .enqueue(new Callback<List<Bike>>() { // Use List<Bike>
                    @Override
                    public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            bikeList = response.body();
                            // Update Adapter
                            bikeAdapter = new BikeAdapter(AiResultActivity.this, bikeList, AiResultActivity.this, true);
                            recyclerView.setAdapter(bikeAdapter);

                            if (bikeList.isEmpty()) {
                                Toast.makeText(AiResultActivity.this, "No matching bikes found for your criteria.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AiResultActivity.this, "Failed to get suggestions.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Bike>> call, Throwable t) {
                        Toast.makeText(AiResultActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        // ---------- BOTTOM NAVIGATION ----------
        bottomNavigation = findViewById(R.id.bottomNavigation);
        aiButton = findViewById(R.id.aiButton);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(AiResultActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(AiResultActivity.this, SearchActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(AiResultActivity.this, FavouriteActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(AiResultActivity.this, ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });

        aiButton.setOnClickListener(v -> {
            Intent intent = new Intent(AiResultActivity.this, AiQuestionActivity.class);
            intent.putExtra("step", 1);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onViewDetailsClick(Bike bike) {
        // Navigation handled by Adapter
    }

    @Override
    public void onFavoriteClick(Bike bike, boolean isFavorite) {
        // Implement favorite logic if needed
        Toast.makeText(this, isFavorite ? "Added to favorites" : "Removed from favorites", Toast.LENGTH_SHORT).show();
    }
}
