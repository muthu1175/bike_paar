package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

public class BudgetBikesActivity extends AppCompatActivity implements BikeAdapter.OnBikeClickListener {

    private RecyclerView bikesRecyclerView;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList;
    private TextView tvTitle;

    private String budgetRange = "";
    private int budgetPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_bikes);

        // Get budget range from intent
        Intent intent = getIntent();
        budgetRange = intent.getStringExtra("BUDGET_RANGE");
        budgetPosition = intent.getIntExtra("BUDGET_POSITION", 0);

        // Setup title
        tvTitle = findViewById(R.id.tvTitle);
        if (budgetRange != null) {
            tvTitle.setText(budgetRange + " Bikes");
        }

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        bikesRecyclerView = findViewById(R.id.bikesRecyclerView);
        bikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bikesRecyclerView.setHasFixedSize(true);

        // Prepare bike data based on budget position
        prepareBikeData();

        // Setup adapter with budget design
        bikeAdapter = new BikeAdapter(this, bikeList, this, true);
        bikesRecyclerView.setAdapter(bikeAdapter);
    }

    private void prepareBikeData() {
        bikeList = new ArrayList<>();

        switch (budgetPosition) {
            case 0: // ₹30k - ₹80k
                load30kTo80kBikes();
                break;
            case 1: // ₹80k - ₹1.5 Lakh
                load80kTo1_5LakhBikes();
                break;
            case 2: // ₹1.5 Lakh - ₹3 Lakh
                load1_5LakhTo3LakhBikes();
                break;
            case 3: // ₹3 Lakh - ₹5 Lakh
                load3LakhTo5LakhBikes();
                break;
            case 4: // ₹5 Lakh - ₹10 Lakh
                load5LakhTo10LakhBikes();
                break;
            case 5: // ₹10 Lakh - ₹30 Lakh
                load10LakhTo30LakhBikes();
                break;
            case 6: // Above ₹30 Lakh
                loadAbove30LakhBikes();
                break;
            default:
                Toast.makeText(this, "No bikes available for this range", Toast.LENGTH_SHORT).show();
        }

        // If no bikes found
        if (bikeList.isEmpty()) {
            Toast.makeText(this, "No bikes found in this price range", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAbove30LakhBikes() {
        // Above ₹30 Lakh bikes (Ultra premium)
        bikeList.add(new Bike(
                "37",
                "Ducati Panigale V4",
                "1103 cc",
                "15 kmpl",
                3500000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/panigale-v4-right-front-three-quarter-8.png",
                "Superbike",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "38",
                "BMW R 1250 GS Adventure",
                "1254 cc",
                "20 kmpl",
                2400000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/r-1250-gs-adventure-right-front-three-quarter-8.png",
                "Adventure",
                "Motorcycle",
                "Adventure"
        ));

        bikeList.add(new Bike(
                "39",
                "Harley-Davidson Street Glide",
                "1868 cc",
                "18 kmpl",
                3500000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/street-glide-right-front-three-quarter-8.png",
                "Touring",
                "Motorcycle",
                "Tour"
        ));

        bikeList.add(new Bike(
                "40",
                "KTM 1290 Super Duke R",
                "1301 cc",
                "16 kmpl",
                1900000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/1290-super-duke-r-right-front-three-quarter-8.png",
                "Beast",
                "Motorcycle",
                "Sport"
        ));
    }

    private void load10LakhTo30LakhBikes() {
        // ₹10 Lakh - ₹30 Lakh bikes (Premium heavyweights)
        bikeList.add(new Bike(
                "31",
                "Harley-Davidson Fat Boy",
                "1868 cc",
                "18 kmpl",
                1800000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/fat-boy-right-front-three-quarter-8.png",
                "Iconic",
                "Motorcycle",
                "Cruiser"
        ));

        bikeList.add(new Bike(
                "32",
                "Triumph Tiger 900",
                "888 cc",
                "22 kmpl",
                1400000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/tiger-900-right-front-three-quarter-8.png",
                "Adventure",
                "Motorcycle",
                "Adventure"
        ));

        bikeList.add(new Bike(
                "33",
                "Ducati Multistrada V4",
                "1158 cc",
                "18 kmpl",
                1900000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/multistrada-v4-right-front-three-quarter-8.png",
                "Touring",
                "Motorcycle",
                "Tour"
        ));

        bikeList.add(new Bike(
                "34",
                "BMW F 900 R",
                "895 cc",
                "20 kmpl",
                1100000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/f-900-r-right-front-three-quarter-8.png",
                "Naked",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "35",
                "Kawasaki Ninja ZX-10R",
                "998 cc",
                "15 kmpl",
                1600000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/ninja-zx-10r-right-front-three-quarter-8.png",
                "Superbike",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "36",
                "Indian Chief Dark Horse",
                "1890 cc",
                "18 kmpl",
                2100000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/chief-dark-horse-right-front-three-quarter-8.png",
                "Cruiser",
                "Motorcycle",
                "Cruiser"
        ));
    }

    private void load5LakhTo10LakhBikes() {
        // ₹5 Lakh - ₹10 Lakh bikes (Super sports and ADVs)
        bikeList.add(new Bike(
                "25",
                "Kawasaki Ninja 650",
                "649 cc",
                "25 kmpl",
                690000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/ninja-650-right-front-three-quarter-8.png",
                "Sport Tourer",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "26",
                "Harley-Davidson Street 750",
                "749 cc",
                "20 kmpl",
                550000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/street-750-right-front-three-quarter-8.png",
                "Cruiser",
                "Motorcycle",
                "Cruiser"
        ));

        bikeList.add(new Bike(
                "27",
                "Triumph Street Triple RS",
                "765 cc",
                "18 kmpl",
                980000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/street-triple-rs-right-front-three-quarter-8.png",
                "Sport",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "28",
                "BMW G 310 R",
                "313 cc",
                "30 kmpl",
                320000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/g-310-r-right-front-three-quarter-8.png",
                "Premium",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "29",
                "KTM 390 Adventure",
                "373 cc",
                "28 kmpl",
                340000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/390-adventure-right-front-three-quarter-8.png",
                "Adventure",
                "Motorcycle",
                "Adventure"
        ));

        bikeList.add(new Bike(
                "30",
                "Royal Enfield Himalayan 450",
                "452 cc",
                "30 kmpl",
                280000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/himalayan-450-right-front-three-quarter-8.png",
                "Adventure",
                "Motorcycle",
                "Adventure"
        ));
    }

    private void load3LakhTo5LakhBikes() {
        // ₹3 Lakh - ₹5 Lakh bikes (Mid-range performance)
        bikeList.add(new Bike(
                "20",
                "Royal Enfield Interceptor 650",
                "648 cc",
                "25 kmpl",
                310000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/interceptor-650-right-front-three-quarter-8.png",
                "Classic",
                "Motorcycle",
                "Cruiser"
        ));

        bikeList.add(new Bike(
                "21",
                "Royal Enfield Continental GT 650",
                "648 cc",
                "25 kmpl",
                320000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/continental-gt-650-right-front-three-quarter-8.png",
                "Cafe Racer",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "22",
                "KTM 390 Duke",
                "373 cc",
                "30 kmpl",
                290000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/390-duke-right-front-three-quarter-8.png",
                "Performance",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "23",
                "Kawasaki Ninja 300",
                "296 cc",
                "30 kmpl",
                340000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/ninja-300-right-front-three-quarter-8.png",
                "Sport",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "24",
                "Benelli Imperiale 400",
                "374 cc",
                "30 kmpl",
                220000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/imperiale-400-right-front-three-quarter-8.png",
                "Classic",
                "Motorcycle",
                "Cruiser"
        ));
    }

    private void load1_5LakhTo3LakhBikes() {
        // ₹1.5 Lakh - ₹3 Lakh bikes (Entry sports and cruisers)
        bikeList.add(new Bike(
                "12",
                "Royal Enfield Classic 350",
                "349 cc",
                "35 kmpl",
                193000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/classic-350-right-front-three-quarter-8.png",
                "Iconic",
                "Motorcycle",
                "Cruiser"
        ));

        bikeList.add(new Bike(
                "13",
                "Royal Enfield Meteor 350",
                "349 cc",
                "35 kmpl",
                202000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/meteor-350-right-front-three-quarter-8.png",
                "Cruiser",
                "Motorcycle",
                "Cruiser"
        ));

        bikeList.add(new Bike(
                "14",
                "KTM 200 Duke",
                "199.5 cc",
                "35 kmpl",
                195000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/200-duke-right-front-three-quarter-8.png",
                "Sporty",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "15",
                "Bajaj Dominar 250",
                "248.8 cc",
                "35 kmpl",
                172000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/dominar-250-right-front-three-quarter-8.png",
                "Powerful",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "16",
                "TVS Apache RTR 200 4V",
                "197.75 cc",
                "40 kmpl",
                148000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/apache-rtr-200-4v-right-front-three-quarter-8.png",
                "Track Inspired",
                "Motorcycle",
                "Sport"
        ));

        bikeList.add(new Bike(
                "17",
                "Hero Xpulse 200T",
                "199.6 cc",
                "45 kmpl",
                138000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/xpulse-200t-right-front-three-quarter-8.png",
                "Adventure",
                "Motorcycle",
                "Adventure"
        ));

        bikeList.add(new Bike(
                "18",
                "Honda CB200X",
                "184.4 cc",
                "40 kmpl",
                147000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/cb200x-right-front-three-quarter-8.png",
                "Adventure Tourer",
                "Motorcycle",
                "Adventure"
        ));

        bikeList.add(new Bike(
                "19",
                "Yamaha FZS-FI V3",
                "149 cc",
                "50 kmpl",
                129000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/fz-s-fi-v3-right-front-three-quarter-8.png",
                "Street Fighter",
                "Motorcycle",
                "Daily use"
        ));
    }

    private void load80kTo1_5LakhBikes() {
        // ₹80k - ₹1.5 Lakh bikes (Premium commuters and 125cc bikes)
        bikeList.add(new Bike(
                "6",
                "TVS Raider 125",
                "124.8 cc",
                "67 kmpl",
                95000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/raider-125-right-front-three-quarter-8.png",
                "Sporty",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "7",
                "Bajaj Pulsar 125",
                "124.4 cc",
                "55 kmpl",
                89000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/pulsar-125-right-front-three-quarter-8.png",
                "Popular",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "8",
                "Honda SP 125",
                "124 cc",
                "65 kmpl",
                92000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/sp-125-right-front-three-quarter-8.png",
                "High Mileage",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "9",
                "Hero Xtreme 125R",
                "124.7 cc",
                "55 kmpl",
                99000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/xtreme-125r-right-front-three-quarter-8.png",
                "Sporty",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "10",
                "Yamaha FZ FI",
                "149 cc",
                "45 kmpl",
                116000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/fz-fi-right-front-three-quarter-8.png",
                "Street Fighter",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "11",
                "Suzuki Gixxer",
                "155 cc",
                "45 kmpl",
                135000,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/gixxer-right-front-three-quarter-8.png",
                "Sporty",
                "Motorcycle",
                "Sport"
        ));
    }

    private void load30kTo80kBikes() {
        // ₹30k - ₹80k bikes (Entry level commuters)
        bikeList.add(new Bike(
                "1",
                "Splendor Plus",
                "97.2 cc",
                "60 kmpl",
                72420,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/splendor-plus-right-front-three-quarter-8.png",
                "Top Seller",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "2",
                "HF Deluxe",
                "97.2 cc",
                "65 kmpl",
                59990,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/hf-deluxe-right-front-three-quarter-8.png",
                "High Mileage",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "3",
                "Platina 100",
                "102 cc",
                "70 kmpl",
                67808,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/platina-100-right-front-three-quarter-8.png",
                "High Mileage",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "4",
                "CT 110X",
                "115.45 cc",
                "70 kmpl",
                69216,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/ct-110x-right-front-three-quarter-8.png",
                "Durable",
                "Motorcycle",
                "Daily use"
        ));

        bikeList.add(new Bike(
                "5",
                "Sport 110",
                "109.7 cc",
                "70 kmpl",
                61500,
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/103155/sport-110-right-front-three-quarter-8.png",
                "",
                "Motorcycle",
                "Daily use"
        ));
    }

    @Override
    public void onViewDetailsClick(Bike bike) {
        // Navigation handled by Adapter
    }

    @Override
    public void onFavoriteClick(Bike bike, boolean isFavorite) {
        String message = isFavorite ? "Added to favorites: " : "Removed from favorites: ";
        Toast.makeText(this, message + bike.name, Toast.LENGTH_SHORT).show();
        // Update favorite in database using Retrofit
    }
}