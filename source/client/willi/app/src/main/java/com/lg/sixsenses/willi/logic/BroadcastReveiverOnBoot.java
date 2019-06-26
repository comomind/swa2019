package com.lg.sixsenses.willi.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReveiverOnBoot extends BroadcastReceiver {

    public static final String TAG = BroadcastReveiverOnBoot.class.getName().toString();

    @Override
    public void onReceive(Context context, Intent intent) {
        String ActionString = intent.getAction();
        if (ActionString != null) {
            if (ActionString.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Start UDP listen Service
                Intent serviceIntent = new Intent(context, CallReceiveService.class);
                context.startService(serviceIntent);
                Log.e(TAG, "Started CallReceiveService with BOOT_COMPLETED");
            }
        }
    }
}
