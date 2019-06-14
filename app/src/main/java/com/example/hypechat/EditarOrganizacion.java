package com.example.hypechat;

import android.app.Dialog;
import android.app.ProgressDialog;
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

public class EditarOrganizacion extends Fragment {

    private ValidadorDeCampos validador;
    private TextView nombre_titulo;
    private EditText nombre;
    private EditText id;
    private EditText bienvenida;
    private Button cancelar;
    private Button guardar_cambio;
    private Button cambiar_pass;
    private Button usuarios;
    private String password, token;
    private Boolean owner;
    private String org_id, org_nombre;
    private String URL_INFO = "https://secure-plateau-18239.herokuapp.com/organization/";
    private String URL_CAMBIO_NOMBRE = "https://secure-plateau-18239.herokuapp.com/organization/name";
    private String URL_CAMBIO_PASSWORD = "https://secure-plateau-18239.herokuapp.com/organization/password";
    private String URL_CAMBIO_MENSAJE = "https://secure-plateau-18239.herokuapp.com/welcomeOrganization";
    private Dialog dialog_cambiar_psw;
    private ProgressDialog progressDialog;
    private JSONArray members;
    private JSONArray moderators;

    public EditarOrganizacion(String organizacion_id) {
        this.org_id = organizacion_id;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_editar_organizacion, container, false);

        validador = new ValidadorDeCampos();
        cancelar = (Button) view.findViewById(R.id.button_editarOrg_cancelar);
        guardar_cambio = (Button) view.findViewById(R.id.button_editarOrg_guardar);
        cambiar_pass = (Button) view.findViewById(R.id.cambiar_contraseña);
        usuarios = (Button) view.findViewById(R.id.usuarios_org);
        nombre = (EditText) view.findViewById(R.id.name_edit_organizacion);
        id = (EditText) view.findViewById(R.id.id_organizacion);
        bienvenida = (EditText) view.findViewById(R.id.welcome_organizacion);
        nombre_titulo = (TextView) view.findViewById(R.id.titulo_organizacion);
        nombre_titulo.setText(org_nombre);
        disableButtons();

        dialog_cambiar_psw = new Dialog(getActivity());
        completarInformacionOrganizacion();

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Cancelar la edicion de la organizacion: " + nombre_titulo.getText().toString());

                getFragmentManager().popBackStackImmediate();


            }
        });

        guardar_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Apretaste para guardar cambios de una organizacion");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);

                if (validador.isValidName(nombre.getText().toString(), getContext()) && validador.isNotCampoVacio(bienvenida.getText().toString(),getContext(),"mensaje de bienvenida")) {
                        enviarCambioNombre();

                }




            }
        });

        cambiar_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Apretaste para cambiar password de una organizacion");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                dialog_cambiar_psw.setContentView(R.layout.popup_cambiar_password);

                Button b_cambiar_contrasenia = (Button) dialog_cambiar_psw.findViewById(R.id.aceptar_cambio_contrasenia);
                ImageView b_cancelar_cambio = (ImageView) dialog_cambiar_psw.findViewById(R.id.boton_cancelar_cambio_contrasenia);

                final EditText pass_viejo = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_viejo);
                final EditText pass_nuevo = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_nuevo);
                final EditText pass_nuevo_bis = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_nuevo_bis);

                b_cancelar_cambio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_cambiar_psw.dismiss();
                    }
                });

                b_cambiar_contrasenia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.i("INFO: ", "Validando que los datos sean correctos!");

                        Log.i("INFO:", "passwordActual:" + password);
                        Log.i("INFO:", "passwordActualInsertado:" + pass_viejo.getText().toString());
                        Log.i("INFO:", "passwordNuevoInsertado:" + pass_nuevo.getText().toString());
                        Log.i("INFO:", "passwordNuevoRepetido:" + pass_nuevo_bis.getText().toString());

                        if (validador.isValidPasswordChange(password, pass_viejo.getText().toString(), pass_nuevo.getText().toString(), pass_nuevo_bis.getText().toString(), getActivity())) {

                            Log.i("INFO: ", "Los datos son correctos!");
                            Log.i("INFO", "hacer el request para cambiar el password!");


                            JSONObject cambiar_psw_body = new JSONObject();
                            try {
                                cambiar_psw_body.put("token", token);
                                cambiar_psw_body.put("organizationID", id.getText().toString());
                                cambiar_psw_body.put("psw", pass_nuevo.getText().toString());
                            } catch (JSONException except) {
                                Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            progressDialog = ProgressDialog.show(getContext(), "Hypechat", "Cambiando Contraseña...",
                                    true);

                            cambiarPswRequest(cambiar_psw_body);
                        }
                    }
                });
                dialog_cambiar_psw.show();

            }
        });

        usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("INFO", "Apretaste para ver los usuarios de una organizacion");
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

                Boolean permiso_agregar_usuarios = false;
                if(owner) permiso_agregar_usuarios = true;
                else {
                    for (int i = 0; i < moderators.length(); i++) {
                        try {
                            if (moderators.getString(i).equals(Usuario.getInstancia().getEmail())) {
                                permiso_agregar_usuarios = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                usuarios.completarinfo(id.getText().toString(), password, token, permiso_agregar_usuarios);



            }
        });



        return view;

    }

    private void enviarCambioNombre() {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("organizationID", this.id.getText().toString());
            requestBody.put("name", this.nombre.getText().toString());
        } catch (JSONException except) {
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, URL_CAMBIO_NOMBRE, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        enviarCambioMensaje();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode) {
                            case (500):
                                Toast.makeText(getActivity(), "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde!", Toast.LENGTH_LONG).show();
                                break;
                            case (405):
                                Toast.makeText(getActivity(), "Server error!", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                //Toast.makeText(LoginActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void enviarCambioMensaje() {
        Log.i("INFO", "Envio de cambios del mensaje de bienvenida de la organizacion:  "+this.id.getText().toString());
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("organizationID", this.id.getText().toString());
            requestBody.put("welcome", this.bienvenida.getText().toString());
        } catch (JSONException except) {
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, URL_CAMBIO_MENSAJE, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        //orga.actualizarDatos();
                        getFragmentManager().popBackStackImmediate();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode) {
                            case (500):
                                Toast.makeText(getActivity(), "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde!", Toast.LENGTH_LONG).show();
                                break;
                            case (405):
                                Toast.makeText(getActivity(), "Server error!", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                Toast.makeText(getActivity(), "Organizacion id no existe", Toast.LENGTH_LONG).show();
                                break;
                                //Toast.makeText(LoginActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void procesarInfo(JSONObject response) {
        try {
            JSONObject orga = response.getJSONObject("organization");
            this.moderators = orga.getJSONArray("moderators");
            this.members = orga.getJSONArray("members");
            this.nombre_titulo.setText(orga.getString("name"));
            this.nombre.setText(orga.getString("name"));
            this.org_nombre = orga.getString("name");
            this.id.setText(orga.getString("id"));
            this.password = orga.getString("psw");
            this.bienvenida.setText(orga.getString("welcome"));
            this.owner = false;
            for (int i = 0; i < orga.getJSONArray("owner").length(); i++){
                if (orga.getJSONArray("owner").getString(i).equals(Usuario.getInstancia().getEmail())){
                    this.owner = true;
                }
            }
            if (this.owner) {
                enableButtons();
            } else {
                disableButtons();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void enableButtons() {
        this.nombre.setEnabled(true);
        this.cambiar_pass.setVisibility(View.VISIBLE);
        this.cambiar_pass.setEnabled(true);
        this.usuarios.setEnabled(true);
        this.bienvenida.setEnabled(true);
    }

    private void disableButtons() {
        this.nombre.setEnabled(false);
        this.cambiar_pass.setEnabled(false);
        this.cambiar_pass.setVisibility(View.INVISIBLE);
        this.usuarios.setEnabled(true);
        this.bienvenida.setEnabled(false);

    }


    private void cambiarPswRequest(final JSONObject cambiar_psw_body) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, URL_CAMBIO_PASSWORD, cambiar_psw_body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("INFO", "La contraseña se modifico correctamente!");

                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "La contraseña ha sido modificada con Exito!", Toast.LENGTH_LONG).show();
                        dialog_cambiar_psw.dismiss();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode) {
                            case (404):
                                Toast.makeText(getActivity(),
                                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                                break;
                            case (400):
                                Toast.makeText(getActivity(),
                                        "Server error!", Toast.LENGTH_LONG).show();
                                break;
                            case (500):
                                Toast.makeText(getActivity(),
                                        "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    public void completarInformacionOrganizacion() {

        this.token = Usuario.getInstancia().getToken();

        String URL = URL_INFO + this.token + "/" + this.org_id;

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



}
