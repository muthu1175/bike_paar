package com.example.bikepaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdminManageBikesAdapter extends RecyclerView.Adapter<AdminManageBikesAdapter.BikeViewHolder> {

    private List<Bike> bikes;
    private OnBikeActionListener actionListener;

    public interface OnBikeActionListener {
        void onEditClick(Bike bike);
        void onDeleteClick(Bike bike);
    }

    public AdminManageBikesAdapter(List<Bike> bikes, OnBikeActionListener actionListener) {
        this.bikes = bikes;
        this.actionListener = actionListener;
    }

    public void updateBikes(List<Bike> newBikes) {
        this.bikes = newBikes;
        notifyDataSetChanged();
    }

    public void filterList(List<Bike> filteredList) {
        this.bikes = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_manage_bike, parent, false);
        return new BikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        Bike bike = bikes.get(position);

        holder.tvBikeName.setText(bike.name);
        holder.tvBikeBrand.setText(bike.brand != null ? bike.brand : "");
        holder.tvBikePrice.setText(bike.getFormattedPrice());

        // Load image using Glide
        if (bike.imageUrl != null && !bike.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(bike.imageUrl)
                    .placeholder(R.drawable.sample_bike) // Using sample_bike as placeholder
                    .into(holder.ivBikeImage);
        } else if (bike.imageRes != 0) {
            holder.ivBikeImage.setImageResource(bike.imageRes);
        } else {
            holder.ivBikeImage.setImageResource(R.drawable.sample_bike);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(bike);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(bike);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikes == null ? 0 : bikes.size();
    }

    static class BikeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBikeImage, btnEdit, btnDelete;
        TextView tvBikeName, tvBikeBrand, tvBikePrice;

        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBikeImage = itemView.findViewById(R.id.ivBikeImage);
            tvBikeName = itemView.findViewById(R.id.tvBikeName);
            tvBikeBrand = itemView.findViewById(R.id.tvBikeBrand);
            tvBikePrice = itemView.findViewById(R.id.tvBikePrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
