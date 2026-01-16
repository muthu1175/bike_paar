package com.example.bikepaar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AiResultAdapter extends RecyclerView.Adapter<AiResultAdapter.ViewHolder> {

    private List<AiBikeModel> bikeList;
    private Context context;

    public AiResultAdapter(List<AiBikeModel> bikeList, Context context) {
        this.bikeList = bikeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ai_bike, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AiBikeModel bike = bikeList.get(position);

        holder.tvBikeName.setText(bike.getName());
        holder.tvPrice.setText(bike.getPrice());
        holder.tvAiPercent.setText("AI " + bike.getAiPercent() + "%");
        holder.imgBike.setImageResource(bike.getImageRes());

        holder.btnViewDetails.setOnClickListener(v -> {
            try {
                // IMPORTANT: Ensure MotorcycleDetailsActivity is the correct target
                android.content.Intent intent = new android.content.Intent(context, MotorcycleDetailsActivity.class);
                
                // 1. Basic Info
                intent.putExtra("BIKE_NAME", bike.getName());
                
                // Parse price safely
                int p = 0;
                try {
                     if (bike.getPrice() != null) {
                         // Remove non-numeric except dot
                         String clean = bike.getPrice().replaceAll("[^0-9]", "");
                         p = Integer.parseInt(clean);
                     }
                } catch (Exception e) {}
                intent.putExtra("BIKE_PRICE", p);
                intent.putExtra("PRICE", bike.getPrice()); // String backup

                intent.putExtra("BIKE_IMAGE_URL", bike.getImageUrl());
                
                // 2. Specs
                intent.putExtra("BIKE_ENGINE", bike.getEngine());
                intent.putExtra("BIKE_DESCRIPTION", bike.getDescription());
                intent.putExtra("MAX_POWER", bike.getMaxPower());
                intent.putExtra("MAX_TORQUE", bike.getMaxTorque());
                intent.putExtra("KERB_WEIGHT", bike.getKerbWeight());
                intent.putExtra("MILEAGE", bike.getMileage());
                
                // 3. Full Specs (for "View All" button inside Details)
                intent.putExtra("TRANSMISSION", bike.getTransmission());
                intent.putExtra("FUEL_TANK_CAPACITY", bike.getFuelTankCapacity());
                intent.putExtra("BRAKING_SYSTEM", bike.getBrakingSystem());
                intent.putExtra("TOP_SPEED", bike.getTopSpeed());
                intent.putExtra("FRONT_BRAKE_TYPE", bike.getFrontBrakeType());
                intent.putExtra("REAR_BRAKE_TYPE", bike.getRearBrakeType());
                intent.putExtra("FRONT_SUSPENSION", bike.getFrontSuspension());
                intent.putExtra("REAR_SUSPENSION", bike.getRearSuspension());
                intent.putExtra("TYRE_TYPE", bike.getTyreType());
                intent.putExtra("HEADLIGHT", bike.getHeadlight());
                intent.putExtra("TAIL_LIGHT", bike.getTailLight());
                intent.putExtra("BATTERY_CAPACITY", bike.getBatteryCapacity());

                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Error opening details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBike;
        TextView tvBikeName, tvPrice, tvAiPercent, btnViewDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBike = itemView.findViewById(R.id.imgBike);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAiPercent = itemView.findViewById(R.id.tvAiPercent);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
