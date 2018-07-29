package com.example.zoebriois.washingmachine2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    static MainActivity MainActivity;
    static TextView MachineStateText;
    double MachineStateCurrent;
    int CycleStateField = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MachineStateText = (TextView) findViewById(R.id.textMachineState);

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
            MachineStateCurrent = globalsVariables.getMachineState();
            if (MachineStateCurrent == 1) {
                MachineStateText.setText("full");
            } else if (MachineStateCurrent == 0) {
                MachineStateText.setText("not full");
            } else if (MachineStateCurrent == 9) {
                MachineStateText.setText("...");
            } else {
                MachineStateText.setText("?");
            }
            Log.i("[WHERE]", "MachineStateTest.setText()");
        }
    };


    // Clicking on START MACHINE
    public void onBStartMachineClick(View v) {
        if (v.getId() == R.id.BStartMachine) {
            AsyncTask<String, Void, String> taskOnCLick1 = new AsyncWriteThingSpeakData(this);
            taskOnCLick1.execute(String.valueOf(CycleStateField), "1");
            Intent i = new Intent(MainActivity.this, ClosingTheTrap.class);
            startActivity(i);
            finish();
        }
    }
}
