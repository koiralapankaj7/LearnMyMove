package com.learnmymove.learnmymove.NaturalLanguageProcessing;

import android.util.Log;

import com.learnmymove.learnmymove.StringPunctuation.RemoveStopWords;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 1:08 PM 05 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class TrainData {

    private String placeName;
    private HashMap<String, String[]> dataSet;

    public TrainData(String placeName) {
        this.placeName = placeName;
        initDataSet();
    }

    private void initDataSet() {

        this.dataSet = new HashMap<>();
        String[] restaurant = {"restaurant", "hukka", "bar", "lounge", "cafe", "food", "beverage",
                "delicious", "hygienic", "testy", "bota", "momo", "khaja", "chowmine", "bakery",
                "coffee", "thakali", "foodland"};
        String[] sports = {"futsal", "pool", "snooker", "sport", "sports", "gym", "fitness", "indoor", "cricket"};
        String[] consultancy = {"consultant", "consultancy", "firm", "associates", "education", "study", "abroad", "council"};
        String[] institutes = {"institute", "nursing", "ca", "accounted"};
        String[] university = {"college", "+2", "plustwo", "school"};
        String[] shopping_mall = {"mall", "plaza", "shopping", "boutique", "shirt", "pants", "tshirt", "shoes", "grown", "sari", "mart"};
        String[] hospital = {"hospital", "clinic", "pharmacy", "medicine", "dental"};

        dataSet.put("restaurant", restaurant);
        dataSet.put("sports", sports);
        dataSet.put("consultancy", consultancy);
        dataSet.put("institutes", institutes);
        dataSet.put("university", university);
        dataSet.put("shopping_mall", shopping_mall);
        dataSet.put("hospital", hospital);

    }

    public String classifyPlaces() {

        try {

            String classifiedPlacesCategory = "";

            //for (LocationDetails locationDetails : collectedPlaces ) {

                ArrayList<String> placeCoordinates = RemoveStopWords.removeStopWords(placeName);

                for (String placeCoordinate : placeCoordinates ) {

                    for (String category : dataSet.keySet()) {

                        for (String coordinates : dataSet.get(category)) {

                            if (placeCoordinate.equals(coordinates)) {

                                classifiedPlacesCategory = category;
                                Log.i("======================", "=======================");
                                Log.i(category, placeCoordinate);
                                Log.i("======================", "=======================");
                                break;

                            }

                        }

                    }

                }

           // }

            return classifiedPlacesCategory;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
