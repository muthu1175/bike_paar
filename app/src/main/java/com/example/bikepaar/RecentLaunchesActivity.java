package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class RecentLaunchesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private RecentLaunchesAdapter adapter;
    private ImageView btnBack, btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_launches);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());


        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentLaunchesAdapter(getBikeList());
        recyclerView.setAdapter(adapter);

        // Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(this, FavouriteActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private List<BikeModel> getBikeList() {
        List<BikeModel> list = new ArrayList<>();
        list.add(new BikeModel(
                "Ducati Panigale V4",
                "Launched 2 days ago",
                "₹24,995",
                "299 km/h · 1103 cc",
                true,
                R.drawable.sample_bike,
                "Just Arrived"
        ));
        list.add(new BikeModel(
                "BMW S1000 RR",
                "Ultimate performance for the road.",
                "₹16,995",
                "Oct 15",
                false,
                R.drawable.sample_bike,
                "New"
        ));
        list.add(new BikeModel(
                "Kawasaki Ninja ZX-10R",
                "Race-ready aerodynamics.",
                "₹17,199",
                "Oct 10",
                false,
                R.drawable.sample_bike,
                ""
        ));
        list.add(new BikeModel(
                "Royal Enfield Hunter 350",
                "Urban retro roadster.",
                "₹4,300",
                "Sep 28",
                false,
                R.drawable.sample_bike,
                ""
        ));
        list.add(new BikeModel(
                "Yamaha YZF-R1M",
                "MotoGP technology for the street.",
                "₹26,999",
                "Sep 15",
                false,
                R.drawable.sample_bike,
                ""
        ));
        return list;
    }

    // Bike Model Class
    public static class BikeModel {
        String title;
        String description;
        String price;
        String specs;
        boolean isFeatured;
        int imageRes;
        String tag;

        public BikeModel(String title, String description, String price, String specs,
                         boolean isFeatured, int imageRes, String tag) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.specs = specs;
            this.isFeatured = isFeatured;
            this.imageRes = imageRes;
            this.tag = tag;
        }
    }
}
