package com.example.ecommerce_app.models;


import java.util.Objects;

public class Product_model {
    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;
    private boolean isAvailable;
    private String quantity;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product_model that = (Product_model) obj;
        return this.id.equals(that.id); // Assuming each product has a unique 'id'
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Default constructor is needed for Firestore deserialization
    public Product_model() {}

    public Product_model(String id, String title, String description, String price,
                         String quantity, String imageUrl, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImage() { return imageUrl; }
    public void setImage(String image) { this.imageUrl = imageUrl; }


    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getImageUrl() { return imageUrl; } // Updated getter method
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}