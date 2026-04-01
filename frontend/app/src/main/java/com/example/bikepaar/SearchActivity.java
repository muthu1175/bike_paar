package com.example.bikepaar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView aiButton;
    private EditText searchEditText;

    // 🔹 Added views for search state
    private ImageView bigSearchIcon;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private LinearLayout chipsContainer;

    // 🔹 SEARCH RESULT
    private RecyclerView rvSearchResults;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();

    // 🔹 For live search debounce
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    private boolean isSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        isSelectionMode = getIntent().getBooleanExtra("IS_SELECTION_MODE", false);

        // ---------------- TOP BAR ----------------
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ImageView ivBell = findViewById(R.id.ivBell);

        ivMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, MenuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ivBell.setOnClickListener(v ->
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());

        // ---------------- Initialize views for search state ----------------
        bigSearchIcon = findViewById(R.id.bigSearchIcon);
        titleTextView = findViewById(R.id.titleTextView);
        subtitleTextView = findViewById(R.id.subtitleTextView);
        if (isSelectionMode) {
            titleTextView.setText("Select a Bike");
            subtitleTextView.setText("Choose a bike to compare");
        }
        chipsContainer = findViewById(R.id.chipsContainer);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        // ---------------- SEARCH ----------------
        LinearLayout searchPill = findViewById(R.id.searchPill);
        searchEditText = findViewById(R.id.searchEditText);

        searchPill.setOnClickListener(v -> {
            searchEditText.requestFocus();
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        });

        // 🔹 LIVE SEARCH - Type panna athan search agum
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove previous search requests
                handler.removeCallbacks(searchRunnable);

                String query = s.toString().trim();

                if (query.isEmpty()) {
                    // If empty, show initial screen
                    showSearchResultsLayout(false);
                } else {
                    // Delay search by 500ms to avoid too many API calls
                    searchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            performSearch(query);
                        }
                    };
                    handler.postDelayed(searchRunnable, 500);
                }
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE) {

                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                }

                // Hide keyboard
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });

        // ---------------- RECYCLER VIEW ----------------
        rvSearchResults.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));

        // Initially hide RecyclerView (it will show when search results come)
        rvSearchResults.setVisibility(View.GONE);

        // ✅ CORRECT CONSTRUCTOR WITH LISTENER
        bikeAdapter = new BikeAdapter(this, bikeList, new BikeAdapter.OnBikeClickListener() {
            @Override
            public void onViewDetailsClick(Bike bike) {
                if (isSelectionMode) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SELECTED_BIKE", bike);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }

            @Override
            public void onFavoriteClick(Bike bike, boolean isFavorite) {
                // Handled internally by Adapter now
            }
        }, false, isSelectionMode);
        rvSearchResults.setAdapter(bikeAdapter);

        // ---------------- CHIPS ----------------
        int[] chipIds = {
                R.id.chipRoyalEnfield,
                R.id.chipHonda,
                R.id.chipHero,
                R.id.chipYamaha,
                R.id.chipAdventure,
                R.id.chipElectric
        };

        for (int id : chipIds) {
            TextView chip = findViewById(id);
            chip.setOnClickListener(v -> {
                String text = ((TextView) v).getText().toString();
                searchEditText.setText(text);
                performSearch(text);
            });
        }

        // ---------------- AI BUTTON ----------------
        aiButton = findViewById(R.id.aiButton);
        aiButton.setOnClickListener(v -> {
            Intent i = new Intent(this, AiQuestionActivity.class);
            i.putExtra("step", 1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ---------------- BOTTOM NAV ----------------
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(this, FavouriteActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        });
    }

    // ================= SEARCH API =================
    private void performSearch(String query) {
        showSearchResultsLayout(true);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        // ✅ Pass Authorization Token
        String rawToken = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE).getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;
        
        api.searchBikes(token, query).enqueue(new Callback<List<Bike>>() {

            @Override
            public void onResponse(Call<List<Bike>> call,
                                   Response<List<Bike>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    bikeList.clear();
                    bikeList.addAll(response.body());
                    bikeAdapter.notifyDataSetChanged();

                    if (bikeList.isEmpty()) {
                        // Show no results message in RecyclerView or keep empty
                    }
                } else {
                    bikeList.clear();
                    bikeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                bikeList.clear();
                bikeAdapter.notifyDataSetChanged();
                Toast.makeText(SearchActivity.this,
                        "Search failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= SHOW/HIDE SEARCH LAYOUT =================
    private void showSearchResultsLayout(boolean showResults) {
        if (showResults) {
            // Hide initial UI elements
            bigSearchIcon.setVisibility(View.GONE);
            titleTextView.setVisibility(View.GONE);
            subtitleTextView.setVisibility(View.GONE);
            chipsContainer.setVisibility(View.GONE);

            // Show search results
            rvSearchResults.setVisibility(View.VISIBLE);
        } else {
            // Show initial UI elements
            bigSearchIcon.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            subtitleTextView.setVisibility(View.VISIBLE);
            chipsContainer.setVisibility(View.VISIBLE);

            // Hide search results
            rvSearchResults.setVisibility(View.GONE);

            // Clear search results
            bikeList.clear();
            bikeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (rvSearchResults.getVisibility() == View.VISIBLE) {
            // If showing search results, go back to initial state
            showSearchResultsLayout(false);
            searchEditText.setText("");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}