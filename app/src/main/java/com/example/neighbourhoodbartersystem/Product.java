package com.example.neighbourhoodbartersystem;

import org.osmdroid.util.GeoPoint;

public class Product {
        private String name;
        private String description;
        private int imageResId;
        private String category; // Add category field
        private GeoPoint location; // Location (if needed)

        public Product(String name, String description, int imageResId, String category, GeoPoint location) {
            this.name = name;
            this.description = description;
            this.imageResId = imageResId;
            this.category = category;
            this.location = location;
        }

        public String getCategory() {
            return category;
        }

        public GeoPoint getLocation() {
            return location;
        }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}
