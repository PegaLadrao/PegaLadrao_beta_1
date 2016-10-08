package com.iniciacao.android.lucas.design_1.sensor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;


import com.iniciacao.android.lucas.design_1.LockScreen;
import com.iniciacao.android.lucas.design_1.administration.AdminManager;
import com.iniciacao.android.lucas.design_1.tools.IO_file;

/**
 * Created by chendehua on 15/12/31.
 */
public class Detection implements SensorEventListener {

    private Context mContext;

    private AdminManager mAdminManager;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private static final int SHAKE_THRESHOLD = 200;
    private static final int TIMER = 3;

    private int timer = TIMER;
    private boolean detection = false;
    private long lastUpdate = 0;

    public Detection(Context mContext){

        this.mContext = mContext;

        senSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAdminManager = new AdminManager(mContext);

    }

    public boolean getState() { return detection; }

    public static boolean DISABLE = false;
    public static boolean ABLE = true;

    public void changeStateTo(boolean detection_l) {
        if(detection_l) enableDetection();
        else disableDetection();
    }

    public void enableDetection(){
        detection = true;
        enableSensor();
    }

    public void disableDetection(){
        detection = false;
        disableSensor();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                double speed = Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);

                if (speed > SHAKE_THRESHOLD) {
                    timer--;

                    if (timer == 0) {
                        makeText("gesture detected");
                        movimentDetected();
                    }

                    lastUpdate += 100;

                } else {
                    timer = TIMER;
                }
            }
        }
    }

    public void movimentDetected() {
        IO_file file = new IO_file(mContext);
        String state = file.recuperar(String.valueOf(IO_file.LAST_STATE));
        if (state.isEmpty()) {
            lockScreen();
        } else {
            if (!Boolean.parseBoolean(state)) {
                lockScreen();
            }
        }
    }

    /**
     *
     * Método responsável por bloqueio de tela do smartphone o dispositivo
     */
    public void lockScreen(){

        mAdminManager.lockScreen();
        Intent intent = new Intent(mContext, LockScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    /**
     * registrar sensor
     */
    private void enableSensor(){
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        makeText("sensor added");
    }


    /**
     * desregistrar sensor
     */
    private void disableSensor(){
        senSensorManager.unregisterListener(this);
        makeText("sensor removed");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void makeText(String s){
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

}
