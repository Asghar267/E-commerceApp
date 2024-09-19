package com.example.ecommerce_app.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ecommerce_app.R;
import com.example.ecommerce_app.models.Product_model;
import com.google.gson.Gson;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product_model> cartList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private CartUpdateListener cartUpdateListener;
    private TextView cartItemCountTextView;

    public CartAdapter(Context context, List<Product_model> cartList, TextView cartItemCountTextView, CartUpdateListener cartUpdateListener) {
        this.context = context;
        this.cartList = cartList;
        this.cartItemCountTextView = cartItemCountTextView;
        this.cartUpdateListener = cartUpdateListener;
        sharedPreferences = context.getSharedPreferences("CartPreferences", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_items_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product_model product = cartList.get(position);

        holder.productName.setText(product.getTitle());
        holder.productPrice.setText(String.valueOf(product.getPrice()));


        // Ensure that quantity is not null
        String quantityStr = product.getQuantity();
        if (quantityStr == null || quantityStr.isEmpty()) {
            // Set a default quantity if null or empty
            quantityStr = "0";
            product.setQuantity(quantityStr);
        }

        holder.count_item.setText(quantityStr);


        // Increment Quantity
        holder.increment.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(product.getQuantity());
            product.setQuantity(String.valueOf(currentQuantity + 1));
            holder.count_item.setText(product.getQuantity());
            saveCartItems();
            cartUpdateListener.onCartUpdated();
        });

        // Decrement Quantity
        holder.decrement.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(product.getQuantity());
            if (currentQuantity > 1) {
                product.setQuantity(String.valueOf(currentQuantity - 1));
                holder.count_item.setText(product.getQuantity());
                saveCartItems();
                cartUpdateListener.onCartUpdated();
            }
        });

        // Remove Item from Cart
        holder.remove.setOnClickListener(v -> {
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
            saveCartItems();
            updateCartIcon();
            cartUpdateListener.onCartUpdated();
        });

        // Load product image using Glide
        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.person_icon)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Image Load Failed: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Image Loaded Successfully");
                        return false;
                    }
                })
                .into(holder.product_image_view);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private void saveCartItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cartItemsJson = gson.toJson(cartList);
        editor.putString("cart_items", cartItemsJson);
        editor.apply();
    }

    private void updateCartIcon() {
        if (cartItemCountTextView != null) {
            cartItemCountTextView.setText(String.valueOf(cartList.size()));
        }
    }

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productPrice,count_item;
        ImageView increment, decrement, remove, product_image_view;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            count_item = itemView.findViewById(R.id.count_item);
            increment = itemView.findViewById(R.id.plus_item);
            decrement = itemView.findViewById(R.id.remove_item);
            remove = itemView.findViewById(R.id.delete_item);
            product_image_view = itemView.findViewById(R.id.product_image_view);

        }
    }
}
