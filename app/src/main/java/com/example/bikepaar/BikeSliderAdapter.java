package com.example.bikepaar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BikeSliderAdapter extends RecyclerView.Adapter<BikeSliderAdapter.BikeViewHolder> {

    private final Context context;
    private final int[] images = {
            R.drawable.bike_1,
            R.drawable.bike_2,
            R.drawable.bike_3
    };

    public BikeSliderAdapter(Context context) {
        this.context = context;
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
        holder.imgSlideBike.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class BikeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlideBike;

        BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSlideBike = itemView.findViewById(R.id.imgSlideBike);
        }
    }
}
