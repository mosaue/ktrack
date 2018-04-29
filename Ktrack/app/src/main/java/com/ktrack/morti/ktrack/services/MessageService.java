package com.ktrack.morti.ktrack.services;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import static com.ktrack.morti.ktrack.services.LocationService.extraMessage;
import static com.ktrack.morti.ktrack.services.LocationService.latitudeKey;
import static com.ktrack.morti.ktrack.services.LocationService.latitudeKeyNP;
import static com.ktrack.morti.ktrack.services.LocationService.longitudeKey;
import static com.ktrack.morti.ktrack.services.LocationService.longitudeKeyNP;
import static com.ktrack.morti.ktrack.activities.MainActivity.phoneKey;

public class MessageService extends IntentService {
    private static final String TAG = "MESSAGESERVICE";


    public MessageService() {
        super("SimpleService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"Message Service Started");

        final String phoneNumber = intent.getStringExtra(phoneKey);
        final Double latitude;
        final Double longitude;
        final String context = intent.getStringExtra(extraMessage);
        Double latitudeTmp = intent.getDoubleExtra(latitudeKey,0);
        Double longitudeTmp = intent.getDoubleExtra(longitudeKey,0);
        if (latitudeTmp==0.0&&longitudeTmp==0.0){
            longitudeTmp = intent.getDoubleExtra(longitudeKeyNP,0);
            latitudeTmp = intent.getDoubleExtra(latitudeKeyNP,0);
        }

        latitude = latitudeTmp;
        longitude = longitudeTmp;
        String message = createMessage(longitude,latitude,context);
        Log.d(TAG,"Trying to send message to: " + phoneNumber + ". Message: " + message);
        try{
            sendSMS(phoneNumber,message);
            Log.e(TAG,"Message sent");
        }catch (Exception e){
            Log.w(TAG,"Message not sent");
        }
        //TODO: Send result back to LocationService
    }

    @Override
    public void onDestroy(){
        Log.d(TAG,"Message Service Destroyed");
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private String createMessage(Double longitude, Double latitude, String context){
        String longitudeString = String.valueOf(longitude).replace(",",".");
        String latitudeString = String.valueOf(latitude).replace(",",".");
        String messageHeader = "";
        switch (context){
            case "normalTracking":
                messageHeader = "Min siste posisjon: ";
                break;
            case "trackingStart":
                messageHeader = "Hei, jeg starter en aktivitet og sender deg min posisjon: ";
                break;
            case "trackingEnd":
                messageHeader = "Hei, jeg er nå ferdig med aktiviteten: ";
                break;
            case "Emergency":
                messageHeader = "Posisjon : ";
                break;
            default:
                break;
        }
        String baseUrl = "https://www.google.com/maps/dir/search/?api=1&query=";
        String messageUrl = baseUrl + longitudeString + "," + latitudeString;
        return messageHeader + messageUrl;
    }
}
