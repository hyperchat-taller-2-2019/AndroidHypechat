package com.example.hypechat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class OrganizacionesFragment extends Fragment {

    private SharedPreferences sharedPref;
    private Button crearOrganizacion, buscarOrganizacion;
    private final String URL_ORGANIZACIONES = "https://secure-plateau-18239.herokuapp.com/organizations/";

    private String URL_CHECK_ID = "https://secure-plateau-18239.herokuapp.com/idOrganizationValid/";
    private String URL_AGREGAR_USUARIO = "https://secure-plateau-18239.herokuapp.com/organization/user";
    private SharedPreferences.Editor sharedEditor;
    private String token;
    private View view;
    private ProgressDialog progressDialog;
    private RecyclerView rv_organizaciones;
    private TextView error_busqueda, error_pass;
    private EditText busqueda_id, pas_org_a_ingresar;
    private Dialog pass_org;
    private AdapterOrganizaciones adaptador_para_organizaciones;
   // private JSONArray organizaciones;

    //metodo que se ejecuta cuando tocamos algun tarjeta de la recycleview
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Organizacion organizacion_clickeada = adaptador_para_organizaciones.obtenerItemPorPosicion(position);
            //Toast.makeText(getContext(), "TOCASTE LA ORGANIZACION: " + organizacion_clickeada.getId(), Toast.LENGTH_SHORT).show();
            mostrarOrganizacion(organizacion_clickeada.getId());
        }
    };



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        this.view = inflater.inflate(R.layout.organizaciones,container,false);
        this.sharedPref = getActivity().getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();
        System.out.printf("ESTOY EN ORGANIZACIONES CON TOKEN:     ");

        this.token = Usuario.getInstancia().getToken();
        System.out.printf(this.token.toString()+"\n");
        this.pass_org= new Dialog(getContext());
        rv_organizaciones = (RecyclerView) view.findViewById(R.id.lista_organizaciones);
        adaptador_para_organizaciones = new AdapterOrganizaciones(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        rv_organizaciones.setLayoutManager(l);
        rv_organizaciones.setAdapter(adaptador_para_organizaciones);
        adaptador_para_organizaciones.setOnItemClickListener(this.onItemClickListener);


        getOrganizaciones();
        error_busqueda = (TextView) view.findViewById(R.id.error_buscar_organizacion);
        error_busqueda.setVisibility(View.INVISIBLE);
        busqueda_id = (EditText) view.findViewById(R.id.text_ingresar_organizacion);

        crearOrganizacion = (Button)view.findViewById(R.id.boton_addOrganizacion);
        buscarOrganizacion = (Button)view.findViewById(R.id.button_buscar_organizacion);


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

        buscarOrganizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para buscar una organizacion");
                error_busqueda.setVisibility(View.INVISIBLE);
                chequearId();
                //startActivity(launchactivity);



            }
        });
        return view;
    }

    private void chequearId() {

        Log.i("INFO", "Chequeo si el ID ya existe");

        Log.i("INFO", "Json Request , check http status codes");
        String URL = this.URL_CHECK_ID+this.busqueda_id.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        //salta el popup de password
                        error_busqueda.setVisibility(view.VISIBLE);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                mostrar_popup_pass_org();
                                break;
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde\"", Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void mostrar_popup_pass_org() {
        Log.i("INFO", "Apretaste para ingresar a una organizacion: "+this.busqueda_id.getText().toString());
        pass_org.setContentView(R.layout.popup_pass_organizacion);

        TextView id_a_ingresar = (TextView)  pass_org.findViewById(R.id.nombre_organizacion_ingresar);
        pas_org_a_ingresar = (EditText)  pass_org.findViewById(R.id.password_organizacion_ingresar);
        Button ingresar = (Button) pass_org.findViewById(R.id.ingreso_organizacion);
        id_a_ingresar.setText(this.busqueda_id.getText().toString());
        error_pass = (TextView) pass_org.findViewById(R.id.error_pass_organizacion);
        error_pass.setVisibility(view.INVISIBLE);
        ImageView b_cancelar_cambio = (ImageView) pass_org.findViewById(R.id.boton_cancelar_agregar_org);



        b_cancelar_cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_org.dismiss();
            }
        });
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_pass.setVisibility(view.INVISIBLE);
                ingresar_a_organizacion(pas_org_a_ingresar.getText().toString());

            }
        });



        pass_org.show();
    }

    private void ingresar_a_organizacion(String password){

        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Validando password...",true);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token",this.token );
            requestBody.put("idOrganization", this.busqueda_id.getText().toString());
            requestBody.put("email", Usuario.getInstancia().getEmail());
            requestBody.put("psw", password);
            Log.i("INFO", "PSW: "+password);
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.i("INFO", "Request body: "+requestBody.toString());
        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_AGREGAR_USUARIO, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        Log.i("INFO", "Se agrego la organizacion "+busqueda_id.getText().toString()+" al usuario "+Usuario.getInstancia().getEmail());
                        busqueda_id.getText().clear();
                        pass_org.dismiss();
                        Toast.makeText(getActivity(), "Se ingreso a la organizacion con exito.", Toast.LENGTH_LONG).show();


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
                                error_pass.setVisibility(view.VISIBLE);

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

    private void getOrganizaciones (){
        Log.i("INFO", "Obtengo las organizaciones del usuario");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo organizaciones del usuario...",true);

        //Preparo Body del POST
        String URL = URL_ORGANIZACIONES + Usuario.getInstancia().getEmail();
        Log.i("INFO", "Json Request getOrganizaciones, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

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
                            case (404):
                                Toast.makeText(getActivity(),"Usuario Invalido!", Toast.LENGTH_LONG).show();
                                break;
                            case (500):
                                 Toast.makeText(getActivity(),"Server error!", Toast.LENGTH_LONG).show();
                                break;
                            case (400):
                                //Toast.makeText(LoginActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void responseOrganizaciones(JSONObject response) {

        try {
            Log.i("INFO",response.toString());
            JSONArray organizaciones = response.getJSONArray("organizations");
            for(int i=0;i< organizaciones.length();i++){
                JSONObject item = organizaciones.getJSONObject(i);
                String id = item.getString("id");
                String name = item.getString("name");
                Organizacion organizacion = new Organizacion(name,id);
                adaptador_para_organizaciones.agregarOrganizacion(organizacion);
            }

            //ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), R.layout.text_list_orga, array);
            //list.setAdapter(adapter);
            //list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //    @Override
            //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //        LinearLayout lay = (LinearLayout) view;
                    //TextView text1 = (TextView)  lay.getChildAt(0);
                   // TextView text2 = (TextView)  lay.getChildAt(1);
                   // sharedEditor.putString("organizacion_name",text1.getText().toString());
                   // sharedEditor.putString("organizacion_id",text2.getText().toString());
                  //  sharedEditor.apply();

                //    obtenerDatosOrganizacion(text2.getText().toString());


                   // Toast.makeText(getActivity(),text2.getText().toString(), Toast.LENGTH_LONG).show();

              //  }
            //});

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void mostrarOrganizacion(String id) {


        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new OrganizacionFragment(id));
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

        //Me traigo el fragmento sabiendo que es el de OrganizacionFragment para cargarle la información
        OrganizacionFragment org = (OrganizacionFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);




    }


}
