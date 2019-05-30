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
import android.widget.TextView;

public class VerUsuariosOrganizacion extends Fragment {

    private Button agregar_usuarios;
    private TextView eliminar;
    private Button volver;
    private String id, password, token;
    private Boolean permiso_editar;
    private android.support.v7.widget.RecyclerView lista_usuarios;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usuarios_organization, container, false);
        agregar_usuarios = (Button) view.findViewById(R.id.agregar_usuario);
        eliminar = (TextView) view.findViewById(R.id.eliminar_usuario);
        volver = (Button) view.findViewById(R.id.button_volverEditarOrg);
        lista_usuarios = (android.support.v7.widget.RecyclerView) view.findViewById(R.id.lista_organizacion_usuarios);

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Volver a la edicion de la organizacion");

                getFragmentManager().popBackStackImmediate();


            }
        });

        agregar_usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Log.i("INFO", "Apretaste para agregar usuario a una organizacion");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new AgregarUsuarioOrganizacion());
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();

                //Me traigo el fragmento sabiendo que es el de perfil para cargarle la información
                AgregarUsuarioOrganizacion add_Usuario = (AgregarUsuarioOrganizacion) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                add_Usuario.completarOrganizacionID(id, false, password, token);

            }
        });



        return view;
    }

    public void completarinfo(String id, String password, String token,Boolean permiso_editar) {
        this.id = id;
        this.password = password;
        this.token = token;
        this.permiso_editar = permiso_editar;
        if(permiso_editar) enableButtons();
        else disableButtons();

    }


    private void enableButtons() {
        this.eliminar.setVisibility(View.VISIBLE);
        this.agregar_usuarios.setVisibility(View.VISIBLE);
        this.agregar_usuarios.setEnabled(true);
        this.lista_usuarios.setEnabled(true);

    }

    private void disableButtons() {
        this.eliminar.setVisibility(View.INVISIBLE);
        this.agregar_usuarios.setVisibility(View.INVISIBLE);
        this.agregar_usuarios.setEnabled(false);
        this.lista_usuarios.setEnabled(false);
    }
}

