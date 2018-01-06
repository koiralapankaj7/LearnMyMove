package com.learnmymove.learnmymove.NaturalLanguageProcessing;

import com.google.android.gms.maps.model.LatLng;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 1:11 PM 05 Jan 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class Places {

    private LatLng latLng;
    private String placeName;

    public Places() {}

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

}
