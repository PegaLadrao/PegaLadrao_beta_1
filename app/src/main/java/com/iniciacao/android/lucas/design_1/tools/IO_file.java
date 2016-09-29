package com.iniciacao.android.lucas.design_1.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by chendehua on 16/2/3.
 */
public class IO_file {

    public static final String FILE_INFORMACAO = "info.txt";
    public static final String FILE_CONFIG_TIME = "file_config_TIME";
    public static final String FILE_HISTORICO = "hist.txt";
    public static final String LAST_STATE = "laststate.txt";
    public static final String FILE_CONFIGURACAO = "config.txt";
    private static final String TAG = "TAG";

    // application context
    public Context context;

    public IO_file(Context context){
        this.context = context;
    }

    public void salvar(String str, String nome_arquivo){

        try {
            FileOutputStream fOut = context.openFileOutput(nome_arquivo, context.MODE_PRIVATE);

            fOut.write(str.getBytes());
            fOut.close();

            Log.i(TAG,"Salvo com sucesso.");
        }catch (Exception e){
            Log.i(TAG,"Falha ao salvar.");
            e.printStackTrace();
        }
    }

    public String recuperar(String nome_arquivo){
        String temp="";

        try{
            FileInputStream fin = context.openFileInput(nome_arquivo);
            int c;
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }

            //string temp contains all the data of the file.
            fin.close();

          // makeText(temp + ", recupear com sucesso.");
        }catch (Exception e){
           // makeText("Arquivo nao existe");
//            e.printStackTrace();
            return "";
        }

        return temp;
    }

    public void deletar(String nome_arquivo){
        //makeText("deletar");
        context.deleteFile(nome_arquivo);
    }

    public boolean checkFile(String nome_arquivo){

        try {
            FileInputStream file = context.openFileInput(nome_arquivo);

            //Toast.makeText(context, "Classe IO_file Arquivo encontrado ", Toast.LENGTH_SHORT).show();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            //Toast.makeText(context, "Classe IO_file Arquivo nao encontrado ", Toast.LENGTH_SHORT).show();


            return false;
        }
    }

    public void makeText(String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


}
