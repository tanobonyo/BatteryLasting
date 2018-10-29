package com.example.btaudio.batterylasting;

import android.graphics.Color;
import android.os.SystemClock;
import android.widget.Chronometer;

public class BtDevice {
    private String name;
    private String address;
    private boolean connected;
    private Chronometer time;
    private long elapsed;

    public BtDevice(String name, String address, long elapsed) {
        this.name= name;
        this.address = address;
        this.connected = false;
        this.elapsed = elapsed;
    }

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        return this.address;
    }

    public long getElapsed(){ return this.elapsed; }

    public boolean getConnected(){
        return this.connected;
    }

    public String getConnectedString(){
        if (this.connected)
            return "Connected!!!";
        else
            return "Disconnected";
    }

    public int getConnectedColor(){
        if (this.connected)
            return Color.GREEN;
        else
            return Color.WHITE;
    }

    public String getTime(){
        if (time != null)
            return String.valueOf(this.time.getContentDescription());
        else
            return "";
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public void setTime(Chronometer time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Address: " + address + ", Status: " + connected + ", Time: " + time  + ", Elap: " + elapsed ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BtDevice) {
            return (((BtDevice) obj).name).equals(this.name);
        }
        return false;
    }

}