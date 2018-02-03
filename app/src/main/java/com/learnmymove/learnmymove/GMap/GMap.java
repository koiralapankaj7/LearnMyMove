package com.learnmymove.learnmymove.GMap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.learnmymove.learnmymove.BuildConfig;
import com.learnmymove.learnmymove.Database.MySQLiteHelper;
import com.learnmymove.learnmymove.GeoFence.GeoFencing;
import com.learnmymove.learnmymove.NaturalLanguageProcessing.LocationDetails;
import com.learnmymove.learnmymove.NaturalLanguageProcessing.TrainData;
import com.learnmymove.learnmymove.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.learnmymove.learnmymove.CheckPermissions.PermisionRequest;


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
        LocationListener,
        GoogleMap.OnMapClickListener {

    private static final String TAG = GMap.class.getSimpleName();

    public static final int REQUEST_LOCATION_CODE = 7;
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;

    private Circle mCircleOptions;

    private double latitude, longitude;
    private static final int PROXIMITY_RADIUS = 300;

    private GeoFencing geoFencing;

    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmap_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Check for storage permission
        PermisionRequest.isExternalStoragePermissionGranted(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_google_map);
        mapFragment.getMapAsync(this);

        buttonClick();


    }

    private void buttonClick() {

        FloatingActionButton fbSuggestMe = findViewById(R.id.fb_suggest_me);
        FloatingActionButton fbSelectPlace = findViewById(R.id.fb_save_place);

        fbSelectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(GMap.this), PLACE_PICKER_REQUEST);
                    // Check for storage permission
                    PermisionRequest.isExternalStoragePermissionGranted(GMap.this);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        fbSuggestMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(GMap.this);
                List<String> categoryList = sqLiteHelper.getDistinctCategory();

                final CharSequence[] categoriesDemo = {"hospital", "school", "shopping_mall", "health", "stadium", "college", "university"};

                final CharSequence[] categories = categoryList.toArray(new CharSequence[categoryList.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(GMap.this);
                builder.setTitle("Suggestion for :");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(GMap.this, categories[which], Toast.LENGTH_LONG).show();
                        makeSuggestion(categories[which].toString());
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });

    }

    private void makeSuggestion(String category) {

        Object dataTransfer[] = new Object[2];
        NearbyPlacesData nearbyPlacesData = new NearbyPlacesData();

        mMap.clear();
        String url = getUrl(latitude, longitude, category);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        nearbyPlacesData.execute(dataTransfer);

        Toast.makeText(this, "Nearby colleges", Toast.LENGTH_SHORT).show();

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

            mMap.setOnMapClickListener(this);
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));


        TextView latlang = findViewById(R.id.tv_latlang);
        latlang.setText(new LatLng(latitude, longitude).toString());


        geoFencingTasks();

        // If you want to stop updating location after getting once uncomment this line.
        /*if (apiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }*/


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();

        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(60000);
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

            case GeoFencing.REQUEST_PERMISSIONS_REQUEST_CODE:

                if (grantResults.length <= 0) {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission granted.");
                    geoFencing.performPendingGeofenceTask();
                } else {
                    // Permission denied.

                    // Notify the user via a SnackBar that they have rejected a core permission for the
                    // app, which makes the Activity useless. In a real app, core permissions would
                    // typically be best requested during a welcome-screen flow.

                    // Additionally, it is important to remember that a permission might have been
                    // rejected without asking the user for permission (device policy or "Never ask
                    // again" prompts). Therefore, a user interface affordance is typically implemented
                    // when permissions are denied. Otherwise, your app could appear unresponsive to
                    // touches or interactions which have required permissions.
                    geoFencing.showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Build intent that displays the App settings screen.
                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                    geoFencing.mPendingGeofenceTask = GeoFencing.PendingGeofenceTask.NONE;
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

            /*
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
            */
            /*
            case R.id.college:

                mMap.clear();
                String url = getUrl(latitude, longitude, "college");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                nearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Nearby colleges", Toast.LENGTH_SHORT).show();

                break;
            */
            /*
            case R.id.hospital:

                mMap.clear();
                url = getUrl(latitude, longitude, "hospital");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                nearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Nearby Hospitals", Toast.LENGTH_SHORT).show();

                break;
            */
            /*
            case R.id.restaurant:

                url = getUrl(latitude, longitude, "restaurant");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                nearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Nearby restaurants", Toast.LENGTH_SHORT).show();

                break;
            */

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

    private void geoFencingTasks() {

        geoFencing = new GeoFencing(this, new LatLng(latitude, longitude));

        if (geoFencing.addGeofences()) {

            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(100)
                    .strokeColor(Color.RED)
                    .fillColor(Color.parseColor("#50222222"))
                    .strokeWidth(5.0f);

            if (mCircleOptions != null) {
                mCircleOptions.remove();
            }

            mCircleOptions = mMap.addCircle(circleOptions);

        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        //Toast.makeText(this, String.valueOf(latLng.latitude) + " : " + String.valueOf(latLng.longitude), Toast.LENGTH_LONG).show();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            Address address = addressList.get(0);
            String url = address.getUrl();


            String add = address.getAddressLine(0);
            //add = add + "\n" + address.getCountryName();
            //add = add + "\n" + address.getCountryCode();
            //add = add + "\n" + address.getAdminArea();
            //add = add + "\n" + address.getPostalCode();
            //add = add + "\n" + address.getSubAdminArea();
            add = add + "\n" + address.getLocality();
            add = add + "\n" + address.getSubLocality();

            Toast.makeText(this, add, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placeName = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placeName);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);

                //Toast.makeText(this, stBuilder.toString(), Toast.LENGTH_LONG).show();


                TrainData trainData = new TrainData(place.getName().toString());

                String results = trainData.classifyPlaces();

                System.out.println("===================== Result ==============================");
                System.out.println(place.getName() + " => " + results);

                LocationDetails locationDetails = new LocationDetails();
                locationDetails.setLatitude(place.getLatLng().latitude);
                locationDetails.setLongitude(place.getLatLng().longitude);
                locationDetails.setPlaceName(place.getName().toString());
                locationDetails.setCategory(trainData.classifyPlaces());


                if (PermisionRequest.isExternalStoragePermissionGranted(this)) {
                    MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);
                    sqLiteHelper.addLocation(locationDetails);
                    Toast.makeText(this, "Inserted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Missing storage permission", Toast.LENGTH_LONG).show();
                }

            }
        }

    }

}
