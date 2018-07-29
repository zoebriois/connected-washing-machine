package com.example.zoebriois.washingmachine2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class CycleRunning extends Activity {
    static TextView RemainingTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_running);

        final Globals globalsVariables = (Globals) getApplicationContext();
        double RemainingTime = globalsVariables.getTime();

        RemainingTimeText = findViewById(R.id.textRemainingTime);
        RemainingTimeText.setText(String.valueOf(RemainingTime));

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

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            final Globals globalsVariables = (Globals) getApplicationContext();
            double RemainingTime = globalsVariables.getTime();
            if (RemainingTime > 0) {
                RemainingTimeText.setText(String.valueOf(RemainingTime) + " min");
            } else {
                Intent i = new Intent(CycleRunning.this, Finished.class);
                startActivity(i);
            }
        }
    };
}