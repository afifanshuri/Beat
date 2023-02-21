package com.example.beat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
    }

    public void goToLogin(View view){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public void goToRegister(View view){
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }


}