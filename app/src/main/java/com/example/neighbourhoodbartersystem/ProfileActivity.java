package com.example.neighbourhoodbartersystem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView settingsIcon = findViewById(R.id.settings_icon);
        Button editButton = findViewById(R.id.button3);
        EditText name = findViewById(R.id.profile_name);
        EditText email = findViewById(R.id.profile_email);
        EditText contact = findViewById(R.id.profile_contact);
        EditText userId = findViewById(R.id.profile_userid);
        TextView ratingStar = findViewById(R.id.profile_ratingStar);
        TextView ratingNum = findViewById(R.id.profile_ratingNum);

        // Get user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userName = prefs.getString("userName", "");
        String userEmail = prefs.getString("userEmail", "");
        String userPhone = prefs.getString("userPhone", "");
        String userID = prefs.getString("userId", "");
        String userRating = prefs.getString("userRating", "");
        String userTotalRatings = prefs.getString("userTotalRatings", "");

        // Set them to EditTexts
        name.setText(userName);
        email.setText(userEmail);
        contact.setText(userPhone);
        userId.setText(userID);
        ratingStar.setText(userRating + "⭐");
        ratingNum.setText("(" + userTotalRatings + ")");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homepage) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.exchange) {
                // Handle exchange item click
                startActivity(new Intent(this, ExchangeActivity.class));
                return true;
            } else if (item.getItemId() == R.id.settings) {
                return true;
            } else {
                return false;
            }
        });

        editButton.setOnClickListener(v -> {
            boolean isEditable = name.isEnabled();

            // Toggle Edit Mode
            name.setEnabled(!isEditable);
            email.setEnabled(!isEditable);
            contact.setEnabled(!isEditable);

            // Change Button Text
            if (!isEditable) {
                editButton.setText("Save");
            } else {
                editButton.setText("Edit");
                String updatedName = name.getText().toString();
                String updatedEmail = email.getText().toString();
                String updatedPhone = contact.getText().toString();

                // Call method to save changes to backend
                saveProfileChanges(userID, updatedName, updatedEmail, updatedPhone);
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void saveProfileChanges(String id, String updatedName, String updatedEmail, String updatedPhone) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.90.1.37:5000/api/update"); // Replace with your actual route
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("_id", id);
                jsonBody.put("name", updatedName);
                jsonBody.put("email", updatedEmail);
                jsonBody.put("phoneNumber", updatedPhone);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                Log.d("SAVE_PROFILE", "Update response code: " + code);

                if (code == 200) {
                    // Optionally update SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("userName", updatedName);
                    editor.putString("userEmail", updatedEmail);
                    editor.putString("userPhone", updatedPhone);
                    editor.apply();

                    runOnUiThread(() -> Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e("SAVE_PROFILE", "Error updating profile", e);
            }
        }).start();
    }
}
