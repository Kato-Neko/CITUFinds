package com.jamburger.kitter.activities;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.components.Notification;

public class NotificationUtils {
    private static final String TAG = "NotificationUtils";

    public static void sendNotification(String title, String message, String recipientId, String details, String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String notificationId = db.collection("Users").document(recipientId).collection("notifications").document().getId();

        Timestamp timestamp = Timestamp.now();

        // Create notification object
        Notification notification = new Notification(notificationId, title, message, details, userName, timestamp);

        // Log the notification details
        Log.d(TAG, "Sending notification: " + notification.toString());

        // Set notification data in Firestore
        db.collection("Users").document(recipientId).collection("notifications").document(notificationId)
                .set(notification)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification sent successfully: " + notificationId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to send notification", e));
    }


    public static void addInitialNotifications(String recipientId) {
        Log.d(TAG, "Adding initial notifications for user: " + recipientId);
        sendNotification("Welcome", "Welcome to Kitter!", recipientId, "Thank you for joining us.", "System");
        sendNotification("First Post", "You have made your first post!", recipientId, "Congratulations on your first post.", "System");
    }

}
