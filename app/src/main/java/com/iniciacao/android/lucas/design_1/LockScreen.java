package com.iniciacao.android.lucas.design_1;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iniciacao.android.lucas.design_1.service.MyAsyncTask;
import com.iniciacao.android.lucas.design_1.service.MyService;
import com.iniciacao.android.lucas.design_1.tools.GetDataFromFile;
import com.iniciacao.android.lucas.design_1.tools.IO_file;
import com.iniciacao.android.lucas.design_1.tools.SMSLocation;

import org.w3c.dom.Text;

public class LockScreen extends AppCompatActivity {

    private TextView tv_countDown;

    private TextView tv_notify;

    private TextView tv_error;

    private TextView tv_msgAlerta;

    private Vibrator vibrator;

    private GetDataFromFile getDataFromFile;

    private InputMethodManager inputMethodManager;

    private IO_file file;

    private EditText edt_passWord;

    private Button btn_checkPassword;

    private View view;

    private SMSLocation smsLocation;

    private MyService myService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((MyService.LocalBinder)service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        bindService(new Intent(this, MyService.class), mConnection, BIND_AUTO_CREATE);

        view = getWindow().getDecorView().getRootView();

        tv_countDown = (TextView)findViewById(R.id.tv_coutDown);

        tv_notify = (TextView)findViewById(R.id.tv_notify);

        tv_error = (TextView)findViewById(R.id.tv_error);

        tv_msgAlerta = (TextView)findViewById(R.id.tv_msgAlerta);

        tv_error.setVisibility(View.GONE);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        edt_passWord = (EditText)findViewById(R.id.edt_password);

        btn_checkPassword = (Button)findViewById(R.id.btn_checkPassword);

        inputMethodManager = (InputMethodManager) LockScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);

        getDataFromFile = new GetDataFromFile(this);

        file = new IO_file(this);



        if(file.checkFile(IO_file.FILE_CONFIG_ALERT)){
            String text = new IO_file(getApplicationContext()).recuperar(IO_file.FILE_CONFIG_ALERT);
            tv_msgAlerta.setText(text);
        }else {
            tv_msgAlerta.setText("ALERTA, Rastreando telefone");
        }

        edt_passWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.showSoftInput(v , InputMethodManager.SHOW_IMPLICIT);
            }
        });

        btn_checkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassWord();
            }
        });

        smsLocation = new SMSLocation(getApplicationContext(), 10000, 10000, false);
        smsLocation.setNumber((new GetDataFromFile(getApplicationContext())).getData("telSeg"));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        boolean lastState;
        IO_file file = new IO_file(getApplicationContext());
        String s = file.recuperar(IO_file.LAST_STATE);

        if (s.isEmpty() == false) {
            lastState = Boolean.parseBoolean(s);
        } else {
            lastState = false;
        }

        if (lastState) {
            timercount = 200;
        } else {
            timercount = 15000;
        }

        countDown();

        setAtive(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Toast.makeText(this, "down", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Toast.makeText(this, "up", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private int timercount;

    private void countDown(){
        new CountDownTimer(timercount, 100){
            @Override
            public void onTick(long millisUntilFinished) {
                tv_countDown.setText(""+millisUntilFinished/1000);
                if (ative) {
                    (new MyAsyncTask(myService) {
                        @Override
                        public void task() {
                            myService.startVibrate();
                        }
                        @Override
                        public void update() {
                            updateObject(myService);
                        }
                        @Override
                        public void errorTaskFailed() {}
                    }).startTask();
                } else {
                    this.cancel();
                    (new MyAsyncTask(myService) {
                        @Override
                        public void task() {
                            myService.stopVibrate();
                        }
                        @Override
                        public void update() {
                            updateObject(myService);
                        }
                        @Override
                        public void errorTaskFailed() {}
                    }).startTask();
                }
            }

            @Override
            public void onFinish() {
                if (ative) {
                    tv_notify.setText("Seu Telefone está sendo rastreado");
                    tv_countDown.setVisibility(View.GONE);
                    (new MyAsyncTask(myService) {
                        @Override
                        public void task() {
                            myService.sendSMS();
                        }
                        @Override
                        public void update() {
                            updateObject(myService);
                        }
                        @Override
                        public void errorTaskFailed() {}
                    }).startTask();
                }
            }
        }.start();
    }

    private boolean isAtive() {
        return ative;
    }

    private void setAtive(boolean ative) {
        this.ative = ative;
        saveStateToFile();
    }

    private boolean ative; // alarme

    private void checkPassWord(){
        String passWord = getDataFromFile.getData("password");

        String numeroTel = getDataFromFile.getData("tel");
        String substr = numeroTel.substring(numeroTel.length() - 4, numeroTel.length());

        if (!edt_passWord.getText().toString().equals( passWord ) && !substr.equals(edt_passWord.getText().toString())) {
            tv_error.setVisibility(View.VISIBLE);
            tv_error.setText("Senha Invalida. Caso tenha esquecido a senha, insira os quatro ultimos digitos de seu numero de telefone ");
        }else{
            setAtive(false);
            (new MyAsyncTask(myService) {
                @Override
                public void task() {
                    myService.stopVibrate();
                }
                @Override
                public void update() {
                    updateObject(myService);
                }
                @Override
                public void errorTaskFailed() {}
            }).startTask();
            (new MyAsyncTask(myService) {
                @Override
                public void task() {
                    myService.stopSMS();
                }
                @Override
                public void update() {
                    updateObject(myService);
                }
                @Override
                public void errorTaskFailed() {}
            }).startTask();
            finish();
        }
    }

    private void saveStateToFile() {
        IO_file file = new IO_file(getApplicationContext());
        file.salvar(String.valueOf(isAtive()), IO_file.LAST_STATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
//        saveStateToFile();
    }
}
