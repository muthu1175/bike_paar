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

    public interface OnReviewDeleteListener {
        void onDeleteClick(int reviewId, int position);
    }

    private JSONArray reviews;
    private OnReviewDeleteListener deleteListener;

    public AdminBikeReviewAdapter(JSONArray reviews, OnReviewDeleteListener deleteListener) {
        this.reviews = reviews;
        this.deleteListener = deleteListener;
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
            
            int reviewId = review.optInt("id", -1);
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

            holder.ivDelete.setOnClickListener(v -> {
                if (deleteListener != null && reviewId != -1) {
                    deleteListener.onDeleteClick(reviewId, position);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviews.length();
    }

    public void removeReview(int position) {
        JSONArray newArray = new JSONArray();
        for (int i = 0; i < reviews.length(); i++) {
            if (i != position) {
                try {
                    newArray.put(reviews.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.reviews = newArray;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reviews.length());
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvBikeModel, tvRating, tvReview;
        android.widget.ImageView ivDelete;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBikeModel = itemView.findViewById(R.id.tvBikeModel);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReview = itemView.findViewById(R.id.tvReview);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
