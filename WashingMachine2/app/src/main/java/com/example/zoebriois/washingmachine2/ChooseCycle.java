package com.example.zoebriois.washingmachine2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChooseCycle extends AppCompatActivity {
    Integer MachineStateField = 1;
    Integer ButtonPushed = 2;
    Integer CycleStateField = 3;
    Integer TrapClosedField = 4;
    Integer TimeField = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cycle);
    }

    // When pushing the home button
    public void onBMainClick(View v) {
        if (v.getId() == R.id.BMain) {
            Intent i = new Intent(ChooseCycle.this, MainActivity.class);
            startActivity(i);
        }

    }

    //When pushing time cycle buttons
    public void onBCycle1Click(View v) {
        if (v.getId() == R.id.BCycle1) {
            // Posting data to ThingSpeak
            // -- CycleState <- 1 & Time <- 2
            AsyncTask<String, Void, String> taskOnCLick1 = new AsyncWriteThingSpeakData(this);
            taskOnCLick1.execute(String.valueOf(CycleStateField),"1",String.valueOf(TimeField),"2");
            Globals globalsVariables = (Globals) getApplicationContext();
            globalsVariables.setTime(2);
            Intent i = new Intent(ChooseCycle.this, CycleRunning.class);
            i.putExtra("TimeChoose",2);
            startActivity(i);
        }

    }

    public void onBCycle2Click(View v) {
        if (v.getId() == R.id.BCycle2) {
            // Posting data to ThingSpeak
            // -- CycleState <- 1 & Time <- 5
            AsyncTask<String, Void, String> taskOnCLick2 = new AsyncWriteThingSpeakData(this);
            taskOnCLick2.execute(String.valueOf(CycleStateField),"1",String.valueOf(TimeField),"5");
            Globals globalsVariables = (Globals) getApplicationContext();
            globalsVariables.setTime(5);
            Intent i = new Intent(ChooseCycle.this, CycleRunning.class);
            i.putExtra("TimeChoose",5);
            startActivity(i);
        }

    }

    public void onBCycle3Click(View v) {
        if (v.getId() == R.id.BCycle3) {
            // Posting data to ThingSpeak
            // -- CycleState <- 1 & Time <- 10
            AsyncTask<String, Void, String> taskOnCLick3 = new AsyncWriteThingSpeakData(this);
            taskOnCLick3.execute(String.valueOf(CycleStateField),"1",String.valueOf(TimeField),"10");
            Globals globalsVariables = (Globals) getApplicationContext();
            globalsVariables.setTime(10);
            Intent i = new Intent(ChooseCycle.this, CycleRunning.class);
            i.putExtra("TimeChoose",10);
            startActivity(i);
        }

    }
}
