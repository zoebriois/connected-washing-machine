package com.example.zoebriois.washingmachine2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class OpeningTheTrap extends Activity {
    static TextView OpeningTheTrapText;
    double TrapClosedCurrent;
    double CycleStateCurrent;
    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            final Globals globalsVariables = (Globals) getApplicationContext();
            TrapClosedCurrent = globalsVariables.getTrapClosed();
            CycleStateCurrent = globalsVariables.getCycleState();
            Log.i("[DATA]","TrapClosedCurrent = " + TrapClosedCurrent);
            Log.i("[DATA]","CycleStateCurrent = " + CycleStateCurrent);
            if (TrapClosedCurrent == 0.0) {
                OpeningTheTrapText.setText("The hatch is now open,\n press the button next to the machine during 3s when you emptied it");
                if (CycleStateCurrent == 3.0) {
                    Intent i = new Intent(OpeningTheTrap.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            } else if (TrapClosedCurrent == 1.0) {
                OpeningTheTrapText.setText("The hatch is opening");
            }
            Log.i("[WHERE]", "OpeningTheTrapText.setText()");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_the_trap);
        OpeningTheTrapText = (TextView) findViewById(R.id.textOpeningTheTrap);

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
}
