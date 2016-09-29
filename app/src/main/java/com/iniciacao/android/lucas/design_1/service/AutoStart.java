package com.iniciacao.android.lucas.design_1.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iniciacao.android.lucas.design_1.service.MyService;


/**
 * Created by cc on 16-6-21.
 */
public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MyService.class);
        context.startService(intent1);
    }
}
