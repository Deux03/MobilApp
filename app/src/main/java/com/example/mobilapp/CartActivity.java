package com.example.mobilapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class CartActivity extends CourseListActivity implements CartAdapter.OnItemClickListener {
    private static final String CHANNEL_ID = "purchase_channel";
    private ArrayList<ShoppingItem> selectedItems;
    private CartAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requestNotificationPermission();
        createNotificationChannel();
        Intent intent = getIntent();
        Serializable extra = intent.getSerializableExtra("selected_items");

        @SuppressWarnings("unchecked")
        ArrayList<ShoppingItem> tempItems = (ArrayList<ShoppingItem>) extra;

        if (tempItems != null) {
            selectedItems = tempItems;
        } else {
            selectedItems = new ArrayList<>();
            Log.e("CartActivity", "Received unexpected data type for selected_items");
        }

        if (selectedItems == null) {
            selectedItems = new ArrayList<>();
        }

        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CartAdapter(this, selectedItems, this);
        recyclerView.setAdapter(mAdapter);

        findViewById(R.id.purchaseButton).setOnClickListener(view -> {
            onPurchaseClick(selectedItems);
        });

    }

    @Override
    public void onDeleteClick(int position) {
        selectedItems.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onPurchaseClick(ArrayList<ShoppingItem> items) {
        Toast.makeText(CartActivity.this, "You bought all of the items", Toast.LENGTH_LONG).show();
        showPurchaseNotification();
        selectedItems.clear();
        mAdapter.notifyDataSetChanged();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Purchase Notification";
            String description = "Channel for purchase notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Log.d("CartActivity", "Notification channel created");
        }
    }

    private void showPurchaseNotification() {
        Log.d("CartActivity", "Showing purchase notification");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pourches)
                .setContentTitle("Purchase Successful")
                .setContentText("Thank you for your purchase!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

        Log.d("CartActivity", "Notification sent");
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CartActivity", "Notification permission granted");
            } else {
                Log.e("CartActivity", "Notification permission denied");
            }
        }
    }



}
