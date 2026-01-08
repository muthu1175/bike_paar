package com.example.bikepaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SportsBikeAdapter extends RecyclerView.Adapter<SportsBikeAdapter.ViewHolder> {

    private List<SportsBike> bikeList;
    private OnBikeClickListener listener;

    public interface OnBikeClickListener {
        void onBikeClick(SportsBike bike);
    }

    public SportsBikeAdapter(List<SportsBike> bikeList, OnBikeClickListener listener) {
        this.bikeList = bikeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sports_bike, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SportsBike bike = bikeList.get(position);

        holder.textViewName.setText(bike.getName());
        holder.textViewDescription.setText(bike.getDescription());
        holder.textViewEngine.setText(bike.getEngine());
        holder.textViewPrice.setText(String.format("₹%,d", bike.getPrice()));

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(bike.getImageUrl())
                .placeholder(R.drawable.sample_bike)
                .into(holder.imageViewBike);

        // Favorite Logic
        if (bike.isFavorite()) {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
            holder.ivFavorite.setColorFilter(android.graphics.Color.RED);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite_border);
            // reset color filter or set to default grey if needed
            holder.ivFavorite.setColorFilter(android.graphics.Color.parseColor("#6B7280"));
        }

        holder.ivFavorite.setOnClickListener(v -> {
            boolean newState = !bike.isFavorite();
            bike.setFavorite(newState);
            notifyItemChanged(position);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            String token = "Token " + holder.itemView.getContext().getSharedPreferences("USER_DATA", android.content.Context.MODE_PRIVATE).getString("TOKEN", "");
            
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("bike_id", bike.getName()); // Use Name as ID
            body.put("model", bike.getName());
            body.put("brand", bike.getBrand() != null ? bike.getBrand() : "Unknown");
            body.put("price", String.valueOf(bike.getPrice()));
            body.put("image", bike.getImageUrl());

            if (newState) {
                apiService.addFavorite(token, body).enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, String>> call, retrofit2.Response<java.util.Map<String, String>> response) {
                        if (!response.isSuccessful()) {
                            // Revert on failure
                            bike.setFavorite(!newState);
                            notifyItemChanged(position);
                            android.widget.Toast.makeText(holder.itemView.getContext(), "Failed to add favorite", android.widget.Toast.LENGTH_SHORT).show();
                        } else {
                             android.widget.Toast.makeText(holder.itemView.getContext(), "Added to favorites", android.widget.Toast.LENGTH_SHORT).show();
                             // Navigate to FavouriteActivity
                             android.content.Context context = holder.itemView.getContext();
                             android.content.Intent intent = new android.content.Intent(context, FavouriteActivity.class);
                             context.startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {
                         bike.setFavorite(!newState);
                         notifyItemChanged(position);
                    }
                });
            } else {
                apiService.removeFavorite(token, body).enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, String>> call, retrofit2.Response<java.util.Map<String, String>> response) {
                        if (response.isSuccessful()) {
                             android.widget.Toast.makeText(holder.itemView.getContext(), "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {}
                });
            }
        });

        holder.buttonViewDetails.setOnClickListener(v -> {
            // Navigate to MotorcycleDetailsActivity
            android.content.Intent intent = new android.content.Intent(holder.itemView.getContext(), MotorcycleDetailsActivity.class);
//            intent.putExtra("BIKE_ID", bike.getId());
            intent.putExtra("BIKE_NAME", bike.getName());
            intent.putExtra("BIKE_ENGINE", bike.getEngine());
            intent.putExtra("BIKE_PRICE", bike.getPrice());
            intent.putExtra("BIKE_IMAGE_URL", bike.getImageUrl());
            intent.putExtra("BIKE_DESCRIPTION", bike.getDescription());

            // Specs
            intent.putExtra("MAX_POWER", bike.getMaxPower());
            intent.putExtra("MAX_TORQUE", bike.getMaxTorque());
            intent.putExtra("KERB_WEIGHT", bike.getKerbWeight());
            intent.putExtra("MILEAGE", bike.getMileage());
            intent.putExtra("TRANSMISSION", bike.getTransmission());
            intent.putExtra("FUEL_TANK_CAPACITY", bike.getFuelTankCapacity());
            intent.putExtra("BRAKING_SYSTEM", bike.getBrakingSystem());
            intent.putExtra("TOP_SPEED", bike.getTopSpeed());

            // Full Specs Extras 
            intent.putExtra("FRONT_BRAKE_TYPE", bike.getFrontBrakeType());
            intent.putExtra("REAR_BRAKE_TYPE", bike.getRearBrakeType());
            intent.putExtra("FRONT_SUSPENSION", bike.getFrontSuspension());
            intent.putExtra("REAR_SUSPENSION", bike.getRearSuspension());
            intent.putExtra("TYRE_TYPE", bike.getTyreType());
            intent.putExtra("HEADLIGHT", bike.getHeadlight());
            intent.putExtra("TAIL_LIGHT", bike.getTailLight());
            intent.putExtra("BATTERY_CAPACITY", bike.getBatteryCapacity());

            holder.itemView.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBikeClick(bike);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBike;
        TextView textViewEngine;
        TextView textViewName;
        TextView textViewDescription;
        TextView textViewPrice;
        Button buttonViewDetails;
        ImageView ivFavorite;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBike = itemView.findViewById(R.id.imageViewBike);
            textViewEngine = itemView.findViewById(R.id.textViewEngine);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }
}