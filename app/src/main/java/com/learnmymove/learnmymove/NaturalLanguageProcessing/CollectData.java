package com.learnmymove.learnmymove.NaturalLanguageProcessing;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 1:07 PM 05 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class CollectData {

    private static double[] latitude = {12.03, 20.35, 58.20, 36.58, 98,20};
    private static double[] longitude = {12.03, 20.35, 58.20, 36.58, 98,20};
    private static String[] placeName = {"Kathmandu sports center", "Pravu dental", "Nayabazar futsal", "Kathmandu mall", "The red mud cafe"};

    public CollectData() { }

    public static List<Places> collectPlaces() {

        List<Places> placeList = new ArrayList<>();

        for (int i = 0; i < placeName.length; i++) {
            Places places = new Places();
            LatLng latLng = new LatLng(latitude[i], longitude[i]);
            places.setLatLng(latLng);
            places.setPlaceName(placeName[i]);
            placeList.add(places);
        }

        return placeList;

    }

}
