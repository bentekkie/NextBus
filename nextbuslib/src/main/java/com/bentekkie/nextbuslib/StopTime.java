package com.bentekkie.nextbuslib;


import android.os.Parcel;
import android.os.Parcelable;

public class StopTime implements Parcelable {

    private final String route;
    private final String headSign;
    private final String departure;

    public StopTime(String route, String headSign, String departure) {
        this.route = route;
        this.headSign = headSign;
        this.departure = departure;
    }


    public static final Creator<StopTime> CREATOR = new Creator<StopTime>() {
        @Override
        public StopTime createFromParcel(Parcel in) {
            return new StopTime(in);
        }

        @Override
        public StopTime[] newArray(int size) {
            return new StopTime[size];
        }
    };

    public String getRoute() {
        return route;
    }

    public String getDeparture() {
        return departure;
    }

    public String getHeadSign() {
        return headSign;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(route);
        dest.writeString(headSign);
        dest.writeString(departure);
    }

    private StopTime(Parcel in){
        route = in.readString();
        headSign = in.readString();
        departure = in.readString();
    }
}
