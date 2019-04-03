package com.example.hypechat;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends AppCompatActivity {

    private EditText textoEmail;
    private EditText textoPassword;
    private ValidadorDeCampos validador;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;
    private ProgressDialog progressDialog;
    private CallbackManager callbackManager;
    private LoginButton facebookLogin;

    //ESTA DESPUES DEBERIA SER LA DIRECCION DE DONDE ESTE EL SERVER REAL Y EL ENDPOINT CORRESPONDIENTE!
    private final String URL = "https://virtserver.swaggerhub.com/taller2-hypechat/Hypechat/1.0.0/login";
    private final Integer USUARIO_VALIDO = 1;

    public void login (View view){
        Log.i("INFO", "Intentando realizar el login a la app");

        String email = this.textoEmail.getText().toString();
        String password = this.textoPassword.getText().toString();
        this.textoEmail.setText("");
        this.textoPassword.setText("");

        if (this.validador.areValidLoginFields(email,password,this)){

            this.progressDialog = ProgressDialog.show(this,"Hypechat","Validando datos...",
                    true);

            //Preparo Body del POST
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("email", email);
                requestBody.put("contraseña", password);
            }
            catch(JSONException except){
                Toast.makeText(this, except.getMessage(), Toast.LENGTH_SHORT).show();
            }

            // SE PUEDE HACER EL REQUEST AL SERVER PARA LOGUEARSE !
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, URL, requestBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            procesarResponse(response);
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,
                                    "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    });

            //Agrego la request a la cola para que se conecte con el server!
            HttpConexionSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }


    }

    private void procesarResponse(JSONObject response) {
        try{
            //Usuario valido?
            if (response.getInt("valido") == USUARIO_VALIDO){
                String token_response = response.getString("token");
                String apodo_response = response.getString("apodo");
                String nombre_response = response.getString("nombre");
                String email_response = response.getString("email");
                sharedEditor.putString("apodo",apodo_response);
                sharedEditor.putString("nombre",nombre_response);
                sharedEditor.putString("email",email_response);
                sharedEditor.putString("token",token_response);
                sharedEditor.apply();
                goHomeActivity();
            }
            else{
                Toast.makeText(LoginActivity.this,
                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException error){
            Toast.makeText(LoginActivity.this,
                    "Hubo un error con el Json de Respuesta", Toast.LENGTH_SHORT).show();;
        }
    }

    private void goHomeActivity() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void loginConFacebook (View view){
        Toast.makeText(this, "Aca te deberias loguear con Facebook!", Toast.LENGTH_SHORT).show();
    }

    public void registro (View view){
        Intent launchactivity= new Intent(this,Registro.class);
        startActivity(launchactivity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("INFO","Se esta iniciando la aplicación");

        //SharedPref para almacenar datos de sesión
        this.sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();


        //Borro data de Shared Pref
        this.sharedEditor.clear();
        this.sharedEditor.apply();

        Log.i("INFO", "Tomando referencias de la UI");
        //Referencias del layout
        this.validador = new ValidadorDeCampos();
        this.textoEmail = (EditText) findViewById(R.id.et_email);
        this.textoPassword = (EditText) findViewById(R.id.et_password);

        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) findViewById(R.id.b_facebook);

        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                goHomeActivity();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md;

                        md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        String something = new String(Base64.encode(md.digest(), 0));
                        //Toast.makeText(StartingPlace.this, something,
                        //  Toast.LENGTH_LONG).show();
                        Log.e("hash key", something);
                        Toast.makeText(getApplicationContext(), something, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NameNotFoundException e1) {
                    // TODO Auto-generated catch block
                    Log.e("name not found", e1.toString());
                }

                catch (NoSuchAlgorithmException e1) {
                    // TODO Auto-generated catch block
                    Log.e("no such an algorithm", e1.toString());
                }
                catch (Exception e1){
                    Log.e("exception", e1.toString());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


}
