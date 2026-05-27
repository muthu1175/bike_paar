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

public class AdminAppReviewAdapter extends RecyclerView.Adapter<AdminAppReviewAdapter.ReviewViewHolder> {

    private JSONArray reviews;
    private OnReplyClickListener replyClickListener;

    public interface OnReplyClickListener {
        void onReplyClick(JSONObject review);
    }

    public AdminAppReviewAdapter(JSONArray reviews, OnReplyClickListener listener) {
        this.reviews = reviews;
        this.replyClickListener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        try {
            JSONObject review = reviews.getJSONObject(position);
            holder.tvUserName.setText(review.getString("user_name"));
            holder.tvReview.setText(review.getString("review"));
            holder.tvRating.setText(review.getString("rating") + "★");
            
            String createdAt = review.getString("created_at");
            // Basic substring if formatting isn't critical here, or use simple formatter
            if (createdAt.length() > 10) {
                holder.tvDate.setText(createdAt.substring(0, 10));
            } else {
                holder.tvDate.setText(createdAt);
            }

            holder.btnReply.setOnClickListener(v -> {
                if (replyClickListener != null) {
                    replyClickListener.onReplyClick(review);
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

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvRating, tvReview, btnReply;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReview = itemView.findViewById(R.id.tvReview);
            btnReply = itemView.findViewById(R.id.btnReply);
        }
    }
}
