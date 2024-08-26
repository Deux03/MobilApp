package com.example.mobilapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private ArrayList<ShoppingItem> mCartItems;
    private Context mContext;
    private OnItemClickListener mListener;
    private int lastPosition = -1;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onPurchaseClick(ArrayList<ShoppingItem> items);
    }

    public CartAdapter(Context context, ArrayList<ShoppingItem> cartItems, OnItemClickListener listener) {
        this.mContext = context;
        this.mCartItems = cartItems;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ShoppingItem currentItem = mCartItems.get(position);
        holder.bindTo(currentItem);

        if(holder.getBindingAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_fade_in);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getBindingAdapterPosition();
        }

    }

    @Override
    public int getItemCount() {
        return mCartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private Button mDeleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.cart_item_title);
            mPriceText = itemView.findViewById(R.id.cart_item_price);
            mItemImage = itemView.findViewById(R.id.cart_item_image);
            mDeleteButton = itemView.findViewById(R.id.delete_button);

            mDeleteButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onDeleteClick(position);
                }
            });
        }

        public void bindTo(ShoppingItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mPriceText.setText(currentItem.getPrice());
            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
        }
    }
}
