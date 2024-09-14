package com.example.ecommerce_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerce_app.databinding.ActivityDashboardBinding;
import com.example.ecommerce_app.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding binding;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        setSupportActionBar(binding.toolbarId);

    }
}