package com.architect.uberclone.models;

public class Request {

    private double lat;
    private double lon;

    String requester_uid;
    String driver_uid;

    public Request() {
        //
    }

    public Request(double lat, double lon, String requester_uid, String driver_uid) {
        this.lat = lat;
        this.lon = lon;
        this.requester_uid = requester_uid;
        this.driver_uid = driver_uid;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getRequester_uid() {
        return requester_uid;
    }

    public String getDriver_uid() {
        return driver_uid;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setRequester_uid(String requester_uid) {
        this.requester_uid = requester_uid;
    }

    public void setDriver_uid(String driver_uid) {
        this.driver_uid = driver_uid;
    }
}