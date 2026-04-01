package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cc150to200Activity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private RecyclerView bikesRecyclerView;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc150to200);

        // Header Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        bikesRecyclerView = findViewById(R.id.bikesRecyclerView);
        bikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        // Fetch Data
        fetchBikes();
    }

    private void fetchBikes() {
        String rawToken = getSharedPreferences("USER_DATA", MODE_PRIVATE).getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        apiService.get150to200ccBikes(token).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bikeList = response.body();
                    // Using showBudgetDesign = true to match user preference for clear price display
                    bikeAdapter = new BikeAdapter(Cc150to200Activity.this, bikeList, Cc150to200Activity.this, true);
                    bikesRecyclerView.setAdapter(bikeAdapter);

                    if (bikeList.isEmpty()) {
                        Toast.makeText(Cc150to200Activity.this, "No bikes found in 150-200cc range", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Cc150to200Activity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(Cc150to200Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Cc150to200", "API Error", t);
            }
        });
    }

    @Override
    public void onViewDetailsClick(Bike bike) {
        // Navigation handled by Adapter
    }

    @Override
    public void onFavoriteClick(Bike bike, boolean isFavorite) {
        // The adapter already toggles the flag internally in bindBudgetViewHolder before calling this callback,
        // or effectively we just need to confirm or show toast. 
        // Logic in adapter: b.isFavorite = newState; listener.onFavoriteClick(b, newState);
        // So we just show toast.
        String msg = isFavorite ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}