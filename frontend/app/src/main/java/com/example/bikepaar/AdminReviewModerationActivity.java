package com.example.bikepaar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class AdminReviewModerationActivity extends AppCompatActivity {

    private RecyclerView rvBikeReviews;
    private ProgressBar progressBar;
    private AdminBikeReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review_moderation);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());
        
        ImageView ivExit = findViewById(R.id.ivExit);
        ivExit.setOnClickListener(v -> finish());

        rvBikeReviews = findViewById(R.id.rvBikeReviews);
        progressBar = findViewById(R.id.progressBar);

        rvBikeReviews.setLayoutManager(new LinearLayoutManager(this));

        fetchBikeReviews();
    }

    private void fetchBikeReviews() {
        progressBar.setVisibility(View.VISIBLE);
        rvBikeReviews.setVisibility(View.GONE);

        android.content.SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String rawToken = sp.getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAdminBikeReviews(token).enqueue(new retrofit2.Callback<java.util.List<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, retrofit2.Response<java.util.List<java.util.Map<String, Object>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    try {
                        String jsonString = new com.google.gson.Gson().toJson(response.body());
                        JSONArray jsonArray = new JSONArray(jsonString);
                        
                        adapter = new AdminBikeReviewAdapter(jsonArray, (reviewId, position) -> deleteReview(reviewId, position));
                        rvBikeReviews.setAdapter(adapter);
                        rvBikeReviews.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AdminReviewModerationActivity.this, "Error parsing reviews", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminReviewModerationActivity.this, "No reviews found or error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminReviewModerationActivity.this, "Failed to load reviews: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReview(int reviewId, int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Review");
        builder.setMessage("Are you sure you want to delete this review?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            progressBar.setVisibility(View.VISIBLE);
            
            android.content.SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
            String rawToken = sp.getString("TOKEN", "");
            String token = rawToken.isEmpty() ? null : "Token " + rawToken;

            ApiService api = ApiClient.getClient().create(ApiService.class);
            api.deleteAdminBikeReview(reviewId, token).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminReviewModerationActivity.this, "Review deleted", Toast.LENGTH_SHORT).show();
                        if (adapter != null) {
                            adapter.removeReview(position);
                        }
                    } else {
                        Toast.makeText(AdminReviewModerationActivity.this, "Failed to delete review", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminReviewModerationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
