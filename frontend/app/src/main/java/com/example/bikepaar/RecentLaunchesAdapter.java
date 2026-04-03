package com.example.bikepaar;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class RecentLaunchesAdapter extends RecyclerView.Adapter<RecentLaunchesAdapter.ViewHolder> {

    private List<Bike> bikeList;

    public RecentLaunchesAdapter(List<Bike> bikeList) {
        this.bikeList = bikeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_normal_bike, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bike bike = bikeList.get(position);

        // Handle Brand Header grouping
        if (bike.brand != null && !bike.brand.isEmpty()) {
            if (position == 0 || !bike.brand.equals(bikeList.get(position - 1).brand)) {
                holder.llBrandHeader.setVisibility(View.VISIBLE);
                holder.tvBrandHeader.setText(bike.brand);
                
                int brandLogoRes = getBrandLogo(bike.brand);
                if (brandLogoRes != 0) {
                    holder.ivBrandLogoHeader.setVisibility(View.VISIBLE);
                    holder.ivBrandLogoHeader.setImageResource(brandLogoRes);
                } else {
                    holder.ivBrandLogoHeader.setVisibility(View.GONE);
                }
            } else {
                holder.llBrandHeader.setVisibility(View.GONE);
            }
        } else {
            holder.llBrandHeader.setVisibility(View.GONE);
        }

        holder.title.setText(bike.name);
        holder.description.setText(bike.description != null && !bike.description.isEmpty() ? bike.description : "New Launch");
        holder.price.setText(bike.getFormattedPrice());
        holder.date.setText(bike.usage); // Usage has the launch year
        
        if (bike.hasBadge()) {
            holder.tag.setVisibility(View.VISIBLE);
            holder.tag.setText(bike.badge);
        } else {
            holder.tag.setVisibility(View.GONE);
        }

        if (bike.imageUrl != null && !bike.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                 .load(bike.imageUrl)
                 .placeholder(R.drawable.sample_bike)
                 .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.sample_bike);
        }

        // View Details Button
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MotorcycleDetailsActivity.class);
            intent.putExtra("BIKE_NAME", bike.name);
            intent.putExtra("BIKE_ENGINE", bike.engine);
            intent.putExtra("BIKE_PRICE", bike.price);
            intent.putExtra("BIKE_IMAGE_URL", bike.imageUrl);
            intent.putExtra("MAX_POWER", bike.getMaxPower());
            intent.putExtra("MAX_TORQUE", bike.getMaxTorque());
            intent.putExtra("KERB_WEIGHT", bike.getKerbWeight());
            intent.putExtra("MILEAGE", bike.getMileage());
            intent.putExtra("TRANSMISSION", bike.getTransmission());
            intent.putExtra("FUEL_TANK_CAPACITY", bike.getFuelTankCapacity());
            intent.putExtra("TOP_SPEED", bike.getTopSpeed());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    private int getBrandLogo(String brandName) {
        if (brandName == null) return 0;
        String lowerBrand = brandName.toLowerCase();
        if (lowerBrand.contains("royal enfield")) return R.drawable.brand_royal_enfield;
        if (lowerBrand.contains("ktm")) return R.drawable.brand_ktm;
        if (lowerBrand.contains("tvs")) return R.drawable.brand_tvs;
        if (lowerBrand.contains("bajaj")) return R.drawable.brand_bajaj;
        if (lowerBrand.contains("hero")) return R.drawable.brand_hero;
        if (lowerBrand.contains("honda")) return R.drawable.brand_honda;
        if (lowerBrand.contains("yamaha")) return R.drawable.brand_yamaha;
        if (lowerBrand.contains("suzuki")) return R.drawable.brand_suzuki;
        if (lowerBrand.contains("kawasaki")) return R.drawable.brand_kawasaki;
        if (lowerBrand.contains("bmw")) return R.drawable.brand_bmw;
        if (lowerBrand.contains("ducati")) return R.drawable.brand_ducati;
        if (lowerBrand.contains("triumph")) return R.drawable.brand_triumph;
        if (lowerBrand.contains("harley")) return R.drawable.brand_harley_davidson;
        return 0;
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llBrandHeader;
        ImageView ivBrandLogoHeader;
        TextView tvBrandHeader, title, description, price, date, tag;
        ImageView image;
        Button btnViewDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            llBrandHeader = itemView.findViewById(R.id.llBrandHeader);
            ivBrandLogoHeader = itemView.findViewById(R.id.ivBrandLogoHeader);
            tvBrandHeader = itemView.findViewById(R.id.tvBrandHeader);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            price = itemView.findViewById(R.id.tvPrice);
            date = itemView.findViewById(R.id.tvDate);
            tag = itemView.findViewById(R.id.tvTag);
            image = itemView.findViewById(R.id.ivBike);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}