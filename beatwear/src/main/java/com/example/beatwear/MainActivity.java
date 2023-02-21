package com.example.beatwear;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends WearableActivity {
    int LAUNCH_SECOND_ACTIVITY = 1;
    BluetoothDevice mBTDevice;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView= (TextView) findViewById(R.id.textView);


        // Enables Always-on
        setAmbientEnabled();
    }

    public void goToRetrieveDevices(View view){
        Intent intent = new Intent(MainActivity.this, RetrieveDevices.class);
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
    }

    public void goToHeartRate(View view){
        Intent intent = new Intent(MainActivity.this, RetrieveHeart.class);
        intent.putExtra("btdevice", mBTDevice);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(data != null) {
                if (resultCode == Activity.RESULT_OK) {
                    mBTDevice = data.getParcelableExtra("btdevice");
                    mTextView.setText(mBTDevice.getName());
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            }

        }
    } //onActivityResult
    
}