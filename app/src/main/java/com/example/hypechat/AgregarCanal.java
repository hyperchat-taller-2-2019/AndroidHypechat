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

public class AgregarCanal extends Fragment {

    private Button crear_canal;
    private Button volver;
    private String id, password, token;
    private Boolean permiso_editar;
    private RecyclerView lista_canales;
    private JSONArray channels;
    private AdapterCanal adaptador_para_canales;
    private ProgressDialog progressDialog;
    private final String URL_CANALES_DISPONIBLES = "https://secure-plateau-18239.herokuapp.com/channelsAvailable/user";
    private final String URL_AGREGAR_CANAL = "https://secure-plateau-18239.herokuapp.com/channel/user";
    private final String URL_CHECK_MODERADOR = "https://secure-plateau-18239.herokuapp.com/moderator/";
    private final String URL_INFO_CANAL = "https://secure-plateau-18239.herokuapp.com/channel/";




    //metodo que se ejecuta cuando tocamos algun tarjeta de la recycleview
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            agregarCanal(adaptador_para_canales.obtenerItemPorPosicion(position));

            //Toast.makeText(getContext(), "TOCASTE el usuario: " + position, Toast.LENGTH_SHORT).show();

        }
    };

    @SuppressLint("ValidFragment")
    public AgregarCanal(String organizacion_id, String password) {
        id = organizacion_id;
        this.password = password;
        this.token = Usuario.getInstancia().getToken();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_agregar_canal, container, false);
        crear_canal = (Button) view.findViewById(R.id.crear_canal);
        volver = (Button) view.findViewById(R.id.button_volverOrganizacion);
        lista_canales = (RecyclerView) view.findViewById(R.id.lista_canales_agregar);

        adaptador_para_canales = new AdapterCanal(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        lista_canales.setLayoutManager(l);
        lista_canales.setAdapter(adaptador_para_canales);
        adaptador_para_canales.setOnItemClickListener(this.onItemClickListener);



        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Volver a la organizacion");

                getFragmentManager().popBackStackImmediate();


            }
        });

        crear_canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Apretaste para crear un canal");

                /*//Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new CrearCanal());
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();

                //Me traigo el fragmento sabiendo que es el de perfil para cargarle la información
                //AgregarUsuarioOrganizacion add_Usuario = (AgregarUsuarioOrganizacion) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
*/
            }

        });


        cargarInfo();
        return view;
    }



    public void completarinfo(String id, String password, String token, Boolean permiso_editar) {
        this.id = id;
        this.password = password;
        this.token = token;
        this.permiso_editar = permiso_editar;
        if(permiso_editar) enableButtons();
        else disableButtons();
        //cargarMiembros();
        //actualizar_lista_members();

    }

    private void cargarInfo(){
        Log.i("INFO", "Obteniendo el permiso del usuario en la organizacion");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo el permiso del usuario en la organizacion...",true);

        //Preparo Body del POST
        String URL = URL_CHECK_MODERADOR + this.token+"/"+this.id+"/"+Usuario.getInstancia().getEmail();


        Log.i("INFO", "Json Request get info organization, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        permiso_editar = true;
                        enableButtons();
                        cargarCanales();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                permiso_editar = false;
                                disableButtons();
                                cargarCanales();
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
    private void cargarCanales(){
        Log.i("INFO", "Obteniendo los canales de la organizacion");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo canales de la organizacion...",true);

        //Preparo Body del POST
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("id", this.id);
            requestBody.put("email",Usuario.getInstancia().getEmail());
        } catch (JSONException except) {
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }


        Log.i("INFO", "Json Request get info organization, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CANALES_DISPONIBLES, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        actualizar_lista_canales(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"No existe el usuario", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                Toast.makeText(getActivity(),"No existe una organizacion con ese id", Toast.LENGTH_LONG).show();
                                break;
                            case (405):
                                Toast.makeText(getActivity(),"No existe el usuario en esa organizacion", Toast.LENGTH_LONG).show();
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




    public void actualizar_lista_canales(JSONObject res) {


        Log.i("INFO", "Actualizo lista de canales");
        try {
            Log.i("INFO","Actualizo los canales del listado para mostrar");
            channels = res.getJSONArray("channel");
            adaptador_para_canales.vaciar_lista();

            for(int i=0;i< channels.length();i++){
                String canal = channels.getString(i);
                adaptador_para_canales.agregarCanal(canal);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void enableButtons() {
        this.crear_canal.setVisibility(View.VISIBLE);
        this.crear_canal.setEnabled(true);

    }

    private void disableButtons() {
        this.crear_canal.setVisibility(View.INVISIBLE);
        this.crear_canal.setEnabled(false);
    }

    private void agregarCanal(final String canal_a_agregar) {
        Log.i("INFO", "Agregando el canal "+canal_a_agregar+" al usuario: "+Usuario.getInstancia().getEmail());
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Abriendo canal...",true);

        //Preparo Body del DELETE
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("id", this.id);
            requestBody.put("name", canal_a_agregar);
            requestBody.put("email",Usuario.getInstancia().getEmail());
            requestBody.put("mo_email", Usuario.getInstancia().getEmail());
        } catch (JSONException except) {
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }


        Log.i("INFO", "Json Request agregar canal, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_AGREGAR_CANAL, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        obtenerIDChat(canal_a_agregar);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"No existe un usuario con ese email", Toast.LENGTH_LONG).show();
                                break;
                            case (401):
                                Toast.makeText(getActivity(),"No existe el usuario en la organizacion", Toast.LENGTH_LONG).show();
                                break;
                            case (402):
                                Toast.makeText(getActivity(),"No existe el canal en la organizacion", Toast.LENGTH_LONG).show();
                                break;
                            case (403):
                                obtenerIDChat(canal_a_agregar);
                                break;
                            case (404):
                                Toast.makeText(getActivity(),"No existe la organizacion", Toast.LENGTH_LONG).show();
                                break;
                            case (405):
                                Toast.makeText(getActivity(),"Canal privado, no tiene permiso para agregarlo", Toast.LENGTH_LONG).show();
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


    private void obtenerIDChat(String canal_a_agregar){
        Log.i("INFO", "Obtengo id del canal");
        progressDialog = ProgressDialog.show(getActivity(),"Hypechat","Obteniendo info del canal...",true);

        String URL = URL_INFO_CANAL +this.token+"/"+this.id+"/"+canal_a_agregar;

        Log.i("INFO", "Json Request getCanales, check http status codes ");

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
            canal = res.getJSONObject("channel");
            String id = canal.getString("_id");
            String name = canal.getString("name");
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,new ChatFragment());
            //Esta es la linea clave para que vuelva al fragmento anterior!
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
            fragmentManager.executePendingTransactions();

            //Me traigo el fragmento sabiendo que es el de ChatFragment para cargarle la información
            ChatFragment chat = (ChatFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            chat.setSalaDeChat(id,name,this.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}