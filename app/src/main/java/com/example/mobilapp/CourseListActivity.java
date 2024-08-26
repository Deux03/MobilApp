package com.example.mobilapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class CourseListActivity extends AppCompatActivity {
    private static final String LOG_TAG = CourseListActivity.class.getName();
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemsData;
    private ArrayList<ShoppingItem> selectedItems;
    private ShoppingItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private FrameLayout blueCircle;
    private TextView contentTextView;

    private int cartItems = 0;
    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Courses");
        selectedItems = new ArrayList<>();

        if(user == null) {
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        int gridNumber = 1;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemsData = new ArrayList<>();
        mAdapter = new ShoppingItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);
        
        queryData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void queryData() {
        mItemsData.clear();

        mItems.orderBy("name").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                ShoppingItem item  = document.toObject(ShoppingItem.class);
                mItemsData.add(item);
            }
            if(mItemsData.isEmpty()){
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeData() {
        String[] itemList = getResources().getStringArray(R.array.course_names);
        String[] iteminfo = getResources().getStringArray(R.array.course_desc);
        String[] itemPrice = getResources().getStringArray(R.array.course_prices);
        TypedArray itemImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.shopping_item_rates);

        mItemsData.clear();
        for(int i = 0; i < itemList.length; i++) {
            mItems.add(new ShoppingItem(itemList[i], iteminfo[i], itemPrice[i], itemRate.getFloat(i, 0), itemImageResource.getResourceId(i, 0)));
            //mItemlist.add(new ShoppingItem(itemList[i], iteminfo[i], itemPrice[i], itemRate.getFloat(i, 0), itemImageResource.getResourceId(i, 0)));
        }
        itemImageResource.recycle();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchOption);
        if (menuItem == null) {
            return false;
        }

        SearchView searchView = (SearchView) menuItem.getActionView();
        if (searchView == null) {
            return false;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logoutButton) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        } else if (id == R.id.cart) {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("selected_items", selectedItems);
            for (int i = 0; i<selectedItems.size(); ++i){
                Log.d(LOG_TAG, "CART ITEM:" + i + ".:" + selectedItems.get(i).getName() + " price" + selectedItems.get(i).getPrice());
            }
            startActivity(intent);
            return true;
        } else if (id == R.id.view_selector) {
            handleViewSelector(item);
            return true;
        } else if (id == R.id.profileBtn) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.course) {
            Intent intent = new Intent(this, CourseListActivity.class);
            intent.putExtra("selected_items", selectedItems);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        blueCircle = rootView.findViewById(R.id.view_alert_blue_circle);
        contentTextView = rootView.findViewById(R.id.view_alert_count_TextView);

        if (cartItems > 0) {
            blueCircle.setVisibility(View.VISIBLE);
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            blueCircle.setVisibility(View.GONE);
            contentTextView.setText("");
        }

        rootView.setOnClickListener(view -> onOptionsItemSelected(alertMenuItem));
        return super.onPrepareOptionsMenu(menu);
    }


    private void handleViewSelector(MenuItem item) {
        if (viewRow) {
            changeSpanCount(item, R.drawable.ic_view, 2);
        } else {
            changeSpanCount(item, R.drawable.ic_view, 1);
        }
    }

    protected void changeSpanCount(MenuItem item, int drawableID, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableID);
        if (mRecyclerView != null) {
            GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
            layoutManager.setSpanCount(spanCount);
        }
    }
    public void updateAlertIcon() {
        cartItems = (cartItems + 1);
        if (cartItems > 0) {
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }
        invalidateOptionsMenu();
    }
    public void addToCart(ShoppingItem item) {
        selectedItems.add(item);
        updateAlertIcon();
    }

}
