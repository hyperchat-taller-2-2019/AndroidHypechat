package com.example.hypechat;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText textoEmail;
    private EditText textoPassword;
    private ValidadorDeCampos validador;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;
    private ProgressDialog progressDialog;

    //ESTA DESPUES DEBERIA SER LA DIRECCION DE DONDE ESTE EL SERVER REAL Y EL ENDPOINT CORRESPONDIENTE!
    private final String url = "https://virtserver.swaggerhub.com/taller2-hypechat/Hypechat/1.0.0/login";

    public void login (View view){
        Log.i("INFO", "Intentando realizar el login a la app");

        String email = this.textoEmail.getText().toString();
        String password = this.textoPassword.getText().toString();
        this.textoEmail.setText("");
        this.textoPassword.setText("");

        if (this.validador.areValidLoginFields(email,password,this)){

            this.progressDialog = ProgressDialog.show(this,"Hypechat","Validando datos...",
                    true);

            String URL_completa = this.url + "?email=" + email + "&contraseña=" + password;
            // SE PUEDE HACER EL REQUEST AL SERVER PARA LOGUEARSE !
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, URL_completa, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            procesarResponse(response);
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,
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
            if (response.getInt("valido") == 0){
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
                Toast.makeText(MainActivity.this,
                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException error){
            Toast.makeText(MainActivity.this,
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
        setContentView(R.layout.activity_main);
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

    }




}
