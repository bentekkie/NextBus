package com.bentekkie.nextbuslib;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.transit.realtime.GtfsRealtime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeedTask extends AsyncTask<String,Integer, List<StopTime>> {



    public static CompletableFuture<List<StopTime>> getTrips(String stopID) {
        return CompletableFuture.supplyAsync(() ->
                new FeedTask().doInBackground(stopID)
        );
    }

    private FeedTask(){}

    @Override
    protected List<StopTime> doInBackground(String... stopId) {
        try {
            return Jsoup.connect(
                    new Uri.Builder()
                            .scheme("https")
                            .authority("web.grt.ca")
                            .appendPath("HastinfoWeb")
                            .appendPath("NextPassingTimes")
                            .appendPath("RefreshNextPassingTimes")
                            .appendQueryParameter("stopIdentifier", stopId[0])
                            .appendQueryParameter("mustBeAccessible", "false")
                            .appendQueryParameter("oneStopRouteAvailable", "False")
                            .appendQueryParameter("selectedRouteKeys", "")
                            .appendQueryParameter("_", Calendar.getInstance().getTime().getTime() + "")
                            .toString())
                    .get().getElementsByClass("Ungrouped").stream()
                    .map(element -> new StopTime(
                            element.getElementsByClass("RoutePublicIdentifier").text()
                                    + " - "
                                    + element.getElementsByClass("RouteDescriptionText").text(),
                            element.getElementsByClass("RouteDescriptionDirection").text(),
                            element.getElementsByClass("NextPassingTimesTime").text()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();

    }
}
