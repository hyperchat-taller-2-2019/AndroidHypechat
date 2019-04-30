package com.example.hypechat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class Organizacion extends Fragment {

    private SharedPreferences sharedPref;
    private ImageButton crearCanal;;
    private ImageButton crearMsjPrivado;
    private final String URL_CANALES_MSJ_PRIVADOS = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/getCanalesYmsjPrivados";
    //private final String URL_MSJ_PRIVADOS = "https://virtserver.swaggerhub.com/vickyperezz/hypeChatAndroid/1.0.0/getMsjPrivados";
    private SharedPreferences.Editor sharedEditor;
    private String token;
    private String organizacion_id;
    private String organizacion_name;
    private TextView titulo;
    private View view;
    private ProgressDialog progressDialog;
    // private JSONArray organizaciones;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        this.view = inflater.inflate(R.layout.organizacion,container,false);
        this.sharedPref = getActivity().getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();

        System.out.printf("ESTOY EN ORGANIZACION CON ID:     ");
        this.token = this.sharedPref.getString("token","no token");
        this.organizacion_id = this.sharedPref.getString("organizacion_id","no id");
        this.organizacion_name = this.sharedPref.getString("organizacion_name","no name");
        System.out.printf(this.organizacion_id.toString()+"\n");

        titulo = (TextView) view.findViewById(R.id.titulo_organizacion);
        titulo.setText(this.organizacion_name);

        getCanalesYmsjPrivados();
        //getMsjPrivados();


        crearCanal= (ImageButton)view.findViewById(R.id.crear_canal);
        crearMsjPrivado = (ImageButton)view.findViewById(R.id.crear_msj_privado);

        crearCanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para crear un canal en la organizacion: "+organizacion_id);

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
        crearMsjPrivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para crear una conversacion privada en la organizacion: "+organizacion_id);

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
        return view;
    }


    private JSONObject get_json_Request_Body(){
        //Preparo Body del POST
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", this.token);
            requestBody.put("organizacion_id",this.organizacion_id);
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return requestBody;

    }


    private void getCanalesYmsjPrivados(){
        Log.i("INFO", "Obtengo los canales y msj privados del usuario en la organizacion: "+organizacion_id);
        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo canales y msj privados del usuario...",true);

        JSONObject request = get_json_Request_Body();

        Log.i("INFO", "Json Request getCanales, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_CANALES_MSJ_PRIVADOS, request, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        responseListaCanales(response);
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
                                // Toast.makeText(LoginActivity.this,"Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                //Toast.makeText(LoginActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    /*private void getMsjPrivados(){
        Log.i("INFO", "Obtengo las conversaciones privadas del usuario en la organizacion: "+organizacion_id);
        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo msj privados del usuario...",true);

        JSONObject request = get_json_Request_Body();

        Log.i("INFO", "Json Request getMsjPrivados, check http status codes");

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
*/

    private void responseListaCanales(JSONObject response){
        try {
            Log.i("INFO",response.toString());
            JSONArray canales = response.getJSONArray("canales");
            ListView list = (ListView) view.findViewById(R.id.lista_canales);
            List<String> array = new ArrayList<>();
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_canales, array);
            for(int i = 0; i< canales.length(); i++){

                JSONObject item = canales.getJSONObject(i);
                String name = item.getString("name");
                array.add("# "+name);
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

    private void responseListaMsjPrivados(JSONObject response){
        try {
            Log.i("INFO",response.toString());
            JSONArray msjPrivados = response.getJSONArray("msjPrivados");
            ListView list = (ListView) view.findViewById(R.id.lista_msj_privados);
            List<String> array = new ArrayList<>();
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_canales, array);
            for(int i = 0; i< msjPrivados.length(); i++){

                JSONObject item = msjPrivados.getJSONObject(i);
                String name = item.getString("name");
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
}
