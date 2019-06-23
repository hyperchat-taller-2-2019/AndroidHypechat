package com.example.hypechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity {

    private EditText textName, textDisplayName, textEmail, textPass;
    private EditText preg_sec_1, preg_sec_2, res_preg_1, res_preg_2;
    private ValidadorDeCampos validador;
    private ProgressDialog progress;
    private Button volver, cancelar;
    String nombre;
    String apodo;
    String email;
    String contraseña;
    String pregunta1;
    String respuesta1;
    String pregunta2;
    String respuesta2;

    //CONSTANTES!!!
    private final String URL_REGISTRO = "https://secure-plateau-18239.herokuapp.com/signUp";
    private final Integer REGISTRO_EXITOSO = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        this.textName = (EditText) findViewById(R.id.name_registro);
        this.textDisplayName = (EditText) findViewById(R.id.displayName_registro);
        this.textEmail = (EditText) findViewById(R.id.email_registro);
        this.textPass = (EditText) findViewById(R.id.pass_registro);
        this.cancelar = (Button) findViewById(R.id.button_registro_cancelar);
        this.validador = new ValidadorDeCampos();

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Volver al login ");

                Intent launchactivity= new Intent(Registro.this,LoginActivity.class);
                startActivity(launchactivity);


            }
        });
    }

    public void  preguntasSecretas(View view){
        nombre = this.textName.getText().toString();
        apodo = this.textDisplayName.getText().toString();
        email = this.textEmail.getText().toString();
        contraseña = this.textPass.getText().toString();

        if (this.validador.areValidRegisterFields(nombre,apodo,email,contraseña,this)){
            setContentView(R.layout.seteo_preg_secretas);
            this.preg_sec_1 = (EditText) findViewById(R.id.preg_sec_1);
            this.preg_sec_2 = (EditText) findViewById(R.id.preg_sec_2);
            this.res_preg_1 = (EditText) findViewById(R.id.res_preg_1);
            this.res_preg_2 = (EditText) findViewById(R.id.res_preg_2);
            this.volver = (Button) findViewById(R.id.button_preg_cancelar);
            volver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("INFO", "Volver al ingreso de datos personales del registro");

                    Intent launchactivity= new Intent(Registro.this,LoginActivity.class);
                    startActivity(launchactivity);


                }
            });
        }

    }

    public void registrarse(View view){


        pregunta1 = this.preg_sec_1.getText().toString();
        pregunta2 = this.preg_sec_2.getText().toString();
        respuesta1 = this.res_preg_1.getText().toString();
        respuesta2 = this.res_preg_2.getText().toString();

        if (this.validador.isNotCampoVacio(pregunta1,this,"pregunta 1")&& this.validador.isNotCampoVacio(pregunta2,this,"pregunta 2") && this.validador.isNotCampoVacio(respuesta1,this,"respuesta 1") && this.validador.isNotCampoVacio(respuesta2,this,"respuesta 2")){
            //Aviso al usuario lo que pasa
            this.progress = ProgressDialog.show(this,"Hypechat","Registrando usuario...",
                    true);

            //ARMO BODY DEL REQUEST POST
            JSONObject requestBody = new JSONObject();
            try{
                requestBody.put("name",nombre);
                requestBody.put("nickname",apodo);
                requestBody.put("email",email);
                requestBody.put("psw",contraseña);
                requestBody.put("question1",pregunta1);
                requestBody.put("question2",pregunta2);
                requestBody.put("asw1",respuesta1);
                requestBody.put("asw2",respuesta2);

            }
            catch (JSONException except){
                Toast.makeText(this, except.getMessage(), Toast.LENGTH_SHORT).show();
            }

            //HAGO REQUEST
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, URL_REGISTRO, requestBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //System.out.println("REGISTRO HOlLAAAAAA\n");
                            System.out.println(response);
                            progress.dismiss();
                            procesarResponseDeRegistro(response);
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.dismiss();
                            switch (error.networkResponse.statusCode){
                                case (400):
                                    Toast.makeText(Registro.this,
                                            "Datos ingresados invalidos", Toast.LENGTH_LONG).show();
                                case (500):
                                    Toast.makeText(Registro.this,
                                            "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                            }

                        }
                    });

            //Agrego request de registro a la cola
            HttpConexionSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    private void procesarResponseDeRegistro(JSONObject response) {


        Toast.makeText(this, "El usuario ha sido registrado con exito!",
                        Toast.LENGTH_LONG).show();

                //ACA VUELVE A LA PANTALLA DE LOGIN PARA QUE INGRESE CON LAS CREDENCIALES REGISTRADAS
                //HABRIA QUE VER SI MEJOR YA LO HACEMOS ENTRAR AL SISTEMA!
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);


    }

}
