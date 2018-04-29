package com.ktrack.morti.ktrack.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ktrack.morti.ktrack.R;
import com.ktrack.morti.ktrack.utils.DatabaseHelper;

import static com.ktrack.morti.ktrack.activities.ContactOverviewActivity.nameKey;
import static com.ktrack.morti.ktrack.activities.ContactOverviewActivity.phoneKey;
import static com.ktrack.morti.ktrack.activities.ContactOverviewActivity.primaryKey;

public class EditContactActivity extends AppCompatActivity {
    private static final String TAG = "AccContact";
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        final Context context = getApplicationContext();
        Intent intent = getIntent();
        final String contactName = intent.getStringExtra(nameKey);
        final String contactPhone = intent.getStringExtra(phoneKey);
        String contactPrimary = intent.getStringExtra(primaryKey);

        final Button addButton = findViewById(R.id.editContact);
        final Button deleteButton = findViewById(R.id.deleteContact);
        final TextView addContactHeader = findViewById(R.id.editContactHeader);

        final EditText newContactName = findViewById(R.id.editName);
        final EditText newContactNumber = findViewById(R.id.editPhoneNumber);
        final CheckBox primaryContact = findViewById(R.id.editPrimaryContact);
        newContactName.setText(contactName);
        newContactNumber.setText(contactPhone);
        if (contactPrimary.equals("Y")){
            primaryContact.setChecked(true);
        }
        addContactHeader.setText(getResources().getString(R.string.edit_contact));
        addButton.setText(getResources().getString(R.string.apply_edit_contacts));
        deleteButton.setText(getResources().getString(R.string.apply_delete_contact));
        mDatabaseHelper = new DatabaseHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                storeContactInformation(newContactName,newContactNumber,primaryContact
                ,contactName,contactPhone);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteContactInformation(contactName,contactPhone);
            }
        });

    }


    private void storeContactInformation(EditText newContactName, EditText newContactNumber, CheckBox primaryContact,
                                         String oldcontactName, String oldContactPhone){
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
            editData(oldcontactName,contactName,oldContactPhone,contactPhone,primaryContactChecker);
            Intent intent = new Intent(this, ContactOverviewActivity.class);
            startActivity(intent);
        }
    }

    private void deleteContactInformation(String oldcontactName, String oldContactPhone){
        deleteData(oldcontactName,oldContactPhone);
        Intent intent = new Intent(this, ContactOverviewActivity.class);
        startActivity(intent);
    }

    private boolean contactDetailsOk(String contactName, String contactPhone){
        return (contactPhone.length()==8 || (contactPhone.startsWith("0047") && contactPhone.length()==12))
                && !contactName.equals(getString(R.string.contactName));
    }

    private void editData(String oldName, String newName, String oldPhone, String newPhone, String mainContact) {
        mDatabaseHelper.editData(oldName,newName,oldPhone,newPhone,mainContact);
    }

    private void deleteData(String name, String phone){
        mDatabaseHelper.deleteData(name,phone);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_menu) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(id == R.id.add_contact_menu_edit){
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ContactOverviewActivity.class);
        startActivity(intent);
    }
}
