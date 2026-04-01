package com.example.bikepaar;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.app.ActivityOptions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PopularBrandsHomeAdapter extends RecyclerView.Adapter<PopularBrandsHomeAdapter.ViewHolder> {

    private List<BrandItem> brandList;
    private Context context;

    public PopularBrandsHomeAdapter(Context context, List<BrandItem> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_brand_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BrandItem brand = brandList.get(position);
        holder.tvBrandName.setText(brand.getName());
        holder.ivBrandLogo.setImageResource(brand.getImageResource());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BrandDetailsActivity.class);
            intent.putExtra("BRAND_NAME", brand.getName());
            intent.putExtra("BRAND_LOGO", brand.getImageResource());
            
            // Basic transition
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // Shared element could be added here similar to BrandActivity if desired
                 android.app.ActivityOptions options = android.app.ActivityOptions.makeSceneTransitionAnimation(
                    (Activity) context, holder.ivBrandLogo, "shared_brand_logo");
                context.startActivity(intent, options.toBundle());
            } else {
                 context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBrandLogo;
        TextView tvBrandName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBrandLogo = itemView.findViewById(R.id.ivBrandLogo);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
        }
    }
}
