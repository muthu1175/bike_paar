package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Below100ccActivity extends AppCompatActivity {

    private RecyclerView rvBikes;
    private BikeAdapter adapter;
    private List<Bike> bikeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_below_100cc);

        // Header Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        rvBikes = findViewById(R.id.rvBikes);
        rvBikes.setLayoutManager(new LinearLayoutManager(this));
        
        bikeList = new ArrayList<>();
        // Use showBudgetDesign = true for the detailed card layout
        adapter = new BikeAdapter(this, bikeList, new BikeAdapter.OnBikeClickListener() {
            @Override
            public void onViewDetailsClick(Bike bike) {
                 // Adapter handles navigation
            }

            @Override
            public void onFavoriteClick(Bike bike, boolean isFavorite) {
                // Adapter handles API call now. UI is auto-updated.
            }
        }, true); // true = use budget/detailed layout

        rvBikes.setAdapter(adapter);

        fetchBikes();
    }

    private void fetchBikes() {
        android.content.SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String rawToken = prefs.getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getBelow100ccBikes(token).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bikeList.clear();
                    bikeList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    if (bikeList.isEmpty()) {
                        Toast.makeText(Below100ccActivity.this, "No bikes found below 100cc", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Below100ccActivity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(Below100ccActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}