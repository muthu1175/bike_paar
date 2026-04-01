package com.example.bikepaar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PopularCompareAdapter extends RecyclerView.Adapter<PopularCompareAdapter.ViewHolder> {

    private Context context;
    private List<CompareItem> compareList;

    public PopularCompareAdapter(Context context, List<CompareItem> compareList) {
        this.context = context;
        this.compareList = compareList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_compare, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompareItem item = compareList.get(position);
        Bike b1 = item.getBike1();
        Bike b2 = item.getBike2();

        if (b1 != null) {
            holder.tvBike1.setText(b1.name);
            if (b1.imageUrl != null && !b1.imageUrl.isEmpty()) {
                Glide.with(context).load(b1.imageUrl).placeholder(R.drawable.sample_bike).into(holder.ivBike1);
            } else if (b1.imageRes != 0) {
                 holder.ivBike1.setImageResource(b1.imageRes);
            } else {
                 holder.ivBike1.setImageResource(R.drawable.sample_bike);
            }
        }

        if (b2 != null) {
            holder.tvBike2.setText(b2.name);
            if (b2.imageUrl != null && !b2.imageUrl.isEmpty()) {
                Glide.with(context).load(b2.imageUrl).placeholder(R.drawable.sample_bike).into(holder.ivBike2);
            } else if (b2.imageRes != 0) {
                 holder.ivBike2.setImageResource(b2.imageRes);
            } else {
                 holder.ivBike2.setImageResource(R.drawable.sample_bike);
            }
        }

        holder.btnCompare.setOnClickListener(v -> {
            if (b1 != null && b2 != null) {
                Intent intent = new Intent(context, CompareResultsActivity.class);
                intent.putExtra("BIKE_1", b1);
                intent.putExtra("BIKE_2", b2);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return compareList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBike1, ivBike2;
        TextView tvBike1, tvBike2;
        Button btnCompare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBike1 = itemView.findViewById(R.id.ivBike1);
            ivBike2 = itemView.findViewById(R.id.ivBike2);
            tvBike1 = itemView.findViewById(R.id.tvBike1);
            tvBike2 = itemView.findViewById(R.id.tvBike2);
            btnCompare = itemView.findViewById(R.id.btnCompare);
        }
    }
}
