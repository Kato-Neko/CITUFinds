package com.jamburger.kitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.R;
import com.jamburger.kitter.activities.ChatHomeActivity;
import com.jamburger.kitter.activities.NotificationActivity;
import com.jamburger.kitter.activities.PostActivity;
import com.jamburger.kitter.adapters.PostAdapter;
import com.jamburger.kitter.components.Post;
import com.google.android.material.appbar.MaterialToolbar;

public class HomeFragment extends Fragment {
    RecyclerView recyclerViewPosts;
    PostAdapter postAdapter;
    MaterialToolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewPosts = view.findViewById(R.id.recyclerview_posts);

        toolbar = view.findViewById(R.id.top_menu);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        postAdapter = new PostAdapter(requireContext());
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setAdapter(postAdapter);
        readPosts();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Ensure fragment can handle menu events
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean onMenuItemClick(MenuItem item) {
        Intent intent;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_post_image) {
            intent = new Intent(requireActivity(), PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("type", "picture");
            startActivity(intent);
        } else if (itemId == R.id.nav_post_text) {
            intent = new Intent(requireActivity(), PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("type", "text");
            startActivity(intent);
        } else if (itemId == R.id.nav_chat) {
            intent = new Intent(requireActivity(), ChatHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (itemId == R.id.nav_notifications) {
            intent = new Intent(requireActivity(), NotificationActivity.class);
            startActivity(intent);
        }
        return true;
    }

    void readPosts() {
        CollectionReference feedReference = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("feed");
        feedReference.get().addOnSuccessListener(feedSnapshots -> {
            postAdapter.clearPosts();
            for (DocumentSnapshot feedSnapshot : feedSnapshots) {
                DocumentReference postReference = feedSnapshot.getDocumentReference("postReference");
                boolean isVisited = Boolean.TRUE.equals(feedSnapshot.getBoolean("visited"));
                assert postReference != null;
                postReference.get().addOnSuccessListener(postSnapshot -> postAdapter.addPost(postSnapshot.toObject(Post.class)));
            }
        });
    }
}
