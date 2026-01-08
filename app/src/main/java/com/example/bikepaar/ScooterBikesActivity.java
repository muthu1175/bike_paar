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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScooterBikesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BikeAdapter adapter;
    private List<Bike> bikeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scooter_bikes);

        ImageView backButton = findViewById(R.id.ivBack);
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        recyclerView = findViewById(R.id.recyclerViewScooterBikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Use standard adapter
        adapter = new BikeAdapter(this, bikeList); 
        recyclerView.setAdapter(adapter);

        fetchScooterBikes();
    }

    private void fetchScooterBikes() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String rawToken = prefs.getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        
        apiService.getScooterBikes(token).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bikeList.clear();
                    bikeList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ScooterBikesActivity.this, "Failed to load scooters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(ScooterBikesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}