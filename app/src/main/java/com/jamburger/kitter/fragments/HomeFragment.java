package com.jamburger.kitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jamburger.kitter.PostActivity;
import com.jamburger.kitter.R;
import com.jamburger.kitter.adapters.PostAdapter;
import com.jamburger.kitter.components.Post;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerViewPosts;
    PostAdapter postAdapter;
    ImageView postButton;
    List<Post> posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewPosts = view.findViewById(R.id.recyclerview_posts);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(requireContext(), posts);
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setAdapter(postAdapter);
        readPosts();

        postButton = view.findViewById(R.id.btn_add_post);
        postButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        return view;
    }

    void readPosts() {
        CollectionReference postsReference = FirebaseFirestore.getInstance().collection("Posts");
        postsReference.get().addOnSuccessListener(postSnapshots -> {
            posts.clear();
            for (DocumentSnapshot postSnapshot : postSnapshots) {
                Post post = postSnapshot.toObject(Post.class);
                if (post.getComments() == null)
                    postsReference.document(post.getPostid()).update("comments", new ArrayList<>());
                if (post.getKitt() == null)
                    postsReference.document(post.getPostid()).update("kitt", "");
                posts.add(post);
            }
            postAdapter.notifyDataSetChanged();
        });
    }
}