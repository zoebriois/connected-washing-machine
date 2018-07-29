package com.example.zoebriois.washingmachine2;

import android.app.Application;

public class Globals extends Application {
    // Globals variables
    private double MachineState = 0;
    private double ButtonPushed = 0;
    private double CycleState = 0;
    private double TrapClosed = 0;
    private double Time = 0.0;


    public double getMachineState() {
        return MachineState;
    }

    public void setMachineState(double machineState) {
        MachineState = machineState;
    }

    public double getButtonPushed() {
        return ButtonPushed;
    }

    public void setButtonPushed(double buttonPushed) {
        ButtonPushed = buttonPushed;
    }

    public double getCycleState() {
        return CycleState;
    }

    public void setCycleState(double cycleState) {
        CycleState = cycleState;
    }

    public double getTrapClosed() {
        return TrapClosed;
    }

    public void setTrapClosed(double trapClosed) {
        TrapClosed = trapClosed;
    }

    public double getTime() {
        return Time;
    }

    public void setTime(double time) {
        Time = time;
    }
}