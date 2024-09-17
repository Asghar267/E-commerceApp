package com.example.ecommerce_app.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ecommerce_app.models.Product_model;
import com.example.ecommerce_app.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product_model> productList;
    private CartUpdateListener cartUpdateListener;

    // Interface for updating the cart
    public interface CartUpdateListener {
        void onAddToCart(Product_model product);
    }

    public ProductAdapter(Context context, List<Product_model> productList) {
        this.context = context;
        this.productList = productList;

    }
    public ProductAdapter(Context context, List<Product_model> productList, CartUpdateListener cartUpdateListener) {
        this.context = context;
        this.productList = productList;
        this.cartUpdateListener = cartUpdateListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.proditem, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product_model product = productList.get(position);

        // Set product details
        holder.productName.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice());

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
                .into(holder.productImage);

        // Set up the Add to Cart button click listener
        holder.addToCartButton.setOnClickListener(v -> {
            if (cartUpdateListener != null) {
                cartUpdateListener.onAddToCart(product);
//                Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "CartUpdateListener is not set!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_view);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}












//package com.example.ecommerce_app.Adapters;
////package com.example.ecommerce_app;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import com.example.ecommerce_app.models.Product_model;
//import com.example.ecommerce_app.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
//
//
//    private Context context;
//    private List<Product_model> productList;
//    private OnProductClickListener onProductClickListener; // Listener for product actions
//    private CartUpdateListener cartUpdateListener;
//
//    public interface CartUpdateListener {
//        void onAddToCart(Product_model product);
//    }
//
//    public ProductAdapter(Context context, List<Product_model> productList, CartUpdateListener cartUpdateListener) {
//        this.context = context;
//        this.productList = productList;
//        this.cartUpdateListener = cartUpdateListener;
//    }
////    public ProductAdapter(Context context, List<Product_model> productList) {
////        this.context = context;
////        this.productList = productList;
////
////    }
//
//
//    @NonNull
//    @Override
//    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
//        View view = LayoutInflater.from(context).inflate(R.layout.proditem, parent, false);
//        return new ProductViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
//        Product_model product = productList.get(position);
//        holder.addToCartButton.setOnClickListener(v -> {
//            if (cartUpdateListener != null) {
//                cartUpdateListener.onAddToCart(product);
//                Toast.makeText(context,"add cart button", Toast.LENGTH_SHORT);
//
//            } else {
//                Toast.makeText(context, "CartUpdateListener is not set!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        // Set up the Add to Cart button
//
//         holder.productName.setText(product.getTitle());
//        holder.productPrice.setText(product.getPrice());
//        Glide.with(context)
//                .load(product.getImage())
//                 .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.person_icon)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        Log.e("Glide", "Image Load Failed: " + e.getMessage());
//                        return false;  // Allow Glide to set the error drawable
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d("Glide", "Image Loaded Successfully");
//                        return false;
//                    }
//                })
//                .into(holder.productImage);
//    }
//
//    public interface OnProductClickListener {
//        void onAddToCartClick(Product_model product);
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        Button addToCartButton;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            addToCartButton = itemView.findViewById(R.id.addToCartButton);
//        }
//    }
//    public class ProductViewHolder extends RecyclerView.ViewHolder {
//        ImageView productImage;
//        TextView productName, productPrice;
//        Button addToCartButton;
//
//        public ProductViewHolder(@NonNull View itemView) {
//            super(itemView);
//            productImage = itemView.findViewById(R.id.product_image_view);
//            productName = itemView.findViewById(R.id.product_name);
//            productPrice = itemView.findViewById(R.id.product_price);
//            addToCartButton = itemView.findViewById(R.id.addToCartButton);
//        }
//    }
//}
//
