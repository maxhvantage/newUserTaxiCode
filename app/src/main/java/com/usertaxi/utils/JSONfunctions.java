package com.usertaxi.utils;

import android.util.Log;

import com.usertaxi.RequestTaxiActivity;
import com.usertaxi.beans.Allbeans;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Eyon on 11/18/2015.
 */
public class JSONfunctions {

    public static JSONObject getJSONfromURL(String url, Allbeans allbeans) {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        // Download JSON data from URL
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            JSONObject jsonObj=new JSONObject();
            jsonObj.put("sourcelatitute", RequestTaxiActivity.sorcelatitude);
            jsonObj.put("sourcelongitude", RequestTaxiActivity.sourcelangitude);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObj);
            StringEntity se = null;
            try {
                se = new StringEntity(jsonArray.toString(), "UTF-8");


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("json value:----", jsonArray.toString());
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {

            jArray = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jArray;
    }
}