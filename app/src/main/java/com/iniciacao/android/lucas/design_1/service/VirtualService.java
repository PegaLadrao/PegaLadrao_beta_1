package com.iniciacao.android.lucas.design_1.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by cc on 16-6-24.
 */
public class VirtualService {

    // Activity which instanced the class
    private Activity activity;

    private boolean serviceIsBound = false;

    public VirtualService(Activity activity) {
        this.activity = activity;
        onStart();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService myService = ((MyService.LocalBinder) service).getService();
            myService.changeStateOfSensor();
            onUnbindService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void changeSensorstate() {
        onBindService();
    }


    public void onBindService() {
        if(!serviceIsBound) {
            Intent intent = new Intent(activity, MyService.class);
            activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            serviceIsBound = true;
        }
    }

    public void onUnbindService() {
        if(serviceIsBound) {
            activity.unbindService(serviceConnection);
            serviceIsBound = false;
        }
    }

    public void onStart() {
        Intent intent = new Intent(activity, MyService.class);
        activity.startService(intent);
    }

    public void onStop() {
        Intent intent = new Intent(activity, MyService.class);
        activity.stopService(intent);
    }
}
