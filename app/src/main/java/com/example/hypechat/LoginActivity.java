package com.example.hypechat;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hypechat.Service.MyFirebaseInstanceService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    private EditText textoEmail;
    private EditText email_recuperacion,  password_nuevo, password_nuevo_bis;
    private EditText textoPassword;
    private ValidadorDeCampos validador;
    private ProgressDialog progressDialog;
    private CallbackManager callbackManager;
    private LoginButton facebookLogin;
    private Dialog olvido_pass, preguntas, restaura_pass;
    private String token_recuperacion;


    //ESTA DESPUES DEBERIA SER LA DIRECCION DE DONDE ESTE EL SERVER REAL Y EL ENDPOINT CORRESPONDIENTE!
    private final String URL_LOGIN = "https://secure-plateau-18239.herokuapp.com/login";
    private final String URL_LOGIN_FACE = "https://virtserver.swaggerhub.com/taller2-hypechat/Hypechat/1.0.0/logFacebook";
    private final String URL_PREGUNTAS= "https://secure-plateau-18239.herokuapp.com/secretQuestions/";
    private final String URL_CHECK_RESPUESTAS = "https://secure-plateau-18239.herokuapp.com/answerQuestions/";
    private final String URL_CHANGE_PASS = "https://secure-plateau-18239.herokuapp.com/recoveredPassword";

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
        //Toast.makeText(this, "Restaurar contrasenia", Toast.LENGTH_SHORT).show();
        Log.i("INFO", "Apretaste para restaurar contraseña");
        olvido_pass.setContentView(R.layout.popup_olvido_password);

        Button mostrar_preg_secretas = (Button) olvido_pass.findViewById(R.id.mostrar_preguntas_secretas);
        ImageView b_cancelar_cambio = (ImageView) olvido_pass.findViewById(R.id.boton_cancelar_restaurar_pass);
        email_recuperacion = (EditText) olvido_pass.findViewById(R.id.rest_email);


        b_cancelar_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                olvido_pass.dismiss();
            }
        });

        mostrar_preg_secretas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(LoginActivity.this,"Hypechat","Verificando email...",
                        true);
                validarEmail(email_recuperacion.getText().toString());
            }
        });
        olvido_pass.show();


    }

    public void validarEmail(String email){
        Log.i("INFO", "Chequeo email valido");
        String URL = URL_PREGUNTAS+email;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        mostrarPreguntasSecretas(email_recuperacion.getText().toString(),response);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(LoginActivity.this,
                                        "No existe ese mail en el sistema.", Toast.LENGTH_LONG).show();
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

    public void mostrarPreguntasSecretas(String email,JSONObject res){
        Log.i("INFO", "Apretaste para mostrar preguntas secretas");
        olvido_pass.dismiss();
        preguntas.setContentView(R.layout.popup_preguntas_secretas);

        TextView pregunta1 = (TextView)  preguntas.findViewById(R.id.pregunta_1);
        TextView pregunta2 = (TextView)  preguntas.findViewById(R.id.pregunta_2);

        try {
            pregunta1.setText(res.getString("question1"));
            pregunta2.setText(res.getString("question2"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final EditText respuesta1 = (EditText) preguntas.findViewById(R.id.respuesta_1);
        final EditText respuesta2 = (EditText) preguntas.findViewById(R.id.respuesta_2);
        Button cambio_pass = (Button) preguntas.findViewById(R.id.aceptar_cambio_contrasenia);
        ImageView b_cancelar_cambio = (ImageView) preguntas.findViewById(R.id.boton_cancelar_restaurar_pass);



        b_cancelar_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preguntas.dismiss();
            }
        });


        cambio_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(LoginActivity.this,"Hypechat","Verificando respuestas...",
                        true);
                verificarRespuestas(respuesta1.getText().toString(),respuesta2.getText().toString());
            }
        });
        preguntas.show();
    }

    private void verificarRespuestas(String res1, String res2) {
        Log.i("INFO", "Verifico las respuestas a las preguntas");
        String URL = URL_CHECK_RESPUESTAS+email_recuperacion.getText().toString()+"/"+res1+"/"+res2;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        cambioPassword(response);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(LoginActivity.this,
                                        "No existe ese mail en el sistema.", Toast.LENGTH_LONG).show();
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

    private void cambioPassword(JSONObject response) {
        Log.i("INFO", "Muestro popup de cambio de contraseña");
        preguntas.dismiss();
        restaura_pass.setContentView(R.layout.popup_restaurar_password);

        try {
            token_recuperacion = response.getString("recoverPasswordToken");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        password_nuevo = (EditText) restaura_pass.findViewById(R.id.et_password_nuevo);
        password_nuevo_bis = (EditText) restaura_pass.findViewById(R.id.et_password_nuevo_bis);
        Button confirmar_cambio = (Button) restaura_pass.findViewById(R.id.aceptar_restauracion_contrasenia);
        ImageView b_cancelar_cambio = (ImageView) restaura_pass.findViewById(R.id.boton_cancelar_cambio_contrasenia);



        b_cancelar_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaura_pass.dismiss();
            }
        });


        confirmar_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validador.isValidPassword(password_nuevo.getText().toString(),LoginActivity.this)&& validador.areTwoStringsEqual(password_nuevo.getText().toString(),password_nuevo_bis.getText().toString(),LoginActivity.this,"Ambos passwords deben ser iguales")){
                    progressDialog = ProgressDialog.show(LoginActivity.this,"Hypechat","Cambiando password...",
                            true);
                    //Preparo Body del POST
                    JSONObject requestBody = new JSONObject();
                    try {
                        //como el response no entrega el password, me lo guardo aca.
                        //this.sharedEditor.putString("contraseña",password);

                        requestBody.put("email", email_recuperacion.getText().toString());
                        requestBody.put("recoverPasswordToken", token_recuperacion);
                        requestBody.put("newPassword",password_nuevo.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    cambiarPassword(requestBody);
                }

            }
        });
        restaura_pass.show();

    }

    private void cambiarPassword(JSONObject requestBody) {
        Log.i("INFO", "Verifico las respuestas a las preguntas");
        System.out.println(requestBody);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, URL_CHANGE_PASS, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        restaura_pass.dismiss();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(LoginActivity.this,
                                        "No existe ese mail en el sistema.", Toast.LENGTH_LONG).show();
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
        this.olvido_pass= new Dialog(this);
        this.preguntas = new Dialog(this);
        this.restaura_pass= new Dialog(this);

        String token_fb = MyFirebaseInstanceService.getToken(this);
        Log.i("INFO","TOKEN FIREBASE: "+ token_fb);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                // send it to server
                Log.i("INFO","TOKEN FIREBASE: "+ token);
            }
        });


        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) findViewById(R.id.b_facebook);

        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginConFacebook();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




}