package com.example.bikepaar;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Map<String, Object>> reviews = new ArrayList<>();

    public void setReviews(List<Map<String, Object>> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Map<String, Object> review = reviews.get(position);

        String userName = "User"; // Default name
        String avatarText = "U";
        String comment = "";
        int rating = 0;
        String date = "Recent";

        // Safely extract data with multiple fallbacks
        if (review.containsKey("user")) {
            Object userObj = review.get("user");
            if (userObj instanceof Map) {
                // If nested user object
                Map userMap = (Map) userObj;
                 if (userMap.containsKey("username")) userName = String.valueOf(userMap.get("username"));
                 else if (userMap.containsKey("name")) userName = String.valueOf(userMap.get("name"));
            } else if (userObj instanceof String) {
                userName = (String) userObj;
            }
        } else if (review.containsKey("username")) {
             userName = String.valueOf(review.get("username"));
        } else if (review.containsKey("user_name")) {
             userName = String.valueOf(review.get("user_name"));
        }

        if (userName != null && !userName.isEmpty()) {
            avatarText = userName.substring(0, 1).toUpperCase();
            if (userName.length() > 1 && userName.contains(" ")) {
                 try {
                     String[] parts = userName.split(" ");
                     if (parts.length > 1) {
                         avatarText = String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0));
                         avatarText = avatarText.toUpperCase();
                     }
                 } catch (Exception e) {}
            }
        }


        if (review.containsKey("review")) {
            comment = String.valueOf(review.get("review"));
        } else if (review.containsKey("comment")) {
            comment = String.valueOf(review.get("comment"));
        } else if (review.containsKey("text")) {
            comment = String.valueOf(review.get("text"));
        } else if (review.containsKey("message")) {
            comment = String.valueOf(review.get("message"));
        }

        if (review.containsKey("rating")) {
            Object ratingObj = review.get("rating");
            if (ratingObj instanceof Number) {
                rating = ((Number) ratingObj).intValue();
            } else if (ratingObj instanceof String) {
                try {
                    rating = (int) Double.parseDouble((String) ratingObj);
                } catch (NumberFormatException e) {
                    rating = 0;
                }
            }
        } else if (review.containsKey("stars")) {
             Object ratingObj = review.get("stars");
             if (ratingObj instanceof Number) rating = ((Number) ratingObj).intValue();
        }

        if (review.containsKey("date_posted")) {
            date = String.valueOf(review.get("date_posted"));
        } else if (review.containsKey("created_at")) {
             date = String.valueOf(review.get("created_at"));
        } else if (review.containsKey("date")) {
             date = String.valueOf(review.get("date"));
        }
        
        // Ensure comment is not null
        if (comment == null) comment = "";

        holder.txtUserName.setText(userName);
        holder.txtAvatar.setText(avatarText);
        holder.txtReviewComment.setText(comment);
        holder.txtReviewDate.setText(date);
        
        // Generate random color for avatar background
        int[] colors = {0xFFE0F2FE, 0xFFF3E8FF, 0xFFDCFCE7, 0xFFFEF3C7, 0xFFFEE2E2};
        int[] textColors = {0xFF2563EB, 0xFF7C3AED, 0xFF16A34A, 0xFFD97706, 0xFFDC2626};
        int colorIndex = Math.abs(userName.hashCode()) % colors.length;
        
        holder.txtAvatar.setBackgroundColor(colors[colorIndex]);
        holder.txtAvatar.setTextColor(textColors[colorIndex]);


        // Set stars
        TextView[] stars = {holder.star1, holder.star2, holder.star3, holder.star4, holder.star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                // stars[i].setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary)); // Removed due to error
                stars[i].setTextColor(0xFFFFD700); // Gold
            } else {
                stars[i].setTextColor(0xFFD1D5DB); // Gray
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtAvatar, txtUserName, txtReviewDate, txtReviewComment;
        TextView star1, star2, star3, star4, star5;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAvatar = itemView.findViewById(R.id.txtAvatar);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtReviewDate = itemView.findViewById(R.id.txtReviewDate);
            txtReviewComment = itemView.findViewById(R.id.txtReviewComment);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);
        }
    }
}
