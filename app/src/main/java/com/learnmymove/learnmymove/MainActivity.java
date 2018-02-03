package com.learnmymove.learnmymove;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learnmymove.learnmymove.Database.MySQLiteHelper;
import com.learnmymove.learnmymove.GMap.GMap;
import com.learnmymove.learnmymove.NaturalLanguageProcessing.CollectData;
import com.learnmymove.learnmymove.NaturalLanguageProcessing.LocationDetails;
import com.learnmymove.learnmymove.NaturalLanguageProcessing.Places;
import com.learnmymove.learnmymove.NaturalLanguageProcessing.TrainData;
import com.learnmymove.learnmymove.StringPunctuation.RemoveStopWords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;


    private static final int REQUEST_CODE_PERMISSION = 2 ;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    GPSTracker gps;
    Button btnFindLocation, btnLunchMap, btnOpenPlaces;
    TextView lblLatitude, lblLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayLocation();

        btnLunchMap = findViewById(R.id.btn_lunch_map);
        btnLunchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("LATITUDE", lblLatitude.getText());
                intent.putExtra("LONGITUDE", lblLongitude.getText());
                intent.putExtra("LOCATION", gps.getAddressList().get(0).getLocality());
                startActivity(intent);
            }
        });

        /*
        //====================================================================
        TrainData trainData = new TrainData(CollectData.collectPlaces());
        HashMap<Places, String> classifiedPlaces = trainData.classifyPlaces();
        for (Object o : classifiedPlaces.entrySet()) {

            Map.Entry pair = (Map.Entry) o;
            System.out.println("===================================================");
            Places places = (Places) pair.getKey();
            System.out.println(places.getPlaceName() + " => " + pair.getValue());
        }
        */


        btnOpenPlaces = findViewById(R.id.btn_places_activity);
        btnOpenPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GMap.class);
                startActivity(intent);
            }

        });





    }

    private void displayLocation() {

        try {

            if (ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnFindLocation = findViewById(R.id.btn_find_location);
        lblLatitude = findViewById(R.id.lbl_latitude);
        lblLongitude = findViewById(R.id.lbl_longitude);

        btnFindLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(MainActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    lblLatitude.setText(String.valueOf(latitude));
                    lblLongitude.setText(String.valueOf(longitude));

                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Location");
                    myRef.setValue(latitude + "," + longitude);
                } else {
                    gps.showSettingsAlert();
                }
            }
        });
    }
}
