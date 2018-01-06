package com.learnmymove.learnmymove.GMap;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 2:23 PM 06 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

class DataParser {

    List<HashMap<String, String>> parse(String jsonData) {

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        Log.d("json data", jsonData);

        try {

            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    // Store all places
    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {

        List<HashMap<String, String>> placeList = new ArrayList<>();
        HashMap<String, String> place;

        for (int i = 0; i < jsonArray.length(); i++) {

            try {

                place = getPlace((JSONObject) jsonArray.get(i));
                placeList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placeList;
    }


    // Store single place
    private HashMap<String, String> getPlace(JSONObject placesJson) {

        HashMap<String, String> placesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude;
        String longitude;
        String reference;

        try {

            if (!placesJson.isNull("name")) {

                placeName = placesJson.getString("name");
            }
            if (!placesJson.isNull("vicinity")) {

                vicinity = placesJson.getString("vicinity");
            }

            latitude = placesJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = placesJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = placesJson.getString("reference");

            placesMap.put("placeName", placeName);
            placesMap.put("vicinity", vicinity);
            placesMap.put("lat", latitude);
            placesMap.put("lng", longitude);
            placesMap.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return placesMap;
    }


}
