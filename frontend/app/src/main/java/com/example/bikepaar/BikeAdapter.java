package com.example.bikepaar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;




import java.util.List;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.VH> {

    private final Context ctx;
    private final List<Bike> bikes;
    private OnBikeClickListener listener;
    private boolean showBudgetDesign = false; // Flag to show different layouts
    private boolean isSelectionMode = false; // Flag for selection mode

    // ================= LISTENER =================
    public interface OnBikeClickListener {
        void onViewDetailsClick(Bike bike);
        void onFavoriteClick(Bike bike, boolean isFavorite);
    }

    // ================= CONSTRUCTOR (DEFAULT) =================
    public BikeAdapter(Context ctx, List<Bike> bikes) {
        this.ctx = ctx;
        this.bikes = bikes;
        this.showBudgetDesign = false;
        this.isSelectionMode = false;
    }

    // ================= CONSTRUCTOR (BUDGET / SELECTION) =================
    public BikeAdapter(Context ctx,
                       List<Bike> bikes,
                       OnBikeClickListener listener,
                       boolean showBudgetDesign) {
        this(ctx, bikes, listener, showBudgetDesign, false);
    }

    public BikeAdapter(Context ctx,
                       List<Bike> bikes,
                       OnBikeClickListener listener,
                       boolean showBudgetDesign,
                       boolean isSelectionMode) {

        this.ctx = ctx;
        this.bikes = bikes;
        this.listener = listener;
        this.showBudgetDesign = showBudgetDesign;
        this.isSelectionMode = isSelectionMode;
    }

    // ================= VIEW HOLDER =================
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = showBudgetDesign
                ? R.layout.item_bike_budget
                : R.layout.item_bike_result;

        View v = LayoutInflater.from(ctx).inflate(layoutRes, parent, false);
        return new VH(v, showBudgetDesign);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Bike b = bikes.get(position);

        if (showBudgetDesign) {
            bindBudgetViewHolder(holder, b);
        } else {
            bindResultViewHolder(holder, b);
        }
    }

    // ================= RESULT VIEW =================
    private void bindResultViewHolder(VH holder, Bike b) {

        if (holder.tvEngine != null) {
            if (b.engine != null && !b.engine.isEmpty()) {
                holder.tvEngine.setText(b.engine);
                holder.tvEngine.setVisibility(View.VISIBLE);
            } else {
                holder.tvEngine.setText("N/A");
                holder.tvEngine.setVisibility(View.VISIBLE);
            }
        }

        holder.tvName.setText(b.name);
        
        if (holder.tvPrice != null) {
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(b.getFormattedPrice());
        }

        // Image Loading (Priority: URL > Resource > Placeholder)
        if (b.imageUrl != null && !b.imageUrl.isEmpty()) {
             Glide.with(ctx)
                    .load(b.imageUrl)
                    .placeholder(R.drawable.sample_bike)
                    .into(holder.ivBike);
        } else if (b.imageRes != 0) {
            holder.ivBike.setImageResource(b.imageRes);
        } else {
            holder.ivBike.setImageResource(R.drawable.sample_bike);
        }

        holder.btnDetails.setOnClickListener(v -> {
            if (isSelectionMode) {
                 if (listener != null) {
                    listener.onViewDetailsClick(b);
                }
                return;
            }

            // Navigate to Details with ALL extras
            android.content.Intent intent = new android.content.Intent(ctx, MotorcycleDetailsActivity.class);
            intent.putExtra("BIKE_NAME", b.name);
            intent.putExtra("BIKE_ENGINE", b.engine);
            intent.putExtra("BIKE_PRICE", b.price);
            intent.putExtra("BIKE_IMAGE_URL", b.imageUrl);
            intent.putExtra("BIKE_DESCRIPTION", b.badge); // or usage/type
            
            // Dynamic Specs
            intent.putExtra("MAX_POWER", b.getMaxPower());
            intent.putExtra("MAX_TORQUE", b.getMaxTorque());
            intent.putExtra("KERB_WEIGHT", b.getKerbWeight());
            intent.putExtra("MILEAGE", b.getMileage());
            intent.putExtra("TRANSMISSION", b.getTransmission());
            intent.putExtra("FUEL_TANK_CAPACITY", b.getFuelTankCapacity());
            intent.putExtra("BRAKING_SYSTEM", b.getBrakingSystem());
            intent.putExtra("TOP_SPEED", b.getTopSpeed());

            // Full Specs
            intent.putExtra("FRONT_BRAKE_TYPE", b.getFrontBrakeType());
            intent.putExtra("REAR_BRAKE_TYPE", b.getRearBrakeType());
            intent.putExtra("FRONT_SUSPENSION", b.getFrontSuspension());
            intent.putExtra("REAR_SUSPENSION", b.getRearSuspension());
            intent.putExtra("TYRE_TYPE", b.getTyreType());
            intent.putExtra("HEADLIGHT", b.getHeadlight());
            intent.putExtra("TAIL_LIGHT", b.getTailLight());
            intent.putExtra("BATTERY_CAPACITY", b.getBatteryCapacity());
            
            ctx.startActivity(intent);

            if (listener != null) {
                listener.onViewDetailsClick(b);
            }
        });

        // Favorite
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setVisibility(View.VISIBLE);
            holder.btnFavorite.setSelected(b.isFavorite); // Use selector for background/src change
            
            // Or manually set image based on state if selector not used
            holder.btnFavorite.setImageResource(b.isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            if (b.isFavorite) {
                holder.btnFavorite.setColorFilter(android.graphics.Color.RED);
            } else {
                holder.btnFavorite.setColorFilter(android.graphics.Color.parseColor("#6B7280"));
            }

            holder.btnFavorite.setOnClickListener(v -> {
                boolean newState = !b.isFavorite;
                b.isFavorite = newState;
                
                holder.btnFavorite.setImageResource(newState ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

                if (newState) {
                    holder.btnFavorite.setColorFilter(android.graphics.Color.RED);
                } else {
                    holder.btnFavorite.setColorFilter(android.graphics.Color.parseColor("#6B7280"));
                }

                // Call internal method to update backend
                // Call internal method to update backend
                holder.toggleFavorite(b, newState);

                if (listener != null) {
                    listener.onFavoriteClick(b, newState); 
                }
            });
        }

        // Hide unused views if they exist in the ViewHolder but not in this layout (safety)
        if (holder.tvPercent != null) holder.tvPercent.setVisibility(View.GONE);
        if (holder.tvSpecs != null) holder.tvSpecs.setVisibility(View.GONE);
        if (holder.tvBadge != null) holder.tvBadge.setVisibility(View.GONE);
        if (holder.tvPriceLabel != null) holder.tvPriceLabel.setVisibility(View.GONE);
    }

    // ================= BUDGET VIEW =================
    private void bindBudgetViewHolder(VH holder, Bike b) {

        holder.tvName.setText(b.name);

        if (holder.tvSpecs != null) {
            if (b.getSpecifications() == null || b.getSpecifications().isEmpty()) {
                holder.tvSpecs.setText(b.vehicleType + " • " + b.usage);
            } else {
                holder.tvSpecs.setText(b.getSpecifications());
            }
            holder.tvSpecs.setVisibility(View.VISIBLE);
        }

        if (holder.tvPrice != null) {
            holder.tvPrice.setText(b.getFormattedPrice());
        }

        if (holder.tvPriceLabel != null) {
            holder.tvPriceLabel.setVisibility(View.GONE);
        }

        if (holder.tvPercent != null) {
            if (b.matchPercent > 0) {
                holder.tvPercent.setVisibility(View.VISIBLE);
                holder.tvPercent.setText(b.matchPercent + "% Match");
            } else {
                holder.tvPercent.setVisibility(View.GONE);
            }
        }

        // Image loading
        if (b.imageUrl != null && !b.imageUrl.isEmpty()) {
            Glide.with(ctx)
                    .load(b.imageUrl)
                    .placeholder(R.drawable.sample_bike)
                    .into(holder.ivBike);
        } else if (b.imageRes != 0) {
            holder.ivBike.setImageResource(b.imageRes);
        }

        // Badge
        if (holder.tvBadge != null) {
            if (b.hasBadge()) {
                holder.tvBadge.setVisibility(View.VISIBLE);
                holder.tvBadge.setText(b.badge);

                if ("Top Seller".equals(b.badge)) {
                    holder.tvBadge.setBackgroundResource(R.drawable.badge_green_background);
                } else if ("High Mileage".equals(b.badge)) {
                    holder.tvBadge.setBackgroundResource(R.drawable.badge_blue_background);
                }
            } else {
                holder.tvBadge.setVisibility(View.GONE);
            }
        }

        // Favorite
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setVisibility(View.VISIBLE);
            holder.btnFavorite.setSelected(b.isFavorite);
            holder.btnFavorite.setImageResource(b.isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            if (b.isFavorite) {
                holder.btnFavorite.setColorFilter(android.graphics.Color.RED);
            } else {
                holder.btnFavorite.setColorFilter(android.graphics.Color.parseColor("#6B7280"));
            }

            holder.btnFavorite.setOnClickListener(v -> {
                boolean newState = !b.isFavorite;
                b.isFavorite = newState;
                holder.btnFavorite.setSelected(newState);
                holder.btnFavorite.setImageResource(newState ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
                if (newState) {
                    holder.btnFavorite.setColorFilter(android.graphics.Color.RED); 
                } else {
                    holder.btnFavorite.setColorFilter(android.graphics.Color.parseColor("#6B7280"));
                }
                
                holder.toggleFavorite(b, newState);

                if (listener != null) {
                    listener.onFavoriteClick(b, newState);
                }
            });
        }

        // Details
        if (holder.btnDetails != null) {
            holder.btnDetails.setOnClickListener(v -> {
                if (isSelectionMode) {
                     if (listener != null) {
                        listener.onViewDetailsClick(b);
                    }
                    return;
                }

                // Navigate to Details with ALL extras
                android.content.Intent intent = new android.content.Intent(ctx, MotorcycleDetailsActivity.class);
                intent.putExtra("BIKE_NAME", b.name);
                intent.putExtra("BIKE_ENGINE", b.engine);
                intent.putExtra("BIKE_PRICE", b.price);
                intent.putExtra("BIKE_IMAGE_URL", b.imageUrl);
                intent.putExtra("BIKE_DESCRIPTION", b.badge); // or usage/type
                
                // Dynamic Specs
                intent.putExtra("MAX_POWER", b.getMaxPower());
                intent.putExtra("MAX_TORQUE", b.getMaxTorque());
                intent.putExtra("KERB_WEIGHT", b.getKerbWeight());
                intent.putExtra("MILEAGE", b.getMileage());
                intent.putExtra("TRANSMISSION", b.getTransmission());
                intent.putExtra("FUEL_TANK_CAPACITY", b.getFuelTankCapacity());
                intent.putExtra("BRAKING_SYSTEM", b.getBrakingSystem());
                intent.putExtra("TOP_SPEED", b.getTopSpeed());

                // Full Specs
                intent.putExtra("FRONT_BRAKE_TYPE", b.getFrontBrakeType());
                intent.putExtra("REAR_BRAKE_TYPE", b.getRearBrakeType());
                intent.putExtra("FRONT_SUSPENSION", b.getFrontSuspension());
                intent.putExtra("REAR_SUSPENSION", b.getRearSuspension());
                intent.putExtra("TYRE_TYPE", b.getTyreType());
                intent.putExtra("HEADLIGHT", b.getHeadlight());
                intent.putExtra("TAIL_LIGHT", b.getTailLight());
                intent.putExtra("BATTERY_CAPACITY", b.getBatteryCapacity());
                
                ctx.startActivity(intent);

                if (listener != null) {
                    listener.onViewDetailsClick(b);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bikes.size();
    }

    // ================= VIEW HOLDER CLASS =================
    static class VH extends RecyclerView.ViewHolder {

        ImageView ivBike;
        ImageButton btnFavorite;
        TextView tvName, tvPrice, tvPercent, tvSpecs, tvBadge, tvPriceLabel, tvEngine;
        TextView btnDetails;

        VH(@NonNull View itemView, boolean isBudgetView) {
            super(itemView);

            ivBike = itemView.findViewById(R.id.ivBike);
            tvName = itemView.findViewById(R.id.tvBikeName);
            btnDetails = itemView.findViewById(R.id.btnViewDetails);

            // Engine text for Result view
            tvEngine = itemView.findViewById(R.id.tvBikeEngine);

            if (isBudgetView) {
                tvSpecs = itemView.findViewById(R.id.tvBikeSpecs);
                tvBadge = itemView.findViewById(R.id.tvBadge);
                tvPriceLabel = itemView.findViewById(R.id.tvPriceLabel);
                btnFavorite = itemView.findViewById(R.id.btnFavorite);
                tvPrice = itemView.findViewById(R.id.tvBikePrice);
                tvPercent = itemView.findViewById(R.id.tvAiPercent);
            } else {
                tvPrice = itemView.findViewById(R.id.tvBikePrice);
                tvPercent = itemView.findViewById(R.id.tvAiPercent);

                tvSpecs = null;
                tvBadge = null;
                tvPriceLabel = null;
                btnFavorite = itemView.findViewById(R.id.btnFavorite); 
            }
        }

        // ================= INTERNAL API LOGIC =================
        void toggleFavorite(Bike bike, boolean isFavorite) {
            Context context = itemView.getContext();
            String tokenRaw = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE).getString("TOKEN", "");
            
            if (tokenRaw.isEmpty()) {
                Toast.makeText(context, "Please login to add favorites", Toast.LENGTH_SHORT).show();
                return; 
            }
            
            // Log for debugging
            // Log.d("BikeAdapter", "Toggling Favorite for: " + bike.name + " State: " + isFavorite);

            String token = "Token " + tokenRaw;
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("bike_id", bike.name); 
            body.put("model", bike.name);
            body.put("brand", "Unknown"); 
            body.put("price", String.valueOf(bike.price));
            body.put("image", bike.imageUrl);

            if (isFavorite) {
                apiService.addFavorite(token, body).enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, String>> call, retrofit2.Response<java.util.Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                String err = response.errorBody() != null ? response.errorBody().string() : "Unknown";
                                Toast.makeText(context, "Failed to add: " + response.code() + " " + err, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {
                         Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                apiService.removeFavorite(token, body).enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, String>> call, retrofit2.Response<java.util.Map<String, String>> response) {
                        if (response.isSuccessful()) {
                             Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        } else {
                             Toast.makeText(context, "Failed to remove: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {
                        Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
