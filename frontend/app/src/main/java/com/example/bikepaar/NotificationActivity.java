package com.example.bikepaar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        rvNotifications = findViewById(R.id.rvNotifications);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        fetchNotifications();
    }

    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        android.content.SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String rawToken = sp.getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getNotifications(token).enqueue(new retrofit2.Callback<java.util.List<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, retrofit2.Response<java.util.List<java.util.Map<String, Object>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    try {
                        String jsonString = new com.google.gson.Gson().toJson(response.body());
                        JSONArray jsonArray = new JSONArray(jsonString);
                        
                        try {
                            double idD = (Double) response.body().get(0).get("id");
                            int latestId = (int) idD;
                            sp.edit().putInt("LAST_SEEN_NOTIFICATION_ID", latestId).apply();
                        } catch (Exception ignored) {}
                        
                        adapter = new NotificationAdapter(jsonArray);
                        rvNotifications.setAdapter(adapter);
                        rvNotifications.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(NotificationActivity.this, "Failed to load notifications: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
