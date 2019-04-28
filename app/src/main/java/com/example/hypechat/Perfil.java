package com.example.hypechat;


import android.app.Dialog;
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


public class Perfil extends Fragment {

    private View vista;

    private EditText nombre_perfil, apodo_perfil, email_perfil, contraseña_perfil;
    private Button cambiarContraseña, modificarPerfil;
    private Dialog dialog_cambiar_psw;
    private ValidadorDeCampos validador;
    private String password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.perfil,container,false);

        dialog_cambiar_psw = new Dialog(getActivity());
        modificarPerfil = (Button)view.findViewById(R.id.boton_modificar_perfil);
        cambiarContraseña = (Button) view.findViewById(R.id.boton_cambiar_contraseña);

        modificarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para modificar perfil");

            }
        });

        validador = new ValidadorDeCampos();

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
                        Log.i("INFO: ", "Validando que los datos sean correctos!");
                        Log.i("INFO:", "passwordActual:" + password);
                        Log.i("INFO:", "passwordActualInsertado:" + pass_viejo.getText().toString());
                        Log.i("INFO:", "passwordNuevoInsertado:" + pass_nuevo.getText().toString());
                        Log.i("INFO:", "passwordNuevoRepetido:" + pass_nuevo_bis.getText().toString());
                        if (validador.isValidPasswordChange(password,pass_viejo.getText().toString(),pass_nuevo.getText().toString(),pass_nuevo_bis.getText().toString(), getActivity())){

                            Log.i("INFO: ", "Los datos son correctos!");
                            Log.i("TO DO","hacer el request para cambiar el password!");

                            Toast.makeText(getActivity(), "La contraseña ha sido modificada con Exito!", Toast.LENGTH_LONG).show();
                            dialog_cambiar_psw.dismiss();
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

    public void completarDatosPerfil(String nombre, String apodo, String email, String contraseña, Boolean  soy_yo){
        this.setNombrePerfil(nombre);
        this.setApodoPerfil(apodo);
        this.setEmailPerfil(email);
        this.password = contraseña;

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
