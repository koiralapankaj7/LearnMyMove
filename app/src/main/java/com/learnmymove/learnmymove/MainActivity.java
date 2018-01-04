package com.learnmymove.learnmymove;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;


    private static final int REQUEST_CODE_PERMISSION = 2 ;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    GPSTracker gps;
    Button btnFindLocation;
    Button btnLunchMap;
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
