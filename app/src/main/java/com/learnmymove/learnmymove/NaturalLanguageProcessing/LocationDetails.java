package com.learnmymove.learnmymove.NaturalLanguageProcessing;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 9:15 PM 02 Feb 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class LocationDetails {

    private int id;
    private double latitude;
    private double longitude;
    private String placeName;
    private String category;

    public LocationDetails() { }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
