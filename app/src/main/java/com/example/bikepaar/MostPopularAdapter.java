package com.example.bikepaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MostPopularAdapter extends RecyclerView.Adapter<MostPopularAdapter.ViewHolder> {

    private List<MostPopularActivity.BikeModel> bikeList;

    public MostPopularAdapter(List<MostPopularActivity.BikeModel> bikeList) {
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
        MostPopularActivity.BikeModel bike = bikeList.get(position);

        holder.title.setText(bike.title);
        holder.description.setText(bike.description);
        holder.price.setText(bike.price);
        holder.tag.setText(bike.tag);

        // Set badge visibility
        if (bike.badge != null && !bike.badge.isEmpty()) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(bike.badge);

            // Set badge color based on text
            if (bike.badge.equals("#1 Seller")) {
                holder.badge.setBackgroundResource(R.drawable.badge_orange);
            } else if (bike.badge.equals("Hot")) {
                holder.badge.setBackgroundResource(R.drawable.badge_red);
            }
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        // Set rating icon
        if (bike.ratingIcon != null && !bike.ratingIcon.isEmpty()) {
            holder.ratingIcon.setText(bike.ratingIcon);
            holder.ratingIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ratingIcon.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, tag, badge, ratingIcon;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            price = itemView.findViewById(R.id.tvPrice);
            tag = itemView.findViewById(R.id.tvTag);
            badge = itemView.findViewById(R.id.tvBadge);
            ratingIcon = itemView.findViewById(R.id.tvRatingIcon);
        }
    }
}
