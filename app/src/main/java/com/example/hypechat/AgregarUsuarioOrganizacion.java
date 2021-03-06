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

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;




public class AgregarUsuarioOrganizacion extends Fragment {

    private ValidadorDeCampos validador;
    private Button agregarUser;
    private Button finalizar;
    private EditText email;
    private TextView nombre_org;
    private ProgressDialog progressDialog;
    private String URL_AGREGAR_USUARIO = "https://secure-plateau-18239.herokuapp.com/organization/user";
    private String organizacion_id = "Organizacion";
    private Boolean creando_organizacion;
    private String token;
    private String psw;
    private VerUsuariosOrganizacion ver_usuarios_fragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_agregar_usuarios_organizacion,container,false);

        validador = new ValidadorDeCampos();
        this.email = (EditText) view.findViewById(R.id.edit_email);
        nombre_org = (TextView) view.findViewById(R.id.nombre_org_add_user);
        nombre_org.setText(organizacion_id);

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
                    ver_usuarios_fragment.cargarMiembros();
                    //ver_usuarios_fragment.actualizar_lista_members();
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
            requestBody.put("token",this.token );
            requestBody.put("idOrganization", this.organizacion_id);
            requestBody.put("email", this.email.getText().toString());
            requestBody.put("psw", this.psw);
            Log.i("INFO", "PSW: "+this.psw);
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Agrego el usuario a la organizacion en el server");

        Log.i("INFO", "Request body: "+requestBody.toString());
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
                                break;
                            case (401):
                                Toast.makeText(getActivity(),"No existe un usuario con ese email", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                Toast.makeText(getActivity(),"No existe una organizacion con ese id", Toast.LENGTH_LONG).show();
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

    public void completarOrganizacionID(String id,Boolean creandoOrg,String psw, String token,VerUsuariosOrganizacion verUsuarios) {
        this.organizacion_id = id;
        nombre_org.setText(organizacion_id);
        this.creando_organizacion = creandoOrg;
        this.psw = psw;
        this.token = token;
        this.ver_usuarios_fragment = verUsuarios;
    }
}
