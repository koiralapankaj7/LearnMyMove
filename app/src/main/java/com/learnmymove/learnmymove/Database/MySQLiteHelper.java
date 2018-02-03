package com.learnmymove.learnmymove.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learnmymove.learnmymove.NaturalLanguageProcessing.LocationDetails;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Project Name => LearnMyMove
 * Created by   => Pankaj Koirala
 * Created on   => 8:59 PM 02 Feb 2018
 * Email Id     => koiralapankaj007@gmail.com
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "LocationCollection.db";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create location table
        String CREATE_LOCATION_TABLE = "CREATE TABLE locations ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "latitude TEXT, " +
                "longitude TEXT, " +
                "place_name TEXT, " +
                "category TEXT )";

        // create location table
        db.execSQL(CREATE_LOCATION_TABLE);
        System.out.println("================= Created location DB ====================");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older locations table if existed
        db.execSQL("DROP TABLE IF EXISTS locations");

        // create fresh locations table
        this.onCreate(db);

    }

    //===========================================================================================================
    //===========================================================================================================

    /**
     * CRUD operations (create "add", read "get", update, delete) locations + get all locations + delete all locations
     */

    // Location table name
    private static final String TABLE_LOCATIONS = "locations";

    // Location Table Columns names
    private static final String ID = "id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String PLACE_NAME = "place_name";
    private static final String CATEGORY = "category";

    private static final String[] COLUMNS = {ID, LATITUDE ,LONGITUDE, PLACE_NAME, CATEGORY};

    public void addLocation(LocationDetails locationDetails){

        System.out.println("================= Add location ====================");
        Log.d("Add Location", locationDetails.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(LATITUDE, String.valueOf(locationDetails.getLatitude())); // get latitude
        values.put(LONGITUDE, String.valueOf(locationDetails.getLongitude())); // get longitude
        values.put(PLACE_NAME, String.valueOf(locationDetails.getPlaceName())); // get placeName
        values.put(CATEGORY, String.valueOf(locationDetails.getCategory())); // get category

        // 3. insert
        db.insert(TABLE_LOCATIONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public LocationDetails getLocation(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_LOCATIONS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build LocationDetails object
        LocationDetails locationDetails = new LocationDetails();

        assert cursor != null;
        locationDetails.setId(Integer.parseInt(cursor.getString(0)));
        locationDetails.setLatitude(Double.parseDouble(cursor.getString(1)));
        locationDetails.setLongitude(Double.parseDouble(cursor.getString(2)));
        locationDetails.setPlaceName(cursor.getString(3));
        locationDetails.setCategory(cursor.getString(4));

        Log.d("getLocation("+id+")", locationDetails.toString());

        //cursor.close();
        //db.close();
        // 5. return locationDetails
        return locationDetails;
    }

    // Get All Locations
    public List<LocationDetails> getAllLocation() {

        List<LocationDetails> locationCollection = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_LOCATIONS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build locationDetails and add it to list
        LocationDetails locationDetails = null;
        if (cursor.moveToFirst()) {
            do {
                locationDetails = new LocationDetails();
                locationDetails.setId(Integer.parseInt(cursor.getString(0)));
                locationDetails.setLatitude(Double.parseDouble(cursor.getString(1)));
                locationDetails.setLongitude(Double.parseDouble(cursor.getString(2)));
                locationDetails.setPlaceName(cursor.getString(3));
                locationDetails.setCategory(cursor.getString(4));

                // Add book to locations
                locationCollection.add(locationDetails);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", locationCollection.toString());

        cursor.close();
        return locationCollection;
    }

    // Get distinct category
    public List<String> getDistinctCategory() {


        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. build the query
        String query = "SELECT  DISTINCT " + CATEGORY + " FROM " + TABLE_LOCATIONS;

        Cursor cursor = db.rawQuery(query, null);

        List<String> categoryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                categoryList.add(cursor.getString(cursor.getColumnIndex(CATEGORY)));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return categoryList;
    }

    // Updating single location
    public int updateLocation(LocationDetails locationDetails) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("latitude", String.valueOf(locationDetails.getLatitude())); // get latitude
        values.put("longitude", String.valueOf(locationDetails.getLongitude())); // get longitude
        values.put("place_name", locationDetails.getPlaceName()); // get category
        values.put("category", locationDetails.getCategory()); // get category

        // 3. updating row
        int i = db.update(TABLE_LOCATIONS, //table
                values, // column/value
                ID+" = ?", // selections
                new String[] { String.valueOf(locationDetails.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single location
    public void deleteLocation(LocationDetails locationDetails) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_LOCATIONS,
                ID+" = ?",
                new String[] { String.valueOf(locationDetails.getId()) });

        // 3. close
        db.close();

        Log.d("deleteLocation", locationDetails.toString());

    }


}
