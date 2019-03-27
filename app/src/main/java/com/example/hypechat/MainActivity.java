package com.example.hypechat;


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

    //ESTA DESPUES DEBERIA SER LA DIRECCION DE DONDE ESTE EL SERVER REAL Y EL ENDPOINT CORRESPONDIENTE!
    private final String url = "https://hypechat-taller2-2019.herokuapp.com/autenticacion/login";

    public void login (View view){
        String email = this.textoEmail.getText().toString();
        String password = this.textoPassword.getText().toString();
        this.textoEmail.setText("");
        this.textoPassword.setText("");



        if (this.validador.areValidLoginFields(email,password,this)){
            String URL_completa = this.url + "?email=" + email + "&contrasenia=" + password;
            // SE PUEDE HACER EL REQUEST AL SERVER PARA LOGUEARSE !
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, URL_completa, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                String apodo_response = response.getString("apodo");
                                String nombre_response = response.getString("nombre");
                                String email_response = response.getString("email");
                                sharedEditor.putString("apodo",apodo_response);
                                sharedEditor.putString("nombre",nombre_response);
                                sharedEditor.putString("email",email_response);
                                sharedEditor.apply();
                                goHomeActivity();
                            }
                            catch (JSONException error){
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();;
                            }

                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
            //Agrego la request a la cola para que se conecte con el server!
            HttpConexionSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
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

        //SharedPref para almacenar datos de sesión
        this.sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();


        //Borro data de Shared Pref
        this.sharedEditor.clear();
        this.sharedEditor.apply();


        //Referencias del layout
        this.validador = new ValidadorDeCampos();
        this.textoEmail = (EditText) findViewById(R.id.et_email);
        this.textoPassword = (EditText) findViewById(R.id.et_password);

    }




}
