package com.example.hypechat.View;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hypechat.Model.Login;
import com.example.hypechat.Presenter.LoginPresenter;
import com.example.hypechat.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class LoginView extends AppCompatActivity implements Login.View {

    private EditText textoEmail;
    private EditText textoPassword;
    private LoginPresenter presentador;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;

    private ProgressDialog progressDialog;
    private CallbackManager callbackManager;
    private LoginButton facebookLogin;



    public void login (View view){
        Log.i("INFO", "Intentando realizar el login a la app");

        String email = this.textoEmail.getText().toString();
        String password = this.textoPassword.getText().toString();
        this.textoEmail.setText("");
        this.textoPassword.setText("");

        presentador.login(email,password,this);

    }



    private void goHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    public void loginConFacebook (){
        presentador.loginConFacebook(this);

    }

    public void registro (View view){
        Intent launchactivity= new Intent(this, RegistroView.class);
        startActivity(launchactivity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("INFO","Se esta iniciando la aplicación");

        this.presentador = new LoginPresenter(this);

        //SharedPref para almacenar datos de sesión
        this.sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();


        //Borro data de Shared Pref
        this.sharedEditor.clear();
        this.sharedEditor.apply();

        Log.i("INFO", "Tomando referencias de la UI");
        //Referencias del layout

        this.textoEmail = (EditText) findViewById(R.id.et_email);
        this.textoPassword = (EditText) findViewById(R.id.et_password);

        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) findViewById(R.id.b_facebook);

        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginConFacebook();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void showHome() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showErrorMgs(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mostrarProcessDialog(String titulo, String mensaje) {
        this.progressDialog = ProgressDialog.show(this,titulo,"Validando datos...",
                true);
    }

    @Override
    public void limpiarProcessDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void guardarDato(String nombre, String dato) {
        sharedEditor.putString(nombre,dato);
        sharedEditor.apply();
    }




}