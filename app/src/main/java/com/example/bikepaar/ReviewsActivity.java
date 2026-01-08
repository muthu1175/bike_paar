package com.example.bikepaar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnSubmitReview;
    private EditText editReview;
    private TextView[] starButtons = new TextView[5];
    private int currentRating = 0;
    private androidx.recyclerview.widget.RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        editReview = findViewById(R.id.editReview);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);

        // Initialize RecyclerView
        recyclerViewReviews.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        recyclerViewReviews.setAdapter(reviewAdapter);

        // Initialize star buttons
        starButtons[0] = findViewById(R.id.star1);
        starButtons[1] = findViewById(R.id.star2);
        starButtons[2] = findViewById(R.id.star3);
        starButtons[3] = findViewById(R.id.star4);
        starButtons[4] = findViewById(R.id.star5);

        // Fetch initial reviews
        fetchReviews();

        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up star rating click listeners
        for (int i = 0; i < starButtons.length; i++) {
            final int rating = i + 1;
            starButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRating(rating);
                }
            });
        }

        // Submit review button
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewText = editReview.getText().toString().trim();

                if (currentRating == 0) {
                    Toast.makeText(ReviewsActivity.this, "Please select a rating", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (reviewText.isEmpty()) {
                    Toast.makeText(ReviewsActivity.this, "Please write your review", Toast.LENGTH_SHORT).show();
                    return;
                }

                 // Backend Integration
                android.content.SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                String token = sp.getString("TOKEN", "");

                if (token.isEmpty()) {
                    Toast.makeText(ReviewsActivity.this, "Please login first", Toast.LENGTH_SHORT).show();
                    return;
                }

                java.util.Map<String, Object> body = new java.util.HashMap<>();
                body.put("rating", currentRating);
                body.put("review", reviewText);

                ApiService api = ApiClient.getClient().create(ApiService.class);
                api.addAppReview("Token " + token, body).enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, String>> call, retrofit2.Response<java.util.Map<String, String>> response) {
                        if (response.isSuccessful()) {
                             Toast.makeText(ReviewsActivity.this,
                                "Review submitted! Rating: " + currentRating + " stars",
                                Toast.LENGTH_SHORT).show();
                            
                            editReview.setText("");
                            setRating(0);
                            
                            // REFRESH REVIEWS IMMEDIATELY
                            fetchReviews();

                        } else {
                             Toast.makeText(ReviewsActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {
                         Toast.makeText(ReviewsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchReviews() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAppReviews().enqueue(new retrofit2.Callback<java.util.List<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, retrofit2.Response<java.util.List<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewAdapter.setReviews(response.body());
                } else {
                    // Handle empty or error state if needed
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, Throwable t) {
                // Handle failure
                Toast.makeText(ReviewsActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRating(int rating) {
        currentRating = rating;
        for (int i = 0; i < starButtons.length; i++) {
            if (i < rating) {
                starButtons[i].setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                starButtons[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}