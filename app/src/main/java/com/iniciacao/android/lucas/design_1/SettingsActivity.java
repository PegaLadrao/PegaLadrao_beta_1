package com.iniciacao.android.lucas.design_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iniciacao.android.lucas.design_1.tools.GetDataFromFile;
import com.iniciacao.android.lucas.design_1.tools.IO_file;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "TAG";

    private final String msg_alertaPadrao = "Mensagem de alerta que irá aparecer na tela de bloqueio. " +
            "Para editar, clique no botão ALTERAR MENSAGEM DE ALERTA."+ "\n";

    private String msg_alerta;

    private String[] infos;

    private String info;

    private String msgPadrão;

    private String sms_time;

    private GetDataFromFile getDataFromFile;

    private IO_file file;

    private ArrayAdapter<String> timeList;

    private Spinner spinner_setTime;

    private TextView txt_mensagemNotificacao;

    private Button button_RecuperarSenha;
    private Button button_mensagem_alerta;


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

        txt_mensagemNotificacao = (TextView)findViewById(R.id.txt_mensagemNotificação);

        button_RecuperarSenha = (Button)findViewById(R.id.button_recuperarSenha);

        button_mensagem_alerta = (Button) findViewById(R.id.button_mensagemNotificacao);
        button_mensagem_alerta.setOnClickListener(this);
        button_RecuperarSenha.setOnClickListener(this);

        unitSpinner();

        String text = new IO_file(getApplicationContext()).recuperar(IO_file.FILE_CONFIG_ALERT);
        txt_mensagemNotificacao.setText(text);

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
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            new AlertDialog.Builder(this)
                .setMessage("Digite mensagem de alerta:")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        txt_mensagemNotificacao.setText(text);
                        new IO_file(getApplicationContext()).salvar(text, IO_file.FILE_CONFIG_ALERT);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
        }
    }

    private void callAlertDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle( "Atanção" );

        builder.setMessage("Sua senha será enviada para o telefone de segurança cadastrado."+"\nTel -> " + getDataFromFile.getData("telSeg"));

        builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( getApplicationContext(), "Enviando...", Toast.LENGTH_SHORT ).show();
                //IMPLEMENTAR MÉTODO DE ENVIO DE SMS
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
//
//    /**
//     * Metodo reponsavel por formatar os dados que seram salvos no arquivo
//     * @return <code>String</code> formatada
//     */
//    private String formatToFile(){
//
//        String  time = sms_time,
//                msg_padrao = msgPadrão;
//
//        return  time + "\n" +
//                msg_padrao + "\n";
//    }
//
//    private void saveToFile() {
//
////        file.salvar(formatToFile(), IO_file.FILE_CONFIG_TIME);
//
//    }
}
