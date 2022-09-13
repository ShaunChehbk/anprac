package com.example.mousecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.mousecontrol.ConsoleEmu;
import com.example.mousecontrol.MouseMgr;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String DEVICE_NAME = "HUAWEI Mouse WYN";

    private ListView listView;
    public ConsoleEmu console;
    private MouseMgr mouseMgr;
    private Button buttonCheck;
    private Button buttonSendSeq;
    private Button resetConsole;

    BluetoothManager bluetoothManager;

    private ScalerRecv scalerRecv;

    BroadcastReceiver inputReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mouseMgr.writeSeq(new String[] {
                    "9f0101020101",
                    "9f0201020101",
                    "9f0204020101"
            });
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("输入源检测")
                    .setMessage("检测到新的输入源")
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "confirm", Toast.LENGTH_SHORT).show();
                                    mouseMgr.writeSingle("9f06010a01080000100000001000");
                                }
                            })
                    .setCancelable(false)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonCheck = (Button) findViewById(R.id.check_gatt);
        buttonSendSeq = (Button) findViewById(R.id.send_seq);
        resetConsole = (Button) findViewById(R.id.clean_ter);
        listView = (ListView) findViewById(R.id.console);

        console = new ConsoleEmu(this,
                                    android.R.layout.simple_list_item_1,
                                    new ArrayList<>());
        mouseMgr = new MouseMgr(this, console);


        listView.setAdapter(console);

        if (scalerRecv == null) {
            scalerRecv = new ScalerRecv(console, mouseMgr);
        }
        //registerReceiver(scalerRecv, new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED"));
        registerReceiver(inputReceiver, new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED"));

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        List<BluetoothDevice> bluetoothDeviceList = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        for (BluetoothDevice bluetoothDevice : bluetoothDeviceList) {
            console.Log(TAG, "Device Name: " + bluetoothDevice.getName());
            mouseMgr.setDevice(bluetoothDevice);
            /*if (bluetoothDevice.getName().equals(DEVICE_NAME)) {
                console.Log(TAG,"Mouse Found");
                mouseMgr.setDevice(bluetoothDevice);
            }*/
        }

        Button.OnClickListener checkListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BluetoothGattService> gattServices = mouseMgr.getServices();
                //Log.d(TAG, "Service count: " + gattServices.size());
                console.Log(TAG, "Service count: " + gattServices.size());
                for (BluetoothGattService service : gattServices) {
                    console.Log(TAG, "Service: " + service.getUuid());
                }

            }
        };
        Button.OnClickListener sendListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mouseMgr.writeSeq(new String[] {
                        "9f0101020101",
                        "9f0201020101",
                        "9f0204020101",
                        "9f06010a01080000100000001000"
                });
            }
        };
        Button.OnClickListener cleanListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                console.Rest();
            }
        };


        buttonCheck.setOnClickListener(checkListener);
        buttonSendSeq.setOnClickListener(sendListener);
        resetConsole.setOnClickListener(cleanListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (scalerRecv != null) {
            unregisterReceiver(scalerRecv);
        }
    }
}