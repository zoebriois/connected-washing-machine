package com.example.zoebriois.washingmachine2;

import android.app.Application;

public class Globals extends Application {
    // Globals variables
    private int MachineState = 0;
    private int ButtonPushed = 0;
    private int CycleState = 0;
    private int TrapClosed = 0;
    private int Time = 0;


    public int getMachineState() {
        return MachineState;
    }

    public void setMachineState(int machineState) {
        MachineState = machineState;
    }

    public int getButtonPushed() {
        return ButtonPushed;
    }

    public void setButtonPushed(int buttonPushed) {
        ButtonPushed = buttonPushed;
    }

    public int getCycleState() {
        return CycleState;
    }

    public void setCycleState(int cycleState) {
        CycleState = cycleState;
    }

    public int getTrapClosed() {
        return TrapClosed;
    }

    public void setTrapClosed(int trapClosed) {
        TrapClosed = trapClosed;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }
}