package com.example.zoebriois.washingmachine2;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncReadThingSpeakData extends AsyncTask<String, Void, String> {

    public Activity activity;
    Integer resultFinalTS[] = {9, 9, 9, 9};
    String response;

    Integer chiffre;

    public AsyncReadThingSpeakData(Activity a) {
        this.activity = a;
    }

    @Override
    protected void onPreExecute() {
        Log.i("[WHERE]", "ReadThingSpeakData    OnPreExecute");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg) {
        Log.i("[WHERE]", "ReadThingSpeakData    doInBackground  begin");
        Log.i("[DATA]", "arg[0] = " + arg[0]);
        chiffre = Integer.valueOf(arg[0]);
        Log.i("[DATA]", "chiffre = " + chiffre);

        //URL url;
        HttpURLConnection urlConnection = null;

        try {
            // To obtain the url passed in parameter
            URL url = new URL("https://api.thingspeak.com/channels/508003/feeds.json?results=1");
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = readStream(urlConnection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("[DATA]", "Out of doInBackground = " + response);
        return response;
    }


    // Converting InputStream to String, called by doInBackground
    private String readStream(InputStream in) {
        Log.i("[WHERE]", "ReadThingSpeakData    readStream  begin");
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
        Log.i("[DATA]", "Out of readStream = " + response.toString());
        return response.toString();
    }


    @Override
    protected void onPostExecute(String response) {
        Log.i("[WHERE]", "ReadThingSpeakData    onPostExecute   begin");
        Log.i("[DATA]", "Begin of onPostexecute = " + response);
        super.onPostExecute(response);

        final Globals globalsVariables = (Globals) activity.getApplicationContext();

        if (response != null) {
            Log.i("[WHERE]", "ReadThingSpeakData    onPostExecute: response != null");
            String fieldName;
            // To have only the valor we want
            JSONObject jsonObjectGlobal = null;
            try {
                jsonObjectGlobal = new JSONObject(String.valueOf(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray feeds = null;
            try {
                feeds = jsonObjectGlobal.getJSONArray("feeds");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // (index = 0 bc with the url we only get one feed)
            JSONObject jsonObjectFeeds = null;
            try {
                jsonObjectFeeds = feeds.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 1; i < 5; i++) {
                fieldName = "field" + i;
                if (jsonObjectFeeds != null) {
                    try {
                        resultFinalTS[i - 1] = jsonObjectFeeds.getInt(fieldName);
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                }
                Log.i("[ DATA ]", "i = " + (i - 1) + "; fieldName = " + fieldName + "; resultFinal[i] = " + resultFinalTS[i - 1]);
            }
            globalsVariables.setField1(resultFinalTS[0]);
            //globalsVariables.setMachineState(resultFinalTS[1]);
            globalsVariables.setField3(resultFinalTS[2]);
            globalsVariables.setField4(resultFinalTS[3]);
            globalsVariables.setMachineState(chiffre);
            Log.i("[DATA]", "OnPostExecute chiffre = " + chiffre);


        } else {
            Log.i("[WHERE]", "ReadThingSpeakData    onPostExecute: response == null");
        }

        try {
            Thread.currentThread();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Integer chiffre_next = chiffre + 1;
        Log.i("[DATA]", "chiffre_next = " + chiffre_next);

        AsyncTask<String, Void, String> taskRefresh = new AsyncReadThingSpeakData(activity);
        taskRefresh.execute(String.valueOf(chiffre_next));
    }
}

