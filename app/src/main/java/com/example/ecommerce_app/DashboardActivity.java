package com.example.ecommerce_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerce_app.Adapters.ProductAdapter;
import com.example.ecommerce_app.databinding.ActivityDashboardBinding;
import com.example.ecommerce_app.models.Category_model;
import com.example.ecommerce_app.models.Product_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements ProductAdapter.CartUpdateListener {
    ActivityDashboardBinding binding;
    public TextView cartItemCountTextView;
    private FirebaseFirestore firestore;
    private int cartItemCount = 0;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<Product_model> cartItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore = FirebaseFirestore.getInstance();
        cartItemCountTextView = findViewById(R.id.cart_item_count);

        // Initialize SharedPreferences and Gson in your onCreate() method
        sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE);
        gson = new Gson();
        cartItemCountTextView = findViewById(R.id.cart_item_count);
        // Call method to fetch categories and populate the UI
        fetchCategoriesAndProducts();
        cartItems = loadCartItems();
        updateCartIcon();
        handler.postDelayed(updateCartIconRunnable, 5000); // Start after 5 seconds

        binding.cartId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, CartActivity.class));
            }
        });

        binding.logoutId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform logout
                FirebaseAuth.getInstance().signOut();

                // Clear the saved user data from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Clear all the saved data
                editor.apply();

                // Show a toast message
                Toast.makeText(DashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Redirect to LoginActivity
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });
    }

    private void fetchCategoriesAndProducts() {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Category_model> categories = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category_model category = document.toObject(Category_model.class);
                            categories.add(category);
                        }
                        addCategoriesToUI(categories);
                    } else {
                        // Handle the error
                    }
                });
    }

    private void addCategoriesToUI(List<Category_model> categories) {
        LinearLayout categoriesContainer = findViewById(R.id.categories_container);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Category_model category : categories) {
            // Inflate the category row layout
            View categoryView = inflater.inflate(R.layout.category_row, categoriesContainer, false);

            // Set category name
            TextView categoryName = categoryView.findViewById(R.id.categoryName_id);
            categoryName.setText(category.getName());


            // Fetch products by category and display them
            RecyclerView productRecyclerView = categoryView.findViewById(R.id.productRecyclerView_id);
            fetchProductsByCategory(category.getId(), productRecyclerView);

            // Add the category view to the container
            categoriesContainer.addView(categoryView);
        }
    }

    @Override
    public void onAddToCart(Product_model product) {
        addToCart(product);
    }

    private void fetchProductsByCategory(String categoryId, RecyclerView recyclerView) {
        firestore.collection("products")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product_model> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product_model product = document.toObject(Product_model.class);
                            productList.add(product);
                         }
                        // Set up the horizontal RecyclerView with product data
                        ProductAdapter productAdapter = new ProductAdapter(this, productList, this);

                        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerView.setAdapter(productAdapter);
                    } else {
                        // Handle the error
                    }
                });
    }

    private void addToCart(Product_model product) {
        // Load the current cart items
        saveCartItems();
        updateCartIcon();

        if (cartItems.contains(product)) {
            Toast.makeText(this, "Product already in cart", Toast.LENGTH_SHORT).show();
        } else {
            product.setQuantity("1");
            cartItems.add(product); // Add the product to cart
            cartItemCount = cartItems.size(); // Update cart count
            cartItemCountTextView.setText(String.valueOf(cartItemCount));  // Update UI
            saveCartItems(cartItems); // Save the updated cart list
            updateCartIcon(); // Update cart icon
            Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCartItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cartItemsJson = gson.toJson(cartItems);
        editor.putString("cart_items", cartItemsJson);
        editor.apply();
    }
    // Load cart items from SharedPreferences
    private List<Product_model> loadCartItems() {
        String cartItemsJson = sharedPreferences.getString("cart_items", null);
        if (cartItemsJson != null) {
            // Convert JSON back to List<Product_model>
            Type type = new TypeToken<List<Product_model>>() {
            }.getType();
            return gson.fromJson(cartItemsJson, type);
        } else {
            return new ArrayList<>(); // Return empty list if no items are saved
        }
    }

    // Save cart items to SharedPreferences
    private void saveCartItems(List<Product_model> cartItems) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cartItemsJson = gson.toJson(cartItems); // Convert cartItems to JSON
        editor.putString("cart_items", cartItemsJson); // Save JSON string
        editor.apply();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    public void updateCartIcon() {
        cartItems = loadCartItems();

        binding.cartItemCount.setText(String.valueOf(cartItems.size()));
    }


        private Handler handler = new Handler();
        private Runnable updateCartIconRunnable = new Runnable() {
            @Override
            public void run() {
                updateCartIcon();  // Call the updateCartIcon method
                handler.postDelayed(this, 5000); // Recall this runnable after 5 seconds (5000 ms)
            }
        };



        @Override
        protected void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(updateCartIconRunnable); // Stop the handler when the activity is destroyed
        }
    }


