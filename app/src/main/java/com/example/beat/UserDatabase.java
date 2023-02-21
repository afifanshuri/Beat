package com.example.beat;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabase extends SQLiteOpenHelper {


    private static final String DB_NAME = "beat"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    UserDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db,0,DB_VERSION);
    }

    //code to call the function to update the database in case the application has an outdated database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    //code to update database
    public void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion<1) {
            db.execSQL("CREATE TABLE USERS (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "NAME TEXT,"
                    + "USERNAME TEXT,"
                    + "PASSWORD TEXT,"
                    + "PHONE INTEGER);");
        }
    }

    //code to insert user details in the database
    public void insertUserDetails(SQLiteDatabase db, String name,
                                  String username, String password, int phone, int resourceId){
        ContentValues uservalues = new ContentValues();
        uservalues.put("NAME", name);
        uservalues.put("USERNAME",username);
        uservalues.put("PASSWORD",password);
        uservalues.put("PHONE",phone);
        db.insert("USERS",null,uservalues);
    }

}
