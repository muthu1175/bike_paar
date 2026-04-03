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

public class UpcomingBikesAdapter extends RecyclerView.Adapter<UpcomingBikesAdapter.ViewHolder> {

    private List<UpcomingBike> bikeList;

    public UpcomingBikesAdapter(List<UpcomingBike> bikeList) {
        this.bikeList = bikeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_bike, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UpcomingBike bike = bikeList.get(position);
        holder.title.setText(bike.title);
        holder.desc.setText(bike.description);
        holder.image.setImageResource(bike.imageRes);
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc;
        ImageView image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvUpcomingTitle);
            desc = itemView.findViewById(R.id.tvUpcomingDesc);
            image = itemView.findViewById(R.id.ivUpcomingImage);
        }
    }

    public static class UpcomingBike {
        String title, description;
        int imageRes;

        public UpcomingBike(String title, String description, int imageRes) {
            this.title = title;
            this.description = description;
            this.imageRes = imageRes;
        }
    }
}
