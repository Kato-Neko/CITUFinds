package com.jamburger.kitter.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jamburger.kitter.R;
import com.jamburger.kitter.adapters.CommentAdapter;
import com.jamburger.kitter.components.Comment;
import com.jamburger.kitter.activities.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private EditText commentInput;
    private ImageView postCommentButton;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String postId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Initialize views
        commentInput = findViewById(R.id.et_comment);
        postCommentButton = findViewById(R.id.btn_send_message);
        recyclerView = findViewById(R.id.recyclerview_comments);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get postId from intent
        postId = getIntent().getStringExtra("postid");

        // Set up the post comment button
        postCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString();
            if (!TextUtils.isEmpty(commentText)) {
                postComment(commentText);
            }
        });

        // Load existing comments
        loadComments();
    }

    private void postComment(String commentText) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You need to be logged in to comment.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        CollectionReference commentsRef = db.collection("comments").document(postId).collection("postComments");
        String commentId = commentsRef.document().getId();
        Comment comment = new Comment(userId, commentText, commentId);

        commentsRef.document(commentId).set(comment).addOnSuccessListener(aVoid -> {
            commentInput.setText(""); // Clear input field
            Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();

            // Fetch the user's details for the notification
            FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username"); // Assumes 'username' is the field for user's real name
                            String details = "Comment ID: " + commentId; // Customize details as needed
                            NotificationUtils.sendNotification("Comment", username + " commented on your post", userId, details);
                        }
                    })
                    .addOnFailureListener(e1 -> {
                        Toast.makeText(CommentsActivity.this, "Failed to fetch user details: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to post comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadComments() {
        CollectionReference commentsRef = db.collection("comments").document(postId).collection("postComments");
        commentsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(CommentsActivity.this, "Failed to load comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                commentList.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    Comment comment = doc.toObject(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }
        });
    }
}
