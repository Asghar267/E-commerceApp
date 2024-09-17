package com.example.ecommerce_app;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.Adapters.CartAdapter;
import com.example.ecommerce_app.models.Product_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartUpdateListener {

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Product_model> cartItems;
    private TextView totalPriceText;
    private TextView cartItemCountTextView;
    private Button checkoutButton;
    private ImageView backBtn;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize SharedPreferences and Gson
        sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE);
        gson = new Gson();

        // Load cart items
        cartItems = loadCartItems();

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceText = findViewById(R.id.total_amount);
        checkoutButton = findViewById(R.id.checkout_button);
//        cartItemCountTextView = findViewById(R.id.total_items);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( CartActivity.this,  DashboardActivity.class));

            }
        });


        // Initialize CartAdapter
        cartAdapter = new CartAdapter(this, cartItems, cartItemCountTextView, this);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        // Calculate and display total price
        calculateTotalPrice();

        // Checkout button functionality
        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                processCheckout();
            }
        });
    }

    // Method to load cart items from SharedPreferences
    private List<Product_model> loadCartItems() {
        String cartItemsJson = sharedPreferences.getString("cart_items", null);
        if (cartItemsJson != null) {
            Type type = new TypeToken<List<Product_model>>() {}.getType();
            return gson.fromJson(cartItemsJson, type);
        } else {
            return new ArrayList<>();
        }
    }

    // Method to calculate total price of all items in the cart
    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (Product_model product : cartItems) {
            try {
                double price = Double.parseDouble(product.getPrice());
                int quantity = Integer.parseInt(product.getQuantity());
                totalPrice += price * quantity;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        totalPriceText.setText(String.format("Total Price: $%.2f", totalPrice));
    }

    @Override
    public void onCartUpdated() {
        calculateTotalPrice();
    }

    // Method to handle the checkout process
    private void processCheckout() {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(CartActivity.this, "Please login first!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CartActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Get user's ID
        String userId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference soldProductsRef = firestore.collection("Sold Products");

        // Prepare data for each product
        List<Map<String, Object>> soldItems = new ArrayList<>();
        for (Product_model product : cartItems) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("productName", product.getTitle());
            productMap.put("price", product.getPrice());
            productMap.put("quantity", product.getQuantity());
            productMap.put("userId", userId);
            soldItems.add(productMap);
        }


        // Store sold products in Firestore
        for (Map<String, Object> soldItem : soldItems) {
            soldProductsRef.add(soldItem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CartActivity.this, "Checkout successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CartActivity.this, "Checkout failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Clear the cart and save the empty cart in SharedPreferences
        cartItems.clear();
        saveCartItems();

        // Update UI
        cartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
    }

    // Method to save updated cart items to SharedPreferences
    private void saveCartItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cartItemsJson = gson.toJson(cartItems);
        editor.putString("cart_items", cartItemsJson);
        editor.apply();
    }
}























//public class CartActivity extends AppCompatActivity {
//
//    private SharedPreferences sharedPreferences;
//    private Gson gson;
//    private RecyclerView cartRecyclerView;
//    private CartAdapter cartAdapter;
//    private List<Product_model> cartItems;
//    private TextView totalPriceText;
//    private Button checkoutButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart);
//
//        // Initialize SharedPreferences and Gson
//        sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE);
//        gson = new Gson();
//
//        // Load cart items
//        cartItems = loadCartItems();
//
//        // Set up RecyclerView and Adapter
//        cartRecyclerView = findViewById(R.id.cart_recycler_view);
//        totalPriceText = findViewById(R.id.total_amount);
//        checkoutButton = findViewById(R.id.checkout_button);
//
//        // Initialize CartAdapter
//        cartAdapter = new CartAdapter(this, cartItems, new CartAdapter.CartUpdateListener() {
//            @Override
//            public void onCartUpdated() {
//                calculateTotalPrice();
//            }
//        });
//
//        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        cartRecyclerView.setAdapter(cartAdapter);
//
//        // Calculate and display total price
//        calculateTotalPrice();
//
//        // Checkout button functionality can be added here
//        checkoutButton.setOnClickListener(v -> {
//            // Handle checkout logic here
//        });
//    }
//
//    // Method to load cart items from SharedPreferences
//    private List<Product_model> loadCartItems() {
//        String cartItemsJson = sharedPreferences.getString("cart_items", null);
//        if (cartItemsJson != null) {
//            Type type = new TypeToken<List<Product_model>>() {}.getType();
//            return gson.fromJson(cartItemsJson, type);
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//    // Method to calculate total price of all items in the cart
//    private void calculateTotalPrice() {
//        double totalPrice = 0;
//        for (Product_model product : cartItems) {
//            try {
//                // Multiply price by the quantity
//                double price = Double.parseDouble(product.getPrice());
//                int quantity = Integer.parseInt(product.getQuantity());
//                totalPrice += price * quantity;
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Update the total price TextView
//        totalPriceText.setText(String.format("Total Price: $%.2f", totalPrice));
//    }
//}











//public class CartActivity extends AppCompatActivity {
//    private SharedPreferences sharedPreferences;
//    private Gson gson;
//    private RecyclerView cartRecyclerView;
//    private CartAdapter cartAdapter; // A new adapter for displaying cart items
//    private List<Product_model> cartItems;
//    private TextView totalPriceText ,decription;
//    private Button checkoutButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_cart);
//        sharedPreferences = getSharedPreferences("CartPreferences", MODE_PRIVATE);
//        gson = new Gson();
//
//        cartItems = loadCartItems();
//        cartRecyclerView = findViewById(R.id.cart_recycler_view);
//        totalPriceText = findViewById(R.id.total_amount);
//        checkoutButton = findViewById(R.id.checkout_button);
//        cartAdapter = new CartAdapter(this, cartItems);
//        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        cartRecyclerView.setAdapter(cartAdapter);
//
//        calculateTotalPrice();
//
//    }
//
//    private List<Product_model> loadCartItems() {
//        String cartItemsJson = sharedPreferences.getString("cart_items", null);
//        if (cartItemsJson != null) {
//            Type type = new TypeToken<List<Product_model>>() {
//            }.getType();
//            return gson.fromJson(cartItemsJson, type);
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//
//    public void calculateTotalPrice() {
//        double totalPrice = 0;
//        for (Product_model product : cartItems) {
//            try {
//                int price = Integer.parseInt(product.getPrice());
//                totalPrice += price *  Integer.parseInt(product.getQuantity());
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//        }
//        totalPriceText.setText(String.format("Total Price: $%.2f", totalPrice));
//
////    private void calculateTotalPrice() {
////        double totalPrice = 0;
////        for (Product_model product : cartItems) {
////            try {
////                // Parse the price from string to double
////                double price = Double.parseDouble(product.getPrice());
////                totalPrice += price;
////            } catch (NumberFormatException e) {
////                // Handle the case where the price string is not a valid double
////                e.printStackTrace();
////                // Optionally, show an error message to the user
////            }
////        }
////        // Update the total price TextView
////        totalPriceText.setText(String.format("Total Price: $%.2f", totalPrice));
////    }
//}
//}