package com.example.beat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterPage extends AppCompatActivity {

    private long userID;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
    }



    public void successRegister(View view){
        EditText fullname = (EditText) findViewById(R.id.fullname_editext);
        EditText username = (EditText) findViewById(R.id.username_editext);
        EditText password = (EditText) findViewById(R.id.password_editext);
        EditText phone = (EditText) findViewById(R.id.phone_editext);

        String sFullname = fullname.getText().toString();
        String sUsername = username.getText().toString();
        String sPassword = password.getText().toString();
        String sPhone = phone.getText().toString();

        Boolean empty = true;

        while(empty==true) {
            if (sUsername.matches("")) {
                Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                empty=true;
                return;
            }

            else if (sUsername.matches("")) {
                Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                empty=true;
                return;
            }

            else if (sUsername.matches("")) {
                Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                empty=true;
                return;
            }

            else if (sUsername.matches("")) {
                Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                empty=true;
                return;
            }
            else
                empty=false;
        }

        try {
            SQLiteOpenHelper beathelper = new UserDatabase(this);
            db = beathelper.getWritableDatabase();

            ContentValues userdetails = new ContentValues();
            userdetails.put("NAME", fullname.getText().toString());
            userdetails.put("USERNAME", username.getText().toString());
            userdetails.put("PASSWORD", password.getText().toString());
            userdetails.put("PHONE", phone.getText().toString());
            db.insert("USERS", null, userdetails);
            db.close();
            Intent intent = new Intent(RegisterPage.this, RegisterSecondPage.class);
            startActivity(intent);
        }
        catch(SQLException e){
            Toast toast = Toast.makeText(this, "Database unavailable @Register", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}

