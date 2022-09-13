package com.example.mousecontrol;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;

import com.example.mousecontrol.ConsoleEmu;

import java.util.List;
import java.util.UUID;

public class MouseMgr {
    private static final String TAG = "MouseMgr";
    private static UUID SERVICE_CHANNEL = UUID.fromString("0000fe40-cc7a-482a-984a-7f2ed5b3e512");
    private static UUID WRITE_CHANNEL = UUID.fromString("0000fe41-cc7a-482a-984a-7f2ed5b3e512");
    private static UUID NOTIFY_CHANNEL = UUID.fromString("0000fe42-cc7a-482a-984a-7f2ed5b3e512");

    private BluetoothDevice targetDevice;
    private BluetoothGatt targetGatt;
    private ConsoleEmu console;
    private Context context;
    private BluetoothGattService targetService;
    private BluetoothGattCharacteristic writeChar;
    private BluetoothGattCharacteristic notifyChar;


    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                console.Log(TAG, "Attempting to start Service discovery");
                targetGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                console.Log(TAG, "Disconnected from Gatt");
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                targetService = targetGatt.getService(SERVICE_CHANNEL);
                if (targetService != null) {
                    console.Log(TAG, "Target Service found");
                    writeChar = targetService.getCharacteristic(WRITE_CHANNEL);
                    if (writeChar != null) {
                        console.Log(TAG, "Write Characteristic: " + writeChar.getUuid());
                        targetGatt.setCharacteristicNotification(writeChar, true);
                    }
                    notifyChar = targetService.getCharacteristic(NOTIFY_CHANNEL);
                    if (notifyChar != null) {
                        console.Log(TAG, "Write Characteristic: " + notifyChar.getUuid());
                        targetGatt.setCharacteristicNotification(notifyChar, true);
                    }
                } else {
                    console.Log(TAG, "Target Service not found");
                }
            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            console.Log(TAG, "Write success");
        }
    };


    @Deprecated
    public MouseMgr(Context context, ConsoleEmu console, BluetoothDevice targetDevice) {
        this.context = context;
        this.targetDevice = targetDevice;
        this.console = console;
    }
    public MouseMgr(Context context, ConsoleEmu console) {
        this.context = context;
        this.console = console;
    }


    public void setDevice(BluetoothDevice targetDevice) {
        this.targetDevice = targetDevice;
        targetGatt = targetDevice.connectGatt(context, false, gattCallback);
        /*targetGatt.setCharacteristicNotification(targetGatt.getService(SERVICE_CHANNEL)
                                                            .getCharacteristic(NOTIFY_CHANNEL),
                                                true);*/
    }

    public void setConsole(ConsoleEmu console) {
        this.console = console;
    }

    public List<BluetoothGattService> getServices() {
        return targetGatt.getServices();
    }


    public void writeSingle(String string) {
        try {
            writeChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            writeChar.setValue(hexStringToByteArray(string));
            targetGatt.writeCharacteristic(writeChar);
        } catch (Exception e) {
            console.Log(TAG, "Failed");
        }
    }
    public String readSingle() {
        return "To-Do";
    }

    public void writeSeq(String[] seq) {
        writeChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

        for (String string : seq) {
            writeChar.setValue(hexStringToByteArray(string));
            targetGatt.writeCharacteristic(writeChar);
        }
    }

    /**
     * Subscribe!
     */
    public void Update() {

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] res = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            res[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return res;
    }
}
