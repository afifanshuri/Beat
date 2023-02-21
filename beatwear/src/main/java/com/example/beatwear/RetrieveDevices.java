package com.example.beatwear;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class RetrieveDevices extends WearableActivity{

    BluetoothAdapter bluetoothadapter;
    BluetoothDevice mBTDevice;

    private TextView mTextView;
    Button pairbtn, descbtn, startbtn;
    ListView bondedDevices;
    Set<BluetoothDevice> pairedDevices;
    ArrayList<String> paireddevices = new ArrayList<String>();
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private String TAG = "RetrieveDevices";
    private Object RetrieveHeart;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_devices);
        mTextView = (TextView) findViewById(R.id.text);
        pairbtn = (Button) findViewById(R.id.button);
        descbtn = (Button) findViewById(R.id.button2);
        bondedDevices = (ListView) findViewById(R.id.listpaireddevices);
        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();


        pairbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevices = bluetoothadapter.getBondedDevices();
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        paireddevices.add(device.getName() + "\n" + device.getAddress());
                    }
                    bondedDevices.setAdapter(new ArrayAdapter<String>(
                            RetrieveDevices.this,
                            android.R.layout.simple_list_item_1,
                            paireddevices));
                    ArrayList<BluetoothDevice> pairedDevicesArray = new ArrayList<BluetoothDevice>();
                    pairedDevicesArray.addAll(pairedDevices);
                    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> listView,
                                                View itemView,
                                                int position,
                                                long id) {
                            for (int x = 0; x <= pairedDevicesArray.size(); x++) {
                                if (position == x) {
                                    //conenct to the paired device chosen
                                    mBTDevice = pairedDevicesArray.get(x);
                                    Intent intent = getIntent();
                                    intent.putExtra("btdevice", mBTDevice);
                                    setResult(Activity.RESULT_OK,intent);
                                    finish();
                                }
                            }
                        }
                    };
                    bondedDevices.setOnItemClickListener(itemClickListener);

                }
            }
        });

        descbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothadapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    //calls another activity (bluetooth defined activity) to get results
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
            }
        });


    }
}