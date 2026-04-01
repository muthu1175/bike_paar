package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
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

public class Cc100to150Activity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private RecyclerView bikesRecyclerView;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc100to150);

        // Set up the back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        bikesRecyclerView = findViewById(R.id.bikesRecyclerView);
        bikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bikesRecyclerView.setHasFixedSize(true);

        // Fetch data from API
        fetchBikes();
    }

    private void fetchBikes() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String rawToken = getSharedPreferences("USER_DATA", MODE_PRIVATE).getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        Call<List<Bike>> call = apiService.get100to150ccBikes(token);

        call.enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bikeList = response.body();
                    
                    if (bikeList.isEmpty()) {
                        Toast.makeText(Cc100to150Activity.this, "No bikes found in this segment", Toast.LENGTH_SHORT).show();
                    }

                    // Use BikeAdapter
                    // Passing 'false' for isBudget because standard view might be preferred, 
                    // but BudgetBikesActivity used 'true'. Let's stick to simple card view if possible.
                    // Checking Adapter signature: BikeAdapter(Context, List, Listener, boolean isBudget)
                    // If isBudget is true, it might use specific layout. 
                    // Let's use false for now as this is Displacement, unless checking layout shows otherwise.
                    // Actually, BudgetBikesActivity passes 'true'. Let's try 'true' to be safe/consistent with "same aathey mari".
                    bikeAdapter = new BikeAdapter(Cc100to150Activity.this, bikeList, Cc100to150Activity.this, true);
                    bikesRecyclerView.setAdapter(bikeAdapter);
                } else {
                    Toast.makeText(Cc100to150Activity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(Cc100to150Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewDetailsClick(Bike bike) {
        // Navigation handled by Adapter
    }

    @Override
    public void onFavoriteClick(Bike bike, boolean isFavorite) {
        // Optional: Implement favorite logic if needed
        String message = isFavorite ? "Added to favorites: " : "Removed from favorites: ";
        Toast.makeText(this, message + bike.name, Toast.LENGTH_SHORT).show();
    }
}