package com.example.hypechat;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class EditarCanal extends Fragment {

    private String organization_id, nombre_canal, token, desc, welcome;
    private EditText textName;
    private ValidadorDeCampos validador;
    private ProgressDialog progress;
    private Button miembros, guardar;
    private Button cancelar;
    private Switch privado;
    private TextView nombre;
    private EditText descripcion;
    private EditText mensaje;
    private Boolean edit_permision, is_privado, error_guardar_cambios;
    private JSONArray usuarios_canal;
    private ProgressDialog progressDialog;
    private String URL_INFO_CANAL = "https://secure-plateau-18239.herokuapp.com/channel/";
    private String URL_SET_DESCRIPCION = "https://secure-plateau-18239.herokuapp.com/description";
    private String URL_SET_WELCOME = "https://secure-plateau-18239.herokuapp.com/welcomeChannel";
    private String URL_SET_PRIVADO = "https://secure-plateau-18239.herokuapp.com/privateChannel";
    private String URL_INFO = "https://secure-plateau-18239.herokuapp.com/organization/";

    public EditarCanal(String org_id, String name) {
        this.organization_id = org_id;
        this.nombre_canal = name;


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_editar_canal,container,false);
        validador = new ValidadorDeCampos();
        cancelar = (Button)view.findViewById(R.id.button_crearCanal_cancelar);
        miembros = (Button) view.findViewById(R.id.button_crearCanal_siguiente);
        guardar = (Button) view.findViewById(R.id.button_editarCanal_guardar);
        nombre = (TextView) view.findViewById(R.id.text_nombre_canal);
        nombre.setText("# "+this.nombre_canal);
        descripcion = (EditText) view.findViewById(R.id.descripcion_canal);
        mensaje = (EditText) view.findViewById(R.id.msj_bienvenida_canal);
        privado = (Switch) view.findViewById(R.id.boton_privado);
        error_guardar_cambios  =false;
        completarInformacionOrganizacion();


        miembros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para ver los miembros del canal");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                if(is_privado){
                    //si es privado muestro solo los miembros del canal
                    Log.i("INFO","Se creo un canal publico");
                    Log.i("INFO","Se va a agregar usuarios al canal ya que es privado");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new AgregarUsuarioCanal(organization_id,nombre_canal));
                    //Esta es la linea clave para que vuelva al fragmento anterior!
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                    fragmentManager.executePendingTransactions();


                }else{
                    //muestro todos
                    Log.i("INFO", "Apretaste para ver los usuarios del canal");
                    //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                    //startActivity(launchactivity);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new VerUsuariosOrganizacion());
                    //Esta es la linea clave para que vuelva al fragmento anterior!
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                    fragmentManager.executePendingTransactions();

                    //Me traigo el fragmento sabiendo que es el de perfil para cargarle la información
                    VerUsuariosOrganizacion usuarios = (VerUsuariosOrganizacion) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);


                    usuarios.completarinfo(organization_id,null, token, false);
                }


            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Cancelar la edicion un canal");

                getFragmentManager().popBackStackImmediate();


            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Guardo la edicion del canal");
                progressDialog = ProgressDialog.show(getActivity(),"Hypechat","Guardando cambios...",true);
                enviar_cambio_descripcion();

            }
        });

        return view;

    }


    public void enviar_cambio_descripcion() {

        if(this.desc.equals(this.descripcion.getText().toString())) enviar_cambio_welcome();
        else {

            Log.i("INFO", "Editar canal, envio cambio descripcion");
            this.token = Usuario.getInstancia().getToken();

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("token", Usuario.getInstancia().getToken());
                requestBody.put("name", this.nombre_canal);
                requestBody.put("organizationID", this.organization_id);
                requestBody.put("description", this.descripcion.getText().toString());

            } catch (JSONException except) {
                Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Log.i("INFO", "Json Request , check http status codes");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, URL_SET_DESCRIPCION, requestBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println(response);
                            desc = descripcion.getText().toString();
                            enviar_cambio_welcome();

                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //progressDialog.dismiss();

                            switch (error.networkResponse.statusCode) {
                                case (404):
                                    error_guardar_cambios = true;
                                    //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                                case (500):
                                    error_guardar_cambios = true;
                                    //Toast.makeText(getActivity(), "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                            }
                        }
                    });

            //Agrego la request a la cola para que se conecte con el server!
            HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        }
    }

    public void enviar_cambio_welcome() {

        if(this.welcome.equals(this.mensaje.getText().toString())) enviar_cambio_private();
        else {

            Log.i("INFO", "Editar canal, envio cambio mensaje bienvenida");
            this.token = Usuario.getInstancia().getToken();

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("token", Usuario.getInstancia().getToken());
                requestBody.put("name", this.nombre_canal);
                requestBody.put("organizationID", this.organization_id);
                requestBody.put("welcome", this.mensaje.getText().toString());

            } catch (JSONException except) {
                Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Log.i("INFO", "Json Request , check http status codes");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, URL_SET_WELCOME, requestBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println(response);
                            welcome = mensaje.getText().toString();
                            enviar_cambio_private();

                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //progressDialog.dismiss();

                            switch (error.networkResponse.statusCode) {
                                case (404):
                                    error_guardar_cambios = true;
                                    //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                                case (500):
                                    error_guardar_cambios = true;
                                    //Toast.makeText(getActivity(), "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                            }
                        }
                    });

            //Agrego la request a la cola para que se conecte con el server!
            HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        }
    }

    public void enviar_cambio_private() {

        Log.i("INFO", "Editar canal, envio cambio privacidad" );
        Log.i("INFO", "Privacidad anterior: "+this.is_privado.toString() );
        Log.i("INFO", "Privacidad actual: "+this.privado.isChecked() );
        if(this.is_privado != this.privado.isChecked()){


            this.token = Usuario.getInstancia().getToken();

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("token", Usuario.getInstancia().getToken());
                requestBody.put("name", this.nombre_canal);
                requestBody.put("organizationID", this.organization_id);
                if(this.privado.isChecked()) {
                    requestBody.put("private", 1);
                }else{
                    requestBody.put("private", 0);
                }

            } catch (JSONException except) {
                Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Log.i("INFO", "Json Request , check http status codes");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, URL_SET_PRIVADO, requestBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println(response);
                            is_privado = privado.isChecked();


                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //progressDialog.dismiss();

                            switch (error.networkResponse.statusCode) {
                                case (404):
                                    error_guardar_cambios = true;
                                    //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                                case (500):
                                    error_guardar_cambios = true;
                                    //Toast.makeText(getActivity(), "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                            }
                        }
                    });

            //Agrego la request a la cola para que se conecte con el server!
            HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        }
        progressDialog.dismiss();
        if(error_guardar_cambios){
            Toast.makeText(getActivity(), "Surgio un error guardando los cambios, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
            error_guardar_cambios = true;
        }else{
            Toast.makeText(getActivity(), "Los cambios se guardaron correctamente", Toast.LENGTH_LONG).show();
        }

    }

    public void completarInformacionOrganizacion() {

        Log.i("INFO", "Editar canal, completar info organizacion");
        this.token = Usuario.getInstancia().getToken();

        String URL = URL_INFO + this.token + "/" + this.organization_id;

        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        procesarInfo(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode) {
                            case (400):
                                //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(), "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void procesarInfo(JSONObject response) {
        try {
            JSONObject orga = response.getJSONObject("organization");

            this.edit_permision = false;
            for (int i = 0; i < orga.getJSONArray("owner").length(); i++){
                if (orga.getJSONArray("owner").getString(i).equals(Usuario.getInstancia().getEmail())){
                    this.edit_permision = true;
                }
            }
            for (int i = 0; i < orga.getJSONArray("moderators").length(); i++){
                if (orga.getJSONArray("owner").getString(i).equals(Usuario.getInstancia().getEmail())){
                    this.edit_permision = true;
                }
            }
             completarInfoCanal();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void completarInfoCanal() {
        Log.i("INFO", "Editar canal, completar info canal");

        this.token = Usuario.getInstancia().getToken();

        String URL = URL_INFO_CANAL + this.token + "/" + this.organization_id +"/"+ this.nombre_canal;

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
                            case (400):
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
        try {
            JSONObject orga = response.getJSONObject("channel");

            this.desc = orga.getString("description");
            this.descripcion.setText(this.desc);
            this.welcome = orga.getString("welcome");
            this.mensaje.setText(this.welcome);
            this.is_privado = orga.getBoolean("private");
            this.privado.setChecked(this.is_privado);
            if (this.edit_permision) {
                enableButtons();
            } else {
                disableButtons();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void enableButtons() {
        this.descripcion.setEnabled(true);
        this.mensaje.setEnabled(true);
        this.privado.setEnabled(true);
        this.guardar.setEnabled(true);
        this.guardar.setVisibility(View.VISIBLE);
    }

    private void disableButtons() {
        this.descripcion.setEnabled(false);
        this.mensaje.setEnabled(false);
        this.privado.setEnabled(false);

        this.guardar.setEnabled(false);
        this.guardar.setVisibility(View.INVISIBLE);
    }

}
