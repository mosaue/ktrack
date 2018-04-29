package com.ktrack.morti.ktrack.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ktrack.morti.ktrack.R;
import com.ktrack.morti.ktrack.contactUtils.Contact;
import com.ktrack.morti.ktrack.contactUtils.ContactAdapter;
import com.ktrack.morti.ktrack.contactUtils.RecyclerTouchListener;
import com.ktrack.morti.ktrack.utils.DatabaseHelper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.ktrack.morti.ktrack.utils.DatabaseHelper.COL2;
import static com.ktrack.morti.ktrack.utils.DatabaseHelper.COL3;
import static com.ktrack.morti.ktrack.utils.DatabaseHelper.COL4;

public class ContactOverviewActivity extends AppCompatActivity {
    public static final String nameKey = "contactName";
    public static final String phoneKey = "contactPhone";
    public static final String primaryKey = "mainContact";

    private static final String TAG = "CURSORtemp";
    private List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_overview);
        mDatabaseHelper = new DatabaseHelper(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ContactAdapter(contactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contact contact = contactList.get(position);
                Intent intent = new Intent(getApplicationContext(), EditContactActivity.class);
                intent.putExtra(nameKey,contact.getName());
                intent.putExtra(phoneKey,contact.getPhone());
                intent.putExtra(primaryKey,contact.getPrimary());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareMovieData();
    }

    private void prepareMovieData() {
        Cursor cursor = mDatabaseHelper.getData();
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String name = cursor.getString(cursor.getColumnIndex(COL2));
                String phone = cursor.getString(cursor.getColumnIndex(COL3));
                String primary = cursor.getString(cursor.getColumnIndex(COL4));
                contactList.add(new Contact(name,phone,primary));
                cursor.moveToNext();
            }
        }
        cursor.close();

        mAdapter.notifyDataSetChanged();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void addData(String name, String phone, String mainContact) {
        boolean insertData = mDatabaseHelper.addData(name,phone,mainContact);

        if (insertData) {
            toastMessage("Contact added");
        } else {
            toastMessage("Contact already exists");
        }
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
