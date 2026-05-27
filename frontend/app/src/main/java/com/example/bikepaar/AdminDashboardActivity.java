package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import android.widget.TextView;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalBikes, tvNewFeedbacks, tvActiveNews;
    private Handler autoRefreshHandler;
    private Runnable autoRefreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalBikes = findViewById(R.id.tvTotalBikes);
        tvNewFeedbacks = findViewById(R.id.tvNewFeedbacks);
        tvActiveNews = findViewById(R.id.tvActiveNews);

        // Profile / Settings
        ImageView ivAdminProfile = findViewById(R.id.ivAdminProfile);
        ivAdminProfile.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminSettingsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Cards (Placeholders for now)
        CardView cardSendNotification = findViewById(R.id.cardSendNotification);
        cardSendNotification.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminSendNotificationActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        CardView cardManageBikes = findViewById(R.id.cardManageBikes);
        cardManageBikes.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminManageBikesActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        CardView cardPublishNews = findViewById(R.id.cardPublishNews);
        cardPublishNews.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminPublishNewsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        CardView cardUserFeedbacks = findViewById(R.id.cardUserFeedbacks);
        cardUserFeedbacks.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUserFeedbacksActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        CardView cardTrendingBikes = findViewById(R.id.cardTrendingBikes);
        cardTrendingBikes.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminTrendingBikesActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        CardView cardReviewModeration = findViewById(R.id.cardReviewModeration);
        cardReviewModeration.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminReviewModerationActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Bottom Nav Settings
        View navDashboard = findViewById(R.id.navDashboard);
        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                // Already here, maybe scroll to top
            });
        }
        
        View navBikes = findViewById(R.id.navBikes);
        if (navBikes != null) {
            navBikes.setOnClickListener(v -> {
                startActivity(new Intent(AdminDashboardActivity.this, AdminManageBikesActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
        
        View navFeedbacks = findViewById(R.id.navFeedbacks);
        if (navFeedbacks != null) {
            navFeedbacks.setOnClickListener(v -> {
                startActivity(new Intent(AdminDashboardActivity.this, AdminUserFeedbacksActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        View navSettings = findViewById(R.id.navSettings);
        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                startActivity(new Intent(AdminDashboardActivity.this, AdminSettingsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        autoRefreshHandler = new Handler(Looper.getMainLooper());
        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadStats();
                autoRefreshHandler.postDelayed(this, 1000); // 1 second
            }
        };

        setupMaintenanceToggle();
    }

    private void setupMaintenanceToggle() {
        androidx.appcompat.widget.SwitchCompat switchMaintenance = findViewById(R.id.switchMaintenance);
        if (switchMaintenance == null) return;

        ApiService api = ApiClient.getRetrofitInstance().create(ApiService.class);
        
        // Fetch current status
        api.getAppStatus().enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean isMaintenance = response.body().get("maintenance_mode");
                    if (isMaintenance != null) {
                        switchMaintenance.setChecked(isMaintenance);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to fetch maintenance status", Toast.LENGTH_SHORT).show();
            }
        });

        // Toggle listener
        switchMaintenance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Map<String, Boolean> body = new java.util.HashMap<>();
            body.put("maintenance_mode", isChecked);
            api.toggleMaintenance(body).enqueue(new Callback<Map<String, Boolean>>() {
                @Override
                public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Boolean newState = response.body().get("maintenance_mode");
                        if (newState != null && newState != isChecked) {
                            switchMaintenance.setChecked(newState);
                        }
                        Toast.makeText(AdminDashboardActivity.this, isChecked ? "Maintenance Mode ON" : "Maintenance Mode OFF", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                    switchMaintenance.setChecked(!isChecked); // Revert
                    Toast.makeText(AdminDashboardActivity.this, "Failed to toggle maintenance mode", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoRefreshHandler.post(autoRefreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (autoRefreshHandler != null && autoRefreshRunnable != null) {
            autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
        }
    }

    private void loadStats() {
        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");
        
        if (token.isEmpty()) return;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAdminDashboardStats().enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Integer> stats = response.body();
                    tvTotalUsers.setText(String.valueOf(stats.get("total_users")));
                    tvTotalBikes.setText(String.valueOf(stats.get("total_bikes")));
                    tvNewFeedbacks.setText(String.valueOf(stats.get("new_feedbacks")));
                    tvActiveNews.setText(String.valueOf(stats.get("active_news")));
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
