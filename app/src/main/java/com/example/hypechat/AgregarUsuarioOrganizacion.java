package com.example.hypechat;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;




public class AgregarUsuarioOrganizacion extends Fragment {

    private ValidadorDeCampos validador;
    private Button agregarUser;
    private Button finalizar;
    private EditText email;
    private ProgressDialog progressDialog;
    private String URL_AGREGAR_USUARIO = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/agregarUsuarioOrganizacion";
    private String organizacion_id;
    private Boolean creando_organizacion;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_add_users,container,false);

        validador = new ValidadorDeCampos();
        this.email = (EditText) view.findViewById(R.id.edit_email);

        agregarUser = (Button)view.findViewById(R.id.r_invitar_usuario);

        agregarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validFieldUser()) {
                    agregarUsuarioOrganizacion_server();
                }

            }
        });

        finalizar = (Button)view.findViewById(R.id.r_finalizar_creacion_organizacion);

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Finaliza la actividad de agregar usuarios.");

                if(creando_organizacion) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new OrganizacionesFragment());
                    //Esta es la linea clave para que vuelva al fragmento anterior!
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                    fragmentManager.executePendingTransactions();
                }else{
                    getFragmentManager().popBackStackImmediate();
                }


            }
        });

        return view;

    }

    private boolean validFieldUser() {
        return validador.isValidEmail(email.getText().toString(),getContext());
    }


    private void agregarUsuarioOrganizacion_server(){

        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Agregando el usuario...",true);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id_organizacion", this.organizacion_id);
            requestBody.put("email_usuario", this.email.getText().toString());
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Agrego el usuario a la organizacion en el server");


        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_AGREGAR_USUARIO, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        Log.i("INFO", "Agregaste un nuevo usuario a la organizacion: "+organizacion_id);
                        email.getText().clear();
                        Toast.makeText(getActivity(), "Usuario Agregado con exito.", Toast.LENGTH_LONG).show();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"El usuario ya se ha agregado a la organizacion", Toast.LENGTH_LONG).show();
                            case (401):
                                Toast.makeText(getActivity(),"No existe un usuario con ese email", Toast.LENGTH_LONG).show();
                            case (500):
                                // Toast.makeText(LoginActivity.this,"Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                //Toast.makeText(LoginActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    public void completarOrganizacionID(String id,Boolean creandoOrg) {
        this.organizacion_id = id;
        this.creando_organizacion = creandoOrg;
    }
}
