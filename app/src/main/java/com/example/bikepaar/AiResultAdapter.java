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

        holder.btnViewDetails.setOnClickListener(v ->
                Toast.makeText(context,
                        bike.getName() + " details clicked",
                        Toast.LENGTH_SHORT).show());
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
