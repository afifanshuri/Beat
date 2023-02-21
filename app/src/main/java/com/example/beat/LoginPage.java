package com.example.beat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {
    Cursor cursor;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }

    public void goToRetrieveHeartRate(View view){
        String usernamedb,passworddb;
        EditText username = (EditText) findViewById(R.id.usernameLoginEditText);
        EditText password = (EditText) findViewById(R.id.passwordLoginEditText) ;
        String realusername = username.getText().toString();
        String realpassword = password.getText().toString();
        try {
            SQLiteOpenHelper beathelper = new UserDatabase(this);
            db = beathelper.getReadableDatabase();
            cursor = db.query("USERS",
                    new String[]{"_id", "USERNAME", "PASSWORD"},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                int userID = cursor.getInt(0);
                usernamedb = cursor.getString(1);
                passworddb = cursor.getString(2);
                if (realusername.equals(usernamedb) && realpassword.equals(passworddb)) {
                    Intent intent = new Intent(this, HomePage.class);
                    intent.putExtra(HomePage.EXTRA_USERID, userID);
                    startActivity(intent);
                }

            }
            cursor.close();
            db.close();
        }catch(SQLException e){
            Toast toast = Toast.makeText(this, "Database unavailable @Profile", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
