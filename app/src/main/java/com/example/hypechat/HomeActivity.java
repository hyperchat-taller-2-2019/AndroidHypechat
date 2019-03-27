package com.example.hypechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //SharedPref para almacenar datos de sesión
        this.sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();


        //Obtengo datos de shared preferences para ponerlos en la vista del layout
        String nombre = this.sharedPref.getString("nombre","No Name");
        String apodo = this.sharedPref.getString("apodo","No NickName");
        String email = this.sharedPref.getString("email","No Email");

        TextView nameText = (TextView) findViewById(R.id.nameText);
        TextView nickText = (TextView) findViewById(R.id.nickText);
        TextView emailText = (TextView) findViewById(R.id.emailText);

        nameText.setText(nombre);
        nickText.setText(apodo);
        emailText.setText(email);
    }
}
