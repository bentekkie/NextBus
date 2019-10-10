package com.bentekkie.nextbus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.bentekkie.nextbuslib.FeedTask;
import com.bentekkie.nextbuslib.StopTask;
import com.bentekkie.nextbuslib.TripAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends WearableActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 11111;

    private boolean locationEnabled = false;
    TextView stopName;
    TripAdapter adapter;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    FrameLayout progressBarHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        stopName = findViewById(R.id.stopNameSingle);
        WearableRecyclerView tripList = findViewById(R.id.tripList);
        progressBarHolder = findViewById(R.id.progressBarHolder);
        tripList.setLayoutManager(
                new WearableLinearLayoutManager(this));
        adapter = new TripAdapter( R.id.route, R.id.time, R.layout.trip_list_item, (v, trip) -> {}, false);
        tripList.setAdapter(adapter);
        tripList.addItemDecoration(new OffsetItemDecoration(this));
        tripList.requestFocus();


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
                final List<Location> locations = locationResult.getLocations();
                updateStops(locations.get(locations.size()-1));

            }
        };

        requestLocation();
        setAmbientEnabled();
        startLocationUpdates().addOnSuccessListener(this::updateStops);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount() == 0) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this::updateStops);
        }
        return super.onKeyDown(keyCode, event);
    }

    synchronized
    private void updateStops(Location location) {
        AlphaAnimation inAnimation =new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        StopTask.getStops(location).thenAcceptAsync(
                stops -> stops.stream().min(Comparator.comparingDouble(s -> location.distanceTo(s.getLocation()))).ifPresent(
                        stop -> FeedTask.getTrips(stop.getStopId()).thenAcceptAsync(
                                trips -> runOnUiThread(() -> {
                                    stopName.setText(stop.getStopName());
                                    adapter.clear();
                                    adapter.addAll(trips);
                                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                                    outAnimation.setDuration(200);
                                    progressBarHolder.setAnimation(outAnimation);
                                    progressBarHolder.setVisibility(View.GONE);
                                }))));
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
                    startLocationUpdates();
                } else {
                    requestLocation();
                }
            }
        }
    }


    private Task<Location> startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60*60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()).addOnCompleteListener(task -> {
            Log.i("test","complete");
        });
        return fusedLocationClient.getLastLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationEnabled) {
            startLocationUpdates().addOnSuccessListener(this::updateStops);
        } else {
            requestLocation();
        }
    }
}
