package com.jamburger.kitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.R;
import com.jamburger.kitter.activities.OtherProfileActivity;
import com.jamburger.kitter.components.Comment;
import com.jamburger.kitter.components.User;
import com.jamburger.kitter.utilities.DateTimeFormatter;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mComments;

    // Listener for item clicks
    private OnItemClickListener mListener;

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CommentAdapter(Context mContext, List<Comment> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        DocumentReference publisherReference = FirebaseFirestore.getInstance().collection("Users").document(comment.getPublisherId());
        publisherReference.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                if (user != null && mContext != null) {
                    Glide.with(mContext).load(user.getProfileImageUrl()).into(holder.profileImage);
                    holder.username.setText(user.getUsername());
                    holder.time.setText(DateTimeFormatter.getTimeDifference(comment.getCommentId(), false));
                    holder.comment.setText(comment.getText());

                    // Set the timestamp text
                    //holder.time.setText(DateTimeFormatter.getTimeDifference(comment.getCommentId(), true));

                    // Set click listener for the container view
                    holder.container.setOnClickListener(view -> {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION && mListener != null) {
                            mListener.onItemClick(pos);
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error
        });
    }


    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username, comment, time;
        View container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            profileImage = itemView.findViewById(R.id.img_profile);
            username = itemView.findViewById(R.id.txt_username);
            time = itemView.findViewById(R.id.txt_time);
            comment = itemView.findViewById(R.id.txt_comment);
        }
    }
}
