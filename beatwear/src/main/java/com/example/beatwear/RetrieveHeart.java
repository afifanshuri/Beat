package com.example.beatwear;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

public class RetrieveHeart extends WearableActivity implements SensorEventListener {

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final String TAG = "RetrieveHeart";
    private static final int PERMISSIONS_REQUEST_BODY_SENSORS = 1;
    BluetoothDevice mBTDevice;

    private TextView mTextViewHeart;
    Button stopretrieve,startbtn;
    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    SensorEventListener sensorEventListener;
    BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothManager manager;
    int GATT = BluetoothProfile.GATT_SERVER;
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothServiceConnection mBluetoothConnection;
    TextView statustext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_heart);
        mBluetoothConnection = new BluetoothServiceConnection(this);
        statustext = (TextView) findViewById(R.id.statustext);
        stopretrieve= (Button) findViewById(R.id.stopbtn);

        mBTDevice = getIntent().getExtras().getParcelable("btdevice");

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));


        if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BODY_SENSORS},
                    PERMISSIONS_REQUEST_BODY_SENSORS);
        } else {
            mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
            mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        };

        statustext.setText(mBTDevice.getAddress() +" "+ mBTDevice.getName());

        Button startbtn = (Button)findViewById(R.id.startbtn);
        Button stopbtn = (Button)findViewById(R.id.stopbtn);


        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSensorManager.unregisterListener(RetrieveHeart.this, mHeartRateSensor);
                if(mSensorManager==null){
                    Log.i(TAG, "LISTENER UNREGISTERED.");
                    mTextViewHeart.setText("sensor turned off");
                }
                Intent intent = new Intent(RetrieveHeart.this, MainActivity.class);
                startActivity(intent);
            }
        });

        if (bluetoothadapter != null && !bluetoothadapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSensorManager.registerListener(RetrieveHeart.this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
                startConnection();
            }
        });



    }




    //method to display toasts
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
        }
    };

    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device,uuid);
        statustext.setText("Retrieving in progress..");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            byte[] bytes = msg.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytes);
            Log.d(TAG, msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

}