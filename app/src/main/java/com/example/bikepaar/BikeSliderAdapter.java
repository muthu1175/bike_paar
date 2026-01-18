package com.example.bikepaar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.Collections;
import java.util.List;

public class BikeSliderAdapter extends RecyclerView.Adapter<BikeSliderAdapter.BikeViewHolder> {

    private final Context context;
    private List<Bike> sliderBikes;

    public BikeSliderAdapter(Context context) {
        this.context = context;
        this.sliderBikes = Collections.emptyList();
    }
    
    // Function to update data
    public void setBikes(List<Bike> bikes) {
        this.sliderBikes = bikes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_bike_slide, parent, false);
        return new BikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        Bike bike = sliderBikes.get(position);
        
        // Load image using Glide
        if (bike.imageUrl != null && !bike.imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(bike.imageUrl)
                    .placeholder(R.drawable.sample_bike) // Use a valid placeholder
                    .centerCrop()
                    .into(holder.imgSlideBike);
        } else if (bike.imageRes != 0) {
            holder.imgSlideBike.setImageResource(bike.imageRes);
        } else {
             holder.imgSlideBike.setImageResource(R.drawable.sample_bike);
        }
    }

    @Override
    public int getItemCount() {
        return sliderBikes.size();
    }

    static class BikeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlideBike;

        BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSlideBike = itemView.findViewById(R.id.imgSlideBike);
        }
    }
}
