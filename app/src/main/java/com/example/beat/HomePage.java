package com.example.beat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    public static final String EXTRA_USERID = "userID";
    protected int userId,id;
    protected String fullname,laststatus;
    protected int recentheartrate;
    protected ImageView monitor,pair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the user ID from the intent
        userId = getIntent().getExtras().getInt(EXTRA_USERID);

        // Set up the UI components
        setProfile();
        monitor = findViewById(R.id.monitorlogo);
        pair = findViewById(R.id.pairlogo);

        // Set up a click listener for the heart rate monitor logo
        monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(HomePage.this, RetrieveHeartRate.class);
                intent2.putExtra("userID",userId);
                startActivity(intent2);
            }
        });

        // Set up a click listener for the device pairing logo
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, RetrieveDevicesApp.class);
                intent.putExtra("userid",userId);
                startActivity(intent);
            }
        });
    }

    // Populate the profile information for the current user
    public void setProfile(){
        try {
            // Get the user's name from the database
            SQLiteOpenHelper beathelper = new UserDatabase(this);
            SQLiteDatabase db = beathelper.getReadableDatabase();
            Cursor cursor = db.query("USERS",
                    new String[]{"_id", "NAME"},
                    "_id = ?",
                    new String[]{Integer.toString(userId)}, null, null, null);
            if (cursor.moveToFirst()) {
                fullname = (cursor.getString(1));
            }
            cursor.close();
            db.close();

            // Set the welcome message with the user's name and ID
            TextView welcomeUser = (TextView) findViewById(R.id.welcometext);
            StringBuilder welcomeUserFullText = new StringBuilder();
            welcomeUserFullText.append("Welcome, ").append(fullname).append("\n UserID: ").append(userId);
            welcomeUser.setText(welcomeUserFullText);

        }catch(SQLException e){
            // Display an error message if there was a problem accessing the database
            Toast toast = Toast.makeText(this, "Database unavailable @Profile", Toast.LENGTH_SHORT);
            toast.show();
        }


        try {
            // Get the user's recent heart rate and health status from the database
            SQLiteOpenHelper beathelper = new UserMedicalInfo(this);
            SQLiteDatabase db = beathelper.getReadableDatabase();
            Cursor cursor = db.query("USERSMEDICAL",
                    new String[]{"_id", "RECENTHEARTRATE","STATUS"},
                    "_id = ?",
                    new String[]{Integer.toString(userId)}, null, null, null);
            if (cursor.moveToFirst()) {
                // Get the recent heart rate and health status from the cursor
                recentheartrate=cursor.getInt(1);
                laststatus=cursor.getString(2);
            }

            // Close the cursor and database connection
            cursor.close();
            db.close();

            // Display the recent heart rate and health status in the user interface
            TextView recentheartratetext = (TextView) findViewById(R.id.recentheartrate);
            TextView laststatustext = (TextView) findViewById(R.id.lastStatus);
            if(recentheartrate==0)
                recentheartratetext.setText("Recent Heart Rate: Not yet taken");
            else
                recentheartratetext.setText("Recent Heart Rate: "+recentheartrate);
            if(laststatus==null)
                laststatustext.setText("User's Health Status: Not yet taken");
            else
                laststatustext.setText("User status: "+laststatus);
        } catch(SQLException e){
            // Display a message to the user if there is an error accessing the database
            Toast toast = Toast.makeText(this, "Database unavailable @Profile", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the options menu from the specified XML resource
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutmenu:
                // Create a dialog box to confirm the user wants to log out
                AlertDialog.Builder builder1 = new AlertDialog.Builder(HomePage.this);
                builder1.setMessage("Are you sure want to log out?");
                builder1.setCancelable(true);

                // Handle the "Yes" button
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                // Create an intent to go back to the main page and start the activity
                                Intent intent = new Intent(HomePage.this, MainPage.class);
                                startActivity(intent);
                            }
                        });

                // Handle the "No" button
                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // Create the alert dialog box and display it
                AlertDialog alert11 = builder1.create();
                alert11.show();

                // Return true to indicate the action has been handled
                return true;

            default:
                // If the selected menu item isn't the logout menu item, let the superclass handle it
                return super.onOptionsItemSelected(item);
        }
    }




}