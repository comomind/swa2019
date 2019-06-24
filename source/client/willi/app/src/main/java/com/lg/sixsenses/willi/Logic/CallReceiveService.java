package com.lg.sixsenses.willi.logic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lg.sixsenses.willi.logic.CallManager.CallHandler;

public class CallReceiveService extends Service {
    public static final String TAG = CallReceiveService.class.getName().toString();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG,"Start CallReceiveService~~~");
        CallHandler.getInstance().setContext(getApplicationContext());
        CallHandler.getInstance().startCallHandler();

    }
}
