package com.ktrack.morti.ktrack.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ktrack.morti.ktrack.R;

public class AddContactActivity extends AppCompatActivity {
    private static final String TAG = "AccContact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final Context context = getApplicationContext();

        final Button addButton = findViewById(R.id.add_contact);
        final EditText newContactName = findViewById(R.id.nameInput);
        final EditText newContactNumber = findViewById(R.id.phoneNumberInput);
        final TextView addContactHeader = findViewById(R.id.addContactHeader);

        addContactHeader.setText(getResources().getString(R.string.add_contact));
        addButton.setText(getResources().getString(R.string.ADD));

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                storeContactInformation(newContactName,newContactNumber);
            }
        });

    }

    private void storeContactInformation(EditText newContactName, EditText newContactNumber){
        String contactName = newContactName.getText().toString();
        String contactPhone = newContactNumber.getText().toString();
        Log.d(TAG,contactName + "," + contactPhone);
        if (contactDetailsOk(contactName,contactPhone)){
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }

    }

    private boolean contactDetailsOk(String contactName, String contactPhone){
        return (contactPhone.length()==8 || (contactPhone.startsWith("0047") && contactPhone.length()==12))
                && !contactName.equals(getString(R.string.contactName));
    }
}
