package com.example.hypechat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PPrincipal extends Fragment {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //SharedPref para almacenar datos de sesión
       // this.sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        //this.sharedEditor = sharedPref.edit();




        //Obtengo datos de shared preferences para ponerlos en la vista del layout
       // String nombre = this.sharedPref.getString("nombre","No Name");
        //String apodo = this.sharedPref.getString("apodo","No NickName");
        //String email = this.sharedPref.getString("email","No Email");
        //String token = this.sharedPref.getString("token","No Token");

        /*TextView nameText = (TextView) findViewById(R.id.nameText);
        TextView nickText = (TextView) findViewById(R.id.nickText);
        TextView emailText = (TextView) findViewById(R.id.emailText);
        TextView tokenText = (TextView) findViewById(R.id.tokenText);

        nameText.setText(nombre);
        nickText.setText(apodo);
        emailText.setText(email);
        tokenText.setText(token);*/
        return inflater.inflate(R.layout.pag_ppal,container,false);

    }




}
