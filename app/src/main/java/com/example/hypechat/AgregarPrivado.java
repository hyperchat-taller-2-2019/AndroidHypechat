package com.example.hypechat;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AgregarPrivado extends Fragment {


    private Button volver;
    private String id, token;
    private Boolean en_org;
    private RecyclerView lista_usuarios;
    private JSONArray usuarios;
    private AdapterMiembros adaptador_para_usuarios;
    private ProgressDialog progressDialog;
    private final String URL_INFO_ORG = "https://secure-plateau-18239.herokuapp.com/organization/";
    private final String URL_CREAR_PRIVADO_ORG = "https://secure-plateau-18239.herokuapp.com/privateChat";
    private final String URL_INFO_PRIVADO ="https://secure-plateau-18239.herokuapp.com/privateChat/";




    //metodo que se ejecuta cuando tocamos algun tarjeta de la recycleview
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            crearPrivado(adaptador_para_usuarios.obtenerItemPorPosicion(position));

            //Toast.makeText(getContext(), "TOCASTE el usuario: " + position, Toast.LENGTH_SHORT).show();

        }
    };

    @SuppressLint("ValidFragment")
    public AgregarPrivado(Boolean org, String id_org) {
        id = id_org;
        this.token = Usuario.getInstancia().getToken();
        this.en_org = org;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(en_org){
            view = inflater.inflate(R.layout.activity_usuarios_org_para_private, container, false);
        }else{
            view = inflater.inflate(R.layout.activity_agregar_canal, container, false);
        }

        volver = (Button) view.findViewById(R.id.button_volver_org);
        lista_usuarios = (RecyclerView) view.findViewById(R.id.lista_organizacion_usuarios);

        adaptador_para_usuarios = new AdapterMiembros(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        lista_usuarios.setLayoutManager(l);
        lista_usuarios.setAdapter(adaptador_para_usuarios);
        adaptador_para_usuarios.setOnItemClickListener(this.onItemClickListener);


        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Volver a la organizacion");

                getFragmentManager().popBackStackImmediate();


            }
        });


        cargarInfo();
        return view;
    }

    public void cargarInfo(){
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


    public void actualizar_lista_members(JSONObject res) {


        Log.i("INFO", "Actualizo lista de miembros");
        try {
            Log.i("INFO","Actualizo los miembros del listado para mostrar");
            JSONObject orga = res.getJSONObject("organization");
            usuarios = orga.getJSONArray("members");
            adaptador_para_usuarios.vaciar_lista();

            for(int i=0;i< usuarios.length();i++){
                String email = usuarios.getString(i);
                if(email.equals(Usuario.getInstancia().getEmail())) continue;
                adaptador_para_usuarios.agregarMiembro(email);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    private void crearPrivado(final String email) {
        Log.i("INFO", "Agregando el chat privado "+email+" al usuario: "+Usuario.getInstancia().getEmail());
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Abriendo chat privado...",true);

        //Preparo Body del DELETE
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("email", email);
            if(en_org) requestBody.put("id", this.id);

        } catch (JSONException except) {
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }


        Log.i("INFO", "Json Request agregar canal, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CREAR_PRIVADO_ORG, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        obtenerIDChatPrivado(email);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"Token invalido", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                Toast.makeText(getActivity(),"No existe el usuario en la organizacion", Toast.LENGTH_LONG).show();
                                break;
                            case (406):
                                Toast.makeText(getActivity(),"No existe la organizacion", Toast.LENGTH_LONG).show();
                                break;
                            case (405):
                                obtenerIDChatPrivado(email);
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


    private void obtenerIDChatPrivado(String email_contacto){
        Log.i("INFO", "Obtengo id del chat privado");
        progressDialog = ProgressDialog.show(getActivity(),"Hypechat","Obteniendo info del chat privado...",true);

        String URL = URL_INFO_PRIVADO +this.token+"/"+email_contacto;
        if(en_org){
            URL = URL +"/"+this.id;
        }

        Log.i("INFO", "Json Request get id de Canal, check http status codes:  ");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        irAlChat(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (404):
                                Toast.makeText(getActivity(),"Organizacion invalida!", Toast.LENGTH_LONG).show();
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


    private void irAlChat(JSONObject res) {
        JSONObject canal = null;
        try {
            if(en_org) {
                canal = res.getJSONObject("private_msj");
                String id = canal.getString("_id");
                String name = canal.getString("name");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ChatFragment(false));
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();

                //Me traigo el fragmento sabiendo que es el de ChatFragment para cargarle la información
                ChatFragment chat = (ChatFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                chat.setSalaDeChat(id, name, this.id);
            }else{
                canal = res.getJSONObject("private_msj");
                String id = canal.getString("_id");
                String name = canal.getString("name");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ChatPrivateFragment(id,name));
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();

                //Me traigo el fragmento sabiendo que es el de ChatFragment para cargarle la información
                ChatFragment chat = (ChatFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}