package com.jamburger.kitter.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jamburger.kitter.R;
import com.jamburger.kitter.adapters.MessageAdapter;
import com.jamburger.kitter.components.Message;
import com.jamburger.kitter.components.User;
import com.jamburger.kitter.utilities.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    User fellow;
    String myUID, fellowUID;
    TextView username;
    EditText message;
    MessageAdapter messageAdapter;
    RecyclerView recyclerViewMessages;
    CollectionReference chatReference;
    ImageView profileImage, sendButton;
    String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myUID = FirebaseAuth.getInstance().getUid();
        fellowUID = getIntent().getStringExtra("userid");

        username = findViewById(R.id.txt_username);
        profileImage = findViewById(R.id.img_profile);
        message = findViewById(R.id.et_message);
        sendButton = findViewById(R.id.btn_send_message);
        recyclerViewMessages = findViewById(R.id.recyclerview_messages);

        CollectionReference users = FirebaseFirestore.getInstance().collection("Users");
        users.document(fellowUID).get().addOnSuccessListener(documentSnapshot -> {
            fellow = documentSnapshot.toObject(User.class);
            if (fellow != null) {
                username.setText(fellow.getUsername());
                Glide.with(this).load(fellow.getProfileImageUrl()).into(profileImage);

                messageAdapter = new MessageAdapter(this, fellow.getProfileImageUrl());
                recyclerViewMessages.setHasFixedSize(true);
                recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewMessages.setAdapter(messageAdapter);

                getChatData();
                readMessages();
            }
        });

        sendButton.setOnClickListener(v -> {
            String messageString = message.getText().toString();
            if (!messageString.isEmpty()) {
                String messageId = DateTimeFormatter.getCurrentTime();
                Log.d(TAG, "Creating new message with ID: " + messageId);
                message.setText("");
                Message newMessage = new Message(messageId, messageString, myUID);
                Log.d(TAG, "Message details: ID=" + newMessage.getMessageId() + ", Text=" + newMessage.getText() + ", SenderID=" + newMessage.getSenderId());

                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("messageId", newMessage.getMessageId());
                messageMap.put("text", newMessage.getText());
                messageMap.put("senderId", newMessage.getSenderId());

                chatReference.add(messageMap)
                        .addOnSuccessListener(documentReference -> Log.d(TAG, "Message sent successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to send message", e));
            }
        });
    }

    private void readMessages() {
        chatReference.orderBy("messageId").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                return;
            }

            if (snapshots != null) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    Message nextMessage = documentSnapshot.toObject(Message.class);
                    Log.d(TAG, "Message received: ID=" + nextMessage.getMessageId() + ", Text=" + nextMessage.getText() + ", SenderID=" + nextMessage.getSenderId());

                    String nextDateMonth = DateTimeFormatter.getDateMonth(nextMessage.getMessageId());
                    Message lastMessage = messageAdapter.getLastMessage();

                    if (lastMessage == null) {
                        String today = DateTimeFormatter.getDateMonth(DateTimeFormatter.getCurrentTime());
                        if (nextDateMonth.equals(today)) nextDateMonth = "Today";
                        Message timestamp = new Message("@", nextDateMonth, "");
                        messageAdapter.addMessage(timestamp);
                    } else {
                        String lastDateMonth = DateTimeFormatter.getDateMonth(lastMessage.getMessageId());
                        if (!lastDateMonth.equals(nextDateMonth)) {
                            String today = DateTimeFormatter.getDateMonth(DateTimeFormatter.getCurrentTime());
                            if (nextDateMonth.equals(today)) nextDateMonth = "Today";
                            Message timestamp = new Message("@", nextDateMonth, "");
                            messageAdapter.addMessage(timestamp);
                        }
                    }

                    messageAdapter.addMessage(nextMessage);
                }
                if (messageAdapter.getItemCount() > 0) {
                    recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }
        });
    }

    private void getChatData() {
        boolean less = myUID.compareTo(fellowUID) < 0;
        chatId = less ? myUID + '&' + fellowUID : fellowUID + '&' + myUID;
        chatReference = FirebaseFirestore.getInstance().collection("chats").document(chatId).collection("messages");
        Log.d(TAG, "Chat data reference path: " + chatReference.getPath());
    }
}
