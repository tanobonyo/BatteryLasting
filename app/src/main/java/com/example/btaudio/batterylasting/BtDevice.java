package com.example.btaudio.batterylasting;

import android.graphics.Color;
import android.os.SystemClock;
import android.widget.Chronometer;

public class BtDevice {
    private String name;
    private String address;
    private boolean connected;
    private Chronometer time;

    public BtDevice(String name, String address) {
        this.name= name;
        this.address = address;
        this.connected = false;
    }

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        return this.address;
    }

    public boolean getConnected(){
        return this.connected;
    }

    public String getConnectedString(){
        if (this.connected)
            return "Connected!!!";
        else
            return "";
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

    public void setTime(Chronometer time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Address: " + address + ", Status: " + connected + ", Time: " + time  ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BtDevice) {
            return (((BtDevice) obj).name).equals(this.name);
        }
        return false;
    }

}