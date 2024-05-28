package com.jamburger.kitter.adapters;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.jamburger.kitter.R;
import com.jamburger.kitter.components.Message;
import com.jamburger.kitter.utilities.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MESSAGE_LAYOUT = 0;
    private static final int TIMESTAMP_LAYOUT = 1;
    String fellowProfileImageUrl;
    String myUID;
    Context mContext;
    List<Message> messages;

    public MessageAdapter(Context mContext, String fellowProfileImageUrl) {
        this.mContext = mContext;
        this.messages = new ArrayList<>();
        myUID = FirebaseAuth.getInstance().getUid();
        this.fellowProfileImageUrl = fellowProfileImageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_LAYOUT)
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_message, parent, false);
        else
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_timestamp, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getMessageId().equals("@") ? TIMESTAMP_LAYOUT : MESSAGE_LAYOUT;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        Message nextMessage = position + 1 < messages.size() ? messages.get(position + 1) : null;

        Log.d("MessageAdapter", "Binding message: " + message.getText() + " at position: " + position);

        if (holder.getItemViewType() == MESSAGE_LAYOUT) {
            holder.message.setText(message.getText());
            holder.time.setText(DateTimeFormatter.getHoursMinutes(message.getMessageId()));
            Log.d("MessageAdapter", "Message time: " + holder.time.getText());

            if (myUID.equals(message.getSenderId())) {
                holder.container.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                holder.profileImage.setVisibility(View.GONE);
                Log.d("MessageAdapter", "Message sent by me, hiding profile image");
            } else {
                // Set chat bubble colors for messages sent by other
                holder.messageCard.setBackgroundColor(ContextCompat.getColor(mContext, R.color.maroon)); // Set the background color here
                holder.messageCard.setRadius(mContext.getResources().getDimension(R.dimen.card_corner_radius));
                holder.message.setTextColor(ContextCompat.getColor(mContext, R.color.text_message));
                holder.time.setTextColor(ContextCompat.getColor(mContext, R.color.text_message));
                if (nextMessage == null || !nextMessage.getSenderId().equals(message.getSenderId())) {
                    holder.profileImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(fellowProfileImageUrl).into(holder.profileImage);
                    Log.d("MessageAdapter", "Message sent by fellow, showing profile image");
                } else {
                    holder.profileImage.setVisibility(View.INVISIBLE);
                    Log.d("MessageAdapter", "Message sent by fellow, but next message is from the same sender, hiding profile image");
                }
            }
        } else {
            holder.timestamp.setText(message.getText());
            Log.d("MessageAdapter", "Timestamp: " + message.getText());
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    public Message getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        ImageView profileImage;
        TextView timestamp;
        View container;

        CardView messageCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.txt_message);
            time = itemView.findViewById(R.id.txt_time);
            profileImage = itemView.findViewById(R.id.img_profile);
            container = itemView.findViewById(R.id.container);
            timestamp = itemView.findViewById(R.id.txt_timestamp);
            messageCard = itemView.findViewById(R.id.message_card);
        }
    }
}
