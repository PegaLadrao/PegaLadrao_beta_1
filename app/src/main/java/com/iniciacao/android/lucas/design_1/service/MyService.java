package com.iniciacao.android.lucas.design_1.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iniciacao.android.lucas.design_1.R;
import com.iniciacao.android.lucas.design_1.tools.GetDataFromFile;
import com.iniciacao.android.lucas.design_1.tools.IO_file;
import com.iniciacao.android.lucas.design_1.sensor.Detection;
import com.iniciacao.android.lucas.design_1.tools.NotificationTools;
import com.iniciacao.android.lucas.design_1.tools.SMSLocation;
import com.iniciacao.android.lucas.design_1.tools.VolumeObserver;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;

public class MyService extends Service {
    private Detection detection;
    private boolean lastState;
    private MediaPlayer mediaPlayer;

    String service = "MyService: ";

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.siren);
        mNotificationTools = new NotificationTools(this);
        detection = new Detection(getApplicationContext());
        boolean tmp = retrieveLastState();
        detection.changeStateTo(tmp);
        if (tmp) mNotificationTools.createNotification();
        message("onCreate");

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        smsLocation = new SMSLocation(getApplicationContext(), 10000, 10000, false);

        IO_file file = new IO_file(getApplicationContext());
        String s = file.recuperar(IO_file.LAST_STATE);

        if (s.isEmpty() == false) {
            lastState = Boolean.parseBoolean(s);
        } else {
            lastState = false;
        }

        if (lastState) {
            detection.lockScreen();
        }
    }

    public void disableSensor() {
        changeSensorStateTo(false);
    }

    public void enableSensor() {
        changeSensorStateTo(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        message("onStartCommand");
        smsLocation.setNumber((new GetDataFromFile(getApplicationContext())).getData("telSeg"));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        message("onDestroy");
    }


    // ==================== utility ==================== //
    public void changeSensorStateTo(boolean state) {
        detection.changeStateTo(state);
        saveLastState();
    }

    public boolean getSensorState() {
        return detection.getState();
    }

    private void saveLastState() {
        IO_file file = new IO_file(getApplicationContext());
        file.salvar(String.valueOf(detection.getState()), IO_file.FILE_HISTORICO);
    }

    private boolean retrieveLastState() {
        IO_file file = new IO_file(getApplicationContext());
        String s = file.recuperar(IO_file.FILE_HISTORICO);
        if (s.equals("") == false) {
            return Boolean.valueOf(s);
        }
        return false;
    }

    // ==================== Binder(interface) ==================== //


    // ==================== Binder(interface) ==================== //
    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private final Binder localBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return localBinder;
    }
    // ==================== Binder(interface) ==================== //


    // ==================== auxiliar ==================== //
    private void message(String s) {
        Toast.makeText(getApplicationContext(), service + s, Toast.LENGTH_SHORT).show();
    }
    // ==================== auxiliar ==================== //


    // ==================== bloqueio e mensagem ==================== //

    private Vibrator vibrator;
    private SMSLocation smsLocation;
    private long pattern[] = {0, 100, 200, 300, 400};

    public void startVibrate() {
        vibrator.vibrate(pattern, 0);
        setLastState(true);
    }

    public void stopVibrate() {
        setLastState(false);
        vibrator.cancel();
    }

    public void startSiren() {
        mediaPlayer = MediaPlayer.create(this, R.raw.siren);
        mediaPlayer.start();
    }

    public void stopSiren() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    public void sendSMS() {
        setLastState(true);
        smsLocation.setAtive(true);
    }

    public void stopSMS() {
        setLastState(false);
        smsLocation.setAtive(false);
    }

    public boolean getLastState() {
        return lastState;
    }

    public void setLastState(boolean b) {
        lastState = b;
        saveRecentState();
    }

    private void saveRecentState() {
        IO_file file = new IO_file(getApplicationContext());
        file.salvar(String.valueOf(lastState), IO_file.LAST_STATE);
    }
    // ==================== bloqueio e mensagem ==================== //

    // notification
    private NotificationTools mNotificationTools;

    public NotificationTools getmNotificationTools() {
        return mNotificationTools;
    }

    public void setmNotificationTools(NotificationTools mNotificationTools) {
        this.mNotificationTools = mNotificationTools;
    }

    // ==================== Volume observer ====================
    private VolumeObserver volumeObserver;

    public void addObserverAndAlwaysMaxVolume() {
        if (volumeObserver == null) {
            volumeObserver = new VolumeObserver(getApplicationContext(), new Handler());
        }
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, volumeObserver);
    }

    public void removeObserver() {
        if (volumeObserver != null) {
            getApplicationContext().getContentResolver().unregisterContentObserver(volumeObserver);
        }
    }

    // ==================== gps status listener ====================
    public class GpsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsEnabled) {
                Toast.makeText(getApplicationContext(), "Provider enabled from receiver", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Provider disabled from receiver", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private GpsStatusReceiver receiver;

    public void registerStatusListener() {
        if (receiver == null) {
            receiver = new GpsStatusReceiver();
        }
        getApplicationContext().registerReceiver(receiver,  new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    public void unregisterStatusListener() {
        if (receiver != null) {
            getApplicationContext().unregisterReceiver(receiver);
        }
    }
}
