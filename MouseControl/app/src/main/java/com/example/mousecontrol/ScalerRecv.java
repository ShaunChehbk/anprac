package com.example.mousecontrol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.widget.Toast;

import com.example.mousecontrol.ConsoleEmu;

public class ScalerRecv extends BroadcastReceiver {
    private static final String TAG = "ScalerRecv";
    private ConsoleEmu console;
    private MouseMgr mouseMgr;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.console.Log(TAG, "Received");
        context.sendBroadcast(new Intent("Input_detected"));

    }
    public ScalerRecv(ConsoleEmu console, MouseMgr mousemgr) {
        this.console = console;
        this.mouseMgr = mousemgr;
    }
}
