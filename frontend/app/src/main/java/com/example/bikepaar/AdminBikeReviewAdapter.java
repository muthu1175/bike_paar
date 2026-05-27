package com.example.bikepaar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminBikeReviewAdapter extends RecyclerView.Adapter<AdminBikeReviewAdapter.ReviewViewHolder> {

    private JSONArray reviews;

    public AdminBikeReviewAdapter(JSONArray reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_bike_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        try {
            JSONObject review = reviews.getJSONObject(position);
            
            String userName = review.optString("user", "Unknown User");
            String bikeModel = review.optString("bike_id", "Unknown Bike");
            int rating = review.optInt("rating", 0);
            String reviewText = review.optString("review", "");
            String date = review.optString("created_at", "");
            
            holder.tvUserName.setText(userName);
            holder.tvBikeModel.setText("🏍️ " + bikeModel);
            holder.tvRating.setText(rating + " STAR REVIEW");
            holder.tvReview.setText("\"" + reviewText + "\"");
            
            if (date.length() > 10) {
                holder.tvDate.setText(date.substring(0, 10));
            } else {
                holder.tvDate.setText(date);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviews.length();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvBikeModel, tvRating, tvReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBikeModel = itemView.findViewById(R.id.tvBikeModel);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReview = itemView.findViewById(R.id.tvReview);
        }
    }
}
