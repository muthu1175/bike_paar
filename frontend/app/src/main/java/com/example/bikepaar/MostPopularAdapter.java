package com.example.bikepaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MostPopularAdapter extends RecyclerView.Adapter<MostPopularAdapter.ViewHolder> {

    private List<Bike> bikeList;

    public MostPopularAdapter(List<Bike> bikeList) {
        this.bikeList = bikeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_most_popular, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bike bike = bikeList.get(position);

        holder.title.setText(bike.name);
        holder.description.setText(bike.description != null && !bike.description.isEmpty() ? bike.description : "Popular choice for riders in India.");
        holder.price.setText(bike.getFormattedPrice());
        holder.tag.setText("Popular");

        // Load image via Glide
        if (bike.imageUrl != null && !bike.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                 .load(bike.imageUrl)
                 .placeholder(R.drawable.sample_bike)
                 .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.sample_bike);
        }

        // Hide unused badges for now as Bike model lacks them
        holder.badge.setVisibility(View.GONE);
        holder.ratingIcon.setVisibility(View.GONE);

        // View Details Button Click
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

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, tag, badge, ratingIcon;
        ImageView image;
        Button btnViewDetails;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            price = itemView.findViewById(R.id.tvPrice);
            tag = itemView.findViewById(R.id.tvTag);
            badge = itemView.findViewById(R.id.tvBadge);
            ratingIcon = itemView.findViewById(R.id.tvRatingIcon);
            
            // Note: In item_most_popular.xml, the image view doesn't have an ID.
            // Wait, we need to add an ID to the image view! Let's assume it's R.id.ivBike or similar.
            // Oh right, it doesn't have an ID in the layout yet. Let's fix that.
            // We'll add it in the next tool call. For now, we will add the ID to find it.
            image = itemView.findViewById(R.id.ivBikeImage);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
