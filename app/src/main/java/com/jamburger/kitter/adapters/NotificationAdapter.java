package com.jamburger.kitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jamburger.kitter.R;
import com.jamburger.kitter.components.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notifications;
    private OnItemClickListener listener;

    public NotificationAdapter(Context context, List<Notification> notifications, OnItemClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.details.setText(notification.getDetails());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, details;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            details = itemView.findViewById(R.id.notification_details);
        }
    }
}
