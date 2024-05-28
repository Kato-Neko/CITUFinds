package com.jamburger.kitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jamburger.kitter.R;
import com.jamburger.kitter.components.Notification;
import com.jamburger.kitter.utilities.DateTimeFormatter;
import com.jamburger.kitter.activities.TargetActivity;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context mContext;
    private List<Notification> mNotifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        mContext = context;
        mNotifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = mNotifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getUserName() + " " + notification.getMessage());
        holder.timestamp.setText(DateTimeFormatter.formatTimestamp(notification.getTimestamp()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, TargetActivity.class);
            intent.putExtra("details", notification.getDetails());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            timestamp = itemView.findViewById(R.id.notification_timestamp);
        }
    }
}
