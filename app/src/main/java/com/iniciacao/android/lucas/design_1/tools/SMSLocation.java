package com.iniciacao.android.lucas.design_1.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by lucas on 9/1/16.
 */
public class SMSLocation implements LocationListener {

    private Context context;
    private LocationManager locationManager;
    private Location lastLocation;
    private Handler handler;
    private long SMSTime;
    private long GPSTime;
    private Runnable r;
    private String number = "NO_NUMBER";
    private boolean ative;

    public long getSMSTime() {
        return SMSTime;
    }

    public void setSMSTime(long SMSTime) {
        this.SMSTime = SMSTime;
    }

    public long getGPSTime() {
        return GPSTime;
    }

    public void setGPSTime(long GPSTime) {
        this.GPSTime = GPSTime;
    }

    public boolean isAtive() {
        return ative;
    }

    public void setAtive(boolean ative) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            Toast.makeText(context, "LocationManager: Provider is disabled", Toast.LENGTH_SHORT).show();
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "LocationManager: Permission is not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.ative == false && ative == true) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, getGPSTime(), 0, this);
        } else if (this.ative == true && ative == false) {
            locationManager.removeUpdates(this);
        }
        this.ative = ative;
        if(ative) handler.postDelayed(r, getSMSTime());
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public SMSLocation(Context context, final long SMSTime, long GPSTime, final boolean ative) {
        this.context = context;
        this.setSMSTime(SMSTime);
        this.setGPSTime(GPSTime);
        this.handler = new Handler();

        IO_file io_file = new IO_file(context);
        String s = io_file.recuperar(IO_file.FILE_CONFIG_TIME);
        if (!s.isEmpty()) {
            setSMSTime(Long.parseLong(s));
            setGPSTime(Long.parseLong(s));
        }

        r = new Runnable() {
            @Override
            public void run() {
                sendSMS();

                if(isAtive()) {
                    handler.postDelayed(r, getSMSTime());
                }
            }
        };

        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        this.setAtive(ative);
    }

    private void sendSMS() {
        if(lastLocation != null) {
            if (number.equalsIgnoreCase("NO_NUMBER") == false) {
                Toast.makeText(context, "sms sent", Toast.LENGTH_SHORT).show();
                SmsManager.getDefault().sendTextMessage(number, null, "Location lat " + lastLocation.getLatitude() + " long " + lastLocation.getLongitude(), null, null);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) { this.lastLocation = location; }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "LocationManager: Provider is enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "LocationManager: Provider is disabled", Toast.LENGTH_SHORT).show();
    }
}

