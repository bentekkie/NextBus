package com.bentekkie.nextbuslib;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class StopTask extends AsyncTask<Location,Integer, List<Stop>> {

    public static CompletableFuture<List<Stop>> getStops(Location location) {
        return CompletableFuture.supplyAsync(() -> new StopTask().doInBackground(location));
    }

    private StopTask(){}

    @Override
    protected List<Stop> doInBackground(Location... locations) {

        ArrayList<Stop> stops = new ArrayList<>();

        try {
            Location l = locations[0];
            URL reqUrl = new URL(new Uri.Builder()
                    .scheme("https")
                    .authority("web.grt.ca")
                    .appendPath("HastinfoWeb")
                    .appendPath("api")
                    .appendPath("NextPassingTimesAPI")
                    .appendPath("GetStopsNearLocation")
                    .appendQueryParameter("latitude", l.getLatitude() + "")
                    .appendQueryParameter("longitude", l.getLongitude() + "")
                    .toString());

            try(InputStream is = reqUrl.openStream();
                Scanner s = new Scanner(is, StandardCharsets.UTF_8.toString())){
                s.useDelimiter("\\A");
                JSONArray arr = new JSONArray(s.hasNext() ? s.next() : "");
                for (int i = 0; i < arr.length(); i++) {
                    stops.add(new Stop(arr.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stops;
    }
}
