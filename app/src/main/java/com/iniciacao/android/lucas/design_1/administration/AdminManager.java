package com.iniciacao.android.lucas.design_1.administration;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lucas on 18/07/16.
 */
public class AdminManager {

    private static final int ADMIN_INTENT = 15;
    private static final String description = "Sample Administrator description";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private Context mContext;

    /**
     *
     * Construtor padrão.
     *
     * @param context - Contexto atual.
     */
    public AdminManager(Context context){

        this.mContext = context;
        mDevicePolicyManager = (DevicePolicyManager)context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(context, MyAdminReceiver.class);


    }

    /**
     *
     * Método responsável pela requisição de permissão de administrador.
     *
     * @param activity - Activity responsável pela requisição.
     */
    public void requestPermission(final Activity activity){

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
        activity.startActivityForResult(intent, ADMIN_INTENT);

    }

    /**
     *
     * Método responsável pela verificação de permissão de administrador.
     *
     * @return - <code>true</code> - PERISSÃO CONCEDIDA. <code>false</code> - PERMISSÃO NEGADA.
     */
    public boolean isAdmin(){

        return mDevicePolicyManager.isAdminActive(mComponentName);
    }

    /**
     *
     * Método responsável pela remoção da permissão de administrador.
     */
    public void removeAdmin(){
        mDevicePolicyManager.removeActiveAdmin(mComponentName);
    }


    /**
     *
     * Método responsável pelo bloqueio de tela do dispositivo.
     */
    public void lockScreen(){
        mDevicePolicyManager.lockNow();
    }
}
