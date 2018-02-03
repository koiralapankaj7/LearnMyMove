package com.learnmymove.learnmymove.GMap;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 2:22 PM 06 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class NearbyPlacesData extends AsyncTask<Object, String, String> {

    private String placesData;
    private GoogleMap mMap;
    private String url;

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap)objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            placesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return placesData;
    }

    @Override
    protected void onPostExecute(String s) {

        List<HashMap<String, String>> placeList;
        DataParser parser = new DataParser();
        placeList = parser.parse(s);
        Log.d("nearby places data","called parse method");
        showNearbyPlaces(placeList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> placeList) {

        for (int i = 0; i < placeList.size(); i++) {

            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> place = placeList.get(i);

            String placeName = place.get("placeName");
            String vicinity = place.get("vicinity");
            double latitude = Double.parseDouble(place.get("lat"));
            double longitude = Double.parseDouble(place.get("lng"));
            //String reference = place.get("reference");

            LatLng latLng = new LatLng(latitude, longitude);

            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        }
    }

}
