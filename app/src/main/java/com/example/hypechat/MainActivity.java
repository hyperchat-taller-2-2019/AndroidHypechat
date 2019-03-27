package com.example.hypechat;


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
                            String texto = "Tu nombre es: " + response.getString("nombre") + "\nTu apodo es: "
                                    + response.getString("apodo") + "\nTu mail es: " + response.getString("email");

                            mostrarenpantalla(texto);
                            }
                            catch (JSONException error){
                                mostrarenpantalla(error.getMessage());
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

    private void mostrarenpantalla(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
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

        this.validador = new ValidadorDeCampos();
        this.textoEmail = (EditText) findViewById(R.id.et_email);
        this.textoPassword = (EditText) findViewById(R.id.et_password);

    }




}
