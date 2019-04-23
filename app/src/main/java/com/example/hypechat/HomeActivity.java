package com.example.hypechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View header;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;

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
        TextView header_nombre_usuario = (TextView) this.header.findViewById(R.id.header_user_name);
        header_nombre_usuario.setText(this.sharedPref.getString("nombre","No Name"));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PPrincipal()).commit();
            navigationView.setCheckedItem(R.id.pag_ppal);
        }
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
                setProfileFragment();
                this.navigationView.setCheckedItem(R.id.perfil);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setProfileFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Perfil()).commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        getSupportFragmentManager().executePendingTransactions();


        //Me traigo el fragmento sabiendo que es el de perfil para cargarle la información
        Perfil perfil = (Perfil) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        perfil.completarDatosPerfil(this.sharedPref.getString("nombre","no name"),this.sharedPref.getString("apodo","no nickname"),
                this.sharedPref.getString("email", "no email"),this.sharedPref.getString("contraseña","no password"));

    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }




}
