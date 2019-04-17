package com.example.hypechat.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.hypechat.Fragments.MensajesPrivados;
import com.example.hypechat.Fragments.Organizaciones;
import com.example.hypechat.Fragments.PPrincipal;
import com.example.hypechat.Fragments.Perfil;
import com.example.hypechat.R;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PPrincipal()).commit();
            navigationView.setCheckedItem(R.id.pag_ppal);
        }



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.pag_ppal:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PPrincipal()).commit();
                break;
            case R.id.organizaciones:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Organizaciones()).commit();
                break;
            case R.id.msj_privados:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MensajesPrivados()).commit();
                break;
            case R.id.perfil:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Perfil()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
