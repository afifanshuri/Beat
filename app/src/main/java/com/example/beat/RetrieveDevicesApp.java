package com.example.beat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class  RetrieveDevicesApp extends AppCompatActivity {

    BluetoothServiceConnection mBluetoothConnection; // object for bluetooth connection
    private static final UUID MY_UUID_INSECURE = // UUID for Bluetooth service
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final int REQUEST_ENABLE_BT = 0; // request code for enabling bluetooth
    private static final int REQUEST_DISCOVER_BT= 1; // request code for discovering devices
    private final int REQUEST_ACCESS_COARSE_LOCATION = 0xb01; // request code for accessing coarse location

    TextView founddevices, BTStatus, test; // text views for displaying bluetooth device info and status
    Switch BTState; // switch for turning on and off bluetooth
    Button discoverableBTBtn, pairBTBtn; // buttons for discovering and pairing bluetooth devices
    BluetoothAdapter bluetoothadapter; // bluetooth adapter for communicating with other bluetooth devices
    ListView discoveredDevices,bondedDevices; // list views for displaying discovered and paired devices
    ArrayList<String> retrieveddevices = new ArrayList<String>(); // array list for storing discovered device info
    ArrayList<BluetoothDevice> retrieveddevicesbluetooth = new ArrayList<BluetoothDevice>(); // array list for storing discovered bluetooth devices
    ArrayList<String> paireddevices = new ArrayList<String>(); // array list for storing paired device info
    Set<BluetoothDevice> pairedDevices; // set for storing paired bluetooth devices
    BluetoothDevice mBTDevice; // selected bluetooth device
    StringBuilder messages; // string builder for displaying received messages
    private String TAG="Afif"; // tag for debugging
    int id; // id for tracking device connections
    private Object RetrieveHeartRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_devices_app);
        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // toolbar for displaying app title
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar(); // action bar for displaying back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        // code below is to avoid app crash because in the parent activity, it asks for intent from the login page
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        discoveredDevices=(ListView)findViewById(R.id.retrievedDevicesList); // list view for displaying discovered devices
        bondedDevices=(ListView)findViewById(R.id.pairedDevicesList); // list view for displaying paired devices
        BTState = (Switch)findViewById(R.id.BTStateBtn); // switch for turning on and off bluetooth
        discoverableBTBtn=(Button)findViewById(R.id.discBtnBT); // button for making device discoverable
        pairBTBtn=(Button)findViewById(R.id.pairBtnBT); // button for pairing with other devices
        test = (TextView)findViewById(R.id.pairedDevicesText); // text view for displaying paired device info

        // LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        // 1) initialize the bluetooth adapter to get started
        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();

        // 2) check whether device has a working bluetooth
        if (bluetoothadapter == null){
            showToast("Bluetooth is not available");
        }
        else
            showToast("Bluetooth is available");

        // set switch for turning on and off bluetooth
        if(bluetoothadapter.isEnabled()) {
            BTState.setChecked(true);
            showToast("Bluetooth is turned on");
        }


        // Set an OnCheckedChangeListener to the BTState switch button
        BTState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If the switch is turned on
                if(BTState.isChecked()){

                    if(!bluetoothadapter.isEnabled()) {

                        showToast("Turning on bluetooth..");
                        // Create a new intent to enable Bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        // Start an activity to enable Bluetooth
                        startActivityForResult(intent, REQUEST_ENABLE_BT);

                        showToast("Bluetooth is turned on");
                    }
                    // If the Bluetooth adapter is already enabled
                    else{

                        showToast("Bluetooth is already turned on!");
                    }
                }
                // If the switch is turned off
                else{
                    // If the Bluetooth adapter is enabled
                    if(bluetoothadapter.isEnabled()){

                        showToast("Turning off bluetooth..");
                        // Disable the Bluetooth adapter
                        bluetoothadapter.disable();

                        showToast("Bluetooth is turned off");
                        // Clear the list of retrieved devices
                        retrieveddevices.clear();
                        // Set the adapter for the discoveredDevices ListView to display the retrieved devices list
                        discoveredDevices.setAdapter(new ArrayAdapter<String>(
                                RetrieveDevicesApp.this,
                                android.R.layout.simple_list_item_1,
                                retrieveddevices));
                    }
                    // If the Bluetooth adapter is already disabled
                    else

                        showToast("Bluetooth is already turned off");
                }
            }
        });

        // Get a list of paired devices
        pairedDevices = bluetoothadapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address of each paired device to an array adapter to display in a ListView
                paireddevices.add(device.getName() + "\n" + device.getAddress());
            }
            // Set the adapter for the bondedDevices ListView to display the paired devices list
            bondedDevices.setAdapter(new ArrayAdapter<String>(
                    RetrieveDevicesApp.this,
                    android.R.layout.simple_list_item_1,
                    paireddevices));
            // Create an array list of paired devices
            ArrayList<BluetoothDevice> pairedDevicesArray = new ArrayList<BluetoothDevice>();
            // Add all paired devices to the array list
            pairedDevicesArray.addAll(pairedDevices);
            // Set an OnItemClickListener for the bondedDevices ListView
            AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> listView,
                                        View itemView,
                                        int position,
                                        long id) {

                    for (int x=0;x<pairedDevicesArray.size();x++) {
                        // If the selected item in the ListView corresponds to the current device in the array list
                        if(position==x) {
                            // Connect to the selected paired device
                            try{
                                // Set the mBTDevice variable to the selected device
                                mBTDevice = pairedDevicesArray.get(x);
                                // Create a new BluetoothServiceConnection object
                                mBluetoothConnection = new BluetoothServiceConnection((Context) RetrieveHeartRate);

                                showToast("Paired with "+mBTDevice.getName()+". Click start retrieving button to start monitoring heart rate.");
                            }
                            catch (Exception e){
                                showToast("Cannot connect to "+mBTDevice.getName());
                            }

                        }
                    }
                }
        };
            bondedDevices.setOnItemClickListener(itemClickListener);
        }

        discoverableBTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetoothadapter.isDiscovering()){
                    showToast("Making your device discoverable...");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

                    //calls another activity (bluetooth defined activity) to get results
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
            }
        });

        pairBTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bluetoothadapter.isDiscovering()) {
                    bluetoothadapter.cancelDiscovery();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    switch (ContextCompat.checkSelfPermission(RetrieveDevicesApp.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        case PackageManager.PERMISSION_DENIED:
                            ActivityCompat.requestPermissions(RetrieveDevicesApp.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_ACCESS_COARSE_LOCATION);

                            break;
                        case PackageManager.PERMISSION_GRANTED:
                            bluetoothadapter.startDiscovery();
                            //send an intent in case a remote device is found
                            IntentFilter intentDiscovery = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                            //intentDiscovery.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                            registerReceiver(receiver, intentDiscovery);

                            AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
                                public void onItemClick(AdapterView<?> listView,
                                                        View itemView,
                                                        int position,
                                                        long id) {
                                    for (int x=0;x<retrieveddevicesbluetooth.size();x++) {
                                        if(position==x) {
                                            BluetoothDevice currentdevice = retrieveddevicesbluetooth.get(x);
                                            try {
                                                createBond(currentdevice);
                                            } catch (Exception e) {
                                               showToast("fail to pair");
                                            }

                                        }
                                    }

                                }
                            };
                            discoveredDevices.setOnItemClickListener(itemClickListener);

                            break;
                    }
                }
            }
        });

    }

    //method for when a remote device is found from startdiscovery()
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //getaction() is to retrieve the action that was sent with the intent (eg:ACTION_FOUND)
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                ArrayList<String> temp;
                ArrayList<BluetoothDevice> temp2;
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                //update discovered devices list
                temp = (ArrayList<String>)retrieveddevices.clone();
                temp2 = (ArrayList<BluetoothDevice>) retrieveddevicesbluetooth.clone();
                retrieveddevices.clear();
                retrieveddevicesbluetooth.clear();
                retrieveddevices.add(device.getName() + "\n" + device.getAddress());
                retrieveddevicesbluetooth.add(device);
                retrieveddevices.addAll(temp);
                retrieveddevicesbluetooth.addAll(temp2);

                //setting the listview with discovered devices
                discoveredDevices.setAdapter(new ArrayAdapter<String>(
                        RetrieveDevicesApp.this,
                        android.R.layout.simple_list_item_1,
                        retrieveddevices));
            }
            }
    };


    //a method called by startActivityResult to return the results from calling the activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode==RESULT_OK)
                    showToast("Bluetooth is turned on!");
                else
                    showToast("Couldn't turn on bluetooth");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //method to avoid app crash bcs of intent in the parent activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //method to display toasts
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    //method to rquest permission to search for nearby devices
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bluetoothadapter.startDiscovery();
                } else {
                    //exit application or do the needful
                }
                return;
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothadapter.isDiscovering()){
        bluetoothadapter.cancelDiscovery();
        unregisterReceiver(receiver);}
    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);

        pairedDevices.clear();
        pairedDevices = bluetoothadapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                paireddevices.add(device.getName() + "\n" + device.getAddress());
            }
        }
        bondedDevices.setAdapter(new ArrayAdapter<String>(
                RetrieveDevicesApp.this,
                android.R.layout.simple_list_item_1,
                paireddevices));
        return returnValue.booleanValue();
    }


    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device,uuid);
    }


}



