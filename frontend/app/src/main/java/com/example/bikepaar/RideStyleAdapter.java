package com.example.bikepaar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideStyleAdapter extends RecyclerView.Adapter<RideStyleAdapter.ViewHolder> {

    private List<RideStyleItem> rideStyleList;
    private OnRideStyleClickListener listener;

    public interface OnRideStyleClickListener {
        void onRideStyleClick(RideStyleItem rideStyle);
    }

    public RideStyleAdapter(List<RideStyleItem> rideStyleList, OnRideStyleClickListener listener) {
        this.rideStyleList = rideStyleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_style, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideStyleItem item = rideStyleList.get(position);

        holder.textViewName.setText(item.getName());
        holder.imageViewIcon.setImageResource(item.getIconResId());

        // Set background color of icon container
        try {
            int color = Color.parseColor(item.getColor());
            int lightColor = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color));
            holder.iconContainer.setCardBackgroundColor(lightColor);
            holder.imageViewIcon.setColorFilter(color);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onRideStyleClick(rideStyleList.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideStyleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView iconContainer;
        ImageView imageViewIcon;
        TextView textViewName;
        ImageView imageViewArrow;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewArrow = itemView.findViewById(R.id.imageViewArrow);
        }
    }
}