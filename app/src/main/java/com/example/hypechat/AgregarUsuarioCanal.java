package com.example.hypechat;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class AgregarUsuarioCanal extends Fragment {

    private ValidadorDeCampos validador;
    private Button agregarUsers;
    private Button finalizar;
    private ProgressDialog progressDialog;
    private String URL_AGREGAR_USUARIOS_CANAL = "https://secure-plateau-18239.herokuapp.com/channel/users";
    private String URL_INFO_ORG = "https://secure-plateau-18239.herokuapp.com/organization/";
    private String URL_INFO_CANAL = "https://secure-plateau-18239.herokuapp.com/channel/";
    private String token;
    private String id, nombre;
    private RecyclerView lista_miembros;
    private JSONArray members_org, members_canal;
    private AdapterMiembrosCanal adaptador_para_usuarios_canal;
    private List<String> currentSelectedItems;
    private Boolean edit_permition =false;



    public AgregarUsuarioCanal(String id, String canal_nombre,Boolean permisos) {
        this.token = Usuario.getInstancia().getToken();
        this.id = id;
        this.nombre = canal_nombre;
        this.edit_permition = permisos;
    }

    //metodo que se ejecuta cuando tocamos algun tarjeta de la recycleview
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            //if(permiso_editar) confirma_eliminar_usuario(adaptador_para_usuarios.obtenerItemPorPosicion(position));

            Toast.makeText(getContext(), "TOCASTE el usuario: " + position, Toast.LENGTH_SHORT).show();

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_agregar_usuarios_canal,container,false);

        validador = new ValidadorDeCampos();

        finalizar = (Button)view.findViewById(R.id.button_finalizar_crear_canal);
        currentSelectedItems = new ArrayList<>();
        lista_miembros = (RecyclerView) view.findViewById(R.id.lista_organizacion_usuarios_a_agregar);

        adaptador_para_usuarios_canal = new AdapterMiembrosCanal(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        lista_miembros.setLayoutManager(l);
        lista_miembros.setAdapter(adaptador_para_usuarios_canal);
        if(edit_permition) lista_miembros.setEnabled(true);
        else lista_miembros.setEnabled(false);

        adaptador_para_usuarios_canal.setOnItemClickListener(new AdapterMiembrosCanal.OnItemCheckListener() {
            @Override
            public void onItemCheck(String item) {
                currentSelectedItems.add(item);
            }

            @Override
            public void onItemUncheck(String item) {
                currentSelectedItems.remove(item);
            }
        });


        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Finaliza la actividad de agregar usuarios al canal.");
                if(edit_permition) {
                    agregarUsuarios_al_Canal();
                }
                else{
                    Log.i("INFO", "Volviendo a la organizacion ");
                    irAOrganizacion();
                }


            }
        });
        cargarMiembros();
        return view;

    }



    private void agregarUsuarios_al_Canal(){

        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Actualizando los usuarios del canal...",true);

        JSONArray array = new JSONArray();
        for(int i=0;i<currentSelectedItems.size();i++){
            array.put(currentSelectedItems.get(i));
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token",this.token );
            requestBody.put("id", this.id);
            requestBody.put("name",this.nombre);
            requestBody.put("mo_email", Usuario.getInstancia().getEmail());
            requestBody.put("emails",array);

        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Agrego los usuarios al canal en el server "+currentSelectedItems.toString());

        Log.i("INFO", "Request body: "+requestBody.toString());
        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_AGREGAR_USUARIOS_CANAL, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        Log.i("INFO", "Actualizaste los usuarios del canal: "+nombre);
                        irAOrganizacion();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"El usuario moderador no existe", Toast.LENGTH_LONG).show();
                                break;
                            case (402):
                                Toast.makeText(getActivity(),"No es privado el canal o no existe", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                Toast.makeText(getActivity(),"No existe una organizacion con ese id", Toast.LENGTH_LONG).show();
                                break;
                            case (405):
                                Toast.makeText(getActivity(),"No tiene permisos para agregar al canal", Toast.LENGTH_LONG).show();
                                break;
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void irAOrganizacion() {
        Log.i("INFO","Se agregaron los usuarios al nuevo canal privado");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new OrganizacionFragment(this.id));
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

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
                        JSONObject orga = null;
                        try {
                            orga = response.getJSONObject("organization");
                            members_org = orga.getJSONArray("members");
                            adaptador_para_usuarios_canal.vaciar_lista();
                            completarInfoCanal();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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
    public void completarInfoCanal() {

        this.token = Usuario.getInstancia().getToken();

        String URL = URL_INFO_CANAL + this.token + "/" + this.id +"/"+ this.nombre;

        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        procesarInfoCanal(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode) {
                            case (404):
                                //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(), "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void procesarInfoCanal(JSONObject response) {
        Log.i("INFO", "Actualizo lista de miembros");
        try {
            Log.i("INFO","Actualizo los miembros del listado para mostrar con permiso de edicion: "+edit_permition);
            JSONObject orga = response.getJSONObject("channel");
            members_canal = orga.getJSONArray("members");



            for(int i=0;i< members_org.length();i++){
                String email = members_org.getString(i);

                MiembroCanal miembro = new MiembroCanal(email,false,edit_permition);
                if(this.members_canal.toString().contains(email)){
                    miembro.setPertenece(true);
                    currentSelectedItems.add(email);
                }
                adaptador_para_usuarios_canal.agregarMiembro(miembro);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
