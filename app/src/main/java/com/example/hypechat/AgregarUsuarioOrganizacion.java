package com.example.hypechat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AgregarUsuarioOrganizacion extends Fragment {


    private Button agregarUser;
    private Button finalizar;
    private EditText email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_add_users,container,false);

        this.email = (EditText) view.findViewById(R.id.edit_email);

        agregarUser = (Button)view.findViewById(R.id.r_invitar_usuario);

        agregarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Agregaste un nuevo usuario");
                email.getText().clear();
                Toast.makeText(getActivity(), "Usuario Agregado con exito.", Toast.LENGTH_LONG).show();


            }
        });

        finalizar = (Button)view.findViewById(R.id.r_finalizar_creacion_organizacion);

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Finaliza la actividad de agregar usuarios.");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new Organizaciones());
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();


            }
        });

        return view;

    }

}
