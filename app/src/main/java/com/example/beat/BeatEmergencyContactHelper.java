package com.example.beat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BeatEmergencyContactHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "beatcontact"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    BeatEmergencyContactHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db,0,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    public void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion<1) {
            db.execSQL("CREATE TABLE USERSCONTACT (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "STRING EMERGENCYNAME,"
                    + "STRING EMERGENCYPHONE);");
        }
    }

    public void insertUserEmergencyContact(SQLiteDatabase db,String name, String phone){
        ContentValues uservalues = new ContentValues();
        uservalues.put("EMERGENCYNAME", name);
        uservalues.put("EMERGENCYPHONE",phone);
        db.insert("USERSCONTACT",null,uservalues);
    }
}
