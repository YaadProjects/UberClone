package com.architect.uberclone.utils;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {

    public static final String TAG = JSONParser.class.getSimpleName();

    static InputStream is = null;
    static JSONObject jObj = null;
    private static String json = "";

    public JSONParser() {
    }

    public static String getJSONFromUrl(String url) {

        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(url).openConnection());
            InputStream in = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            json = sb.toString();
            in.close();

        } catch (IOException ex) {
            Log.e(TAG, "getJSONFromUrl: " + ex.getMessage());
        }

        return json;
    }
}