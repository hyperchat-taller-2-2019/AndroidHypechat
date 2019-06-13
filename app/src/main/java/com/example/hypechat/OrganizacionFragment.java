package com.example.hypechat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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

public class OrganizacionFragment extends Fragment {

    private SharedPreferences sharedPref;
    private ImageButton agregarCanal;;
    private ImageButton agregarMsjPrivado;
    private ImageButton editar_organizacion;
    private final String URL_CANALES = "https://secure-plateau-18239.herokuapp.com/channels/user";
    private final String URL_MSJ_PRIVADOS = "https://secure-plateau-18239.herokuapp.com/privateMsj";
    private final String URL_INFO_ORG = "https://secure-plateau-18239.herokuapp.com/organization/";
    private final String URL_INFO_CANAL = "https://secure-plateau-18239.herokuapp.com/channel/";
    private final String URL_INFO_PRIVADO ="";
    private SharedPreferences.Editor sharedEditor;
    private String token;
    private String organizacion_id;
    private String organizacion_name;
    private TextView titulo, mensaje;
    private View view;
    private ProgressDialog progressDialog;
    private Boolean owner;
    private String user_email;
    private String password;
    // private JSONArray organizaciones;
    private ImageButton ubicacion_equipo;
    private String mensaje_bienvenida;

    @SuppressLint("ValidFragment")
    public OrganizacionFragment(String id) {

        this.organizacion_id = id;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        this.view = inflater.inflate(R.layout.organizacion,container,false);
        /*this.sharedPref = getActivity().getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();

        this.organizacion_name = sharedPref.getString("organizacion_name","no organizacion");
        this.organizacion_id = sharedPref.getString("organizacion_id","no id");
*/
        this.user_email = Usuario.getInstancia().getEmail();

        this.token = Usuario.getInstancia().getToken();

        titulo = (TextView) view.findViewById(R.id.titulo_organizacion);
        mensaje = (TextView) view.findViewById(R.id.msj_bienvenida_organizacion);
        this.titulo.setText(organizacion_name);
        this.mensaje.setText(mensaje_bienvenida);
        actualizarDatos();




        agregarCanal= (ImageButton)view.findViewById(R.id.crear_canal);
        agregarMsjPrivado = (ImageButton)view.findViewById(R.id.crear_msj_privado);
        editar_organizacion = (ImageButton)view.findViewById(R.id.edit_Organizacion);
        ubicacion_equipo = (ImageButton) view.findViewById(R.id.btn_ubicacion_equipo);




        agregarCanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para agregar un canal en la organizacion: "+organizacion_id);
                //Toast.makeText(getActivity(),"Crear Canal", Toast.LENGTH_LONG).show();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new AgregarCanal(organizacion_id,password));
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();


            }
        });
        agregarMsjPrivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para crear una conversacion privada en la organizacion: "+organizacion_id);
                Toast.makeText(getActivity(),"Crear conversacion privada", Toast.LENGTH_LONG).show();
                //FragmentManager fragmentManager = getFragmentManager();
                //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.replace(R.id.fragment_container,new CrearOrganizacion());
                //Esta es la linea clave para que vuelva al fragmento anterior!
                //fragmentTransaction.addToBackStack(null);
                //fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                //fragmentManager.executePendingTransactions();


            }
        });

        editar_organizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para editar la organizacion: "+organizacion_id);
                //Toast.makeText(getActivity(),"Editar OrganizacionFragment", Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new EditarOrganizacion());
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();


                //Me traigo el fragmento sabiendo que es el de EditarOrganizacion para cargarle la información
                EditarOrganizacion editar_organization = (EditarOrganizacion) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                editar_organization.completarInformacionOrganizacion(organizacion_id,OrganizacionFragment.this);




            }
        });

        ubicacion_equipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Aca se abre el mapa para ver la localizacion de los miembros de la organizacion
                Intent intent = new Intent(getActivity(),UbicacionActivity.class);
                intent.putExtra("id_organizacion",organizacion_id);
                startActivity(intent);
            }
        });


        return view;
    }


    private JSONObject get_json_Request_Body(){
        //Preparo Body del POST
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("id",this.organizacion_id);
            requestBody.put("email",this.user_email);
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return requestBody;

    }


    private void getCanales(){
        Log.i("INFO", "Obtengo los canales del usuario en la organizacion: "+organizacion_id);
        progressDialog = ProgressDialog.show(getActivity(),"Hypechat","Obteniendo canales del usuario...",true);

        JSONObject request = get_json_Request_Body();

        Log.i("INFO", "Json Request getCanales, check http status codes "+ request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CANALES, request, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        responseListaCanales(response);
                        getMsjPrivados();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"Usuario  Invalido!", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                 Toast.makeText(getActivity(),"Organizacion invalida!", Toast.LENGTH_LONG).show();
                                 break;
                            case (405):
                                Toast.makeText(getActivity(),"No existe el usuario en esa organizacion!", Toast.LENGTH_LONG).show();
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

    private void getMsjPrivados(){
        Log.i("INFO", "Obtengo los msj privados del usuario en la organizacion: "+organizacion_id);
        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo msj privados del usuario...",true);

        JSONObject request = get_json_Request_Body();



        Log.i("INFO", "Json Request getCanales, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_MSJ_PRIVADOS, request, new Response.Listener<JSONObject>() {

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



    private void responseListaCanales(JSONObject response){
        try {
            Log.i("INFO",response.toString());
            JSONArray canales = response.getJSONArray("channel");
            ListView list = (ListView) view.findViewById(R.id.lista_canales);
            List<String> array = new ArrayList<>();
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_canales, array);
            for(int i = 0; i< canales.length(); i++){


                String name = canales.getString(i);
                array.add("# "+name);
            }

            //ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_orga, array);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView text1 = (TextView) view;
                    Toast.makeText(getActivity(),text1.getText().toString(), Toast.LENGTH_LONG).show();
                    //Ir al chat de ese canal
                    String nombre_sala = text1.getText().toString().replace("# ","");
                    obtenerIDChat(nombre_sala);

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerIDChat(String nombre_de_sala){
        Log.i("INFO", "Obtengo id del canal");
        progressDialog = ProgressDialog.show(getActivity(),"Hypechat","Obteniendo info del canal...",true);

        String URL = URL_INFO_CANAL +this.token+"/"+this.organizacion_id+"/"+nombre_de_sala;


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
            chat.setSalaDeChat(id,name,this.organizacion_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void responseListaMsjPrivados(JSONObject response){
        try {
            Log.i("INFO",response.toString());
            JSONArray msjPrivados = response.getJSONArray("msjs");
            ListView list = (ListView) view.findViewById(R.id.lista_msj_privados);
            List<String> array = new ArrayList<>();
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_canales, array);
            for(int i = 0; i< msjPrivados.length(); i++){


                String name = msjPrivados.getString(i);
                array.add(name);
            }

            //ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_orga, array);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView text1 = (TextView) view;
                    Toast.makeText(getActivity(),text1.getText().toString(), Toast.LENGTH_LONG).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void completarInfoOrganizacion(JSONObject orga) {
        try {
            JSONObject infoOrga = orga.getJSONObject("organization");
            this.organizacion_name = infoOrga.getString("name");
            this.titulo.setText(this.organizacion_name);
            this.mensaje_bienvenida = infoOrga.getString("welcome");
            this.mensaje.setText(mensaje_bienvenida);
            this.organizacion_id = infoOrga.getString("id");
            this.owner = false;
            for (int i = 0; i < infoOrga.getJSONArray("owner").length(); i++){
                if (infoOrga.getJSONArray("owner").getString(i).equals(Usuario.getInstancia().getEmail())){
                    this.owner = true;
                }
            }
            this.password = infoOrga.getString("psw");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCanales();

    }

    public void actualizarDatos(){
        Log.i("INFO", "Obteniendo datos de la organizacion");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo organizaciones del usuario...",true);

        //Preparo Body del POST

        String URL = URL_INFO_ORG + this.token+ "/" + organizacion_id;

        Log.i("INFO", "Json Request getOrganizaciones, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        completarInfoOrganizacion(response);

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



}
