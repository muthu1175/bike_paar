package com.example.bikepaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecentLaunchesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FEATURED = 0;
    private static final int TYPE_NORMAL = 1;

    private List<RecentLaunchesActivity.BikeModel> bikeList;

    public RecentLaunchesAdapter(List<RecentLaunchesActivity.BikeModel> bikeList) {
        this.bikeList = bikeList;
    }

    @Override
    public int getItemViewType(int position) {
        return bikeList.get(position).isFeatured ? TYPE_FEATURED : TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FEATURED) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_featured_bike, parent, false);
            return new FeaturedViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_normal_bike, parent, false);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecentLaunchesActivity.BikeModel bike = bikeList.get(position);

        if (holder.getItemViewType() == TYPE_FEATURED) {
            FeaturedViewHolder featuredHolder = (FeaturedViewHolder) holder;
            featuredHolder.title.setText(bike.title);
            featuredHolder.description.setText(bike.description);
            featuredHolder.price.setText(bike.price);
            featuredHolder.specs.setText(bike.specs);
            featuredHolder.tag.setText(bike.tag);
            featuredHolder.image.setImageResource(bike.imageRes);
        } else {
            NormalViewHolder normalHolder = (NormalViewHolder) holder;
            normalHolder.title.setText(bike.title);
            normalHolder.description.setText(bike.description);
            normalHolder.price.setText(bike.price);
            normalHolder.date.setText(bike.specs);
            normalHolder.image.setImageResource(bike.imageRes);

            if (!bike.tag.isEmpty()) {
                normalHolder.tag.setVisibility(View.VISIBLE);
                normalHolder.tag.setText(bike.tag);
            } else {
                normalHolder.tag.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    // Featured ViewHolder
    static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, specs, tag;
        ImageView image;

        FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            price = itemView.findViewById(R.id.tvPrice);
            specs = itemView.findViewById(R.id.tvSpecs);
            tag = itemView.findViewById(R.id.tvTag);
            image = itemView.findViewById(R.id.ivBike);
        }
    }

    // Normal ViewHolder
    static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, date, tag;
        ImageView image, btnFavorite;

        NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            price = itemView.findViewById(R.id.tvPrice);
            date = itemView.findViewById(R.id.tvDate);
            tag = itemView.findViewById(R.id.tvTag);
            image = itemView.findViewById(R.id.ivBike);
        }
    }
}