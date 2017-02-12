package com.architect.uberclone.utils;

public class PathFinder {

    public static String makeURL (double sourceLat, double sourceLon, double destLat, double destLon ) {

        return "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" +
                Double.toString(sourceLat) +
                "," +
                Double.toString(sourceLon) +
                "&destination=" +
                Double.toString(destLat) +
                "," +
                Double.toString(destLon) +
                "&sensor=false&mode=driving&alternatives=true" +
                "&key=AIzaSyAqzRwwdqff4aWayf7UHK6WxgEudUsss0Q";
    }
}