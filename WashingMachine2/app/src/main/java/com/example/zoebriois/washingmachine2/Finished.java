package com.example.zoebriois.washingmachine2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class Finished extends Activity {
    int ButtonPushedField = 2;
    double ButtonPushedCurrent = 0;

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            final Globals globalsVariables = (Globals) getApplicationContext();
            ButtonPushedCurrent = globalsVariables.getButtonPushed();

            if (ButtonPushedCurrent == 1) {
                Intent i = new Intent(Finished.this, OpeningTheTrap.class);
                startActivity(i);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);

        // Collecting data from ThingSpeak
        AsyncTask<String, Void, String> taskOnCreate = new AsyncReadThingSpeakData(this);
        taskOnCreate.execute();

        // Updating the textView
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 10000);
    }

    private void TimerMethod() {
        if (!this.isFinishing()) {
            this.runOnUiThread(Timer_Tick);
        }
    }

    //When pushing time cycle buttons
    public void onBOpenHatchClick(View v) {
        if (v.getId() == R.id.BOpenHatch) {
            AsyncTask<String, Void, String> taskOnCLick1 = new AsyncWriteThingSpeakData(this);
            taskOnCLick1.execute(String.valueOf(ButtonPushedField), "1");
            Globals globalsVariables = (Globals) getApplicationContext();
            globalsVariables.setButtonPushed(1);
            Intent i = new Intent(Finished.this, OpeningTheTrap.class);
            startActivity(i);
            finish();
        }

    }





}
