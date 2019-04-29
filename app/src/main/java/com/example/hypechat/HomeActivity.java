package com.example.hypechat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View header;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;
    private ProgressDialog progressDialog;
    private final String URL_PERFIL = "https://virtserver.swaggerhub.com/taller2-hypechat/Hypechat/1.0.0/consultarPerfil/";
    // "https://secure-plateau-18239.herokuapp.com/perfil/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //SharedPref para almacenar datos de sesión
        this.sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);


        this.navigationView = findViewById(R.id.navView);
        this.navigationView.setNavigationItemSelectedListener(this);

        //Setea el header de la navigation bar para que tenga el nombre de usuario (podria ser apodo o mail)
        this.header = navigationView.getHeaderView(0);
        setHeaderUserName(this.sharedPref.getString("nombre","No Name"));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PPrincipal()).commit();
            navigationView.setCheckedItem(R.id.pag_ppal);
        }
    }

    private void setHeaderUserName(String name) {
        TextView header_nombre_usuario = (TextView) this.header.findViewById(R.id.header_user_name);
        header_nombre_usuario.setText(name);
    }

    @Override
    //"Configuraciones" y "Mi perfil" no muestran la opcion seleccionada en el navigationview
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.pag_ppal:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PPrincipal()).commit();
                this.navigationView.setCheckedItem(R.id.pag_ppal);
                break;
            case R.id.organizaciones:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Organizaciones()).commit();
                this.navigationView.setCheckedItem(R.id.organizaciones);
                break;
            case R.id.msj_privados:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MensajesPrivados()).commit();
                this.navigationView.setCheckedItem(R.id.msj_privados);
                break;
            case R.id.perfil:
                goToProfile(this.sharedPref.getString("email","no email"));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToProfile(String email_del_perfil) {
        getProfileData(email_del_perfil);
        this.navigationView.setCheckedItem(R.id.perfil);
    }

    private void getProfileData(String email) {
        String URL = this.URL_PERFIL+email;
        Log.i("INFO", "Se esta por consultar el perfil del mail: " + email);
        Log.i("INFO","En la ruta: "+URL);
        this.progressDialog = ProgressDialog.show(
                this,"Hypechat","Obteniendo datos del perfil...",true);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        //Por ahora siempre busca los datos en la base y siempre los guarda en SharedPref para
                        //armar la vista en "setProfileFragment()"
                        procesarDatosDeUsuarioRecibidos(response);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        HttpConexionSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void procesarDatosDeUsuarioRecibidos(JSONObject response) {
        try {
            Log.i("INFO",response.toString());
            String email = this.sharedPref.getString("email","");
            String nombre = this.sharedPref.getString("nombre","");
            String apodo = this.sharedPref.getString("apodo","");
            String contraseña = this.sharedPref.getString("contraseña","");
            Boolean soy_yo = false;
            //Deberia comparar por el mail real que devuelve la API
            if (response.getString("email").equals("string")){
                Log.i("INFO","Consultas tu perfil!");
                soy_yo = true;
            }
            else{
                Log.i("INFO", "perfil de otro usuario");
                email = response.getString("email");
                nombre = response.getString("nombre");
                apodo = response.getString("apodo");
            }

            setProfileFragment(nombre,apodo,email,contraseña,soy_yo);
        }
        catch (JSONException exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setProfileFragment(String nombre, String apodo, String email, String contrasenia,Boolean soy_yo) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new Perfil());
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

        //Me traigo el fragmento sabiendo que es el de perfil para cargarle la información
        Perfil perfil = (Perfil) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        perfil.completarDatosPerfil(nombre,apodo,email,contrasenia,soy_yo);

    }

    public void irAMiPerfil(View view){
        this.goToProfile(this.sharedPref.getString("email","no email"));
    }


    public void crear_canal(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new CrearOrganizacion());
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

    }

    public void ver_canales(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new Organizaciones());
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            //Esta linea hace que cuando se aprete el boton "atras", se vuelva al ultimo fragmento que se encoló
            getSupportFragmentManager().popBackStackImmediate();
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }




}
