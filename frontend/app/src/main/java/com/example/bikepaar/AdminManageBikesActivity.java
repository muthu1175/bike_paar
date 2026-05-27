package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.content.SharedPreferences;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Handler;
import android.os.Looper;

public class AdminManageBikesActivity extends AppCompatActivity {

    private RecyclerView rvBikes;
    private ProgressBar progressBar;
    private AdminManageBikesAdapter adapter;
    private List<Bike> bikeList;
    private Handler autoRefreshHandler;
    private Runnable autoRefreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_bikes);

        // Menu / Back Button (Using menu icon as back for now to keep flow simple)
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(v -> finish());
        
        // Floating Action Button
        FloatingActionButton fabAddBike = findViewById(R.id.fabAddBike);
        fabAddBike.setOnClickListener(v -> {
            startActivity(new Intent(AdminManageBikesActivity.this, AdminAddBikeActivity.class));
        });

        // Setup Bottom Nav
        View navDashboard = findViewById(R.id.navDashboard);
        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new Intent(AdminManageBikesActivity.this, AdminDashboardActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
        
        View navBikes = findViewById(R.id.navBikes);
        if (navBikes != null) {
            navBikes.setOnClickListener(v -> {
                // Already here
            });
        }
        
        View navFeedbacks = findViewById(R.id.navFeedbacks);
        if (navFeedbacks != null) {
            navFeedbacks.setOnClickListener(v -> {
                startActivity(new Intent(AdminManageBikesActivity.this, AdminUserFeedbacksActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        View navSettings = findViewById(R.id.navSettings);
        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                startActivity(new Intent(AdminManageBikesActivity.this, AdminSettingsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        // Setup RecyclerView
        rvBikes = findViewById(R.id.rvBikes);
        progressBar = findViewById(R.id.progressBar);
        rvBikes.setLayoutManager(new LinearLayoutManager(this));
        bikeList = new ArrayList<>();
        
        adapter = new AdminManageBikesAdapter(bikeList, new AdminManageBikesAdapter.OnBikeActionListener() {
            @Override
            public void onEditClick(Bike bike) {
                Intent intent = new Intent(AdminManageBikesActivity.this, AdminEditBikeActivity.class);
                intent.putExtra("bike", bike);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Bike bike) {
                deleteBike(bike);
            }
        });
        rvBikes.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBikes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoRefreshHandler = new Handler(Looper.getMainLooper());
        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadBikes(true);
                autoRefreshHandler.postDelayed(this, 1000); // 1 second
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBikes(false);
        autoRefreshHandler.postDelayed(autoRefreshRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (autoRefreshHandler != null && autoRefreshRunnable != null) {
            autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
        }
    }

    private void filterBikes(String text) {
        List<Bike> filteredList = new ArrayList<>();
        for (Bike bike : bikeList) {
            if (bike.name.toLowerCase().contains(text.toLowerCase()) || 
                (bike.brand != null && bike.brand.toLowerCase().contains(text.toLowerCase()))) {
                filteredList.add(bike);
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    private void loadBikes(boolean isAutoRefresh) {
        if (!isAutoRefresh) {
            progressBar.setVisibility(View.VISIBLE);
        }
        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllBikesAdmin("Token " + token).enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (!isAutoRefresh) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    bikeList = response.body();
                    adapter.updateBikes(bikeList);
                } else {
                    Toast.makeText(AdminManageBikesActivity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                if (!isAutoRefresh) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(AdminManageBikesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteBike(Bike bike) {
        if (bike.name == null || bike.name.isEmpty()) {
            Toast.makeText(this, "Cannot delete bike without name", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        progressBar.setVisibility(View.VISIBLE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.deleteBike(bike.name, "Token " + token).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(AdminManageBikesActivity.this, "Bike deleted successfully", Toast.LENGTH_SHORT).show();
                    bikeList.remove(bike);
                    adapter.updateBikes(bikeList);
                } else {
                    Toast.makeText(AdminManageBikesActivity.this, "Failed to delete bike", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminManageBikesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
