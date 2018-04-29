package com.ktrack.morti.ktrack.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ktrack.morti.ktrack.utils.*;

import com.ktrack.morti.ktrack.services.LocationService;
import com.ktrack.morti.ktrack.R;

import java.util.ArrayList;

import static com.ktrack.morti.ktrack.utils.DatabaseHelper.COL2;
import static com.ktrack.morti.ktrack.utils.DatabaseHelper.COL3;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MAINACTIVITY";
    public static final String phoneKey = "phoneKey";
    public static final String intervalKey = "intervalKey";

    DatabaseHelper mDatabaseHelper;

    Integer startButtonVisibility;
    Integer stopButtonVisibility;
    Integer contactInformationVisibility;
    String phoneNumber;
    String contact;
    String intervalString;
    String contactInformationString;
    Integer toolbarVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        mDatabaseHelper = new DatabaseHelper(this);
        getState();
    }


    @Override
    public void onResume(){
        super.onResume();
        final Context context = getApplicationContext();
        final Integer toastDuration = Toast.LENGTH_SHORT;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseHelper = new DatabaseHelper(this);

        final Button startButton = findViewById(R.id.startButton);
        final Button stopButton = findViewById(R.id.stopButton);
        final Button mapButton = findViewById(R.id.mapButton);
        final TextView contactInformation = findViewById(R.id.phoneNumberInput);
        final TextView intervalText = findViewById(R.id.intervalText);


        mapButton.setText(R.string.mapButton);
        startButton.setText(R.string.START);
        stopButton.setText(R.string.STOP);

        final Spinner intervalSpinner = findViewById(R.id.intervalChooser);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.intervals_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(adapter);
        intervalSpinner.setOnItemSelectedListener(new SpinnerActivity());
        getState();

        startButton.setVisibility(startButtonVisibility);
        stopButton.setVisibility(stopButtonVisibility);
        mapButton.setVisibility(stopButtonVisibility);
        intervalText.setText(intervalString);
        intervalSpinner.setVisibility(startButtonVisibility);
        contactInformation.setText(contactInformationString);
        toolbar.setVisibility(toolbarVisibility);


        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String intervalChosen = intervalSpinner.getSelectedItem().toString();
                startButtonAction(context,toastDuration,startButton,stopButton,mapButton,intervalSpinner
                       ,contactInformation ,intervalText,intervalChosen,toolbar);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopButtonAction(context,startButton,stopButton,mapButton,intervalSpinner
                        ,intervalText,contactInformation,toolbar);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void getState(){
        if (!doesPrimaryContactExists()){
            startButtonVisibility = View.INVISIBLE;
            stopButtonVisibility = View.INVISIBLE;
            intervalString = getResources().getString(R.string.noPrimaryContact);
            contactInformationVisibility = View.INVISIBLE;
            contactInformationString = "";
            toolbarVisibility = View.VISIBLE;
        }else{
            getPrimaryContactDetails();
            LocationServiceGlobals locationServiceGlobals = LocationServiceGlobals.getInstance();
            if (locationServiceGlobals.getLocationServiceStatus()) {
                startButtonVisibility = View.INVISIBLE;
                stopButtonVisibility = View.VISIBLE;
                intervalString = String.format(getResources().getString(R.string.intervalSelected), locationServiceGlobals.getInterval());
                contactInformationString = String.format(getResources().getString(R.string.phoneNumberSelected), contact +", " +  phoneNumber);
                toolbarVisibility = View.INVISIBLE;
            } else {
                startButtonVisibility = View.VISIBLE;
                stopButtonVisibility = View.INVISIBLE;
                contactInformationString = String.format(getResources().getString(R.string.notTracking), contact +", " +  phoneNumber);
                intervalString = getResources().getString(R.string.intervalNotChosen);
                toolbarVisibility = View.VISIBLE;
            }
            contactInformationVisibility = View.VISIBLE;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_contact_menu) {
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivity(intent);
        }else if (id == R.id.edit_contacts_menu){
            Intent intent = new Intent(this, ContactOverviewActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void startButtonAction(Context context, Integer toastDuration, Button startButton
        , Button stopButton, Button mapButton, Spinner intervalSpinner, TextView contactInformation
        , TextView intervalText, String intervalChosen, Toolbar toolbar){
        if (phoneNumber.length()!=8 && !(phoneNumber.startsWith("0047") && phoneNumber.length()==12)){
            CharSequence text;
            text = "Enter a phone number!";
            Toast toast = Toast.makeText(context, text, toastDuration);
            toast.show();
        }else{
            if (intervalChosen.equals("")){
                intervalChosen = "60";
            }
            startButtonVisibility = View.INVISIBLE;
            stopButtonVisibility = View.VISIBLE;
            toolbarVisibility = View.INVISIBLE;

            startButton.setVisibility(startButtonVisibility);
            stopButton.setVisibility(stopButtonVisibility);
            mapButton.setVisibility(stopButtonVisibility);
            toolbar.setVisibility(toolbarVisibility);

            intervalSpinner.setVisibility(startButtonVisibility);
            contactInformationString = String.format(getResources().getString(R.string.phoneNumberSelected), contact +", " +  phoneNumber);
            contactInformation.setText(contactInformationString);
            intervalString = String.format(getResources().getString(R.string.intervalSelected), intervalChosen);
            intervalText.setText(intervalString);

            Log.d(TAG,"Service is starting");
            Intent intent = new Intent(context, LocationService.class);
            intent.putExtra(phoneKey, phoneNumber);
            intent.putExtra(intervalKey, intervalChosen);
            context.startService(intent);
        }
    }

    private void stopButtonAction(Context context, Button startButton, Button stopButton
            , Button mapButton, Spinner intervalSpinner, TextView intervalText, TextView contactInformation
            , Toolbar toolbar){
        startButtonVisibility = View.VISIBLE;
        stopButtonVisibility = View.INVISIBLE;
        toolbarVisibility = View.VISIBLE;
        startButton.setVisibility(startButtonVisibility);
        stopButton.setVisibility(stopButtonVisibility);
        mapButton.setVisibility(stopButtonVisibility);
        intervalSpinner.setVisibility(startButtonVisibility);
        toolbar.setVisibility(toolbarVisibility);
        intervalText.setText(R.string.intervalNotChosen);
        intervalString = getResources().getString(R.string.intervalNotChosen);
        intervalText.setText(intervalString);
        contactInformationString = String.format(getResources().getString(R.string.notTracking), contact +", " +  phoneNumber);
        contactInformation.setText(contactInformationString);
        stopService(new Intent(context,LocationService.class));
    }

    private boolean doesPrimaryContactExists(){
        boolean primaryExist = mDatabaseHelper.primaryContactExists();
        Log.e(TAG,"Primary contact checked " + primaryExist);
        return mDatabaseHelper.primaryContactExists();
    }

    private void getPrimaryContactDetails(){
        Cursor data = mDatabaseHelper.getPrimaryContact();

        while(data.moveToNext()){
            contact = data.getString(data.getColumnIndex(COL2));
            phoneNumber = data.getString(data.getColumnIndex(COL3));
        }
        data.close();
    }
}
