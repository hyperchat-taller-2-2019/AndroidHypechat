package com.example.hypechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;

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
        String token = this.sharedPref.getString("token","No Token");

        TextView nameText = (TextView) findViewById(R.id.nameText);
        TextView nickText = (TextView) findViewById(R.id.nickText);
        TextView emailText = (TextView) findViewById(R.id.emailText);
        TextView tokenText = (TextView) findViewById(R.id.tokenText);

        nameText.setText(nombre);
        nickText.setText(apodo);
        emailText.setText(email);
        tokenText.setText(token);



    }

    private void goLoginScreen() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void logout(View view){
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }


}
