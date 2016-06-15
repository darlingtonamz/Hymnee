package com.amanzed.hymnee.source;

import android.app.Activity;

import com.amanzed.hymnee.MainActivity;
import com.amanzed.hymnee.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Created by Amanze on 5/15/2016.
 */
public class GetHymnJSON {
    static JSONObject response = null;

    public static JSONObject getOffline(Activity activity, String source){
        InputStream is = activity.getResources().openRawResource(getResourceId(activity, source, "raw"));
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            response = new JSONObject(writer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static JSONObject getOnline(Activity activity, String source){
        return response;
    }

    public static int getResourceId(Activity activity, String imageName, String folder) {
        return activity.getResources().getIdentifier(imageName, folder, activity.getPackageName());
    }
}
