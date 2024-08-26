package com.example.mobilapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends CourseListActivity {
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView phoneTypeTextView;
    private TextView addressTextView;
    private TextView accountTypeTextView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        phoneTypeTextView = findViewById(R.id.phoneTypeTextView);
        addressTextView = findViewById(R.id.addressTextView);
        accountTypeTextView = findViewById(R.id.accountTypeTextView);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            fetchUserProfileData();
        } else {
            Toast.makeText(ProfileActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchUserProfileData() {
        String userId = currentUser.getUid();

        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String username = "username: No data";
                        String email = "email: No data";
                        String phone = "phone: No data";
                        String phoneType = "phoneType: No data";
                        String address = "address: No data";
                        String accountType = "accountType: No data";
                        if (document.exists()) {
                            username = "username: " + document.getString("username");
                            email = "email: " + document.getString("email");
                            phone = "phone: " + document.getString("phone");
                            phoneType = "phoneType: " + document.getString("phoneType");
                            address = "address: " + document.getString("address");
                            accountType = "accountType: " + document.getString("accountType");

                        } else {
                            Toast.makeText(ProfileActivity.this, "User profile data not found!", Toast.LENGTH_SHORT).show();
                        }
                        usernameTextView.setText(username);
                        emailTextView.setText(email);
                        phoneTextView.setText(phone);
                        phoneTypeTextView.setText(phoneType);
                        addressTextView.setText(address);
                        accountTypeTextView.setText(accountType);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error getting profile data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
