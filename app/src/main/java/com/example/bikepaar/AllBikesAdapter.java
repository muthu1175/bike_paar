package com.example.bikepaar;

import android.content.Context;
import android.content.Intent; // Add this import
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bikepaar.R;
import com.example.bikepaar.Motorcycle;

import java.util.ArrayList;
import java.util.List;

public class AllBikesAdapter extends RecyclerView.Adapter<AllBikesAdapter.ViewHolder> {

    private Context context;
    private List<Motorcycle> bikeList;
    private List<Motorcycle> originalList;

    public AllBikesAdapter(Context context, List<Motorcycle> bikeList) {
        this.context = context;
        this.bikeList = bikeList;
        this.originalList = new ArrayList<>(bikeList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bike_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Motorcycle bike = bikeList.get(position);

        // Set data
        holder.brandName.setText(bike.getBrand());
        holder.bikeName.setText(bike.getName());
        holder.ccText.setText(bike.getCc());
        holder.typeText.setText(bike.getType());
        holder.priceText.setText("₹ " + bike.getPrice());

        // Load image
        Glide.with(context)
                .load(bike.getImageUrl())
                .placeholder(R.drawable.sample_bike)
                .into(holder.bikeImage);

        // Set favorite icon
        if (bike.isFavorite()) {
            holder.favIcon.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.favIcon.setImageResource(R.drawable.ic_favorite_border);
        }

        // Show bestseller badge
        if (bike.isBestseller()) {
            holder.bestsellerBadge.setVisibility(View.VISIBLE);
        } else {
            holder.bestsellerBadge.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.favIcon.setOnClickListener(v -> {
            bike.setFavorite(!bike.isFavorite());
            notifyItemChanged(position);
            Toast.makeText(context, bike.isFavorite() ? "Added to favorites" : "Removed from favorites",
                    Toast.LENGTH_SHORT).show();
        });

        holder.viewDetailsButton.setOnClickListener(v -> {
            // Navigate to MotorcycleDetailsActivity
            Intent intent = new Intent(context, MotorcycleDetailsActivity.class);
            intent.putExtra("BIKE_NAME", bike.getName());
            intent.putExtra("BIKE_BRAND", bike.getBrand());
            intent.putExtra("BIKE_CC", bike.getCc());
            intent.putExtra("BIKE_TYPE", bike.getType());
            intent.putExtra("BIKE_PRICE", bike.getPrice());
            intent.putExtra("BIKE_IMAGE_URL", bike.getImageUrl());

            // Add these if your Motorcycle class has these fields
//            if (bike.getEngine() != null) {
//                intent.putExtra("BIKE_ENGINE", bike.getEngine());
//            }
//            if (bike.getMileage() != null) {
//                intent.putExtra("BIKE_MILEAGE", bike.getMileage());
//            }
//            if (bike.getDescription() != null) {
//                intent.putExtra("BIKE_DESCRIPTION", bike.getDescription());
//            }
//            if (bike.getCategory() != null) {
//                intent.putExtra("BIKE_CATEGORY", bike.getCategory());
//            }

            context.startActivity(intent);
        });

        // Whole card click (optional)
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Opening " + bike.getName() + " details",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    // Filter method
    public void filterByCategory(String category) {
        if (category.equals("All")) {
            bikeList.clear();
            bikeList.addAll(originalList);
        } else {
            List<Motorcycle> filteredList = new ArrayList<>();
            for (Motorcycle bike : originalList) {
                if (bike.getType().equalsIgnoreCase(category)) {
                    filteredList.add(bike);
                }
            }
            bikeList.clear();
            bikeList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bikeImage;
        ImageView favIcon;
        TextView brandName;
        TextView bikeName;
        TextView ccText;
        TextView typeText;
        TextView priceText;
        TextView bestsellerBadge;
        Button viewDetailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bikeImage = itemView.findViewById(R.id.bikeImage);
            favIcon = itemView.findViewById(R.id.favIcon);
            brandName = itemView.findViewById(R.id.brandName);
            bikeName = itemView.findViewById(R.id.bikeName);
            ccText = itemView.findViewById(R.id.ccText);
            typeText = itemView.findViewById(R.id.typeText);
            priceText = itemView.findViewById(R.id.priceText);
            bestsellerBadge = itemView.findViewById(R.id.bestsellerBadge);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
        }
    }
}