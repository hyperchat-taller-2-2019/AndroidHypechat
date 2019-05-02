package com.example.hypechat;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class Perfil extends Fragment {

    private View vista;

    private EditText nombre_perfil, apodo_perfil, email_perfil;
    private Button cambiarContraseña, modificarPerfil;
    private Dialog dialog_cambiar_psw;
    private ValidadorDeCampos validador;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;
    private ProgressDialog progressDialog;
    private final String URL_CAMBIAR_PASSWORD = "https://secure-plateau-18239.herokuapp.com/psw";
    private final String URL_CAMBIAR_PERFIL = "https://secure-plateau-18239.herokuapp.com/profile";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.perfil,container,false);

        dialog_cambiar_psw = new Dialog(getActivity());
        modificarPerfil = (Button)view.findViewById(R.id.boton_modificar_perfil);
        cambiarContraseña = (Button) view.findViewById(R.id.boton_cambiar_contraseña);
        validador = new ValidadorDeCampos();
        this.sharedPref = getActivity().getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();

        modificarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para modificar perfil");
                nombre_perfil = (EditText) view.findViewById(R.id.nombre_perfil);
                apodo_perfil = (EditText) view.findViewById(R.id.apodo_perfil);
                email_perfil = (EditText) view.findViewById(R.id.email_perfil);

                String nombre_perfil_string = nombre_perfil.getText().toString();
                String apodo_perfil_string = apodo_perfil.getText().toString();
                String email_perfil_string = email_perfil.getText().toString();

                if (validador.isValidProfileChange(nombre_perfil_string,apodo_perfil_string,email_perfil_string,getActivity())){
                    Log.i("TO DO:", "Se puede mandar el request para modificar los datos del usuario!");
                }
            }
        });

        cambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para cambiar contraseña");
                dialog_cambiar_psw.setContentView(R.layout.popup_cambiar_password);

                Button b_cambiar_contrasenia = (Button) dialog_cambiar_psw.findViewById(R.id.aceptar_cambio_contrasenia);
                ImageView b_cancelar_cambio = (ImageView) dialog_cambiar_psw.findViewById(R.id.boton_cancelar_cambio_contrasenia);

                final EditText pass_viejo = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_viejo);
                final EditText pass_nuevo = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_nuevo);
                final EditText pass_nuevo_bis = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_nuevo_bis);

                b_cancelar_cambio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_cambiar_psw.dismiss();
                    }
                });

                b_cambiar_contrasenia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = sharedPref.getString("contraseña","");

                        Log.i("INFO: ", "Validando que los datos sean correctos!");

                        Log.i("INFO:", "passwordActual:" + password);
                        Log.i("INFO:", "passwordActualInsertado:" + pass_viejo.getText().toString());
                        Log.i("INFO:", "passwordNuevoInsertado:" + pass_nuevo.getText().toString());
                        Log.i("INFO:", "passwordNuevoRepetido:" + pass_nuevo_bis.getText().toString());

                        if (validador.isValidPasswordChange(password,pass_viejo.getText().toString(),pass_nuevo.getText().toString(),pass_nuevo_bis.getText().toString(), getActivity())){

                            Log.i("INFO: ", "Los datos son correctos!");
                            Log.i("INFO","hacer el request para cambiar el password!");

                            String token_usuario = sharedPref.getString("token","");

                            JSONObject cambiar_psw_body = new JSONObject();
                            try {
                                cambiar_psw_body.put("token", token_usuario);
                                cambiar_psw_body.put("newPsw", pass_nuevo.getText().toString());
                            }
                            catch(JSONException except){
                                Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            progressDialog = ProgressDialog.show(getContext(),"Hypechat","Cambiando Contraseña...",
                                    true);

                            cambiarPswRequest(cambiar_psw_body);
                        }
                    }
                });
                dialog_cambiar_psw.show();
            }
        });


        this.nombre_perfil = (EditText) view.findViewById(R.id.nombre_perfil);
        this.apodo_perfil = (EditText) view.findViewById(R.id.apodo_perfil);
        this.email_perfil = (EditText) view.findViewById(R.id.email_perfil);


        return view;

    }

    private void cambiarPswRequest(final JSONObject cambiar_psw_body) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, URL_CAMBIAR_PASSWORD, cambiar_psw_body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("INFO", "La contraseña se modifico correctamente!");

                        progressDialog.dismiss();

                        try{
                            sharedEditor.putString("contraseña",cambiar_psw_body.getString("newPsw"));
                            Toast.makeText(getActivity(), "La contraseña ha sido modificada con Exito!", Toast.LENGTH_LONG).show();
                        }catch (JSONException exception){
                            Log.i("INFO", exception.getMessage());
                        }

                        dialog_cambiar_psw.dismiss();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),
                                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),
                                        "Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                Toast.makeText(getActivity(),
                                        "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void completarDatosPerfil(String nombre, String apodo, String email, Boolean  soy_yo){
        this.setNombrePerfil(nombre);
        this.setApodoPerfil(apodo);
        this.setEmailPerfil(email);

        if (soy_yo){
            mostrarBotones();
            setEditTextNormales();
        }
        else{
            ocultarBotones();
            setearEditTextComoNoEditables();
        }
    }

    private void setearEditTextComoNoEditables() {
        this.nombre_perfil.setEnabled(false);
        this.apodo_perfil.setEnabled(false);
        this.email_perfil.setEnabled(false);
    }

    private void setEditTextNormales() {
        this.nombre_perfil.setEnabled(true);
        this.apodo_perfil.setEnabled(true);
        this.email_perfil.setEnabled(true);
    }

    private void mostrarBotones() {
        this.modificarPerfil.setVisibility(View.VISIBLE);
        this.cambiarContraseña.setVisibility(View.VISIBLE);
    }

    private void ocultarBotones() {
        this.modificarPerfil.setVisibility(View.GONE);
        this.cambiarContraseña.setVisibility(View.GONE);
    }

    private void setEmailPerfil(String email) {
        this.email_perfil.setText(email);
    }

    private void setNombrePerfil(String nombre){
        this.nombre_perfil.setText(nombre);
    }

    private void setApodoPerfil(String apodo){
        this.apodo_perfil.setText(apodo);
    }


}
