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

public class MostPopularActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private MostPopularAdapter adapter;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_popular);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());


        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MostPopularAdapter(getBikeList());
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
                "Yamaha R15 V4",
                "The R15 V4 offers track-inspired performance with a 155cc liquid-cooled engine and quick shifter.",
                "₹1,82,500",
                "High Demand",
                "#1 Seller",
                "",
                false,
                0
        ));
        list.add(new BikeModel(
                "Royal Enfield Classic",
                "Timeless design meets modern reliability. The ultimate cruiser for long rides and city commutes.",
                "₹2,20,000",
                "4.8 (12k+ sold)",
                "",
                "★",
                false,
                0
        ));
        list.add(new BikeModel(
                "KTM Duke 390",
                "The corner rocket. Aggressive styling, explosive power, and top-tier electronics package.",
                "₹3,60,000",
                "4.9 (8k+ sold)",
                "Hot",
                "★",
                false,
                0
        ));
        list.add(new BikeModel(
                "Honda CBR 650R",
                "A middleweight sports tourer with inline-4 smoothness and everyday practicality.",
                "₹9,30,000",
                "Top Rated",
                "",
                "",
                false,
                0
        ));
        list.add(new BikeModel(
                "BMW G 310 R",
                "Premium roadster experience in a compact package. Agile handling for the urban jungle.",
                "₹2,85,000",
                "4.6 (5k+ sold)",
                "",
                "★",
                false,
                0
        ));
        return list;
    }

    // Bike Model Class
    public static class BikeModel {
        String title;
        String description;
        String price;
        String tag;
        String badge;
        String ratingIcon;
        boolean isFavorite;
        int imageRes;

        public BikeModel(String title, String description, String price, String tag,
                         String badge, String ratingIcon, boolean isFavorite, int imageRes) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.tag = tag;
            this.badge = badge;
            this.ratingIcon = ratingIcon;
            this.isFavorite = isFavorite;
            this.imageRes = imageRes;
        }
    }
}
