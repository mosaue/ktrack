package com.ktrack.morti.ktrack.utils;

public class LocationServiceGlobals {
    private static LocationServiceGlobals instance;

    // Global variable
    private boolean locationServiceRunning;
    private String phoneNumber;
    private String interval;

    // Restrict the constructor from being instantiated
    private LocationServiceGlobals(){}

    public void setLocationServiceStatus(boolean running){
        this.locationServiceRunning=running;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInterval() {
        return interval;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean getLocationServiceStatus(){
        return this.locationServiceRunning;
    }

    public static synchronized LocationServiceGlobals getInstance(){
        if(instance==null){
            instance=new LocationServiceGlobals();
            instance.setLocationServiceStatus(false);
        }
        return instance;
    }
}
