package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class BrandActivity extends AppCompatActivity implements BrandAdapter.OnBrandClickListener {

    private RecyclerView recyclerView;
    private BrandAdapter adapter;
    private List<BrandItem> brandList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewBrands);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup bottom navigation
        setupBottomNavigation();

    }

    private void setupRecyclerView() {
        // Create brand list
        brandList = new ArrayList<>();

        // Add all 45 brands
        brandList.add(new BrandItem("Brixton", R.drawable.brand_brixton));
        brandList.add(new BrandItem("Yamaha", R.drawable.brand_yamaha));
        brandList.add(new BrandItem("Bajaj", R.drawable.brand_bajaj));
        brandList.add(new BrandItem("Harley Davidson", R.drawable.brand_harleydavidson));
        brandList.add(new BrandItem("KTM", R.drawable.brand_ktm));
        brandList.add(new BrandItem("Kawasaki", R.drawable.brand_kawasaki));
        brandList.add(new BrandItem("Royal Enfield", R.drawable.brand_royal_enfield));
        brandList.add(new BrandItem("Honda", R.drawable.brand_honda));
        brandList.add(new BrandItem("Hero", R.drawable.brand_hero));
        brandList.add(new BrandItem("Suzuki", R.drawable.brand_suzuki));
        brandList.add(new BrandItem("Aprilia", R.drawable.brand_aprilia));
        brandList.add(new BrandItem("Vespa", R.drawable.brand_vespa));
        brandList.add(new BrandItem("Benelli", R.drawable.brand_benelli));
        brandList.add(new BrandItem("Triumph", R.drawable.brand_triumph));
        brandList.add(new BrandItem("BMW", R.drawable.brand_bmw));
        brandList.add(new BrandItem("Ducati", R.drawable.brand_ducati));
        brandList.add(new BrandItem("Jawa", R.drawable.brand_jawa));
        brandList.add(new BrandItem("TVS", R.drawable.brand_tvs));
        brandList.add(new BrandItem("Ola", R.drawable.brand_ola));
        brandList.add(new BrandItem("BSA", R.drawable.brand_bsa1));
        brandList.add(new BrandItem("Indian", R.drawable.brand_indian));
        brandList.add(new BrandItem("Yezdi", R.drawable.brand_yezdi));
        brandList.add(new BrandItem("Keeway", R.drawable.brand_keeway1));
        brandList.add(new BrandItem("Vida", R.drawable.brand_vida));
        brandList.add(new BrandItem("Zontes", R.drawable.brand_zontes));
        brandList.add(new BrandItem("River", R.drawable.brand_river1));
        brandList.add(new BrandItem("QJMotor", R.drawable.brand_qjmotor));
        brandList.add(new BrandItem("Revolt", R.drawable.brand_revolt));
        brandList.add(new BrandItem("VLF", R.drawable.brand_vlf));
        brandList.add(new BrandItem("Oben", R.drawable.brand_oben));
        brandList.add(new BrandItem("Ampere", R.drawable.brand_ampere));
        brandList.add(new BrandItem("Motovolt", R.drawable.brand_motovolt));
        brandList.add(new BrandItem("Simple", R.drawable.brand_simple));
        brandList.add(new BrandItem("Matter", R.drawable.brand_matter));
        brandList.add(new BrandItem("Norton", R.drawable.brand_norton));
        brandList.add(new BrandItem("Ultraviolette", R.drawable.brand_ultraviolette));
        brandList.add(new BrandItem("CFMoto", R.drawable.brand_cfmoto));
        brandList.add(new BrandItem("Bounce Infinity", R.drawable.brand_bounce_infinity));
        brandList.add(new BrandItem("Ferrato", R.drawable.brand_ferrato));
        brandList.add(new BrandItem("Kinetic Green", R.drawable.brand_kinetic_green));
        brandList.add(new BrandItem("PURE", R.drawable.brand_pure));
        brandList.add(new BrandItem("Lectrix", R.drawable.brand_lectrix));
        brandList.add(new BrandItem("Evolet", R.drawable.brand_evolet));
        brandList.add(new BrandItem("Hop", R.drawable.brand_hop));
        brandList.add(new BrandItem("Joy e-bike", R.drawable.brand_joy_ebike));

        // Setup adapter
        adapter = new BrandAdapter(brandList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent i = new Intent(BrandActivity.this, HomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_search) {
                Intent i = new Intent(BrandActivity.this, SearchActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_fav) {
                Intent i = new Intent(BrandActivity.this, FavouriteActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(BrandActivity.this, ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBrandClick(int position, android.widget.ImageView sharedImageView) {
        BrandItem brand = brandList.get(position);

        // Get the brand logo resource ID
        int logoResId = getBrandLogoResource(brand.getName());

        Intent intent = new Intent(BrandActivity.this, BrandDetailsActivity.class);
        intent.putExtra("BRAND_NAME", brand.getName());
        intent.putExtra("BRAND_LOGO", logoResId);
        
        // Shared Element Transition
        android.app.ActivityOptions options = android.app.ActivityOptions.makeSceneTransitionAnimation(
                BrandActivity.this,
                sharedImageView,
                "shared_brand_logo"
        );

        startActivity(intent, options.toBundle());
    }

    private int getBrandLogoResource(String brandName) {
        // Map brand names to drawable resources
        switch (brandName.toLowerCase()) {
            case "brixton": return R.drawable.brand_brixton;
            case "yamaha": return R.drawable.brand_yamaha;
            case "bajaj": return R.drawable.brand_bajaj;
            case "harley davidson": return R.drawable.brand_harleydavidson;
            case "ktm": return R.drawable.brand_ktm;
            case "kawasaki": return R.drawable.brand_kawasaki;
            case "royal enfield": return R.drawable.brand_royal_enfield;
            case "honda": return R.drawable.brand_honda;
            case "hero": return R.drawable.brand_hero;
            case "suzuki": return R.drawable.brand_suzuki;
            case "aprilia": return R.drawable.brand_aprilia;
            case "vespa": return R.drawable.brand_vespa;
            case "benelli": return R.drawable.brand_benelli;
            case "triumph": return R.drawable.brand_triumph;
            case "bmw": return R.drawable.brand_bmw;
            case "ducati": return R.drawable.brand_ducati;
            case "jawa": return R.drawable.brand_jawa;
            case "tvs": return R.drawable.brand_tvs;
            case "ola": return R.drawable.brand_ola;
            case "bsa": return R.drawable.brand_bsa1;
            case "indian": return R.drawable.brand_indian;
            case "yezdi": return R.drawable.brand_yezdi;
            case "keeway": return R.drawable.brand_keeway;
            case "vida": return R.drawable.brand_vida;
            case "zontes": return R.drawable.brand_zontes;
            case "river": return R.drawable.brand_river1;
            case "qjmotor": return R.drawable.brand_qjmotor;
            case "revolt": return R.drawable.brand_revolt;
            case "vlf": return R.drawable.brand_vlf;
            case "oben": return R.drawable.brand_oben;
            case "ampere": return R.drawable.brand_ampere;
            case "motovolt": return R.drawable.brand_motovolt;
            case "simple": return R.drawable.brand_simple;
            case "matter": return R.drawable.brand_matter;
            case "norton": return R.drawable.brand_norton;
            case "ultraviolette": return R.drawable.brand_ultraviolette;
            case "cfmoto": return R.drawable.brand_cfmoto;
            case "bounce infinity": return R.drawable.brand_bounce_infinity;
            case "ferrato": return R.drawable.brand_ferrato;
            case "kinetic green": return R.drawable.brand_kinetic_green;
            case "pure": return R.drawable.brand_pure;
            case "lectrix": return R.drawable.brand_lectrix;
            case "evolet": return R.drawable.brand_evolet;
            case "hop": return R.drawable.brand_hop;
            case "joy e-bike": return R.drawable.brand_joy_ebike;
            default: return R.drawable.brand_honda;
        }
    }
}