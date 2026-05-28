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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private JSONArray notifications;

    public NotificationAdapter(JSONArray notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        try {
            JSONObject notification = notifications.getJSONObject(position);
            holder.tvTitle.setText(notification.getString("title"));
            holder.tvMessage.setText(notification.getString("message"));
            
            String createdAt = notification.getString("created_at");
            holder.tvTime.setText(formatTime(createdAt));
            
            if (notification.has("image") && !notification.isNull("image")) {
                String imageUrl = notification.getString("image");
                if (!imageUrl.isEmpty()) {
                    holder.ivNotificationImage.setVisibility(View.VISIBLE);
                    // Handle relative URL (if Django returns /media/...)
                    if (!imageUrl.startsWith("http")) {
                        imageUrl = "http://10.44.111.87:8000" + imageUrl;
                    }
                    Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .into(holder.ivNotificationImage);
                } else {
                    holder.ivNotificationImage.setVisibility(View.GONE);
                }
            } else {
                holder.ivNotificationImage.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notifications.length();
    }

    private String formatTime(String rawTime) {
        try {
            // Django rest framework returns time like "2024-04-20T10:30:00Z" or similar
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(rawTime);
            
            if (date != null) {
                // Calculate time difference
                long diff = new Date().getTime() - date.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (days > 0) return days + " days ago";
                if (hours > 0) return hours + " hours ago";
                if (minutes > 0) return minutes + " minutes ago";
                return "Just now";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Recently";
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView ivNotificationImage;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            ivNotificationImage = itemView.findViewById(R.id.ivNotificationImage);
        }
    }
}
