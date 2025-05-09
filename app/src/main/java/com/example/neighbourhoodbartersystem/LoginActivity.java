package com.example.neighbourhoodbartersystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity; // ✅ Use ONLY this, not ComponentActivity

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if already logged in
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login); // ✅ No ambiguity here

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        TextView tvSignUp = findViewById(R.id.tvSignUp);

        // Sign-up redirect
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegActivity.class);
            startActivity(intent);
        });

        // Login logic
        loginBtn.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    URL url = new URL("http://10.152.2.178:5000/api/login"); // replace with your actual backend IP
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    // JSON payload
                    JSONObject payload = new JSONObject();
                    payload.put("email", user);
                    payload.put("password", pass);

                    OutputStream os = conn.getOutputStream();
                    os.write(payload.toString().getBytes("utf-8"));
                    os.close();

                    int responseCode = conn.getResponseCode();
                    Log.d("LOGIN", "Response Code: " + responseCode);

                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        Scanner scanner = new Scanner(is).useDelimiter("\\A");
                        String response = scanner.hasNext() ? scanner.next() : "";
                        scanner.close();

                        JSONObject json = new JSONObject(response);

                        // Save login state and user info
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userId", json.getString("_id"));
                        editor.putString("userName", json.getString("name"));
                        editor.putString("userEmail", json.getString("email"));
                        editor.putString("userPhone", json.getString("phoneNumber"));
                        editor.putString("profilePic", json.optString("profilePicture", ""));
                        editor.putString("userRating", String.valueOf(json.optDouble("rating", 0.0)));
                        editor.putString("userTotalRatings", String.valueOf(json.optInt("totalRatings", 0)));
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show());
                    }

                } catch (Exception e) {
                    Log.e("LOGIN_ERROR", "Login failed: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}
