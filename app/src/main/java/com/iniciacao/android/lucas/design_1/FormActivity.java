package com.iniciacao.android.lucas.design_1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.LinearProgressButton;
import com.iniciacao.android.lucas.design_1.tools.FormValidation;
import com.iniciacao.android.lucas.design_1.tools.IO_file;
import com.iniciacao.android.lucas.design_1.materia_design.ProgressGenerator;

public class FormActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    private EditText edt_nome, edt_telefone, edt_senha, edt_confi_senha,
            edt_telefone_seguranca, edt_conf_telefone_seguranca;

    private Resources mResources;

    private IO_file file;

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mSharedPreferences_editor;

    private final static String USER_PASSWORD = "user_passWord";

    private LinearProgressButton btnMorph1;

    private final int REQUEST_PERMISSIONS_CODE_READ_CONTACTS = 2;

    private boolean passWordVisibility = false;

    private final String SAVE = "Salvar Dados";

    private final String EDIT = "Editar";

    private boolean PERMISSION_STATUS = false;

    private FormValidation formValidation;

    private View view, customAlert;

    private boolean firstFocus;

    static final int PICK_CONTACT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_form);
        setSupportActionBar(toolbar);

        Window window = getWindow();


        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        view = getWindow().getDecorView().getRootView();

        formValidation = new FormValidation(view);

        permissionRequest();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mResources = getResources();

        init();

        btnMorph1 = (LinearProgressButton) findViewById(R.id.button_cadastrar);
        assert btnMorph1 != null;
        restrieve();
        btnMorph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMorphButton1Clicked(btnMorph1);
            }
        });



        edt_senha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edt_senha.getRight() - edt_senha.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {

                        if(!passWordVisibility) {
                            edt_senha.setTransformationMethod(null);
                            passWordVisibility = true;
                            Log.i("LOG", passWordVisibility+" if");
                        }else {
                            edt_senha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passWordVisibility = false;
                            Log.i("LOG", passWordVisibility+" else");
                        }
                        return true;
                    }
                }
                return false;
            }
        });

    }


    /**
     * Metodo responsavel por inicializacao dos campos de cadastro
     */
    private void init(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                callClearErrors(s);
            }
        };

        edt_nome = (EditText) this.findViewById( R.id.editText_Nome );
        edt_nome.addTextChangedListener( textWatcher );

        edt_telefone = ( EditText ) this.findViewById( R.id.editText_Telefone );
        edt_telefone.addTextChangedListener( new PhoneNumberFormattingTextWatcher() );

        edt_senha = ( EditText ) this.findViewById( R.id.editText_Senha );
        edt_senha.addTextChangedListener( textWatcher );

        edt_confi_senha = ( EditText ) this.findViewById( R.id.editText_ConfiSenha );
        edt_confi_senha.addTextChangedListener( textWatcher );

        edt_telefone_seguranca = ( EditText ) this.findViewById( R.id.editText_TelefoneSeg );
        edt_telefone_seguranca.addTextChangedListener( new PhoneNumberFormattingTextWatcher() );

        edt_telefone_seguranca.setOnFocusChangeListener(this);

        edt_conf_telefone_seguranca = ( EditText ) this.findViewById( R.id.editText_ConfTelefoneSeg );
        edt_conf_telefone_seguranca.addTextChangedListener( new PhoneNumberFormattingTextWatcher() );

        mSharedPreferences = this.getSharedPreferences("button_state", MODE_PRIVATE);

        mSharedPreferences_editor = mSharedPreferences.edit();

        file = new IO_file(this);


    }


    /**
     * Metodo responsavel por limpar sessao de erros
     *
     * @param s Editable
     */
    private void callClearErrors(Editable s) {

        if (!s.toString().isEmpty()) {

            clearErrorFields(edt_nome);
            clearErrorFields(edt_telefone);
            clearErrorFields(edt_senha);
            clearErrorFields(edt_confi_senha);
            clearErrorFields(edt_telefone_seguranca);
            clearErrorFields(edt_conf_telefone_seguranca);

        }
    }

    /**
     * Limpa os ícones e as mensagens de erro dos campos desejados
     *
     * @param editTexts lista de campos do tipo EditText
     */
    private void clearErrorFields(EditText... editTexts) {

        for (EditText editText : editTexts) {

            editText.setError(null);

        }
    }


    /**
     * Metodo reponsavel por formatar os dados que seram salvos no arquivo
     * @return <code>String</code> formatada
     */
    private String formatToFile(){

        String  nome_s = edt_nome.getText().toString(),
                telefone_s = edt_telefone.getText().toString().replaceAll("\\s",""),
                senha_s = edt_senha.getText().toString(),
                telefone_seguranca_s = edt_telefone_seguranca.getText().toString().replaceAll("\\s","");

        return  nome_s + "\n" +
                telefone_s + "\n" +
                senha_s + "\n" +
                telefone_seguranca_s + "\n";
    }

    public void saveToFile() {
        file.salvar(formatToFile(), IO_file.FILE_INFORMACAO);
    }

    private boolean restrieve(){
        String info = file.recuperar(IO_file.FILE_INFORMACAO);

        if(!info.equals("")){
            String[] infos = info.split("\n");

            edt_nome.setText(infos[0]);
            edt_telefone.setText(infos[1]);
            mSharedPreferences =
                    getApplicationContext().
                            getSharedPreferences("user", Context.MODE_PRIVATE);
            mSharedPreferences_editor = mSharedPreferences.edit();
            mSharedPreferences_editor.putString(USER_PASSWORD, infos[2]);
            mSharedPreferences_editor.apply();
            edt_telefone_seguranca.setText(infos[3]);
            enableEdition( false );
            morphToFailure( btnMorph1, EDIT );


        } else {
            morphToSquare(btnMorph1, SAVE);
            return false;
        }
        return true;
    }

    public void enableEdition( boolean action ){

        if( !action ){

            edt_nome.setEnabled( action );

            edt_telefone.setEnabled( action );

            edt_telefone_seguranca.setEnabled( action );

            edt_senha.setVisibility( View.GONE );

            edt_confi_senha.setVisibility( View.GONE );

            edt_conf_telefone_seguranca.setVisibility( View.GONE );

        }else{

            edt_nome.setEnabled( action );

            edt_telefone.setEnabled( action );

            edt_telefone_seguranca.setEnabled( action );

            edt_senha.setVisibility( View.VISIBLE );

            edt_confi_senha.setVisibility( View.VISIBLE );

            edt_conf_telefone_seguranca.setVisibility( View.VISIBLE );

        }
    }

    private void onMorphButton1Clicked(final LinearProgressButton btnMorph) {


        if(btnMorph.getText() == SAVE) {


            if(formValidation.validarCadastro()) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle( "Atanção" );

                builder.setMessage("Você gostaria de salva esse dados? " + "\n\nNome: "
                        + edt_nome.getText().toString() + "\n\nTelefone: " + edt_telefone.getText().toString() + "\n\nTelefone de Segurança: "
                        + edt_telefone_seguranca.getText().toString() +"\n\n\n"+ mResources.getString(R.string.observacao));

                builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText( getApplicationContext(), "Salvando..", Toast.LENGTH_SHORT ).show();

                        simulateProgress1(btnMorph, EDIT);

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
            }else{

                Toast.makeText(getApplicationContext()," Invalido ", Toast.LENGTH_SHORT).show();
            }
        }else {
            restrieve();

            customAlert = getLayoutInflater().inflate(R.layout.dialog_change_data, null);

            final AlertDialog dialog = new AlertDialog.Builder(this).create();

            final EditText edt_alertDialog_senha = (EditText) customAlert.findViewById(R.id.edt_alertDialog_senha);

            final String senha = mSharedPreferences.getString( USER_PASSWORD, null );

            TextView cancelar = (TextView)customAlert.findViewById( R.id.cancelar );

            TextView verificar = (TextView)customAlert.findViewById( R.id.verificar );

            assert verificar != null;
            verificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    assert edt_alertDialog_senha != null;
                    if (!edt_alertDialog_senha.getText().toString().equals( senha )) {

                        edt_alertDialog_senha.setError(" Senha invalida");

                    } else {

                        dialog.dismiss();

                        morphToSquare(btnMorph, SAVE);

                        enableEdition( true );

                        edt_senha.setText("");
                        edt_confi_senha.setText("");
                        edt_conf_telefone_seguranca.setText("");

                    }
                }
            });

            assert cancelar != null;
            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.setView(customAlert);

            dialog.show();

        }
    }

    public void morphToSquare(final MorphingButton btnMorph, String text) {
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



    public void simulateProgress1(@NonNull final LinearProgressButton button, final String text) {
        int progressColor = color(R.color.mb_purple);
        int color = color(R.color.mb_gray);
        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
        int width = dimen(R.dimen.mb_width_200);
        int height = dimen(R.dimen.mb_height_8);
        int duration = integer(R.integer.mb_animation);

        ProgressGenerator generator = new ProgressGenerator(new ProgressGenerator.OnCompleteListener() {
            @Override
            public void onComplete() {
                morphToFailure(button, text);
                button.unblockTouch();
                saveToFile();
                enableEdition(false);
            }
        });
        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressColor, progressCornerRadius, width, height, duration);
        generator.start(button);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            edt_telefone_seguranca.setText(formatPhone(phones.getString(phones.getColumnIndex("data1"))));
                            edt_conf_telefone_seguranca.setText(formatPhone(phones.getString(phones.getColumnIndex("data1"))));

                        }
                    }
                }
                break;

        }
    }

    private String formatPhone(String phone) {
        String res = "";
        char tmp;
        int i = 0, len = phone.length();

        while(i < len) {
            tmp = phone.charAt(i);
            if (Character.isDigit(tmp)) {
                res += tmp;
            }
            i++;
        }

        return res;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
//            if (edt_telefone_seguranca.getText().toString().isEmpty()) {
                if (PERMISSION_STATUS) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            //}
        }
    }

    /**
     *
     * Método responsável por verificar as permissões de acesso ao gps e envio de sms
     * @return <code>true</code> = permissões concedidas <code>false</code> = permissões negadas
     */
    private boolean permissionRequest(){

        if(ContextCompat.checkSelfPermission(FormActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(FormActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSIONS_CODE_READ_CONTACTS);

        }
        else {

            PERMISSION_STATUS = true;
            return true;

        }

        PERMISSION_STATUS = false;
        return false;

    }

}
