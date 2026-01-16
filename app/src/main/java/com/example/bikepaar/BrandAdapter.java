package com.example.bikepaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    private List<BrandItem> brandList;
    private OnBrandClickListener listener;

    public interface OnBrandClickListener {
        void onBrandClick(int position, ImageView sharedImageView);
    }

    public BrandAdapter(List<BrandItem> brandList, OnBrandClickListener listener) {
        this.brandList = brandList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BrandItem brand = brandList.get(position);
        holder.brandName.setText(brand.getName());
        holder.brandImage.setImageResource(brand.getImageResource());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBrandClick(position, holder.brandImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView brandImage;
        TextView brandName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            brandImage = itemView.findViewById(R.id.brandImage);
            brandName = itemView.findViewById(R.id.brandName);
        }
    }
}