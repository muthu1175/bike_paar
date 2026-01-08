package com.example.bikepaar;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReviewsActivity extends AppCompatActivity {

    private ImageView[] stars = new ImageView[5];
    private int currentRating = 4; // Default 4 stars
    private EditText reviewText;
    private Button btnSubmit;
    private RecyclerView recyclerViewBikeReviews;
    private ReviewAdapter reviewAdapter;

    private String bikeName; // Using Name as ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);

        // Get Bike Name/ID
        bikeName = getIntent().getStringExtra("BIKE_NAME");

        // Back Button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Initialize star rating system
        initializeStarRating();

        // Initialize views
        reviewText = findViewById(R.id.review_text);
        btnSubmit = findViewById(R.id.btn_submit_review);
        recyclerViewBikeReviews = findViewById(R.id.recyclerViewBikeReviews);

        // Initialize RecyclerView
        recyclerViewBikeReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        recyclerViewBikeReviews.setAdapter(reviewAdapter);

        // Submit Review Button
        btnSubmit.setOnClickListener(v -> submitReview());

        loadReviews();
    }

    private void loadReviews() {
        if (bikeName == null) return;

        android.content.SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getBikeReviews("Token " + token, bikeName).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                 TextView tvNoReviews = findViewById(R.id.tv_no_reviews);
                 if (response.isSuccessful() && response.body() != null) {
                     List<Map<String, Object>> reviews = response.body();
                     reviewAdapter.setReviews(reviews);
                     
                     if (reviews.isEmpty()) {
                         tvNoReviews.setVisibility(View.VISIBLE);
                         recyclerViewBikeReviews.setVisibility(View.GONE);
                     } else {
                         tvNoReviews.setVisibility(View.VISIBLE);
                         recyclerViewBikeReviews.setVisibility(View.VISIBLE);
                         recyclerViewBikeReviews.smoothScrollToPosition(0);
                     }
                 } else {
                     tvNoReviews.setVisibility(View.VISIBLE);
                     try {
                         String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                         tvNoReviews.setText("Failed: " + response.code() + " - " + errorBody);
                     } catch (Exception e) {
                         tvNoReviews.setText("Failed: " + response.code());
                     }
                 }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                TextView tvNoReviews = findViewById(R.id.tv_no_reviews);
                tvNoReviews.setVisibility(View.VISIBLE);
                tvNoReviews.setText("Network/App Error: " + t.getMessage());
                t.printStackTrace(); 
                Toast.makeText(UserReviewsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeStarRating() {
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        // Set default rating (4 stars)
        updateStars(currentRating);

        // Add click listeners to stars
        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i;
            stars[i].setOnClickListener(v -> {
                currentRating = starIndex + 1;
                updateStars(currentRating);
            });
        }
    }

    private void updateStars(int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void submitReview() {
        String review = reviewText.getText().toString().trim();

        if (currentRating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (review.isEmpty()) {
            Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bikeName == null) {
             Toast.makeText(this, "Bike not identified", Toast.LENGTH_SHORT).show();
             return;
        }

        // Backend Integration
        android.content.SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("bike_id", bikeName); // Sending Name as ID
        body.put("rating", currentRating);
        body.put("review", review);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.addBikeReview("Token " + token, body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserReviewsActivity.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    reviewText.setText("");
                    currentRating = 4;
                    updateStars(currentRating);
                    
                    hideKeyboard();
                    
                    // Instant Refresh
                    Toast.makeText(UserReviewsActivity.this, "Refreshing reviews...", Toast.LENGTH_SHORT).show();
                    loadReviews();
                } else {
                    Toast.makeText(UserReviewsActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(UserReviewsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}