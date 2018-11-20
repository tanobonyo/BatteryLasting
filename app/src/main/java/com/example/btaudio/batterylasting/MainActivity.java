package com.example.btaudio.batterylasting;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Long.getLong;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private static final int REQUEST_ENABLE_BT = 1000;
    //private Button btnBluetooth = (Button)findViewById(R.id.btnBluetooth);
    private BluetoothAdapter mBluetoothAdapter;

    // Array of strings...
    private ArrayList<BtDevice> deviceArray;
    private BtDevice lastBtDeviceConnected;
    private ListView listView;
    private BtDeviceAdapter myBtDeviceAdapter;
    private Chronometer simpleChronometer;

    private Handler handlerBt = new Handler();
    private  Handler handlerRefresh = new Handler();

    private Runnable runnableBt;
    private Runnable runnableRefresh;

    private long timeWhenStopped = 0;
    private boolean needtostart = false;

    BluetoothProfile.ServiceListener mProfileListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the application context
        mContext = getApplicationContext();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer

        listView = (ListView) findViewById(R.id.lvwDevices);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                BtDevice objeto = (BtDevice) listView.getItemAtPosition(position);
                Toast.makeText(mContext,objeto.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext,"Se resetea el contador", Toast.LENGTH_SHORT).show();
                resetPreferenceValue(objeto.getAddress());
            }
        });

        Button boton = (Button) findViewById(R.id.btnBluetooth);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerBt.postDelayed(runnableBt, 1000);
                handlerBt.postDelayed(runnableRefresh, 1000);
            }
        });

        runnableBt = new Runnable() {
            public void run() {
                showBoundedDevices();
                handlerBt.postDelayed(this, 5000);
            }
        };

        runnableRefresh = new Runnable() {
            public void run() {
                if (myBtDeviceAdapter != null)
                    myBtDeviceAdapter.notifyDataSetChanged();
                handlerRefresh.postDelayed(this, 1000);
            }
        };

        //Listenner
        mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    BluetoothA2dp btA2dp = (BluetoothA2dp) proxy;
                    List<BluetoothDevice> a2dpConnectedDevices = btA2dp.getConnectedDevices();
                    if (a2dpConnectedDevices.size() != 0) {
                        for (BluetoothDevice device : a2dpConnectedDevices) {
                            for (BtDevice myDevice: deviceArray) {
                                if (myDevice.getName().equals(device.getName())) {
                                    myDevice.setConnected(true);
                                    myDevice.setTime(simpleChronometer);
                                    myDevice.setElapsed(readPreferenceValue(myDevice.getAddress()) );
                                    lastBtDeviceConnected = myDevice;
                                    if (needtostart == true) {
                                        timeWhenStopped = readPreferenceValue(myDevice.getAddress());
                                        simpleChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
                                        simpleChronometer.start();
                                        needtostart = false;
                                    }
                                    myBtDeviceAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, btA2dp);
                }
                if (profile == BluetoothProfile.HEADSET) {
                    BluetoothHeadset btHeadset = (BluetoothHeadset) proxy;
                    List<BluetoothDevice> headsetConnectedDevices = btHeadset.getConnectedDevices();
                    if (headsetConnectedDevices.size() != 0) {
                        for (BluetoothDevice device : headsetConnectedDevices) {
                            for (BtDevice myDevice: deviceArray) {
                                if (myDevice.getName().equals(device.getName())) {
                                    myDevice.setConnected(true);
                                    myDevice.setTime(simpleChronometer);
                                    myDevice.setElapsed(readPreferenceValue(myDevice.getAddress()) );
                                    lastBtDeviceConnected = myDevice;
                                    if (needtostart == true) {
                                        timeWhenStopped = readPreferenceValue(myDevice.getAddress());
                                        simpleChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
                                        simpleChronometer.start();
                                        needtostart = false;
                                    }
                                    myBtDeviceAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, btHeadset);
                }
            }

            public void onServiceDisconnected(int profile) {
                // TODO
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lastBtDeviceConnected != null) {
            timeWhenStopped = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
            savePreferenceValue(lastBtDeviceConnected.getAddress());
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    private void showBoundedDevices() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent;
            enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            deviceArray = new ArrayList<BtDevice>();

            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    // super.onSaveInstanceState(savedInstanceState);

                    if (!pairedDevices.equals(device.getName())) {
                        BtDevice newDevice = new BtDevice(device.getName(), device.getAddress(), readPreferenceValue(device.getAddress()));
                        if ((lastBtDeviceConnected != null) && (newDevice.equals(lastBtDeviceConnected)))
                            newDevice.setTime(simpleChronometer);

                        deviceArray.add(newDevice);
                    }
                }
            }
            myBtDeviceAdapter = new BtDeviceAdapter(this, deviceArray);
            listView.setAdapter(myBtDeviceAdapter);

            // Check A2DP connected devices
            mBluetoothAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.A2DP);
            mBluetoothAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.HEADSET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_OK){
                Toast.makeText(mContext,"Bluetooth Habilitado", Toast.LENGTH_SHORT).show();
                showBoundedDevices();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mContext,"Bluetooth no habilitado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected long readPreferenceValue(String name) {
        SharedPreferences preferences = getSharedPreferences("SAVEVALUES", Context.MODE_PRIVATE);
//        return preferences.getLong(name, SystemClock.elapsedRealtime() - timeWhenStopped);
        return preferences.getLong(name, SystemClock.elapsedRealtime() - timeWhenStopped);
    }

    protected void savePreferenceValue(String name) {
        SharedPreferences preferences = getSharedPreferences("SAVEVALUES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(name, timeWhenStopped);
        editor.apply();
    }

    protected void resetPreferenceValue(String name){
        SharedPreferences preferences = getSharedPreferences("SAVEVALUES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(name, 0);
        editor.apply();
        timeWhenStopped = 0; //Count reset and initialize chronometer
        needtostart = true;
    }
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(mContext,"STATE_OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(mContext,"STATE_TURNING_OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(mContext,"STATE_ON", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(mContext,"STATE_TURNING_ON", Toast.LENGTH_SHORT).show();
                        break;
                }
                Toast.makeText(mContext,String.valueOf(state), Toast.LENGTH_SHORT).show();
            }

            //filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            //filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            //filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                Toast.makeText(mContext,"ACTION_ACL_CONNECTED", Toast.LENGTH_SHORT).show();
                needtostart = true;
//                timeWhenStopped = readPreferenceValue(lastBtDeviceConnected.getName());
//                simpleChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
//                simpleChronometer.start();
            }
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)) {
                Toast.makeText(mContext,"ACTION_ACL_DISCONNECT_REQUESTED", Toast.LENGTH_SHORT).show();
            }
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Toast.makeText(mContext,"ACTION_ACL_DISCONNECTED", Toast.LENGTH_SHORT).show();

             //   savedInstanceState.putLong("Time", simpleChronometer.getBase());
              //  timerTime.setBase(SystemClock.elapsedRealtime());
            //    simpleChronometer.setBase(SystemClock.elapsedRealtime());
                simpleChronometer.stop();
                timeWhenStopped = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
    //            SharedPreferences preferences = getSharedPreferences("SAVEVALUES", Context.MODE_PRIVATE);
      //          SharedPreferences.Editor editor = preferences.edit();
        //        editor.putLong("PRUEBA", timeWhenStopped);
          //      editor.apply();
                savePreferenceValue(lastBtDeviceConnected.getAddress());

            }
        }
    };
}