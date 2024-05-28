package com.jamburger.kitter.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.R;
import com.jamburger.kitter.adapters.NotificationAdapter;
import com.jamburger.kitter.components.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "NotificationActivity";

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private CollectionReference notificationsRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerViewNotifications = findViewById(R.id.recyclerview_notifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        recyclerViewNotifications.setAdapter(notificationAdapter);

        loadNotifications();
    }

    private void loadNotifications() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e(TAG, "User ID is null");
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        notificationsRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("notifications");

        notificationsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error getting notifications", e);
                Toast.makeText(NotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                Log.d(TAG, "Snapshot size: " + snapshots.size());
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    Notification notification = dc.getDocument().toObject(Notification.class);
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "Notification added: " + notification.getTitle());
                            notificationList.add(notification);
                            break;
                        case MODIFIED:
                            for (int i = 0; i < notificationList.size(); i++) {
                                if (notificationList.get(i).getId().equals(notification.getId())) {
                                    notificationList.set(i, notification);
                                    break;
                                }
                            }
                            break;
                        case REMOVED:
                            notificationList.removeIf(n -> n.getId().equals(notification.getId()));
                            break;
                    }
                }
                notificationAdapter.notifyDataSetChanged();
            }
        });
    }
}
