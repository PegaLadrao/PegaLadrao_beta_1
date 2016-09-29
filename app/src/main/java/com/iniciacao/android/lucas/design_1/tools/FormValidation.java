package com.iniciacao.android.lucas.design_1.tools;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.iniciacao.android.lucas.design_1.R;

/**
 * Created by lucas on 23/08/16.
 */

public class FormValidation {

    private View view;
    private Resources mResources;

    private EditText edt_nome;
    private EditText edt_telefone;
    private EditText edt_senha;
    private EditText edt_confi_senha;
    private EditText edt_telefone_seguranca;
    private EditText edt_conf_telefone_seguranca;

    public FormValidation(View view) {
        this.view = view;
        getAllEditText();
        mResources = view.getResources();
    }

    private void getAllEditText() {
        View mInflaterView = this.view;

        edt_nome = ( EditText ) mInflaterView.findViewById( R.id.editText_Nome );
        edt_telefone = ( EditText ) mInflaterView.findViewById( R.id.editText_Telefone );
        edt_senha = ( EditText ) mInflaterView.findViewById( R.id.editText_Senha );
        edt_confi_senha = ( EditText ) mInflaterView.findViewById( R.id.editText_ConfiSenha );
        edt_telefone_seguranca = ( EditText ) mInflaterView.findViewById( R.id.editText_TelefoneSeg );
        edt_conf_telefone_seguranca = ( EditText ) mInflaterView.findViewById( R.id.editText_ConfTelefoneSeg );
    }

    /**
     * Metodo responsavel pela validacao dos campos de cadastro
     *
     * @return <code>true</code> campos validados <code>false</code> campos invalidos
     */

    public boolean validarCadastro() {

        return verificarCamposVazios() && verificarTamanho() && validarConfSenhaConfTelefone() && checkTelefone();
    }

    /**
     * Metodo resposavel por verificar se os campos confirmar senha e confirmar telefone de seguranca estao validos
     *
     * @return <code>true</code> campos validados <code>false</code> campos invalidos
     */
    private boolean validarConfSenhaConfTelefone(){

        String  senha       = edt_confi_senha.getText().toString(),
                telefoneSeg = edt_conf_telefone_seguranca.getText().toString();

        return !(!(validarSenhaConf(senha)) || !(validarTelefoneConf(telefoneSeg)));

    }

    /**
     * Metodo responsavel por verificar se a o campo confirmar senha esta igual a senha desejada
     *
     * @param confSenha confirmacao de senha do ussuario
     * @return <code>true</code> campo validado <code>false</code> campo invalido
     */
    private boolean validarSenhaConf( String confSenha ){

        String senha = edt_senha.getText().toString();

        if ( !(confSenha.equals(senha)) ){

            edt_confi_senha.requestFocus();

            edt_confi_senha.setError( mResources.getString( R.string.cadastro_confSenhaInvalido ) );

            return false;

        }

        return true;
    }

    /**
     * Metodo responsavel por verificar se o campo confirmar telefone seguranca esta igual o telefone de seguranca
     *
     * @param confTelefone telefone de seguranca
     *
     * @return <code>true</code> campo validado <code>false</code> campos invalido
     */
    private boolean validarTelefoneConf( String confTelefone ) {

        String telefoneSeg = edt_telefone_seguranca.getText().toString();

        if (!(confTelefone.equals(telefoneSeg))) {

            edt_conf_telefone_seguranca.requestFocus();

            edt_conf_telefone_seguranca.setError(mResources.getString(R.string.cadastro_confTelefoneSegIvalido));

            return false;

        }
        return true;
    }

    /**
     * Metodo responsavel para verificar se o telefone de seguranca nao e igual ao telefone ja cadastrado
     *
     * @return <code>true</code> campo validado <code>false</code> campos invalido
     */
    private boolean checkTelefone(){

        String segTelefone = edt_telefone_seguranca.getText().toString();

        return validarTelefone(segTelefone);

    }

    /**
     * Metodo auxiliar para verificar se o telefone de seguranca nao e igual ao telefone ja cadastrado
     *
     * @return <code>true</code> campo validado <code>false</code> campos invalido
     */
    private boolean validarTelefone( String segTelefone ){

        String telefone    = edt_telefone.getText().toString();

        if ( segTelefone.equals( telefone ) ){

            edt_telefone_seguranca.requestFocus();

            edt_telefone_seguranca.setError( mResources.getString( R.string.cadastro_telefoneSegInvalido ) );

            return false;

        }

        return true;

    }

    /**
     * Metodo responsavel por verificar se os campos senha telefone e telefone de seguranca estao nos formatos corretos
     *
     * @return <code>true</code> tamanhos corretos <code>false</code> tamanhos incorretos
     */
    private boolean verificarTamanho(){
        String  nome        = edt_nome.getText().toString(),
                senha       = edt_senha.getText().toString(),
                telefone    = edt_telefone.getText().toString(),
                telefoneSeg = edt_telefone_seguranca.getText().toString();

        if( telefone.length() < 13 || telefone.charAt(0) != '0'){
            edt_telefone.requestFocus();
            edt_telefone.setError(mResources.getString(R.string.cadastro_telefoneInvalido));
            return false;
        }else if( nome.length() < 2){
            edt_nome.requestFocus();
            edt_nome.setError( mResources.getString( R.string.cadastro_nomeInvalido ) );
            return false;
        }else if( senha.length() < 5 ){
            edt_senha.requestFocus();
            edt_senha.setError(mResources.getString( R.string.cadastro_senhaInvalida ));
            return false;
        }else if( telefoneSeg.length() < 13 || telefoneSeg.charAt(0) != '0' ) {
            edt_telefone_seguranca.requestFocus();
            edt_telefone_seguranca.setError( mResources.getString(R.string.cadastro_telefoneInvalido) );
            return false;
        }
        System.out.println(telefone);
        return true;
    }


    /**
     * Metodo responsavel por verificar se existe algum campo vazio
     *
     * @return <code>true</code> possui campos vazio <code>false</code> nao possui campos vazios
     */
    private boolean verificarCamposVazios() {
        String nome = edt_nome.getText().toString(),
                telefone = edt_telefone.getText().toString(),
                senha = edt_senha.getText().toString(),
                confSenha = edt_confi_senha.getText().toString(),
                telefoneSeg = edt_telefone_seguranca.getText().toString(),
                confTelefoneSeg = edt_conf_telefone_seguranca.getText().toString();

        return !(camposVazios(nome, telefone, senha, confSenha, telefoneSeg, confTelefoneSeg));
    }

    /**
     * Metodo auxiliar responsavel pela verificacao de campos vazios no formulario de cadastro e disparar erros
     *
     * @return <code>true</code>existem campos vazios. <code>false</code>nao existem campos vazios
     */
    private boolean camposVazios(String nome, String telefone, String senha, String confSenha, String telefoneSeg, String confTelefoneSeg){

        if( TextUtils.isEmpty( nome ) ){

            edt_nome.requestFocus();

            edt_nome.setError( mResources.getString( R.string.cadastro_campos ) );

            return true;

        }else if( TextUtils.isEmpty( telefone ) ){

            edt_telefone.requestFocus();

            edt_telefone.setError( mResources.getString( R.string.cadastro_campos ) );

            return true;

        }else if( TextUtils.isEmpty( senha ) ){

            edt_senha.requestFocus();

            edt_senha.setError( mResources.getString( R.string.cadastro_campos ) );

            return true;

        }else if( TextUtils.isEmpty( confSenha ) ){

            edt_confi_senha.requestFocus();

            edt_confi_senha.setError( mResources.getString( R.string.cadastro_campos ) );

            return true;

        }else if( TextUtils.isEmpty( telefoneSeg ) ){

            edt_telefone_seguranca.requestFocus();

            edt_telefone_seguranca.setError( mResources.getString( R.string.cadastro_campos ) );

            return true;

        }else if( TextUtils.isEmpty( confTelefoneSeg ) ){

            edt_conf_telefone_seguranca.requestFocus();

            edt_conf_telefone_seguranca.setError( mResources.getString( R.string.cadastro_campos ) );

            return true;

        }

        return false;
    }

}
