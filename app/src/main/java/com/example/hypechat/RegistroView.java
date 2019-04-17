package com.example.hypechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistroView extends AppCompatActivity implements Registro.View{

    private EditText textName, textDisplayName, textEmail, textPass;
    private ProgressDialog progress;

    private RegistroPresenter presentador;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        this.presentador = new RegistroPresenter(this);

        this.textName = (EditText) findViewById(R.id.name_registro);
        this.textDisplayName = (EditText) findViewById(R.id.displayName_registro);
        this.textEmail = (EditText) findViewById(R.id.email_registro);
        this.textPass = (EditText) findViewById(R.id.pass_registro);


    }

    public void registrarse (View view){
        String nombre = this.textName.getText().toString();
        String apodo = this.textDisplayName.getText().toString();
        String email = this.textEmail.getText().toString();
        String contraseña = this.textPass.getText().toString();

        presentador.registrarUsuario(nombre,apodo,email,contraseña,this);

    }


    @Override
    public void showHome() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showErrorMgs(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mostrarProcessDialog(String titulo, String mensaje) {
        this.progress = ProgressDialog.show(this,titulo,"Validando datos...",
                true);
    }

    @Override
    public void limpiarProcessDialog() {
        progress.dismiss();
    }

}
