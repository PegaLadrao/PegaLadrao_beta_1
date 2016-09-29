package com.iniciacao.android.lucas.design_1.service;

import android.app.Service;
import android.os.Handler;
import android.util.Log;

/**
 * Created by cc on 16-9-9.
 */
public abstract class MyAsyncTask implements Runnable {
    private Handler handler;
    private int handlerCount;
    private Service myService;
    private final int MAX_HANDLER_COUNT = 10;

    public MyAsyncTask(Service myService){
        handler = new Handler();
        handlerCount = 0;
        this.myService = myService;
    }

    @Override
    public void run() {
        update();
        if (myService != null) {
            task();
            handlerCount = 0;
        } else {
            routine();
        }
    }

    private void routine() {
        if (handlerCount < MAX_HANDLER_COUNT) {
            handler.postDelayed(this, handlerCount * 100 + 100);
            handlerCount++;
            Log.i("MyAsyncTask", "handlerCount: " + handlerCount);
        } else {
            errorTaskFailed();
        }
    }

    public void startTask() { handler.postDelayed(this, 100);}
    public void updateObject(Service myService){ this.myService = myService;}
    
    public abstract void task();
    public abstract void update();
    public abstract void errorTaskFailed();
}
