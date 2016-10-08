package com.iniciacao.android.lucas.design_1;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.iniciacao.android.lucas.design_1.administration.AdminManager;
import com.iniciacao.android.lucas.design_1.intro.IntroActivity;
import com.iniciacao.android.lucas.design_1.service.MyAsyncTask;
import com.iniciacao.android.lucas.design_1.service.MyService;
import com.iniciacao.android.lucas.design_1.service.VirtualService;
import com.iniciacao.android.lucas.design_1.tools.IO_file;
import com.iniciacao.android.lucas.design_1.tools.NotificationTools;
import com.iniciacao.android.lucas.design_1.tools.SMSLocation;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity {

    private AdminManager mAdminManager;

    private VirtualService mVirtualService;

    private MyService myService;

    private KeyguardManager mKeyguardManager;

    private IO_file mIO_file;

    private SMSLocation smsLocation;

    private MorphingButton morphingButton;

    private FabSpeedDial fabSpeedDial;

    private RelativeLayout relativeLayout;

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mSharedPreferences_editor;

    private static final int ADMIN_INTENT = 15;

    private final static String BUTTON_KEY = "Button_state";

    private final static String BUTTON_STATE = "state";

    private static final String FILE_INFORMACAO = "info.txt";

    private final String ENABLE = "Ativar Proteção";

    private final String DISABLE = "Desativar";

    private final int REQUEST_PERMISSIONS_CODE_GPS = 1;

    private final int REQUEST_PERMISSIONS_CODE_SMS = 2;

    private final static String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initClasses();
        initComponents();
        firstAccess();
        bindService(new Intent(this, MyService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.testar) {
            startActivity(new Intent(this, TestarActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSIONS_CODE_GPS:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permissão de GPS -> CONCEDIDA", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(this, "Permissão de GPS -> NEGADA", Toast.LENGTH_SHORT).show();

                }
                return;
            }

            case REQUEST_PERMISSIONS_CODE_SMS:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permissão de SMS -> CONCEDIDA", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(this, "Permissão de SMS -> NEGADA", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    // VERIFICANDO SE A PERMISSÃO DE ADMINISTRADOR FOI CONCEDIDA.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Permissão de Administrador -> CONCEDIDA", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permissão de Administrador -> NEGADA", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método responsavel por chamada da Intro
     * */
    private void gotoIntro(){
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *
     * Método responsável por verificar o primeiro acesso do usuário
     */
    private void firstAccess(){

        mSharedPreferences = this.getSharedPreferences("first_access", MODE_PRIVATE);

        mSharedPreferences_editor = mSharedPreferences.edit();

        final String senha = mSharedPreferences.getString( "first_time", null );

        if ( senha == null ){

            //INTRODUÇÃO
            gotoIntro();

            mSharedPreferences_editor.putString("first_time", "FALSE");

            mSharedPreferences_editor.apply();

        }
    }

    private void initClasses(){

        mAdminManager = new AdminManager(this);

        mVirtualService = new VirtualService(this);

        mIO_file = new IO_file(this);

        smsLocation = new SMSLocation(this);

        mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);

    }

    /**
     * Método responsável por inicializar componente do layout
     */
    private void initComponents(){

        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        mSharedPreferences = getApplicationContext().getSharedPreferences(BUTTON_KEY, Context.MODE_PRIVATE);

        mSharedPreferences_editor = mSharedPreferences.edit();

        morphingButton = (MorphingButton) findViewById(R.id.btnMorph1);

        assert morphingButton != null;

        morphingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onMorphButtonClicked(morphingButton);

            }
        });

        ButtonState(morphingButton);

        fabSpeedDial = ((FabSpeedDial) findViewById(R.id.fab_speed_dial));

        assert fabSpeedDial != null;

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter(){
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.user_data){

                    Intent intent = new Intent(MainActivity.this, FormActivity.class);

                    startActivity(intent);


                }else if(menuItem.getItemId() == R.id.lock_screen){

                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));


                }

                return false;
            }
        });
    }

    /**
     *
     * Método responsável por verificar as permissões de acesso ao gps e envio de sms
     * @return <code>true</code> = permissões concedidas <code>false</code> = permissões negadas
     */
    private boolean permissionRequest(){

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                callDialog("Pega Ladrão precisa de sua localização para habilitar a proteção", new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE_GPS);
            }
        }
        else if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)) {

                callDialog("Pega Ladrão precisa de sua permissão para enviar SMS para seu telefone de segurança", new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSIONS_CODE_GPS);
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
     * @param code Código da permissão.
     */
    private void callDialog(String message, final String[] permissions, final int code ){

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder( MainActivity.this);

        builder.setTitle("Permission")
                .setMessage( message );

        builder.setPositiveButton("HABILITAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(code == 0) {

                    ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_PERMISSIONS_CODE_GPS);
                    dialog.dismiss();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_PERMISSIONS_CODE_SMS);
                    dialog.dismiss();
                }
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

    /**
     *
     * Método responsável por verificar se o usuário possui todos os requisitos para habilitar a proteção do smartphone
     *
     * @return <code>true</code> - proteção habilitada. <code>false</code> - proteção desabilitada.
     */
    private boolean startProtectAvailable(){

        if(mIO_file.checkFile(FILE_INFORMACAO)) {

            Log.i("TAG","checkFile = true");

            if (mKeyguardManager.isKeyguardSecure()) {

                Log.i("TAG","isKeyguardSecure = true");

                if (mAdminManager.isAdmin()) {

                    Log.i("TAG","isAdmin = true");

                    if(smsLocation.isGpsOn()) {

                        Log.i("TAG","isGpsOn = true");

                        if (permissionRequest()) {

                            return true;
                        }
                    }else{
                        Log.i("TAG","isGpsOn = false");
                        Snackbar.make(relativeLayout,"Seu GPS está desabilitado, favor habilitar", Snackbar.LENGTH_LONG)
                                .setAction("\nHabilitar", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }

                }
                else {

                    Log.i("TAG","isAdmin = false");

                    adminPermissionAlert();
                }
            }
            else {

                Log.i("TAG","isKeyguardSecure = false");

                lockScreenAlert();
            }
        }
        else{

            Log.i("TAG","checkFile = false");

            Snackbar.make(relativeLayout,"Você deve cadastrar seu dados", Snackbar.LENGTH_LONG)
                    .setAction("\nCADASTRAR", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, FormActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();

        }
        return false;
    }



    /**
     *
     * Método responsável por verificar e salvar o ultimo estado do toggle button.
     */
    private void ButtonState(MorphingButton btnMorph){
        String s = (new IO_file(getApplicationContext())).recuperar(IO_file.FILE_HISTORICO);
        boolean state;
        if (!s.isEmpty()){
            state = Boolean.parseBoolean(s);
            if (state) {
                morphToFailure(btnMorph, DISABLE);
            } else {
                morphToSquare(btnMorph, 0, ENABLE);
            }
        }
        else {
            morphToSquare(btnMorph, 0, ENABLE);
        }

//
//        final String state = mSharedPreferences.getString(BUTTON_STATE, null);
//
//        if(state != null){
//            if(state.equals("true")){
//                morphToSquare(btnMorph, 0, ENABLE);
//            }else {
//                morphToFailure(btnMorph, DISABLE);
//            }
//        }else{
//            morphToSquare(btnMorph, 0, ENABLE);
//        }

    }

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

    private void onMorphButtonClicked(final MorphingButton btnMorph) {

        if(startProtectAvailable()) {

            if (btnMorph.getText() == DISABLE) {

                mVirtualService.disableSensor();

                morphToSquare(btnMorph, 500, ENABLE);

                mSharedPreferences_editor.putString(BUTTON_STATE, "true");

                mSharedPreferences_editor.apply();

                (new MyAsyncTask(myService) {
                    @Override
                    public void task() {
                        myService.getmNotificationTools().deleteNotification();
                    }
                    @Override
                    public void update() {
                        updateObject(myService);
                    }
                    @Override
                    public void errorTaskFailed() {}
                }).startTask();

            } else {

                mVirtualService.enableDetection();

                morphToFailure(btnMorph, DISABLE);

                mSharedPreferences_editor.putString(BUTTON_STATE, "false");

                mSharedPreferences_editor.apply();

                (new MyAsyncTask(myService) {
                    @Override
                    public void task() {
                        myService.getmNotificationTools().createNotification();
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
    }

    /**
     *
     * Metódo responsável por alertar o usuário caso o mesmo não possua uma senha de bloqueio nativa do smartphone.
     */
    private void lockScreenAlert(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( "Atanção" );

        builder.setMessage("Você não possui uma BLOQUEIO DE TELA! \n\n" +
                "Vá para 'Configurações' -> 'Segurança' -> 'Bloqueio de Tela' para habilitar a tela de bloqueio.\n\n" +
                "OBSERVAÇÃO!\n" +
                "Para habilitar a tela de boqueio selecione 'Padrão', 'PIN', ou 'Senha' em Bloquio de Tela");

        builder.setPositiveButton("CONFIGURAÇÕES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //CHAMANDO INTENT DE CONFICURAÇÃO DE SEGURANÇA.
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);

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
     * Metódo responsável por alertar o usuário caso o mesmo não tenha habilitado a permissão de administrador.
     */
    private void adminPermissionAlert(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( "ATENÇÃO!" );

        builder.setMessage("Você não possui PERMISSÃO DE ADMINISTRADOR! \n\n" +
                "Para que o PEGA LADRÃO possa proteger seu dispositivo, é necessário permissão de administrador.");

        builder.setPositiveButton("PERMITIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAdminManager.requestPermission(MainActivity.this);
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

    public void morphToSquare(final MorphingButton btnMorph, int duration, String text) {

        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_200))
                .height(dimen(R.dimen.mb_height_60))
                .color(color(R.color.mb_blue))
                .colorPressed(color(R.color.mb_blue_dark))
                .text(text);
        btnMorph.morph(square);
    }

    public void morphToSuccess(final MorphingButton btnMorph) {

        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(dimen(R.dimen.mb_height_60))
                .width(dimen(R.dimen.mb_width_120))
                .height(dimen(R.dimen.mb_height_60))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done)
                .text("Success");
        btnMorph.morph(circle);
    }

    public void morphToFailure(final MorphingButton btnMorph, String text) {

        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(dimen(R.dimen.mb_height_60))
                .width(dimen(R.dimen.mb_width_150))
                .height(dimen(R.dimen.mb_height_60))
                .color(color(R.color.mb_red))
                .colorPressed(color(R.color.mb_red_dark))
                .icon(R.drawable.ic_lock_black)
                .text(text);
        btnMorph.morph(circle);
    }

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }


}
