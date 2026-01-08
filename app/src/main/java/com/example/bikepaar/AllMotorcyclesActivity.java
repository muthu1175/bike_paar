package com.example.bikepaar;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllMotorcyclesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SportsBikeAdapter adapter;
    private List<SportsBike> bikeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_motorcycle);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        fetchAllBikes();
    }

    private void fetchAllBikes() {
        android.content.SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String rawToken = prefs.getString("TOKEN", "");
        String token = rawToken.isEmpty() ? null : "Token " + rawToken;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllBikes(token).enqueue(new Callback<List<SportsBike>>() {
            @Override
            public void onResponse(Call<List<SportsBike>> call, Response<List<SportsBike>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bikeList = response.body();
                    adapter = new SportsBikeAdapter(bikeList, bike -> {
                        // Handle click if needed, adapter handles it internally too
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(AllMotorcyclesActivity.this, "Failed to load bikes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SportsBike>> call, Throwable t) {
                Toast.makeText(AllMotorcyclesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}