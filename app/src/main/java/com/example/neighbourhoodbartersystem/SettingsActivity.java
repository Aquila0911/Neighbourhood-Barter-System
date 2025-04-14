package com.example.neighbourhoodbartersystem;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private MapView mapView;
    private TextView logout;
    private Spinner locationSpinner;

    private List<LocationData> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_settings);

        requestLocationPermissions();

        mapView = findViewById(R.id.mapView);
        logout = findViewById(R.id.logout_text);
        locationSpinner = findViewById(R.id.locationSpinner);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Define locations
        locations = Arrays.asList(
                new LocationData("Emerald", new GeoPoint(13.3531, 74.7945)),
                new LocationData("Pearl", new GeoPoint(13.3524, 74.7938)),
                new LocationData("Embassy", new GeoPoint(13.3522, 74.7932)),
                new LocationData("NIH", new GeoPoint(13.3537, 74.7898)),
                new LocationData("KMC Manipal", new GeoPoint(13.3534, 74.7921))
        );

        // Set up spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getTitlesFromLocations(locations)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        // On selection
        locationSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                LocationData selected = locations.get(position);
                updateMapWithNearbyMarkers(selected, locations);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });

        logout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(SettingsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private List<String> getTitlesFromLocations(List<LocationData> list) {
        List<String> titles = new ArrayList<>();
        for (LocationData loc : list) {
            titles.add(loc.title);
        }
        return titles;
    }

    private void updateMapWithNearbyMarkers(LocationData center, List<LocationData> all) {
        mapView.getOverlays().clear();

        double rangeKm = 3.0;

        // Add selected location marker
        Marker centerMarker = new Marker(mapView);
        centerMarker.setPosition(center.geoPoint);
        centerMarker.setTitle(center.title + " (Selected)");
        centerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(centerMarker);

        for (LocationData loc : all) {
            if (!loc.title.equals(center.title)) {
                float distance = calculateDistanceInMeters(center.geoPoint, loc.geoPoint);
                if (distance <= rangeKm * 1000) {
                    Marker marker = new Marker(mapView);
                    marker.setPosition(loc.geoPoint);
                    marker.setTitle(loc.title + " (" + (int) distance + "m)");
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    mapView.getOverlays().add(marker);
                }
            }
        }

        mapView.getController().setZoom(20.0);
        mapView.getController().setCenter(center.geoPoint);
        mapView.invalidate();
    }

    private float calculateDistanceInMeters(GeoPoint p1, GeoPoint p2) {
        float[] result = new float[1];
        android.location.Location.distanceBetween(
                p1.getLatitude(), p1.getLongitude(),
                p2.getLatitude(), p2.getLongitude(),
                result
        );
        return result[0];
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    static class LocationData {
        String title;
        GeoPoint geoPoint;

        LocationData(String title, GeoPoint geoPoint) {
            this.title = title;
            this.geoPoint = geoPoint;
        }
    }
}
