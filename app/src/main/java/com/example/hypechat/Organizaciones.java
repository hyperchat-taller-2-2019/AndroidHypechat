package com.example.hypechat;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Organizaciones extends Fragment {

    private SharedPreferences sharedPref;
    private Button crearOrganizacion;
    private final String URL_ORGANIZACIONES = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/getOrganizaciones";
    private SharedPreferences.Editor sharedEditor;
    private String token;
    private View view;
    private ProgressDialog progressDialog;
   // private JSONArray organizaciones;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        this.view = inflater.inflate(R.layout.organizaciones,container,false);
        this.sharedPref = getActivity().getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);

        this.token = this.sharedPref.getString("token","no token");
        System.out.printf(this.token.toString());

        getOrganizaciones();


        crearOrganizacion = (Button)view.findViewById(R.id.boton_addOrganizacion);

        crearOrganizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para crear una organizacion");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new CrearOrganizacion());
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();


            }
        });
        return view;
    }


    private void getOrganizaciones (){
        Log.i("INFO", "Obtengo las organizaciones del usuario");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo organizaciones del usuario...",true);

        //Preparo Body del POST
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.i("INFO", "Json Request getOrganizaciones, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_ORGANIZACIONES, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        responseOrganizaciones(response);

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
                                // Toast.makeText(LoginActivity.this,"Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                //Toast.makeText(LoginActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void responseOrganizaciones(JSONObject response) {

        try {
            Log.i("INFO",response.toString());
            JSONArray organizaciones = response.getJSONArray("organizaciones");
            ListView list = (ListView) view.findViewById(R.id.lista_organizaciones);
            ArrayList<String> array = new ArrayList<String>();
            for(int i=0;i< organizaciones.length();i++){
                System.out.printf("ENTRO ACA \n");
                JSONObject item = organizaciones.getJSONObject(i);
                String id = item.getString("id");
                String name = item.getString("name");
                System.out.printf(id+"   \n");
                System.out.printf(name+"\n");
                array.add(name);

            }

            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.orga_text_list, array);
            list.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
