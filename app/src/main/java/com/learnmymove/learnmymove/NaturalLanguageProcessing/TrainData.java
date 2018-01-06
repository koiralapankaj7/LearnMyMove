package com.learnmymove.learnmymove.NaturalLanguageProcessing;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.learnmymove.learnmymove.StringPunctuation.RemoveStopWords;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 1:08 PM 05 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class TrainData {

    //HashMap<String, String> classifiedPlaces;

    private List<Places> collectedPlaces;

    private HashMap<String, String[]> dataSet;

    public TrainData(List<Places> collectedPlaces) {
        this.collectedPlaces = collectedPlaces;
        initDataSet();
    }

    private void initDataSet() {

        this.dataSet = new HashMap<>();
        String[] restaurant = {"restaurant", "hukka", "bar", "lounge", "cafe", "food", "beverage", "delicious", "hygienic", "testy", "bota"};
        String[] sports = {"futsal", "pool", "snooker", "sport", "sports", "gym", "fitness", "indoor", "cricket"};
        String[] consultancy = {"consultant", "consultancy", "firm", "associates", "education", "study", "abroad", "council"};
        String[] institutes = {"institute", "nursing", "ca", "accounted"};
        String[] colleges = {"college", "+2", "plustwo"};
        String[] shopping = {"mall", "plaza", "shopping", "boutique", "shirt", "pants", "tshirt", "shoes", "grown", "sari"};
        String[] clinic = {"hospital", "clinic", "pharmacy", "medicine", "dental"};

        dataSet.put("restaurant", restaurant);
        dataSet.put("sports", sports);
        dataSet.put("consultancy", consultancy);
        dataSet.put("institutes", institutes);
        dataSet.put("colleges", colleges);
        dataSet.put("shopping", shopping);
        dataSet.put("clinic", clinic);

    }

    public HashMap<Places, String> classifyPlaces() {

        try {

            HashMap<Places, String> classifiedPlaces = new HashMap<>();

            for (Places place : collectedPlaces ) {

                ArrayList<String> placeCoordinates = RemoveStopWords.removeStopWords(place.getPlaceName());

                for (String placeCoordinate : placeCoordinates ) {

                    for (String category : dataSet.keySet()) {

                        for (String coordinates : dataSet.get(category)) {

                            if (placeCoordinate.equals(coordinates)) {

                                classifiedPlaces.put(place, category);
                                Log.i("======================", "=======================");
                                Log.i(category, placeCoordinate);
                                Log.i("======================", "=======================");
                                break;

                            }

                        }

                    }

                }

            }

            return classifiedPlaces;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
