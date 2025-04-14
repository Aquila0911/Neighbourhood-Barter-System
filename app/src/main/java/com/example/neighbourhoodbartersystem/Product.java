package com.example.neighbourhoodbartersystem;

import org.osmdroid.util.GeoPoint;

public class Product {
    private String name;
    private String description;
    private int imageResId;
    private GeoPoint location;
    private String category;  // New category field

    public Product(String name, String description, int imageResource, GeoPoint location, String category) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
        this.location = location;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public GeoPoint getLocation() { return location; }

}
