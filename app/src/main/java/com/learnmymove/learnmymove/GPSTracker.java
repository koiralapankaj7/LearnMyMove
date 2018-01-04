package com.learnmymove.learnmymove;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.List;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 10:51 AM 04 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    boolean isGPSEnabled = false;   // Set flag for GPS

    boolean isNetworkEnabled = false;   // Set flag for network

    boolean canGetLocation = false;     // If we can get location currently

    Location location;
    double latitude;
    double longitude;

    private static final  long MIN_DISTANCE_FOR_UPDATES = 1;   // In every 1 meters location will be updated
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000; // Equivalent to one minute (60000 ms = 1 min)
    protected LocationManager locationManager;  // Get the location

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();  // Get the current location
    }

    // Get current location
    public Location getLocation() {

        // Anything could go wrong at any time so use try and catch block
        try {

            // Setup location manager
            this.locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Get GPS status. If enabled this will return true.
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Get Network status. If enabled this will return true.
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // If gps and network is not enabled do nothing
            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {

                this.canGetLocation = true;     // Now location can be access

                // If network is enable use this block of code to access location
                if (isNetworkEnabled) {

                    // Check either user have access to this two permission or not.
                    if (
                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            ) {

                        return null;
                    }

                    // Request location updates and set to location manager.
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_FOR_UPDATES, this);

                    // Check if location manager value is null or not.
                    if (locationManager != null) {

                        // Get last known location from location manager. Give us access to the location.
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        // Check if location is not null
                        if (location != null) {
                            // Get value of longitude and latitude
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }

                }

                // If GPS is enabled use this block of code to access location
                if (isGPSEnabled) {

                    // Check location status
                    if (location == null) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_FOR_UPDATES, this);

                        // Check if location manager value is null or not.
                        if (locationManager != null) {

                            // Get last known location from location manager. Give us access to the location.
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            // Check if location is not null
                            if (location != null) {
                                // Get value of longitude and latitude
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }

                        }

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    // Stop GPS
    public void stopUsingGPS(){

        if (locationManager != null) {

            // Check either user have access to this two permission or not.
            if (
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                    ) {

                return;
            }
            // Remove GPS from location manager
            locationManager.removeUpdates(GPSTracker.this);
        }

    }

    // Get latitude
    public double getLatitude() {

        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;

    }

    // Get longitude
    public double getLongitude() {

        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;

    }

    public List<Address> getAddressList() {

        try {
            return new Geocoder(mContext).getFromLocation(latitude, longitude, 1);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Dialog title
        alertDialog.setTitle("GPS settings");

        // Dialog message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to the settings menu?");

        // On pressing setting button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show alert
        alertDialog.show();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
