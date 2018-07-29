package com.example.zoebriois.washingmachine2;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncWriteThingSpeakData extends AsyncTask<String, Void, String> {

    public Activity activity;
    String response;

    public AsyncWriteThingSpeakData(Activity a) {
        this.activity = a;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg) {
        //URL url;
        HttpURLConnection urlConnection = null;

        try {
            // To obtain the url passed in parameter
            URL url = null;
            if (arg.length==2) {
                url = new URL("https://api.thingspeak.com/update.json?api_key=5CX2PMYHFI0BKZ6U&field"
                        + arg[0] + "=" + arg[1]);
            }
            else if (arg.length==4) {
                url = new URL("https://api.thingspeak.com/update.json?api_key=5CX2PMYHFI0BKZ6U&field"
                        + arg[0] + "=" + arg[1] + "&field" + arg[2] + "=" + arg[3]);
            }

            Log.i("[DATA]","WriteThingSpeakData url = "+ url);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = readStream(urlConnection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("[DATA]","WriteThingSpeakData     response = "+ response.toString());
        return response;
    }


    // Converting InputStream to String, called by doInBackground
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }


    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
    }
}
