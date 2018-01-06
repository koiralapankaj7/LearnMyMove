package com.learnmymove.learnmymove.GMap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.learnmymove.learnmymove.R;

import java.io.IOException;
import java.util.List;


/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 11:34 AM 06 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

// If countered error change context.(This from context)

public class GMap extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final int REQUEST_LOCATION_CODE = 7;
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;

    private double latitude, longitude;
    private static final int PROXIMITY_RADIUS = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_google_map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        Log.d("lat = ",""+latitude);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions marker = new MarkerOptions().position(latLng).title("Current Location");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_man));

        currentLocationMarker = mMap.addMarker(marker);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        if (apiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_LOCATION_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (apiClient == null) {
                            buildGoogleApiClient();
                        }

                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    protected synchronized void buildGoogleApiClient() {

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        apiClient.connect();
    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }


    public void onButtonClick(View view) {

        Object dataTransfer[] = new Object[2];
        NearbyPlacesData nearbyPlacesData = new NearbyPlacesData();

        switch (view.getId()) {

            case R.id.search:

                EditText text = findViewById(R.id.txt_search);
                String location = text.getText().toString();
                List<Address> addressList;

                if (!location.equals("")) {

                    Geocoder geocoder = new Geocoder(this);

                    try {

                        addressList = geocoder.getFromLocationName(location, 5);

                        if (addressList != null) {

                            for (int i = 0; i < addressList.size(); i++) {

                                Address address = addressList.get(i);

                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                mMap.addMarker(markerOptions);
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;

            case R.id.college:

                mMap.clear();
                String url = getUrl(latitude, longitude, "college");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                nearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Nearby colleges", Toast.LENGTH_SHORT).show();

                break;

            case R.id.hospital:

                mMap.clear();
                url = getUrl(latitude, longitude, "hospital");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                nearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Nearby Hospitals", Toast.LENGTH_SHORT).show();

                break;

            case R.id.restaurant:

                url = getUrl(latitude, longitude, "restaurant");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                nearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Nearby restaurants", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private String getUrl(double latitude, double longitude, String category) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+category);
        googlePlaceUrl.append("&keyword"+category);
        googlePlaceUrl.append("&key="+"AIzaSyBzDU_oKUE1yXvWRFvUM4Zjqd8mVl0ViMU");

        return googlePlaceUrl.toString();

    }

}
