package com.iniciacao.android.lucas.design_1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iniciacao.android.lucas.design_1.tools.GetDataFromFile;
import com.iniciacao.android.lucas.design_1.tools.IO_file;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "TAG";

    private final String msg_alertaPadraoObs = "Mensagem padrão: ALERTA, rastreando telefone";

    public static final String msg_alertaPadrao = "ALERTA, rastreando telefone";

    public static final String FILE_INFORMACAO = "info.txt";

    private String sms_time;

    private GetDataFromFile getDataFromFile;

    private IO_file file;

    private ArrayAdapter<String> timeList;

    private Spinner spinner_setTime;

    private TextView txt_mensagemAlerta;

    private Button button_RecuperarSenha;

    private Button button_mensagem_alerta;

    private RelativeLayout relativeLayout;

    private final int REQUEST_PERMISSIONS_CODE_SMS = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_form);
        setSupportActionBar(toolbar);

        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        file = new IO_file(this);

        getDataFromFile = new GetDataFromFile(this);

        txt_mensagemAlerta = (TextView)findViewById(R.id.txt_mensagemAlerta);

        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout_settings);

        button_RecuperarSenha = (Button)findViewById(R.id.button_recuperarSenha);

        button_mensagem_alerta = (Button) findViewById(R.id.button_mensagemNotificacao);
        button_mensagem_alerta.setOnClickListener(this);
        button_RecuperarSenha.setOnClickListener(this);

        unitSpinner();

        setMensagemAlerta();

    }

    private void setMensagemAlerta(){

        if (file.checkFile(IO_file.FILE_CONFIG_ALERT)){
            String text = new IO_file(getApplicationContext()).recuperar(IO_file.FILE_CONFIG_ALERT);
            txt_mensagemAlerta.setText(text);
        }else {
            txt_mensagemAlerta.setText(msg_alertaPadraoObs);
            new IO_file(getApplicationContext()).salvar(msg_alertaPadrao, IO_file.FILE_CONFIG_ALERT);
        }
    }

    private int getIdx(String s) {
        String[] mSMS_TIME= getResources().getStringArray( R.array.timeLIst );

        for (int i = 0; i < mSMS_TIME.length; i++) {
            if (getTime(mSMS_TIME[i]).equals(s)) {
                return i;
            }
        }

        return -1;
    }

    private String getTime(String sms_time) {
        String time = "";

        if (sms_time.equals("15 segundos"))         time = "15000";
        else if (sms_time.equals("30 segundos"))    time = "30000";
        else if (sms_time.equals("1 minuto"))       time = "60000";
        else if (sms_time.equals("2 minutos"))      time = "120000";
        else if (sms_time.equals("3 minutos"))      time = "180000";
        else if (sms_time.equals("4 minutos"))      time = "240000";
        else if (sms_time.equals("5 minutos"))      time = "300000";

        return time;
    }

    private void unitSpinner(){
        spinner_setTime = (Spinner)findViewById(R.id.spinner_setTime);
        timeList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        timeList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_setTime.setAdapter(timeList);
        String[] mSMS_TIME= getResources().getStringArray( R.array.timeLIst );
        timeList.addAll(mSMS_TIME);

        String t = (new IO_file(getApplicationContext())).recuperar(IO_file.FILE_CONFIG_TIME);
        int idx = getIdx(t);
        spinner_setTime.setSelection(idx, false);

        spinner_setTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sms_time = parent.getItemAtPosition(position).toString();
                String time = getTime(sms_time);
                file.salvar(time, IO_file.FILE_CONFIG_TIME);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_recuperarSenha){
            callAlertDialog();
        } else if (v.getId() == R.id.button_mensagemNotificacao) {

            if(file.checkFile(IO_file.FILE_CONFIG_ALERT)) {

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                new AlertDialog.Builder(this)
                        .setMessage("Digite mensagem de alerta:")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String text = input.getText().toString();
                                txt_mensagemAlerta.setText(text);
                                new IO_file(getApplicationContext()).salvar(text, IO_file.FILE_CONFIG_ALERT);
                            }
                        })
                        .setNegativeButton("Mensagem padrão", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txt_mensagemAlerta.setText(msg_alertaPadraoObs);
                                new IO_file(getApplicationContext()).salvar(msg_alertaPadrao, IO_file.FILE_CONFIG_ALERT);
                                dialog.cancel();
                            }
                        })
                        .show();
            }else {
                Snackbar.make(relativeLayout,"Você não possui dados cadastrados", Snackbar.LENGTH_LONG)
                        .setAction("\nCADASTRAR", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(SettingsActivity.this, FormActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    private void callAlertDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle( "Atanção" );

        builder.setMessage("Sua senha será enviada de seu telefone para seu número de telefone. Certifique se seu plano de sms está habilitado");

        builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(file.checkFile(FILE_INFORMACAO)) {
                    if (permissionRequest()) {
                        Toast.makeText(getApplicationContext(), "Enviando...", Toast.LENGTH_SHORT).show();
                        SmsManager.getDefault().sendTextMessage(getDataFromFile.getData("tel"), null, "Sua senha é: " + getDataFromFile.getData("password"), null, null);
                    }
                }else {
                    Snackbar.make(relativeLayout,"Você não possui dados cadastrados", Snackbar.LENGTH_LONG)
                            .setAction("\nCADASTRAR", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SettingsActivity.this, FormActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     *
     * Método responsável por verificar as permissões de acesso ao gps e envio de sms
     * @return <code>true</code> = permissões concedidas <code>false</code> = permissões negadas
     */
    private boolean permissionRequest(){

        if(ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, Manifest.permission.SEND_SMS)) {

                callDialogPermission("Pega Ladrão precisa de sua permissão para enviar SMS para seu telefone de segurança", new String[]{Manifest.permission.SEND_SMS});
            } else {

                ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSIONS_CODE_SMS);
            }
        }
        else {

            return true;
        }

        return false;

    }

    /**
     * Método responsável alertar o usuário de que as permissões não foram aceitas.
     * @param message Messagem que será mostrada.
     * @param permissions Permissão requisitada.
     */
    private void callDialogPermission(String message, final String[] permissions){

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SettingsActivity.this);

        builder.setTitle("Permissão")
                .setMessage( message );

        builder.setPositiveButton("HABILITAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ActivityCompat.requestPermissions(SettingsActivity.this, permissions, REQUEST_PERMISSIONS_CODE_SMS);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
