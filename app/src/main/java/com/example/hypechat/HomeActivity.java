package com.example.hypechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.io.UnsupportedEncodingException;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View header;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;
    private ProgressDialog progressDialog;
    private final String URL_PERFIL = "https://secure-plateau-18239.herokuapp.com/profile/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);


        this.navigationView = findViewById(R.id.navView);
        this.navigationView.setNavigationItemSelectedListener(this);

        //Setea el header de la navigation bar para que tenga el nombre de usuario (podria ser apodo o mail)
        this.header = navigationView.getHeaderView(0);
        setHeaderUserName(Usuario.getInstancia().getNombre());

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

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrganizacionesFragment()).commit();
                this.navigationView.setCheckedItem(R.id.organizaciones);
                break;
            case R.id.msj_privados:
                goToChat();
                break;
            case R.id.perfil:
                goToProfile(Usuario.getInstancia().getEmail());
                break;
            case R.id.logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void irAChat(View view){
        goToChat();
    }

    private void goToChat(){
        //this.navigationView.setCheckedItem(R.id.msj_privados);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new MensajesPrivados());
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

        //Me traigo el fragmento sabiendo que es el de perfil para cargarle la información
        ChatFragment chat = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

    }

    private void goToProfile(String email_del_perfil) {
        getProfileData(email_del_perfil);
        this.navigationView.setCheckedItem(R.id.perfil);
    }

    private void getProfileData(String email) {
        String URL = this.URL_PERFIL+email;
        Log.i("INFO", "Se esta por consultar el perfil del mail: " + email);
        Log.i("INFO","En la ruta: "+URL);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token",Usuario.getInstancia().getToken());
            Log.i("INFO","En el body: "+requestBody.toString());
        }
        catch(JSONException except){
            Toast.makeText(this, except.getMessage(), Toast.LENGTH_SHORT).show();
        }


        this.progressDialog = ProgressDialog.show(this,"Hypechat","Obteniendo datos del perfil...",true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, requestBody, new Response.Listener<JSONObject>() {

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
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(HomeActivity.this,"Token invalido",Toast.LENGTH_LONG).show();
                                if(error.networkResponse.data!=null) {
                                    String body;
                                    try {
                                        body = new String(error.networkResponse.data,"UTF-8");
                                        Log.i("ERROR",body);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }

                                break;

                            case (500):
                                Toast.makeText(HomeActivity.this,"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        /*
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, requestBody, new Response.Listener<JSONObject>() {

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
                });*/
        HttpConexionSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void procesarDatosDeUsuarioRecibidos(JSONObject response) {
        try {
            Log.i("INFO",response.toString());

            String email = Usuario.getInstancia().getEmail();
            String nombre = Usuario.getInstancia().getNombre();
            String apodo = Usuario.getInstancia().getNickname();
            String url_foto_perfil = Usuario.getInstancia().getUrl_foto_perfil();

            Boolean soy_yo = false;

            //Deberia comparar por el mail real que devuelve la API
            if (response.getString("email").equals(email)){
                Log.i("INFO","Consultas tu perfil!");
                soy_yo = true;
            }
            else{
                Log.i("INFO", "perfil de otro usuario");
                //Agregar foto perfil de otro usuario cuando se implemente!
                email = response.getString("email");
                nombre = response.getString("name");
                apodo = response.getString("nickname");
            }

            setProfileFragment(nombre,apodo,email,url_foto_perfil,soy_yo);
        }
        catch (JSONException exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setProfileFragment(String nombre, String apodo, String email,String url_foto_perfil,Boolean soy_yo) {
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
        perfil.setHeader(this.header);
        perfil.completarDatosPerfil(nombre,apodo,email,url_foto_perfil,soy_yo);
    }

    public void irAMiPerfil(View view){
        this.goToProfile(Usuario.getInstancia().getEmail());
    }


    public void crear_organizacion(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new CrearOrganizacion());
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

    }

    public void ver_organizaciones(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new OrganizacionesFragment());
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
