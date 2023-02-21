package com.example.beat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;


public class RetrieveHeartRate extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 123;
    BluetoothDevice mBTDevice;
    UUID MY_UUID_INSECURE;
    BluetoothServiceConnection mBluetoothConnection;
    private String TAG="RETRIEVE_HEART_RATE_ACTIVITY";
    int id, age,sex,cp, restecg, slope,ca,thal,fbs,chol,trestbps,exang;
    double thalach, oldpeak;
    TextView heartresult, heartresult2;
    String currentHeartRate, emergencyContact;
    String phone;
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    boolean userOK;
    Interpreter interpreter;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_heart_rate);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
        id=getIntent().getExtras().getInt("userID",0);
        heartresult=(TextView)findViewById(R.id.heartresult);
        heartresult2=(TextView)findViewById(R.id.heartresult2);
        userOK=true;

        //Permission to send SMS
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }

        try {
            SQLiteOpenHelper beathelper = new UserMedicalInfo(this);
            SQLiteDatabase db = beathelper.getReadableDatabase();
            Cursor cursor = db.query("USERSMEDICAL",
                    new String[]{"_id", "AGE","SEX","CP","TRESTBPS","CHOL","FBS","RESTECG","THALACH","EXANG","OLDPEAK","SLOPE",
                            "CA","THAL","EMERGENCY_CONTACT"},
                    "_id = ?",
                    new String[]{Integer.toString(id)}, null, null, null);

            if (cursor.moveToFirst()) {
                age = cursor.getInt(1);
                sex = cursor.getInt(2);
                cp = cursor.getInt(3);
                trestbps = cursor.getInt(4);
                chol = cursor.getInt(5);
                fbs = cursor.getInt(6);
                restecg = cursor.getInt(7);
                thalach = cursor.getInt(8);
                exang = cursor.getInt(9);
                oldpeak = cursor.getInt(10);
                slope = cursor.getInt(11);
                ca = cursor.getInt(12);
                thal = cursor.getInt(13);
                emergencyContact = cursor.getString(14);
            }
            cursor.close();
            db.close();

        }catch(SQLException e){
            Toast toast = Toast.makeText(this, "Database unavailable @RetrieveHeartRate1", Toast.LENGTH_SHORT);
            toast.show(); 
        }

        heartresult.setText("Click 'start retrieving' on the smartwatch app to start monitoring heart rate");

        StringBuilder phoneAppend = new StringBuilder();
        phoneAppend.append("0").append(emergencyContact);
        phone = phoneAppend.toString();

        this.registerReceiver(mReceiver, filter);

        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //convert the tflite model file into MappedByteBuffer format.
    // It is an optimized format that will help us to increase efficiency and speed.
    private MappedByteBuffer loadModelFile() throws IOException
    {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("heart.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,len);
    }

    //insert inputs into float array and feed into the model
    // get the output with the help of the model.
    public float doInference(int val)
    {
        float[] input = new float[13];
        input[0] = Float.parseFloat(String.valueOf(age));
        input[1] = Float.parseFloat(String.valueOf(sex));
        input[2] = Float.parseFloat(String.valueOf(cp));
        input[3] = Float.parseFloat(String.valueOf(trestbps));
        input[4] = Float.parseFloat(String.valueOf(chol));
        input[5] = Float.parseFloat(String.valueOf(fbs));
        input[6] = Float.parseFloat(String.valueOf(restecg));
        input[7] = Float.parseFloat(String.valueOf(val));
        input[8] = Float.parseFloat(String.valueOf(exang));
        input[9] = Float.parseFloat(String.valueOf(oldpeak));
        input[10] = Float.parseFloat(String.valueOf(slope));
        input[11] = Float.parseFloat(String.valueOf(ca));
        input[12] = Float.parseFloat(String.valueOf(thal));
        float[][] output = new float[1][2];

        interpreter.run(input,output);
        return output[0][1];
    }

    //print toast messages
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    //receive heart rate from wearOS
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                heartresult.setText("Watch has disconnected. Please reconnect again");
                heartresult2.setText(" ");
            }

            //retrieve current heart rate from watch app
            currentHeartRate = intent.getStringExtra("theMessage");
            int newcurrentHeartRate = Integer.parseInt(currentHeartRate);
            heartresult2.setText("Current heart rate: "+(newcurrentHeartRate+20)+ "BPM");


            //send current heart rate to model
             float result=doInference(newcurrentHeartRate+20);
            heartresult.setText(""+result);

                //check whether current heart rate > max heart rate
                if(result>0.5){
                    heartresult.setText("User Health Status: AT RISK"+ "\n" + "Probability of risk: "+ result);

                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                     } else {
                     //deprecated in API 26
                     v.vibrate(500);
                     }

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RetrieveHeartRate.this);
                    builder1.setMessage("Abnormality has been detected. Press YES to confirm that you are okay. Press NO or ignore if you need assistance");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    userOK=true;
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    try {

                                        if (ContextCompat.checkSelfPermission(RetrieveHeartRate.this,
                                                Manifest.permission.SEND_SMS)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            //if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                            if (shouldShowRequestPermissionRationale(
                                                    Manifest.permission.SEND_SMS)) {
                                            } else {
                                                //ActivityCompat.requestPermissions(getActivity(),
                                                requestPermissions(
                                                        new String[]{Manifest.permission.SEND_SMS},
                                                        MY_PERMISSIONS_REQUEST_SEND_SMS);
                                            }
                                        } else {
                                            Log.i("smsB", "Have permission... send the sms now");
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage(phone, null, "Abnormality has been detected from this owner's heart rate. Please get assistance immediately.", null, null);
                                            Toast.makeText(RetrieveHeartRate.this, "SMS sent.",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                    }catch (Exception e){
                                        Toast.makeText(getApplicationContext(),"Message not Sent",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    try {
                        SQLiteOpenHelper beathelper = new UserMedicalInfo(RetrieveHeartRate.this);
                        SQLiteDatabase db = beathelper.getWritableDatabase();
                        ContentValues uservalues = new ContentValues();
                        uservalues.put("STATUS","AT RISK");
                        uservalues.put("RECENTHEARTRATE",currentHeartRate);
                        db.update("USERSMEDICAL",
                                uservalues,
                                "_id = ?",
                                new String[] {Integer.toString(id)});
                        db.close();
                    }catch(SQLException e){
                        Toast toast = Toast.makeText(RetrieveHeartRate.this, "Database unavailable @RetrieveHeartRate2", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    //heartresult.setText("Current max heart rate "+Double.toString(maxheart)+ "BPM"+'\n'+"ABNORMALITY DETECTED!");
                }


                //if current heart rate normal
                else{
                    heartresult.setText("User Health Status: NORMAL"+ "\n" + "Probability of risk: "+ result);
                    try {
                        SQLiteOpenHelper beathelper = new UserMedicalInfo(RetrieveHeartRate.this);
                        SQLiteDatabase db = beathelper.getWritableDatabase();
                        ContentValues uservalues = new ContentValues();
                        uservalues.put("STATUS","NORMAL");
                        uservalues.put("RECENTHEARTRATE",currentHeartRate);
                        db.update("USERSMEDICAL",
                                uservalues,
                                "_id = ?",
                                new String[] {Integer.toString(id)});
                        db.close();
                    }catch(SQLException e){
                        Toast toast = Toast.makeText(RetrieveHeartRate.this, "Database unavailable @RetrieveHeartRate2", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }

                // Releases model resources if no longer used.
                /**model.close();


            } catch (IOException e) {
                // TODO Handle the exception
            }**/



        }
    };


}