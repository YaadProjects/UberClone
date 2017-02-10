package com.architect.uberclone.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestLocation implements Parcelable {

    double latitude;
    double longtitude;

    public RequestLocation(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    @Override
    public String toString() {
        return  "Location: { lat: " + this.latitude + ", lon: " + this.longtitude + " }";
    }

    private RequestLocation(Parcel in) {
        latitude = in.readDouble();
        longtitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longtitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RequestLocation> CREATOR = new Parcelable.Creator<RequestLocation>() {
        @Override
        public RequestLocation createFromParcel(Parcel in) {
            return new RequestLocation(in);
        }

        @Override
        public RequestLocation[] newArray(int size) {
            return new RequestLocation[size];
        }
    };
}