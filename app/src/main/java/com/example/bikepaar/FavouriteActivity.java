package com.example.bikepaar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private SportsBikeAdapter adapter;
    private List<SportsBike> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoriteList = new ArrayList<>();
        
        // Pass dummy listener, adapter handles internal clicks
        adapter = new SportsBikeAdapter(favoriteList, bike -> {
            // Optional: navigate to details
        });
        recyclerView.setAdapter(adapter);

        fetchFavorites();

        // Bottom Nav Logic
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_fav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_fav) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void fetchFavorites() {
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Please login to view favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getFavorites("Token " + token).enqueue(new Callback<List<SportsBike>>() {
            @Override
            public void onResponse(Call<List<SportsBike>> call, Response<List<SportsBike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteList.clear();
                    List<SportsBike> fetched = response.body();
                    for(SportsBike b : fetched) {
                        b.setFavorite(true);
                    }
                    favoriteList.addAll(fetched);
                    adapter.notifyDataSetChanged();
                    
                    if (favoriteList.isEmpty()) {
                         recyclerView.setVisibility(android.view.View.GONE);
                         findViewById(R.id.emptyStateLayout).setVisibility(android.view.View.VISIBLE);
                    } else {
                         recyclerView.setVisibility(android.view.View.VISIBLE);
                         findViewById(R.id.emptyStateLayout).setVisibility(android.view.View.GONE);
                    }
                } else {
                    Toast.makeText(FavouriteActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SportsBike>> call, Throwable t) {
                Toast.makeText(FavouriteActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override 
    protected void onResume() {
        super.onResume();
        fetchFavorites(); // Refresh when coming back
    }
}