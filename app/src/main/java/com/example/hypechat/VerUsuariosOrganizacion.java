package com.example.hypechat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VerUsuariosOrganizacion extends Fragment {

    private Button agregar_usuarios;
    private TextView eliminar;
    private Button volver;
    private String id, password, token;
    private Boolean permiso_editar;
    private RecyclerView lista_usuarios;
    private JSONArray members;
    private AdapterMiembros adaptador_para_usuarios;
    private ProgressDialog progressDialog;
    private Dialog eliminar_usuario;
    private final String URL_INFO_ORG = "https://secure-plateau-18239.herokuapp.com/organization/";
    private final String URL_ELIMINAR_MEMBER = "https://secure-plateau-18239.herokuapp.com/member";



    //metodo que se ejecuta cuando tocamos algun tarjeta de la recycleview
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            if(permiso_editar) confirma_eliminar_usuario(adaptador_para_usuarios.obtenerItemPorPosicion(position));

            //Toast.makeText(getContext(), "TOCASTE el usuario: " + position, Toast.LENGTH_SHORT).show();

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usuarios_organization, container, false);
        agregar_usuarios = (Button) view.findViewById(R.id.agregar_usuario);
        eliminar = (TextView) view.findViewById(R.id.eliminar_usuario);
        volver = (Button) view.findViewById(R.id.button_volverEditarOrg);
        lista_usuarios = (RecyclerView) view.findViewById(R.id.lista_organizacion_usuarios);

        adaptador_para_usuarios = new AdapterMiembros(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        lista_usuarios.setLayoutManager(l);
        lista_usuarios.setAdapter(adaptador_para_usuarios);
        adaptador_para_usuarios.setOnItemClickListener(this.onItemClickListener);

        eliminar_usuario = new Dialog(getContext());

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
                add_Usuario.completarOrganizacionID(id, false, password, token,VerUsuariosOrganizacion.this);

            }
        });



        return view;
    }

    public void completarinfo(String id, String password, String token, Boolean permiso_editar) {
        this.id = id;
        this.password = password;
        this.token = token;
        this.permiso_editar = permiso_editar;
        if(permiso_editar) enableButtons();
        else disableButtons();
        cargarMiembros();
        //actualizar_lista_members();

    }

    public void cargarMiembros(){
        Log.i("INFO", "Obteniendo miembros de la organizacion");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo miembros de la organizacion...",true);

        //Preparo Body del POST

        String URL = URL_INFO_ORG + this.token+ "/" + this.id;

        Log.i("INFO", "Json Request get info organization, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        actualizar_lista_members(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                break;
                            //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void confirma_eliminar_usuario(final String email_a_eliminar){
        Log.i("INFO", "Apretaste para eliminar un usuario");
        eliminar_usuario.setContentView(R.layout.popup_eliminar_usuario);

        TextView email = (TextView)  eliminar_usuario.findViewById(R.id.text_email_remove);
        email.setText(email_a_eliminar);

        Button confirmar_eliminar = (Button) eliminar_usuario.findViewById(R.id.eliminar_usuario);
        ImageView b_cancelar = (ImageView) eliminar_usuario.findViewById(R.id.boton_cancelar_eliminar);



        b_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar_usuario.dismiss();
            }
        });


        confirmar_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(getContext(),"Hypechat","Eliminando usuario...",
                        true);
                eliminar_usuario(email_a_eliminar);
            }
        });
        eliminar_usuario.show();
    }

    private void eliminar_usuario(String email_a_eliminar) {
        Log.i("INFO", "Eliminando el miembro "+email_a_eliminar+" de la organizacion: "+this.id);
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Eliminando el miembro de la organizacion...",true);

        //Preparo Body del DELETE
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("organizationID", this.id);
            requestBody.put("userEmail",email_a_eliminar);
        } catch (JSONException except) {
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }


        Log.i("INFO", "Json Request get info organization, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, URL_ELIMINAR_MEMBER, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        eliminar_usuario.dismiss();
                        cargarMiembros();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (404):
                                Toast.makeText(getActivity(),"No existe la organizacion", Toast.LENGTH_LONG).show();
                                break;
                            //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }


    public void actualizar_lista_members(JSONObject res) {


        Log.i("INFO", "Actualizo lista de miembros");
        try {
            Log.i("INFO","Actualizo los miembros del listado para mostrar");
            JSONObject orga = res.getJSONObject("organization");
            members = orga.getJSONArray("members");
            adaptador_para_usuarios.vaciar_lista();

            for(int i=0;i< members.length();i++){
                String email = members.getString(i);
                adaptador_para_usuarios.agregarMiembro(email);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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

