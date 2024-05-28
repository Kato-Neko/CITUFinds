package com.jamburger.kitter.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jamburger.kitter.R;

public class TargetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        TextView detailsTextView = findViewById(R.id.details_text_view);

        String details = getIntent().getStringExtra("details");
        if (details != null) {
            detailsTextView.setText(details);
        }
    }
}
