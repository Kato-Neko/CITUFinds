package com.jamburger.kitter.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.R;
import com.jamburger.kitter.adapters.NotificationAdapter;
import com.jamburger.kitter.components.Notification;
import com.jamburger.kitter.components.Message;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private CollectionReference notificationsRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView and adapter
        recyclerViewNotifications = findViewById(R.id.recyclerview_notifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList, this::openNotificationDetails);
        recyclerViewNotifications.setAdapter(notificationAdapter);

        // Load notifications
        loadNotifications();
    }

    private void loadNotifications() {
        // Check if user is authenticated
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated");
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get reference to notifications collection for current user
        notificationsRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(currentUser.getUid())
                .collection("notifications");

        // Listen for real-time updates to notifications
        notificationsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error getting notifications", e);
                Toast.makeText(NotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                // Clear existing notification list
                notificationList.clear();

                // Iterate over document changes
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            // Handle added notification
                            Notification addedNotification = dc.getDocument().toObject(Notification.class);
                            Log.d(TAG, "Notification added: " + addedNotification.getTitle());
                            notificationList.add(addedNotification);
                            break;
                        case MODIFIED:
                            // Handle modified notification
                            Notification modifiedNotification = dc.getDocument().toObject(Notification.class);
                            for (int i = 0; i < notificationList.size(); i++) {
                                if (notificationList.get(i).getId().equals(modifiedNotification.getId())) {
                                    notificationList.set(i, modifiedNotification);
                                    break;
                                }
                            }
                            break;
                        case REMOVED:
                            // Handle removed notification
                            Notification removedNotification = dc.getDocument().toObject(Notification.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                notificationList.removeIf(n -> n.getId().equals(removedNotification.getId()));
                            }
                            break;
                    }
                }

                // Notify adapter of data changes
                notificationAdapter.notifyDataSetChanged();
            }
        });
    }

    private void openNotificationDetails(Notification notification) {
        Message message=new Message();
        // Create an intent to open the NotificationDetailsActivity
        Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
        // Pass the ID of the clicked notification as an extra with the intent
        intent.putExtra("senderId", message.getSenderId());
        // Start the NotificationDetailsActivity
        startActivity(intent);
    }
}
