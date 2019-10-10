package com.bentekkie.nextbuslib;

import android.location.Location;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class Stop implements Serializable {

    private final String stopId;
    private final String stopCode;
    private final String stopName;
    private final String stopDesc;
    private final double stopLat;
    private final double stopLon;
    private final HashMap<String,String> tripsIds = new HashMap<>();

    public Location getLocation() {
        Location location = new Location("");
        location.setLongitude(stopLon);
        location.setLatitude(stopLat);
        return location;
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopCode() {
        return stopCode;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStopDesc() {
        return stopDesc;
    }

    /*
    {
        "Stop": {
            "Identifier": "4070",
            "Description": "Westvale / Heather Hill",
            "Usage": "Stop",
            "PhoneNumber": "4070",
            "Location": {
                "Type": "Stop",
                "Identifier": "4070",
                "LongLat": {
                    "Longitude": -80.55822026491532,
                    "Latitude": 43.448410945317072
                }
            }
        },
        "Styles": "SuggestionTypeStop",
        "Text": "4070 - Westvale / Heather Hill"
    }
     */

    public Stop(JSONObject jsonObject) throws JSONException {
        JSONObject innerStop = jsonObject.getJSONObject("Stop");
        stopDesc = innerStop.getString("Description");
        stopName = stopDesc;
        stopCode = innerStop.getString("Identifier");
        stopId = stopCode;
        JSONObject locationInfo = innerStop.getJSONObject("Location").getJSONObject("LongLat");
        stopLat = locationInfo.getDouble("Latitude");
        stopLon = locationInfo.getDouble("Longitude");

    }
    public Stop(String stopId, String stopCode, String stopName, String stopDesc, double stopLat, double stopLon) {
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
    }

    public void addTripID(String tripID, String tripHeadsign){
        tripsIds.put(tripID,tripHeadsign);
    }

    public String getHeadsign(String tripID) {
        return tripsIds.getOrDefault(tripID,"");
    }

    public HashMap<String, String> getTripsIds() {
        return tripsIds;
    }

    @NonNull
    @Override
    public String toString() {
        return "ID: "+stopId+",Name: "+stopName;
    }
}
