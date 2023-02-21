package com.example.beat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserMedicalInfo extends SQLiteOpenHelper {

    private static final String DB_NAME = "beatmedical"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    UserMedicalInfo(Context context) {
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
            db.execSQL("CREATE TABLE USERSMEDICAL (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "AGE INTEGER,"
                    + "SEX INTEGER,"
                    + "CP INTEGER,"
                    + "TRESTBPS INTEGER,"
                    + "CHOL INTEGER,"
                    + "FBS INTEGER,"
                    + "RESTECG INTEGER,"
                    + "THALACH DOUBLE,"
                    + "EXANG INTEGER,"
                    + "OLDPEAK DOUBLE,"
                    + "SLOPE INTEGER,"
                    + "CA INTEGER,"
                    + "THAL INTEGER,"
                    + "EMERGENCY_CONTACT STRING,"
                    + "RECENTHEARTRATE INTEGER,"
                    + "STATUS STRING);");
        }
    }

    public void insertUserMedicalDetails(SQLiteDatabase db, int age, int sex, int cp,
                                         int restecg, int thalach, int slope, int trestbps,
                                         int chol, int fbs, int exang, double oldpeak,
                                         int ca, int thal, String status, int recentheartrate,
                                         String emergencycontact){
        ContentValues uservalues = new ContentValues();
        uservalues.put("AGE", age);
        uservalues.put("SEX",sex);
        uservalues.put("CP",cp);
        uservalues.put("TRESTBPS",trestbps);
        uservalues.put("CHOL",chol);
        uservalues.put("FBS",fbs);
        uservalues.put("RESTECG",restecg);
        uservalues.put("THALACH",thalach);
        uservalues.put("EXANG",exang);
        uservalues.put("OLDPEAK",oldpeak);
        uservalues.put("SLOPE",slope);
        uservalues.put("CA",ca);
        uservalues.put("THAL",thal);
        uservalues.put("EMERGENCY_CONTACT",emergencycontact);
        uservalues.put("RECENTHEARTRATE",recentheartrate);
        uservalues.put("STATUS",status);
        db.insert("USERSMEDICAL",null,uservalues);
    }

}
