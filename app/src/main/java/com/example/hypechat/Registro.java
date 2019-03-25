package com.example.hypechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    private EditText textName, textDisplayName, textEmail, textPass;
    private ValidadorDeCampos validador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        this.textName = (EditText) findViewById(R.id.name_registro);
        this.textDisplayName = (EditText) findViewById(R.id.displayName_registro);
        this.textEmail = (EditText) findViewById(R.id.email_registro);
        this.textPass = (EditText) findViewById(R.id.pass_registro);
        this.validador = new ValidadorDeCampos();


    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void registrarse (View view){
        if (this.validador.areValidRegisterFields(this.textName.getText().toString(),this.textDisplayName.getText().toString(),this.textEmail.getText().toString(),this.textPass.getText().toString(),this)){
            Toast.makeText(this, "Todos los Campos OK! ", Toast.LENGTH_SHORT).show();
        }

        /*if(textName.getText().toString().isEmpty()){
            Toast.makeText(this,"Name field cannot be empty",Toast.LENGTH_LONG).show();
        }else{
            if(textDisplayName.getText().toString().isEmpty()){
                Toast.makeText(this,"Display name field cannot be empty",Toast.LENGTH_LONG).show();
            }else {
                if(textEmail.getText().toString().isEmpty()){
                    Toast.makeText(this,"Email field cannot be empty",Toast.LENGTH_LONG).show();
                }else if(!isValidEmail(textEmail.getText().toString())){
                    Toast.makeText(this,"Not a valid email",Toast.LENGTH_LONG).show();
                } else{
                    if(textPass.getText().toString().isEmpty()){
                        Toast.makeText(this,"Password field cannot be empty",Toast.LENGTH_LONG).show();
                    }else if(textPass.getText().toString().length() < 8){
                        Toast.makeText(this,"Passwords must be at least 8 characters long",Toast.LENGTH_LONG).show();
                    } else {

                    }
                }
            }

        }*/
    }

}
