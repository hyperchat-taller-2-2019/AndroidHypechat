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

import org.json.JSONException;
import org.json.JSONObject;

public class EditarOrganizacion extends Fragment {

    private ValidadorDeCampos validador;
    private TextView nombre_titulo;
    private EditText nombre;
    private EditText id;
    private Button cancelar;
    private Button guardar_cambio;
    private Button cambiar_pass;
    private Button agregar_usuario;
    private String password, owner, token;
    private String URL_CAMBIO_NOMBRE = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/setNombreOrganizacion";
    private String URL_CAMBIO_PASSWORD = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/setPasswordOrganizacion";
    private Dialog dialog_cambiar_psw;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_editar_organizacion,container,false);
        validador = new ValidadorDeCampos();
        cancelar = (Button) view.findViewById(R.id.button_editarOrg_cancelar);
        guardar_cambio = (Button) view.findViewById(R.id.button_editarOrg_guardar);
        cambiar_pass = (Button) view.findViewById(R.id.cambiar_contraseña);
        agregar_usuario = (Button) view.findViewById(R.id.agregar_usuarios_org);
        nombre = (EditText) view.findViewById(R.id.name_edit_organizacion);
        id = (EditText) view.findViewById(R.id.id_organizacion);
        nombre_titulo = (TextView) view.findViewById(R.id.titulo_organizacion);
        dialog_cambiar_psw = new Dialog(getActivity());

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Cancelar la edicion de la organizacion: "+nombre_titulo.getText().toString());

                getFragmentManager().popBackStackImmediate();


            }
        });

        guardar_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para guardar cambios de una organizacion");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                if(!nombre_titulo.equals(nombre.getText().toString())){
                    if(validador.isValidName(nombre.getText().toString(),getContext())) {
                        enviarCambioNombre();
                    }
                }else{
                    getFragmentManager().popBackStackImmediate();
                }


            }
        });

        cambiar_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para cambiar password de una organizacion");
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

                        if (validador.isValidPasswordChange(password,pass_viejo.getText().toString(),pass_nuevo.getText().toString(),pass_nuevo_bis.getText().toString(), getActivity())){

                            Log.i("INFO: ", "Los datos son correctos!");
                            Log.i("INFO","hacer el request para cambiar el password!");


                            JSONObject cambiar_psw_body = new JSONObject();
                            try {
                                cambiar_psw_body.put("token", token);
                                cambiar_psw_body.put("id_organizacion",id.getText().toString());
                                cambiar_psw_body.put("psw", pass_nuevo.getText().toString());
                            }
                            catch(JSONException except){
                                Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            progressDialog = ProgressDialog.show(getContext(),"Hypechat","Cambiando Contraseña...",
                                    true);

                            cambiarPswRequest(cambiar_psw_body);
                        }
                    }
                });
                dialog_cambiar_psw.show();

            }
        });

        agregar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para agregar usuario a una organizacion");
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
                add_Usuario.completarOrganizacionID(id.getText().toString(),false);


            }
        });

        return view;

    }

    private void enviarCambioNombre() {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("id_organizacion",this.id.getText().toString());
            requestBody.put("nombre_organizacion",this.nombre.getText().toString());
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CAMBIO_NOMBRE, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        getFragmentManager().popBackStackImmediate();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde!", Toast.LENGTH_LONG).show();
                            case (405):
                                 Toast.makeText(getActivity(),"Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                //Toast.makeText(LoginActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    public void completarInformacionOrganizacion(String token, String nombre, String id, String pass, String owner,Boolean sameUser){
        Log.i("INFO","Completo la informacion de la  organizacion: "+nombre+" ; "+id+" ; "+pass+" ; "+owner);
        this.token = token;
        this.nombre_titulo.setText(nombre);
        this.nombre.setText(nombre);
        this.id.setText(id);
        this.id.setEnabled(false);
        this.password = pass;
        this.owner = owner;
        if(sameUser){
            enableButtons();
        }else{
            disableButtons();
        }
    }

    private void enableButtons() {
        this.nombre.setEnabled(true);
        this.cambiar_pass.setEnabled(true);
        this.agregar_usuario.setEnabled(true);
    }

    private void disableButtons() {
        this.nombre.setEnabled(false);
        this.cambiar_pass.setEnabled(false);
        this.agregar_usuario.setEnabled(false);

    }


    private void cambiarPswRequest(final JSONObject cambiar_psw_body) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CAMBIO_PASSWORD, cambiar_psw_body, new Response.Listener<JSONObject>() {

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
                        switch (error.networkResponse.statusCode){
                            case (404):
                                Toast.makeText(getActivity(),
                                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),
                                        "Server error!", Toast.LENGTH_LONG).show();
                            case (400):
                                Toast.makeText(getActivity(),
                                        "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

}
