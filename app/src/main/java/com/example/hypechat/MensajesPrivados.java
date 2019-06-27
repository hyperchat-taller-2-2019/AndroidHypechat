package com.example.hypechat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MensajesPrivados extends Fragment {

    private TextView error_msj;
    private EditText buscar;
    private Button agregar_chat;
    private RecyclerView lista_chats;
    private JSONArray emails;
    private ProgressDialog progressDialog;
    private AdapterMiembros adaptador_para_usuarios;
    private ValidadorDeCampos validador;

    private final String URL_MSJ_PRIVADOS = "https://secure-plateau-18239.herokuapp.com/privateChats/";
    private final String URL_INFO_PRIVADO ="https://secure-plateau-18239.herokuapp.com/privateChat/";
    private final String URL_CREAR_PRIVADO_ORG = "https://secure-plateau-18239.herokuapp.com/privateChat";


    //metodo que se ejecuta cuando tocamos algun tarjeta de la recycleview
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            obtenerIDChatPrivado(adaptador_para_usuarios.obtenerItemPorPosicion(position));

            //Toast.makeText(getContext(), "TOCASTE el usuario: " + position, Toast.LENGTH_SHORT).show();

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chats_privados,container,false);

        error_msj = (TextView) view.findViewById(R.id.error_buscar_email);
        error_msj .setVisibility(View.INVISIBLE);
        buscar = (EditText) view.findViewById(R.id.text_ingresar_email);
        agregar_chat = (Button) view.findViewById(R.id.button_agregar_chat_privado);
        lista_chats = (RecyclerView) view.findViewById(R.id.lista_chats_privados);

        validador = new ValidadorDeCampos();
        adaptador_para_usuarios = new AdapterMiembros(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        lista_chats.setLayoutManager(l);
        lista_chats.setAdapter(adaptador_para_usuarios);
        adaptador_para_usuarios.setOnItemClickListener(this.onItemClickListener);

        agregar_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_msj .setVisibility(View.INVISIBLE);
                if(validador.isNotCampoVacio(buscar.getText().toString(),getContext(),"email")){
                    crearPrivado(buscar.getText().toString());
                }





            }
        });

        getMsjPrivados();

        return view;

    }


    private void getMsjPrivados(){
        Log.i("INFO", "Obtengo los msj privados del usuario ");
        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo chats privados del usuario...",true);


        String URL = URL_MSJ_PRIVADOS + Usuario.getInstancia().getToken()+"/";


        Log.i("INFO", "Json Request getCanales, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        responseListaMsjPrivados(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
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


    private void responseListaMsjPrivados(JSONObject response){
        Log.i("INFO", "Actualizo lista de chats");
        try {
            Log.i("INFO","Actualizo los chats del listado para mostrar");
            emails = response.getJSONArray("msjs");
            adaptador_para_usuarios.vaciar_lista();

            for(int i=0;i< emails.length();i++){
                String email = emails.getString(i);
                adaptador_para_usuarios.agregarMiembro(email);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerIDChatPrivado(String email_contacto){
        Log.i("INFO", "Obtengo id del chat privado");
        progressDialog = ProgressDialog.show(getActivity(),"Hypechat","Obteniendo info del chat privado...",true);

        String URL = URL_INFO_PRIVADO +Usuario.getInstancia().getToken()+"/"+email_contacto;


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
            canal = res.getJSONObject("private_msj");

            String id = canal.getString("_id");
            String name = canal.getString("name");
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,new ChatPrivateFragment(id,name));
            //Esta es la linea clave para que vuelva al fragmento anterior!
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
            fragmentManager.executePendingTransactions();

            //Me traigo el fragmento sabiendo que es el de ChatFragment para cargarle la información
            ChatPrivateFragment chat = (ChatPrivateFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            chat.setSalaDeChat();
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
            requestBody.put("token", Usuario.getInstancia().getToken());
            requestBody.put("email", email);

        } catch (JSONException except) {
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }


        Log.i("INFO", "Json Request agregar canal, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CREAR_PRIVADO_ORG, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        buscar.getText().clear();
                        error_msj .setVisibility(View.INVISIBLE);
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
                                error_msj.setText("No existe el email ingresado en el sistema");
                                error_msj .setVisibility(View.VISIBLE);
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



}
