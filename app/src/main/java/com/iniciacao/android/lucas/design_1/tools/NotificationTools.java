package com.iniciacao.android.lucas.design_1.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.iniciacao.android.lucas.design_1.LockScreen;
import com.iniciacao.android.lucas.design_1.MainActivity;
import com.iniciacao.android.lucas.design_1.R;


/**
 * Created by lucas on 14/07/16.
 *
 * Classe para implementação de métodos relacionados ao uso de notificações.
 */
public class NotificationTools {

    private NotificationManager mNotificationManager;

    private NotificationCompat.Builder builder;

    private Notification mNotification;

    private final int NOTIFICATION_NUMBER = 0;

    private Context mContext;

    /**
     * Construtor padrão.
     *
     * @param context - Conexto da aplicação.
     */
    public NotificationTools(Context context){
        this.mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context.getApplicationContext());


    }

    // ========================= Notificação ========================= //

    /**
     *
     * Método responsável pela criação de notificações.
     */
    public void createNotification (){

        builder.setTicker("MyService");
        builder.setContentTitle("PROTEÇÃO ATIVADA");
        builder.setContentText("Serviço Ativado");
        builder.setSmallIcon(R.mipmap.ic_notification);
        //Vibration
        builder.setVibrate(new long[] { 200, 1000});

        builder.setOngoing(true);

        IO_file file = new IO_file(mContext);
        boolean lastState;
        String s = file.recuperar(IO_file.LAST_STATE);
        if (s.isEmpty() == false) {
            lastState = Boolean.parseBoolean(s);
        } else {
            lastState = false;
        }
        Intent resultIntent;
        if (lastState) {
            resultIntent = new Intent(mContext, LockScreen.class);
        } else {
            resultIntent = new Intent(mContext, MainActivity.class);
            resultIntent.setAction(Intent.ACTION_MAIN);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                resultIntent, 0);

        builder.setContentIntent(pendingIntent);
        mNotification = builder.build();
        mNotificationManager.notify(NOTIFICATION_NUMBER, mNotification);
    }

    /**
     *
     * Método responsavel pela remoção de notificações.
     */
    public void deleteNotification(){
        mNotificationManager.cancelAll();
    }

    // ========================= Notificação ========================= //

}
