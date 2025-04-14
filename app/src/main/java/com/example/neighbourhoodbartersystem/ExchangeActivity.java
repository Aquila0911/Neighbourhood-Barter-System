package com.example.neighbourhoodbartersystem;

import static com.example.neighbourhoodbartersystem.SettingsActivity.calculateDistanceInMeters;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class ExchangeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange); // Set the layout resource

        // Find the ConstraintLayout in the activity layout
        ConstraintLayout constraintLayout = findViewById(R.id.exchange);

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.exchange);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView filterIcon = findViewById(R.id.filter_iconex);

        recyclerView = findViewById(R.id.recyclerView);

        // Set RecyclerView with a dynamic grid layout
        int numberOfColumns = 2; // Change this to 3 or more for more columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        productList = new ArrayList<>();
        List<SettingsActivity.LocationData> locations = SettingsActivity.locations;

        // Add products with locations
        productList = new ArrayList<>();
        productList.add(new Product("Laptop", "High-performance laptop", R.drawable.settings, locations.get(0).geoPoint, "Electronics"));
        productList.add(new Product("Bicycle", "Mountain bike", R.drawable.settings, locations.get(1).geoPoint, "Sports"));
        productList.add(new Product("Phone", "Android phone", R.drawable.settings, locations.get(2).geoPoint, "Electronics"));
        productList.add(new Product("Headphones", "Noise-canceling headphones", R.drawable.settings, locations.get(3).geoPoint, "Electronics"));
        productList.add(new Product("Watch", "Smartwatch with tracking", R.drawable.settings, locations.get(4).geoPoint, "Electronics"));

        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        filterIcon.setOnClickListener(v -> showFilterPopup(v));
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homepage) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.exchange) {
                // Handle exchange item click
                return true;
            } else if (item.getItemId() == R.id.settings) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } //else if (item.getItemId() == R.id.chat) {
            // Handle settings item click
            //return true;}
            else {
                return false;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ex_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.youritemsm) {
            startActivity(new Intent(this, YourItemsActivity.class)); return true;
        } else if (item.getItemId() == R.id.exchange) {
            Toast.makeText(this, "current page", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.ongoingm) {
            startActivity(new Intent(this, OngoingActivity.class)); return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void showFilterPopup(View view) {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.filter_popup, null);

        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Get references to popup UI elements
        EditText priceInput = popupView.findViewById(R.id.price_input);
        Spinner categorySpinner = popupView.findViewById(R.id.category_spinner);
        Button applyButton = popupView.findViewById(R.id.apply_button);
        Button closeButton = popupView.findViewById(R.id.close_popup);

        // Set up Spinner with categories
        String[] categories = {"Select Category", "Electronics", "Clothing", "Books"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);

        // Close button listener
        closeButton.setOnClickListener(v -> popupWindow.dismiss());

        // Apply button listener
        applyButton.setOnClickListener(v -> {
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            String distanceStr = priceInput.getText().toString();

            if (distanceStr.isEmpty()) {
                Toast.makeText(this, "Please enter a max distance", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float maxDistance = Float.parseFloat(distanceStr);
                filterProductsByLocationAndCategory(maxDistance, selectedCategory);
                popupWindow.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid distance value", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filterProductsByLocationAndCategory(float maxDistance, String selectedCategory) {
        List<Product> filteredList = new ArrayList<>();

        // Get the selected location from SettingsActivity (you can pass it dynamically based on user's input)
        GeoPoint selectedLocation = SettingsActivity.selectedLocation;
        // Example: Use a location from SettingsActivity

        for (Product product : productList) {
            // Calculate distance between selectedLocation and product location
            float distance = calculateDistanceInMeters(selectedLocation, product.getLocation()) / 1000;
            // Convert to kilometers
            boolean isCategoryMatch = selectedCategory.equals("Select Category") || product.getCategory().equals(selectedCategory);

            // Check if within max distance and category match
            if (distance <= maxDistance && isCategoryMatch) {
                filteredList.add(product);  // Add product if it's within the range and category matches
            }
        }

        // Update RecyclerView with filtered products
        adapter.updateProductList(filteredList);
    }


}
