package com.example.hypechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class MainActivity extends AppCompatActivity {


    //Este es un ejemplo de una request simple GET hecha con el singleton de la clase HttpConexionSingleton
    public void makeRequest (View view){
        Toast.makeText(this, "por hacer request!", Toast.LENGTH_SHORT).show();
        String url = "https://siu-api.herokuapp.com/";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Algo salio mal con la Request", Toast.LENGTH_LONG).show();
                    }
                });
        HttpConexionSingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
    }

}
