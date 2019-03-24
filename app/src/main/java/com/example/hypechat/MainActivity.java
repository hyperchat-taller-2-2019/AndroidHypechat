package com.example.hypechat;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private EditText textoEmail;
    private EditText textoPassword;


    //Este es un ejemplo de una request simple GET hecha con el singleton de la clase HttpConexionSingleton
    public void login (View view){
        String email = this.textoEmail.getText().toString();
        String password = this.textoPassword.getText().toString();
        this.textoEmail.setText("");
        this.textoPassword.setText("");

        Toast.makeText(this, "Email: " + email + "\n" + "Password: " + password, Toast.LENGTH_LONG).show();

    }

    public void loginConFacebook (View view){
        Toast.makeText(this, "Aca te deberias loguear con Facebook!", Toast.LENGTH_SHORT).show();
    }

    public void registro (View view){
        Toast.makeText(this, "Aca te deberias Registrar!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.textoEmail = (EditText) findViewById(R.id.et_email);
        this.textoPassword = (EditText) findViewById(R.id.et_password);

    }

}
