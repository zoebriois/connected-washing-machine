package com.example.zoebriois.washingmachine2;

import android.app.Activity;
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
    double resultFinalTS[] = {9, 9, 9, 9, 9};
    String response;

    public AsyncReadThingSpeakData(Activity a) {
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
            URL url = new URL("https://api.thingspeak.com/channels/508003/feeds.json?results=1");
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = readStream(urlConnection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

            for (int i = 1; i < 6; i++) {
                fieldName = "field" + i;
                try {
                    assert jsonObjectFeeds != null;
                    if (jsonObjectFeeds.get(fieldName) != null) {
                        resultFinalTS[i - 1] = jsonObjectFeeds.getInt(fieldName);
                        switch (i) {
                            case 1:
                                globalsVariables.setMachineState(resultFinalTS[i - 1]);
                                break;
                            case 2:
                                globalsVariables.setButtonPushed(resultFinalTS[i-1]);
                                break;
                            case 3:
                                globalsVariables.setCycleState(resultFinalTS[i-1]);
                                break;
                            case 4:
                                globalsVariables.setTrapClosed(resultFinalTS[i-1]);
                                break;
                            case 5:
                                globalsVariables.setTime(resultFinalTS[i-1]);
                                Log.i("[DATA]", "Time = " + String.valueOf(globalsVariables.getTime()));
                                break;
                        }
                        Log.i("[DATA]","i = " + (i-1) + "; fieldName = " + fieldName + "; resultFinal[i] = " + resultFinalTS[i-1]);
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        }


        Log.i("[DATA]","\nMachineState = " + globalsVariables.getMachineState()
                + "\nButtonPushed = " + globalsVariables.getButtonPushed()
                + "\nCycleState = " + globalsVariables.getCycleState()
                + "\nTrapClosed = " + globalsVariables.getTrapClosed()
                + "\nChooseTime = " + globalsVariables.getTime());

        // Delay
        try {
            Thread.currentThread();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // if the activity who start the AsyncTask is already working we restart the AsyncTask
        if (!activity.isFinishing()) {
            Log.i("[WHERE]", "ReadThingSpeakData    Restarting AsyncTask");
            AsyncTask<String, Void, String> taskRefresh = new AsyncReadThingSpeakData(activity);
            taskRefresh.execute();
        }
        else{
            Log.i("[WHERE]", "ReadThingSpeakData    Activity is Finishing");
        }
    }
}

