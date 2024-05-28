package com.jamburger.kitter.activities;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.components.Notification;
import java.util.Date;

public class NotificationUtils {
    private static final String TAG = "NotificationUtils";

    public static void sendNotification(String type, String message, String targetUserId, String details) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e(TAG, "User ID is null");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                String userName = document.getString("name");

                Notification notification = new Notification(
                        generateId(db),
                        type,
                        message,
                        details,
                        new Date(),
                        type,
                        userId,
                        userName
                );

                db.collection("Users").document(targetUserId).collection("notifications").document(notification.getId())
                        .set(notification.toMap())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification added successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error adding notification", e));
            } else {
                Log.e(TAG, "Error fetching user name", task.getException());
            }
        });
    }

    private static String generateId(FirebaseFirestore db) {
        return db.collection("Users").document().getId();
    }
}
