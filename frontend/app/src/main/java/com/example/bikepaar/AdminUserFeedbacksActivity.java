package com.example.bikepaar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class AdminUserFeedbacksActivity extends AppCompatActivity {

    private RecyclerView rvFeedbacks;
    private ProgressBar progressBar;
    private AdminAppReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_feedbacks);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        rvFeedbacks = findViewById(R.id.rvFeedbacks);
        progressBar = findViewById(R.id.progressBar);

        rvFeedbacks.setLayoutManager(new LinearLayoutManager(this));

        // Setup Bottom Nav
        View navDashboard = findViewById(R.id.navDashboard);
        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new android.content.Intent(AdminUserFeedbacksActivity.this, AdminDashboardActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
        
        View navBikes = findViewById(R.id.navBikes);
        if (navBikes != null) {
            navBikes.setOnClickListener(v -> {
                startActivity(new android.content.Intent(AdminUserFeedbacksActivity.this, AdminManageBikesActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
        
        View navFeedbacks = findViewById(R.id.navFeedbacks);
        if (navFeedbacks != null) {
            navFeedbacks.setOnClickListener(v -> {
                // Already here
            });
        }

        View navSettings = findViewById(R.id.navSettings);
        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                startActivity(new android.content.Intent(AdminUserFeedbacksActivity.this, AdminSettingsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        fetchReviews();
    }

    private void fetchReviews() {
        progressBar.setVisibility(View.VISIBLE);
        rvFeedbacks.setVisibility(View.GONE);

        // Fetch using OkHttp
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8000/api/app-reviews/";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminUserFeedbacksActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
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
                                adapter = new AdminAppReviewAdapter(jsonArray, review -> showReplyDialog(review));
                                rvFeedbacks.setAdapter(adapter);
                                rvFeedbacks.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(AdminUserFeedbacksActivity.this, "No feedbacks yet", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    private void showReplyDialog(JSONObject review) {
        try {
            int userId = review.getInt("user_id");
            String userName = review.getString("user_name");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reply to " + userName);

            final EditText input = new EditText(this);
            input.setHint("Write your reply here...");
            builder.setView(input);

            builder.setPositiveButton("Send", (dialog, which) -> {
                String replyMsg = input.getText().toString().trim();
                if (!replyMsg.isEmpty()) {
                    sendReply(userId, replyMsg);
                } else {
                    Toast.makeText(this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendReply(int userId, String replyMessage) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", userId);
        payload.put("reply_message", replyMessage);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.replyFeedback(payload).enqueue(new retrofit2.Callback<Map<String, String>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, String>> call, retrofit2.Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminUserFeedbacksActivity.this, "Reply sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminUserFeedbacksActivity.this, "Failed to send reply", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(AdminUserFeedbacksActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
