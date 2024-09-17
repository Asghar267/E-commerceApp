package com.example.ecommerce_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.Adapters.ProductAdapter;
import com.example.ecommerce_app.R;
import com.example.ecommerce_app.models.Category_model;
import com.example.ecommerce_app.models.Product_model;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;



public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<Category_model> categoryList;

    public CategoryAdapter(Context context, ArrayList<Category_model> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category_model category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        // Fetch products for this category from Firestore
        fetchProductsByCategory(category.getId(), holder);
    }

    private void fetchProductsByCategory(String categoryId, CategoryViewHolder holder) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<Product_model> productList = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(context, productList);

        // Set up horizontal RecyclerView for products
        holder.productRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.productRecyclerView.setAdapter(productAdapter);

        firestore.collection("products")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product_model product = document.toObject(Product_model.class);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();  // Update product RecyclerView
                    } else {
                        // Log error message
                        Log.e("CategoryAdapter", "Error fetching products: " + task.getException().getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        RecyclerView productRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName_id);
            productRecyclerView = itemView.findViewById(R.id.productRecyclerView_id);
        }
    }
}