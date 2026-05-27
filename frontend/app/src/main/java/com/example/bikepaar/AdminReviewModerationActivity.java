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

        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8000/api/admin/bike-reviews/";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminReviewModerationActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (jsonArray.length() > 0) {
                                adapter = new AdminBikeReviewAdapter(jsonArray);
                                rvBikeReviews.setAdapter(adapter);
                                rvBikeReviews.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(AdminReviewModerationActivity.this, "No reviews found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminReviewModerationActivity.this, "Error parsing reviews", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminReviewModerationActivity.this, "Error fetching reviews", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
