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

public class BudgetAbove30LActivity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private RecyclerView bikesRecyclerView;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();
    private ApiService apiService;
    private static final String TAG = "BudgetAbove30L";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable transition to see if it fixes the 'Mismatch' error
        overridePendingTransition(0, 0);
        Log.d(TAG, "onCreate: Activity Started");

        try {
            setContentView(R.layout.activity_budget_above30l);

            // Header Back Button
            ImageView backButton = findViewById(R.id.backButton);
            if (backButton != null) {
                backButton.setOnClickListener(v -> finish());
            } else {
                Log.e(TAG, "onCreate: backButton is null");
            }

            // Initialize RecyclerView
            bikesRecyclerView = findViewById(R.id.bikesRecyclerView);
            if (bikesRecyclerView != null) {
                bikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Log.e(TAG, "onCreate: bikesRecyclerView is null check layout ID matches");
            }

            // Initialize ApiService
            apiService = ApiClient.getClient().create(ApiService.class);

            // Fetch Data
            fetchBikes();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: CRASH", e);
            Toast.makeText(this, "Error initializing screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void fetchBikes() {
        Log.d(TAG, "fetchBikes: Calling API");
        String rawToken = getSharedPreferences("USER_DATA", MODE_PRIVATE).getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        
        apiService.getAbove30LBikes(token).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                Log.d(TAG, "onResponse: Code " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    bikeList = response.body();
                    Log.d(TAG, "onResponse: Received " + bikeList.size() + " items");

                    if (bikesRecyclerView != null) {
                        // Using budget design
                        bikeAdapter = new BikeAdapter(BudgetAbove30LActivity.this, bikeList, BudgetAbove30LActivity.this, true);
                        bikesRecyclerView.setAdapter(bikeAdapter);
                    } else {
                        Log.e(TAG, "onResponse: RecyclerView is null, cannot attach adapter");
                    }

                    if (bikeList.isEmpty()) {
                        Toast.makeText(BudgetAbove30LActivity.this, "No bikes found above 30L", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "onResponse: Failed. Code: " + response.code() + " msg: " + response.message());
                    Toast.makeText(BudgetAbove30LActivity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(BudgetAbove30LActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Error/Failure", t);
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
