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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;
import java.util.List;

public class Organizaciones extends Fragment {

    private SharedPreferences sharedPref;
    private Button crearOrganizacion;
    private final String URL_ORGANIZACIONES = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/getOrganizaciones";
    private final String URL_INFO_ORG = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/getInfoOrganizacion";
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
        this.sharedEditor = sharedPref.edit();
        System.out.printf("ESTOY EN ORGANIZACIONES CON TOKEN:     ");

        this.token = this.sharedPref.getString("token","no token");
        System.out.printf(this.token.toString()+"\n");

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
            List<HashMap<String,String>> array = new ArrayList<>();
            SimpleAdapter adapter = new SimpleAdapter(getActivity(),array,R.layout.text_list_orga,new String[]{"Primera","Segunda"},new int[]{R.id.textlist1,R.id.textlist2});
            for(int i=0;i< organizaciones.length();i++){
                HashMap<String,String> resultadoItem = new HashMap<>();

                JSONObject item = organizaciones.getJSONObject(i);
                String id = item.getString("id");
                String name = item.getString("name");
                resultadoItem.put("Primera",name);
                resultadoItem.put("Segunda",id);
                array.add(resultadoItem);
            }

            //ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_orga, array);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LinearLayout lay = (LinearLayout) view;
                    TextView text1 = (TextView)  lay.getChildAt(0);
                    TextView text2 = (TextView)  lay.getChildAt(1);
                    sharedEditor.putString("organizacion_name",text1.getText().toString());
                    sharedEditor.putString("organizacion_id",text2.getText().toString());
                    sharedEditor.apply();

                    obtenerDatosOrganizacion(text2.getText().toString());


                   // Toast.makeText(getActivity(),text2.getText().toString(), Toast.LENGTH_LONG).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerDatosOrganizacion(String id){
        Log.i("INFO", "Obteniendo datos de la organizacion");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo organizaciones del usuario...",true);

        //Preparo Body del POST
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("id_organizacion",id);

        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.i("INFO", "Json Request getOrganizaciones, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL_INFO_ORG, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        mostrarOrganizacion(response);

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

    private void mostrarOrganizacion(JSONObject response) {
        try {
        this.sharedEditor.putString("organizacion_name",response.getString("nombre"));
        this.sharedEditor.putString("organizacion_id",response.getString("id"));
        this.sharedEditor.apply();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new Organizacion());
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

        //Me traigo el fragmento sabiendo que es el de Organizacion para cargarle la información
        Organizacion org = (Organizacion) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            org.completarInfoOrganizacion(response.getString("nombre"),response.getString("id"),response.getString("owner_email"),response.getString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
