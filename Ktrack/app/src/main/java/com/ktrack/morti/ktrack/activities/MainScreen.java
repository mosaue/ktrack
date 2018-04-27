package com.ktrack.morti.ktrack.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ktrack.morti.ktrack.utils.*;

import com.ktrack.morti.ktrack.services.LocationService;
import com.ktrack.morti.ktrack.R;

public class MainScreen extends AppCompatActivity{
    private static final String TAG = "MAINACTIVITY";
    public static final String phoneKey = "phoneKey";
    public static final String intervalKey = "intervalKey";
    public static final String intervalTextKey = "intervalTextKey";
    public static final String phoneTextKey = "phoneTextKey";

    Integer startButtonVisibility;
    Integer stopButtonVisibility;
    String startButtonStateKey = "START_VISIBILITY";
    String stopButtonStateKey = "STOP_VISIBILITY";
    String phoneNumber;
    String intervalString;
    String phoneString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = getApplicationContext();
        final Integer toastDuration = Toast.LENGTH_SHORT;


        final Button startButton = findViewById(R.id.startButton);
        final Button stopButton = findViewById(R.id.stopButton);
        final Button mapButton = findViewById(R.id.mapButton);
        final EditText phoneInput = findViewById(R.id.phoneNumberInput);
        final TextView intervalText = findViewById(R.id.intervalText);
        final TextView phoneText = findViewById(R.id.phoneText);

        mapButton.setText(R.string.mapButton);
        startButton.setText(R.string.START);
        stopButton.setText(R.string.STOP);

        final Spinner intervalSpinner = findViewById(R.id.intervalChooser);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.intervals_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(adapter);
        intervalSpinner.setOnItemSelectedListener(new SpinnerActivity());
        getState(savedInstanceState);

        startButton.setVisibility(startButtonVisibility);
        stopButton.setVisibility(stopButtonVisibility);
        mapButton.setVisibility(stopButtonVisibility);
        phoneInput.setVisibility(startButtonVisibility);
        phoneText.setVisibility(stopButtonVisibility);
        phoneText.setText(phoneString);
        intervalText.setText(intervalString);
        intervalSpinner.setVisibility(startButtonVisibility);
        phoneInput.setVisibility(startButtonVisibility);
        phoneText.setVisibility(stopButtonVisibility);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneNumber = phoneInput.getText().toString();
                String intervalChosen = intervalSpinner.getSelectedItem().toString();
                startButtonAction(context,toastDuration,startButton,stopButton,mapButton,intervalSpinner
                    ,phoneInput,phoneText,intervalText,intervalChosen);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopButtonAction(context,startButton,stopButton,mapButton,intervalSpinner,phoneInput
                    ,phoneText,intervalText);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putInt(startButtonStateKey, startButtonVisibility);
        outState.putInt(stopButtonStateKey, stopButtonVisibility);
        outState.putString(intervalTextKey, intervalString);
        outState.putString(phoneTextKey, phoneString);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void getState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            startButtonVisibility = savedInstanceState.getInt(startButtonStateKey);
            stopButtonVisibility = savedInstanceState.getInt(stopButtonStateKey);
            intervalString = savedInstanceState.getString(intervalTextKey);
            phoneString = savedInstanceState.getString(phoneTextKey);
        }else{
            startButtonVisibility = View.VISIBLE;
            stopButtonVisibility = View.INVISIBLE;
            phoneString = "";
            intervalString = getResources().getString(R.string.intervalNotChosen);
        }
        LocationServiceGlobals locationServiceGlobals = LocationServiceGlobals.getInstance();
        if (locationServiceGlobals.getLocationServiceStatus()){
            startButtonVisibility = View.INVISIBLE;
            stopButtonVisibility = View.VISIBLE;
            intervalString = String.format(getResources().getString(R.string.intervalSelected), locationServiceGlobals.getInterval());
            phoneString = String.format(getResources().getString(R.string.phoneNumberSelected), locationServiceGlobals.getPhoneNumber());
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
        if (id == R.id.add_contact) {
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivity(intent);
        }else if (id == R.id.choose_mainContact){

        }

        return super.onOptionsItemSelected(item);
    }

    private void startButtonAction(Context context, Integer toastDuration, Button startButton
        , Button stopButton, Button mapButton, Spinner intervalSpinner, EditText phoneInput
        , TextView phoneText, TextView intervalText, String intervalChosen){
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

            startButton.setVisibility(startButtonVisibility);
            stopButton.setVisibility(stopButtonVisibility);
            mapButton.setVisibility(stopButtonVisibility);
            intervalSpinner.setVisibility(startButtonVisibility);
            phoneInput.setVisibility(startButtonVisibility);
            phoneText.setVisibility(stopButtonVisibility);

            phoneString = String.format(getResources().getString(R.string.phoneNumberSelected), phoneNumber);
            phoneText.setText(phoneString);

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
            , Button mapButton, Spinner intervalSpinner, EditText phoneInput, TextView phoneText
            , TextView intervalText){
        startButtonVisibility = View.VISIBLE;
        stopButtonVisibility = View.INVISIBLE;
        startButton.setVisibility(startButtonVisibility);
        stopButton.setVisibility(stopButtonVisibility);
        mapButton.setVisibility(stopButtonVisibility);
        intervalSpinner.setVisibility(startButtonVisibility);
        phoneInput.setVisibility(startButtonVisibility);
        intervalText.setText(R.string.intervalNotChosen);
        phoneText.setVisibility(stopButtonVisibility);
        intervalString = getResources().getString(R.string.intervalNotChosen);
        intervalText.setText(intervalString);
        stopService(new Intent(context,LocationService.class));
    }
}
