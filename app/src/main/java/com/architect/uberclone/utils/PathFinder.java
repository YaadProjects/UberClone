package com.architect.uberclone.utils;

public class PathFinder {

    public static String makeURL (double sourceLat, double sourceLon, double destLat, double destLon ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");

        // from
        urlString.append("?origin=");
        urlString.append(Double.toString(sourceLat));
        urlString.append(",");
        urlString.append(Double.toString( sourceLon));

        // to
        urlString.append("&destination=");
        urlString.append(Double.toString( destLat));
        urlString.append(",");
        urlString.append(Double.toString( destLon));

        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyAqzRwwdqff4aWayf7UHK6WxgEudUsss0Q");

        return urlString.toString();
    }
}