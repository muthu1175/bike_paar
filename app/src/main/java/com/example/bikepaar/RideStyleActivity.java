package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RideStyleActivity extends AppCompatActivity implements RideStyleAdapter.OnRideStyleClickListener {

    private RecyclerView recyclerView;
    private RideStyleAdapter adapter;
    private List<RideStyleItem> rideStyleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_style);

        // Setup back button
        ImageView backButton = findViewById(R.id.ivBack);
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRideStyles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create ride style data
        rideStyleList = new ArrayList<>();
        rideStyleList.add(new RideStyleItem("Sports", R.drawable.ic_sports, "#EF4444"));
        rideStyleList.add(new RideStyleItem("Scooter", R.drawable.ic_scooter, "#14B8A6"));
        rideStyleList.add(new RideStyleItem("Cruiser", R.drawable.ic_cruiser, "#8B5CF6"));
        rideStyleList.add(new RideStyleItem("Commuter", R.drawable.ic_commuter, "#3B82F6"));
        rideStyleList.add(new RideStyleItem("Street", R.drawable.ic_street, "#F97316"));
        rideStyleList.add(new RideStyleItem("Super", R.drawable.ic_super, "#EAB308"));
        rideStyleList.add(new RideStyleItem("Scrambler", R.drawable.ic_scrambler, "#22C55E"));
        rideStyleList.add(new RideStyleItem("Adventure", R.drawable.ic_adventure, "#6366F1"));
        rideStyleList.add(new RideStyleItem("Tourer", R.drawable.ic_tourer, "#EC4899"));

        // Setup adapter
        adapter = new RideStyleAdapter(rideStyleList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRideStyleClick(RideStyleItem rideStyle) {
        // Handle click on ride style item
        switch (rideStyle.getName()) {
            case "Sports":
                Intent sportsIntent = new Intent(RideStyleActivity.this, SportsBikesActivity.class);
                startActivity(sportsIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Scooter":
                Intent scooterIntent = new Intent(RideStyleActivity.this, ScooterBikesActivity.class);
                startActivity(scooterIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Cruiser":
                Intent cruiserIntent = new Intent(RideStyleActivity.this, CruiserBikesActivity.class);
                startActivity(cruiserIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Commuter":
                Intent commuterIntent = new Intent(RideStyleActivity.this, CommuterBikesActivity.class);
                startActivity(commuterIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Street":
                Intent streetIntent = new Intent(RideStyleActivity.this, StreetBikesActivity.class);
                startActivity(streetIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Super":
                Intent superIntent = new Intent(RideStyleActivity.this, SuperBikesActivity.class);
                startActivity(superIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Scrambler":
                Intent scramblerIntent = new Intent(RideStyleActivity.this, ScramblerBikesActivity.class);
                startActivity(scramblerIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Adventure":
                Intent adventureIntent = new Intent(RideStyleActivity.this, AdventureBikesActivity.class);
                startActivity(adventureIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Tourer":
                Intent tourerIntent = new Intent(RideStyleActivity.this, TourerBikesActivity.class);
                startActivity(tourerIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
                Toast.makeText(this, "Selected: " + rideStyle.getName(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}