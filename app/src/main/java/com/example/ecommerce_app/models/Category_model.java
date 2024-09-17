package com.example.ecommerce_app.models;

public class Category_model {
    private String id;
    private String name;

    // Default constructor for Firestore deserialization
    public Category_model() {}

    public Category_model(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}