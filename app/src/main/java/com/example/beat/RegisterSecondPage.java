package com.example.beat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterSecondPage extends AppCompatActivity {

    TextView test, emergencycontact;
    EditText age, trestbps, chol, thalach, oldpeak;
    Spinner sex, cp, exang, slope, fbs,ca, thal, restecg;
    private SQLiteDatabase db;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_page);
        Intent intent = new Intent();
        userID= intent.getIntExtra("userid",0);

        age = (EditText) findViewById(R.id.ageedittext);
        trestbps = (EditText) findViewById(R.id.trestbpsedittext);
        chol = (EditText) findViewById(R.id.choledittext);
        oldpeak = (EditText) findViewById(R.id.oldpeakedittext);
        sex = (Spinner) findViewById(R.id.sexspinner);
        cp = (Spinner) findViewById(R.id.cpspinner);
        ca = (Spinner) findViewById(R.id.caspinner);
        exang = (Spinner) findViewById(R.id.exangspinner);
        fbs = (Spinner) findViewById(R.id.fbsspinner);
        thal = (Spinner) findViewById(R.id.thalspinner);
        slope = (Spinner) findViewById(R.id.slopespinner);
        restecg = (Spinner) findViewById(R.id.restecgspinner);
        emergencycontact = (EditText) findViewById(R.id.emergencycontactedittext);
    }

    public void goToLogin(View view){
        try {
            SQLiteOpenHelper beathelper = new UserMedicalInfo(this);
            db = beathelper.getWritableDatabase();
            ContentValues uservalues = new ContentValues();
            int spinneritem = sex.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("SEX",0);
                    break;
                case 1:
                    uservalues.put("SEX",1);
                    break;

            }

            uservalues.put("AGE", Integer.parseInt(age.getText().toString()));
            uservalues.put("TRESTBPS",Integer.parseInt(trestbps.getText().toString()));
            uservalues.put("CHOL",Integer.parseInt(chol.getText().toString()));
            uservalues.put("OLDPEAK",Double.parseDouble(oldpeak.getText().toString()));
            uservalues.put("EMERGENCY_CONTACT",emergencycontact.getText().toString());

            spinneritem = ca.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("CA",0);
                    break;
                case 1:
                    uservalues.put("CA",1);
                    break;
                case 2:
                    uservalues.put("CA",2);
                    break;
                case 3:
                    uservalues.put("CA",3);
                    break;

            }

            spinneritem = fbs.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("FBS",1);
                    break;
                case 1:
                    uservalues.put("FBS",0);
                    break;
            }

            spinneritem = thal.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("THAL",0);
                    break;
                case 1:
                    uservalues.put("THAL",1);
                    break;
                case 2:
                    uservalues.put("THAL",2);
                    break;

            }

            spinneritem = cp.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("CP",0);
                    break;
                case 1:
                    uservalues.put("CP",1);
                    break;
                case 2:
                    uservalues.put("CP",2);
                    break;
                case 3:
                    uservalues.put("CP",3);
                    break;
            }

            spinneritem = restecg.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("RESTECG",0);
                    break;
                case 1:
                    uservalues.put("RESTECG",1);
                    break;
                case 2:
                    uservalues.put("RESTECG",2);
                    break;
            }


            spinneritem = slope.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("SLOPE",0);
                    break;
                case 1:
                    uservalues.put("SLOPE",1);
                    break;
                case 2:
                    uservalues.put("SLOPE",2);
                    break;
            }

            spinneritem = exang.getSelectedItemPosition();
            switch(spinneritem){
                case 0:
                    uservalues.put("SLOPE",0);
                    break;
                case 1:
                    uservalues.put("SLOPE",1);
                    break;
            }


            db.insert("USERSMEDICAL",null,uservalues);
            db.close();
            Intent intent = new Intent(RegisterSecondPage.this, MainPage.class);
            startActivity(intent);
        }
        catch(SQLException e){
            Toast toast = Toast.makeText(this, "Database unavailable @RegisterSecond", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}