package com.example.hypechat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BuscarOrganizacion extends Fragment {

    private EditText textName;
    private ValidadorDeCampos validador;
    private ProgressDialog progress;
    private Button crearOrganizacion;
    private Button cancelar;
    private EditText nombre;
    private EditText id;
    private TextView mensaje_error, mostrar_id;
    private String mensaje;
    private ProgressDialog progressDialog;
    private String URL_CHECK_ID = "https://secure-plateau-18239.herokuapp.com/idOrganizationValid/";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_crear_organizacion,container,false);


        return view;

    }



}
