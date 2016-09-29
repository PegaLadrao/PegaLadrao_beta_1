package com.iniciacao.android.lucas.design_1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private TextView txt_mensagemNotificação;

    private Button button_RecuperarSenha;


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

        txt_mensagemNotificação = (TextView)findViewById(R.id.txt_mensagemNotificação);

        button_RecuperarSenha = (Button)findViewById(R.id.button_recuperarSenha);

        button_RecuperarSenha.setOnClickListener(this);

        unitSpinner();

    }

    private void unitSpinner(){

        spinner_setTime = (Spinner)findViewById(R.id.spinner_setTime);

        timeList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        timeList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_setTime.setAdapter(timeList);
        String[] mSMS_TIME= getResources().getStringArray( R.array.timeLIst );
        timeList.addAll(mSMS_TIME);

        spinner_setTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sms_time = parent.getItemAtPosition(position).toString();

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

    /**
     * Metodo reponsavel por formatar os dados que seram salvos no arquivo
     * @return <code>String</code> formatada
     */
    private String formatToFile(){

        String  time = sms_time,
                msg_padrao = msgPadrão;

        return  time + "\n" +
                msg_padrao + "\n";
    }

    private void saveToFile() {


        file.salvar(formatToFile(), IO_file.FILE_INFORMACAO);

    }
}
