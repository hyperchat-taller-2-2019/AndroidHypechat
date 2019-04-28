package com.example.hypechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private ValidadorDeCampos validador;
    private ProgressDialog progress;

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
        this.validador = new ValidadorDeCampos();


    }

    public void registrarse (View view){
        String nombre = this.textName.getText().toString();
        String apodo = this.textDisplayName.getText().toString();
        String email = this.textEmail.getText().toString();
        String contraseña = this.textPass.getText().toString();

        if (this.validador.areValidRegisterFields(nombre,apodo,email,contraseña,this)){
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
            }
            catch (JSONException except){
                Toast.makeText(this, except.getMessage(), Toast.LENGTH_SHORT).show();
            }

            //HAGO REQUEST
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, URL_REGISTRO, requestBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("REGISTRO HOlLAAAAAA\n");
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
