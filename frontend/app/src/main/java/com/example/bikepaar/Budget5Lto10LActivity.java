package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Budget5Lto10LActivity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private RecyclerView bikesRecyclerView;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_5l_10l);

        // Header Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        bikesRecyclerView = findViewById(R.id.bikesRecyclerView);
        bikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Fetch Data
        fetchBikes();
    }

    private void fetchBikes() {
        String rawToken = getSharedPreferences("USER_DATA", MODE_PRIVATE).getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        apiService.get5Lto10LBikes(token).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bikeList = response.body();
                    // Using budget design
                    bikeAdapter = new BikeAdapter(Budget5Lto10LActivity.this, bikeList, Budget5Lto10LActivity.this, true);
                    bikesRecyclerView.setAdapter(bikeAdapter);

                    if (bikeList.isEmpty()) {
                        Toast.makeText(Budget5Lto10LActivity.this, "No bikes found in 5L-10L range", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Budget5Lto10LActivity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(Budget5Lto10LActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Budget5L10L", "API Error", t);
            }
        });
    }

    @Override
    public void onViewDetailsClick(Bike bike) {
        // Navigation handled by Adapter
    }

    @Override
    public void onFavoriteClick(Bike bike, boolean isFavorite) {
        String msg = isFavorite ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
