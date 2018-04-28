package com.ktrack.morti.ktrack.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ktrack.morti.ktrack.R;
import com.ktrack.morti.ktrack.utils.DatabaseHelper;

public class AddContactActivity extends AppCompatActivity {
    private static final String TAG = "AccContact";
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final Context context = getApplicationContext();

        final Button addButton = findViewById(R.id.add_contact);
        final EditText newContactName = findViewById(R.id.nameInput);
        final EditText newContactNumber = findViewById(R.id.newPhoneNumber);
        final TextView addContactHeader = findViewById(R.id.addContactHeader);
        final CheckBox primaryContact = findViewById(R.id.primaryContact);

        addContactHeader.setText(getResources().getString(R.string.add_contact));
        addButton.setText(getResources().getString(R.string.ADD));
        mDatabaseHelper = new DatabaseHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                storeContactInformation(newContactName,newContactNumber,primaryContact);
            }
        });

    }

    private void storeContactInformation(EditText newContactName, EditText newContactNumber, CheckBox primaryContact){
        String contactName = newContactName.getText().toString();
        String contactPhone = newContactNumber.getText().toString();
        Log.d(TAG,contactName + "," + contactPhone);
        String primaryContactChecker = "N";
        if (primaryContact.isChecked()){
            primaryContactChecker = "Y";
        }
        if (contactDetailsOk(contactName,contactPhone)){
            if (doesPrimaryContactExist() && primaryContact.isChecked()){
                removePrimary();
            }
            Log.e(TAG,primaryContactChecker);
            addData(contactName,contactPhone,primaryContactChecker);
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }

    }

    private boolean contactDetailsOk(String contactName, String contactPhone){
        return (contactPhone.length()==8 || (contactPhone.startsWith("0047") && contactPhone.length()==12))
                && !contactName.equals(getString(R.string.contactName));
    }

    public void addData(String name, String phone, String mainContact) {
        boolean insertData = mDatabaseHelper.addData(name,phone,mainContact);

        if (insertData) {
            toastMessage("Contact added");
        } else {
            toastMessage("Contact already exists");
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    private void removePrimary(){
        mDatabaseHelper.endPrimary();
    }

    private boolean doesPrimaryContactExist(){
        return mDatabaseHelper.primaryContactExists();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
