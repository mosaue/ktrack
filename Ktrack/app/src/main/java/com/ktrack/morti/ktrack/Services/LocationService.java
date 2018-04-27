package com.ktrack.morti.ktrack.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.ktrack.morti.ktrack.Utils.LocationServiceGlobals;
import com.ktrack.morti.ktrack.Utils.MessageContext;

import static com.ktrack.morti.ktrack.Activities.MainScreen.intervalKey;
import static com.ktrack.morti.ktrack.Activities.MainScreen.phoneKey;
import static java.lang.Thread.sleep;

public class LocationService extends Service
{
    private static final String TAG = "LOCATION";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1000;

    public static String latitudeKey = "latitude";
    public static String latitudeKeyNP = "latitudeNP";
    public static String longitudeKey = "longitude";
    public static String longitudeKeyNP = "longitudeNP";
    public static String extraMessage = "extraMessage";

    private boolean running;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation.set(location);
            Log.e(TAG, "onLocationChanged: " + location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        //TODO: Communicate position back to activity
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG,"LocationService Started");
        final Context context = getApplicationContext();
        final Integer interval = Integer.parseInt(intent.getStringExtra(intervalKey));
        final String phoneNumber = intent.getStringExtra(phoneKey);
        LocationServiceGlobals serviceGlobals = LocationServiceGlobals.getInstance();
        serviceGlobals.setLocationServiceStatus(true);
        serviceGlobals.setPhoneNumber(phoneNumber);
        serviceGlobals.setInterval(interval.toString());
        running = true;
        Runnable r = new Runnable() {
            public void run() {
                Integer startCounter = 1;
                Integer counter = 1;
                while (running) {
                    try {
                        if (counter==interval*60){
                            sendMessage(context,phoneNumber,MessageContext.normalTracking);
                            counter = 1;
                        }else if (startCounter == 10){
                            if (!positionFound()){
                                startCounter = 0;
                                counter = 0;
                            }else {
                                sendMessage(context,phoneNumber,MessageContext.trackingStart);
                            }
                        }
                        //TODO: get message result and handle case when it fails
                        //TODO: Logic for emergency
                        counter++;
                        startCounter++;
                        sleep(1000);
                    }catch (Exception e){
                        stopSelf();
                    }
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        running = false;
        initiateEndMessage();
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initiateEndMessage(){
        Runnable r = new Runnable() {
            public void run() {
                final Context context = getApplicationContext();
                LocationServiceGlobals serviceGlobals = LocationServiceGlobals.getInstance();
                String phoneNumber = serviceGlobals.getPhoneNumber();
                sendMessage(context,phoneNumber,MessageContext.trackingEnd);
                LocationServiceGlobals locationServiceGlobals = LocationServiceGlobals.getInstance();
                locationServiceGlobals.setLocationServiceStatus(false);
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void sendMessage(Context context, String phoneNumber, MessageContext messageContext){
        Intent intent = new Intent(context, MessageService.class);
        intent.putExtra(phoneKey, phoneNumber);
        intent.putExtra(longitudeKey,mLocationListeners[0].mLastLocation.getLongitude());
        intent.putExtra(latitudeKey,mLocationListeners[0].mLastLocation.getLatitude());
        intent.putExtra(longitudeKeyNP,mLocationListeners[1].mLastLocation.getLongitude());
        intent.putExtra(latitudeKeyNP,mLocationListeners[1].mLastLocation.getLatitude());
        intent.putExtra(extraMessage, messageContext.getCode());
        context.startService(intent);
    }

    private boolean positionFound(){
        if(mLocationListeners[0].mLastLocation.getLatitude() == 0.0 && mLocationListeners[0].mLastLocation.getLongitude() == 0.0
                && mLocationListeners[1].mLastLocation.getLongitude() == 0.0 && mLocationListeners[1].mLastLocation.getLatitude() == 0.0){
            return false;
        }
        return true;
    }
}
