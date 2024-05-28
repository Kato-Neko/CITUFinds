package com.jamburger.kitter.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.R;

public class NotificationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_details); // Use the new layout file

        // Get notification ID from intent
        String notificationId = getIntent().getStringExtra("notificationId");

        // Fetch notification details from Firestore based on the ID
        FirebaseFirestore.getInstance()
                .collection("notifications")
                .document(notificationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Notification document found
                        // Extract details and display in the activity
                        String title = documentSnapshot.getString("title");
                        String message = documentSnapshot.getString("message");

                        // Set text to TextViews in your activity's layout
                        TextView titleTextView = findViewById(R.id.notification_details_title);
                        TextView messageTextView = findViewById(R.id.notification_details_message);
                        titleTextView.setText(title);
                        messageTextView.setText(message);
                    } else {
                        // Notification document not found
                        // Handle the case when the notification is no longer available
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur while fetching notification details
                });
    }
}
