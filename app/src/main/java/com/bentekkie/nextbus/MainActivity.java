package com.bentekkie.nextbus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bentekkie.nextbuslib.StopAdapter;
import com.bentekkie.nextbuslib.StopTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.Comparator;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 11111;

    private boolean locationEnabled = false;

    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_main);
        RecyclerView listView = findViewById(R.id.stopList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        StopAdapter adapter = new StopAdapter(R.id.stopName, R.id.stopID, R.layout.stop_list_item, (v, stop) -> {
            Intent intent = new Intent(this,StopActivity.class);
            intent.putExtra(StopActivity.EXTRA_STOP_DATA,stop);
            startActivity(intent);
        }, false);
        listView.setAdapter(adapter);

        Consumer<Location> updateStops = location -> StopTask.getStops(location).thenAcceptAsync(stops -> {
            //Log.i("test",stops.toString());
            Log.i("test",location.toString());
            runOnUiThread(() -> {
                adapter.clear();
                adapter.addAll(stops);
                adapter.sort(Comparator.comparingDouble(stop -> stop.getLocation().distanceTo(location)));
            });
        });


        locationCallback = new LocationCallback(){
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                locationResult.getLocations().forEach(updateStops);
            }
        };

        requestLocation();
        startLocationUpdates().addOnSuccessListener(updateStops::accept);

    }

    private void requestLocation() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            locationEnabled = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationEnabled = true;
                    startLocationUpdates();
                } else {
                    requestLocation();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (locationEnabled) {
            startLocationUpdates();
        } else {
            requestLocation();
        }
    }

    private Task<Location> startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        return fusedLocationClient.getLastLocation();
    }
}
