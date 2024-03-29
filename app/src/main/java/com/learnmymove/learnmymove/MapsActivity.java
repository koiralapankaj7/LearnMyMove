package com.learnmymove.learnmymove;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String locationTitle;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //if (getIntent().getExtras() != null) {

            latitude = Double.parseDouble(getIntent().getExtras().getString("LATITUDE"));
            longitude = Double.parseDouble(getIntent().getExtras().getString("LONGITUDE"));
            locationTitle = getIntent().getExtras().getString("LOCATION");
        //}


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

        // Add a marker in current location and move the camera
        LatLng latLng = new LatLng(latitude, longitude);

        MarkerOptions marker = new MarkerOptions().position(latLng).title(locationTitle);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_man));
        mMap.addMarker(marker);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(500)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#50222222"))
                .strokeWidth(5.0f)
        );
    }
}
