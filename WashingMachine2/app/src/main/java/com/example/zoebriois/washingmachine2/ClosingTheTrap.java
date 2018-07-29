package com.example.zoebriois.washingmachine2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ClosingTheTrap extends Activity {
    static TextView ClosingTheTrapText;
    Button BCycle1;
    Button BCycle2;
    Button BCycle3;
    double TrapClosedCurrent = 0;
    Integer TimeField = 5;
    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            final Globals globalsVariables = (Globals) getApplicationContext();
            TrapClosedCurrent = globalsVariables.getTrapClosed();

            if (TrapClosedCurrent == 1) {
                Button BCycle1=findViewById(R.id.BCycle1);
                Button BCycle2=findViewById(R.id.BCycle2);
                Button BCycle3=findViewById(R.id.BCycle3);
                ClosingTheTrapText.setText("The hatch is now closed, you can choose the cycle duration:");
                BCycle1.setVisibility(View.VISIBLE);
                BCycle2.setVisibility(View.VISIBLE);
                BCycle3.setVisibility(View.VISIBLE);
            } else if (TrapClosedCurrent == 0) {
                ClosingTheTrapText.setText("The hatch is closing");
            }
            Log.i("[WHERE]", "ClosingTheTrapText.setText()");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing_the_trap);
        ClosingTheTrapText = (TextView) findViewById(R.id.textClosingTheTrap);

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
        if ((!this.isFinishing())&(TrapClosedCurrent==0)) {
            this.runOnUiThread(Timer_Tick);
        }
    }

    //When pushing time cycle buttons
    public void onBCycle1Click(View v) {
        if (v.getId() == R.id.BCycle1) {
            // Posting data to ThingSpeak: Time <- 2
            AsyncTask<String, Void, String> taskOnCLick1 = new AsyncWriteThingSpeakData(this);
            taskOnCLick1.execute(String.valueOf(TimeField), "2");
            Globals globalsVariables = (Globals) getApplicationContext();
            globalsVariables.setTime(2);
            Intent i = new Intent(ClosingTheTrap.this, CycleRunning.class);
            startActivity(i);
            finish();
        }

    }

    public void onBCycle2Click(View v) {
        if (v.getId() == R.id.BCycle2) {
            // Posting data to ThingSpeak: Time <- 5
            AsyncTask<String, Void, String> taskOnCLick2 = new AsyncWriteThingSpeakData(this);
            taskOnCLick2.execute(String.valueOf(TimeField), "5");
            Globals globalsVariables = (Globals) getApplicationContext();
            globalsVariables.setTime(5);
            Intent i = new Intent(ClosingTheTrap.this, CycleRunning.class);
            startActivity(i);
            finish();
        }

    }

    public void onBCycle3Click(View v) {
        if (v.getId() == R.id.BCycle3) {
            // Posting data to ThingSpeak: Time <- 10
            AsyncTask<String, Void, String> taskOnCLick3 = new AsyncWriteThingSpeakData(this);
            taskOnCLick3.execute(String.valueOf(TimeField), "10");
            Globals globalsVariables = (Globals) getApplicationContext();
            globalsVariables.setTime(10);
            Intent i = new Intent(ClosingTheTrap.this, CycleRunning.class);
            startActivity(i);
            finish();
        }

    }
}
