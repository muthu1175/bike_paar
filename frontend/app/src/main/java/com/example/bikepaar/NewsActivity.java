package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CardView news1, news2, news4;
    private Button btnBikeMaintenance;
    private ViewPager2 vpUpcomingBikes;
    private Handler sliderHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_news);

            btnBack = findViewById(R.id.btnBack);
            news1 = findViewById(R.id.news1);
            news2 = findViewById(R.id.news2);
            news4 = findViewById(R.id.news4);
            btnBikeMaintenance = findViewById(R.id.btnBikeMaintenance);
            vpUpcomingBikes = findViewById(R.id.vpUpcomingBikes);

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> onBackPressed());
            }

            if (vpUpcomingBikes != null) {
                List<UpcomingBikesAdapter.UpcomingBike> upcomingList = new ArrayList<>();
                upcomingList.add(new UpcomingBikesAdapter.UpcomingBike("KTM 390 Adventure R", "Launching Q4 2024. Next-gen off-roader.", R.drawable.sample_bike));
                upcomingList.add(new UpcomingBikesAdapter.UpcomingBike("Royal Enfield Classic 650", "The highly anticipated twin-cylinder classic.", R.drawable.sample_bike));
                upcomingList.add(new UpcomingBikesAdapter.UpcomingBike("Bajaj Pulsar N125", "Affordable performance wrapped in new styling.", R.drawable.sample_bike));

                UpcomingBikesAdapter adapter = new UpcomingBikesAdapter(upcomingList);
                vpUpcomingBikes.setAdapter(adapter);

                vpUpcomingBikes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        sliderHandler.removeCallbacks(sliderRunnable);
                        sliderHandler.postDelayed(sliderRunnable, 3000);
                    }
                });
            }

            if (btnBikeMaintenance != null) {
                btnBikeMaintenance.setOnClickListener(v -> {
                    startActivity(new Intent(NewsActivity.this, MaintenanceActivity.class));
                });
            }

            if (news1 != null) news1.setOnClickListener(v -> {});
            if (news2 != null) news2.setOnClickListener(v -> {});
            if (news4 != null) news4.setOnClickListener(v -> {});

        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Crash error details: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (vpUpcomingBikes != null && vpUpcomingBikes.getAdapter() != null) {
                int currentItem = vpUpcomingBikes.getCurrentItem();
                int nextItem = currentItem + 1;
                if (nextItem >= vpUpcomingBikes.getAdapter().getItemCount()) {
                    nextItem = 0;
                }
                vpUpcomingBikes.setCurrentItem(nextItem, true);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vpUpcomingBikes != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
