package com.example.hypechat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CrearCanal extends Fragment {

    private EditText textName;
    private ValidadorDeCampos validador;
    private ProgressDialog progress;
    private Button siguiente;
    private Button cancelar;
    private Switch privado;
    private EditText nombre;
    private EditText descripcion;
    private EditText mensaje;
    private String id;
    private ProgressDialog progressDialog;
    private String URL_CREAR_CANAL = "https://secure-plateau-18239.herokuapp.com/channel";
    private String URL_CHECK_NOMBRE_CANAL = "https://secure-plateau-18239.herokuapp.com/channelValid/";

    @SuppressLint("ValidFragment")
    public CrearCanal(String id) {
        this.id = id;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_crear_canal,container,false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        validador = new ValidadorDeCampos();
        cancelar = (Button)view.findViewById(R.id.button_crearCanal_cancelar);
        siguiente = (Button) view.findViewById(R.id.button_crearCanal_siguiente);
        nombre = (EditText) view.findViewById(R.id.name_canal);
        descripcion = (EditText) view.findViewById(R.id.descripcion_canal);
        mensaje = (EditText) view.findViewById(R.id.msj_bienvenida_canal);
        privado = (Switch) view.findViewById(R.id.boton_privado);



        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para crear un canal");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                if(validarCampos()){
                    chequearNombreDuplicado();
                }



            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Cancelar la creacion un canal");

                getFragmentManager().popBackStackImmediate();


            }
        });

        return view;

    }

    private boolean validarCampos() {
        String nombre_canal = this.nombre.getText().toString();
        String desc_canal = this.descripcion.getText().toString();
        String mensaje_canal = this.mensaje.getText().toString();
        if(validador.isNotCampoVacio(nombre_canal,getContext(),"nombre del canal")){
            return true;

        }else{
            return false;
        }


    }

    private void chequearNombreDuplicado() {

        Log.i("INFO", "Chequeo si el nombre del canal ya existe");

        Log.i("INFO", "Json Request , check http status codes");

        String URL = this.URL_CHECK_NOMBRE_CANAL+this.id+"/"+this.nombre.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        crearCanal_server();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"Ya existe un canal con ese nombre, intente con otro.", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde\"", Toast.LENGTH_LONG).show();


                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void crearCanal_server(){

        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Creando el canal...",true);

        String desc_canal = this.descripcion.getText().toString();
        String mensaje_canal = this.mensaje.getText().toString();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", this.nombre.getText().toString());
            requestBody.put("id", this.id);
            requestBody.put("owner", Usuario.getInstancia().getEmail());
            if(!desc_canal.isEmpty()) requestBody.put("description", desc_canal);
            if(!mensaje_canal.isEmpty()) requestBody.put("welcome", mensaje_canal);
            if(privado.isChecked()) requestBody.put("private", 1);
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Envio la creacion de la organizacion al server");


        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CREAR_CANAL, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        privacidadCanal();
                        //agregarUser();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                            case (400):
                                Toast.makeText(getActivity(),"El ID ya existe, intente con otro.", Toast.LENGTH_LONG).show();
                            case (404):
                                Toast.makeText(getActivity(),"El usuario es invalido", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void agregarUsers(){


/*
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
        add_Usuario.completarOrganizacionID(this.id.getText().toString(),true,this.psw.getText().toString(),Usuario.getInstancia().getToken(),null);
*/
    }

    private void privacidadCanal(){
        if(privado.isChecked()) {
            Log.i("INFO","Se creo un canal publico");
            Log.i("INFO","Se va a agregar usuarios al canal ya que es privado");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new AgregarUsuarioCanal(this.id,this.nombre.getText().toString(),true));
            //Esta es la linea clave para que vuelva al fragmento anterior!
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
            fragmentManager.executePendingTransactions();

        }else{
            Log.i("INFO","Se creo un canal publico");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new OrganizacionFragment(this.id));
            //Esta es la linea clave para que vuelva al fragmento anterior!
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
            fragmentManager.executePendingTransactions();


        }
    }



}