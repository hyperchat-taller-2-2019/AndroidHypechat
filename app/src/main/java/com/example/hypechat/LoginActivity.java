package com.example.hypechat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private EditText textoEmail;
    private EditText textoPassword;
    private ValidadorDeCampos validador;
    private ProgressDialog progressDialog;
    private CallbackManager callbackManager;
    private LoginButton facebookLogin;


    //ESTA DESPUES DEBERIA SER LA DIRECCION DE DONDE ESTE EL SERVER REAL Y EL ENDPOINT CORRESPONDIENTE!
    private final String URL_LOGIN = "https://secure-plateau-18239.herokuapp.com/login";
    private final String URL_LOGIN_FACE = "https://virtserver.swaggerhub.com/taller2-hypechat/Hypechat/1.0.0/logFacebook";


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
                //como el response no entrega el password, me lo guardo aca.
                //this.sharedEditor.putString("contraseña",password);
                Usuario.getInstancia().setPassword(password);

                requestBody.put("email", email);
                requestBody.put("psw", password);
            }
            catch(JSONException except){
                Toast.makeText(this, except.getMessage(), Toast.LENGTH_SHORT).show();
            }

            JsonRequest_login(URL_LOGIN, requestBody);

        }


    }

    public void restaurar(View view){
        Toast.makeText(this, "Restaurar contrasenia", Toast.LENGTH_SHORT).show();
    }

    public void JsonRequest_login(String URL, JSONObject requestBody) {
        // SE PUEDE HACER EL REQUEST AL SERVER PARA LOGUEARSE !
        Log.i("INFO", "Json Request login, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("11111111111111\n");
                        progressDialog.dismiss();
                        System.out.println(response);
                        procesarResponse(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(LoginActivity.this,
                                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(LoginActivity.this,
                                        "Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                Toast.makeText(LoginActivity.this,
                                        "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                            default:
                                Toast.makeText(LoginActivity.this, "Ocurrio un error!!!", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void procesarResponse(JSONObject response) {
        try{
            //Usuario valido?
            Log.i("INFO",response.toString());

            Usuario.getInstancia().setEmail(response.getString("email"));
            Usuario.getInstancia().setNombre(response.getString("name"));
            Usuario.getInstancia().setNickname(response.getString("nickname"));
            Usuario.getInstancia().setToken(response.getString("token"));

            goHomeActivity();

        }
        catch (JSONException error){
            Log.i("INFO",error.getMessage());
            Toast.makeText(LoginActivity.this,
                    "Hubo un error con el Json de Respuesta", Toast.LENGTH_SHORT).show();;
        }
    }

    private void goHomeActivity() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }




    public void loginConFacebook (){

        this.progressDialog = ProgressDialog.show(this,"Hypechat","Validando datos...",
                true);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", AccessToken.getCurrentAccessToken().getToken());

        }
        catch(JSONException except){
            Toast.makeText(this, except.getMessage(), Toast.LENGTH_SHORT).show();
        }

        JsonRequest_login(URL_LOGIN_FACE,requestBody);


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
        //Referencias del layout
        this.validador = new ValidadorDeCampos();
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




}