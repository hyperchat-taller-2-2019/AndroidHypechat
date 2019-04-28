package com.example.hypechat;

import android.content.Context;
import android.widget.Toast;

public class ValidadorDeCampos {

    private final int MIN_PASS_LENGTH = 8;


    public boolean areValidLoginFields (String email, String password, Context ctx){
        return (isValidEmail(email,ctx) && isValidPassword(password, ctx));
    }

    public boolean areValidRegisterFields (String name,String display_name, String email, String password,Context ctx){
        return (isValidName(name,ctx) && isValidDisplayName(display_name,ctx) && isValidEmail(email, ctx) && isValidPassword(password, ctx));
    }

    private boolean isValidName(String name, Context ctx) {
        if (name.isEmpty()){
            Toast.makeText(ctx, "Olvidó completar su Nombre !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidDisplayName(String display_name, Context ctx) {
        if (display_name.isEmpty()){
            Toast.makeText(ctx, "Olvidó completar su Apodo !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isValidPassword(String password,Context ctx){
        if (password.isEmpty()){
            Toast.makeText(ctx, "Olvidó completar su contraseña !", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.length()  < MIN_PASS_LENGTH){
            Toast.makeText(ctx, "El password debe tener como mínimo "+ MIN_PASS_LENGTH + " caracteres !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    public boolean isValidEmail(String email, Context ctx){
        if (email.isEmpty()){
            Toast.makeText(ctx, "Olvidó completar el email !", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(ctx, "El mail tiene formato invalido !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean areTwoStringsEqual(String oneString, String otherString, Context ctx){
        if (oneString.equals(otherString)){
            return true;
        }
        Toast.makeText(ctx, "Los password NO coinciden!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean isValidPasswordChange(String password, String pass_viejo, String pass_nuevo, String pass_nuevo_bis,Context ctx){
        //faltan datos?
        if (password.isEmpty() || pass_viejo.isEmpty() || pass_nuevo.isEmpty() || pass_nuevo_bis.isEmpty()){
            Toast.makeText(ctx, "Faltan Datos para Continuar!", Toast.LENGTH_SHORT).show();
            return false;
        }
        //El password viejo es correcto!
        if (this.areTwoStringsEqual(password,pass_viejo,ctx)){
            //El nuevo candidato es igual en los dos campos!
            if (this.areTwoStringsEqual(pass_nuevo,pass_nuevo_bis,ctx)){
                //El password nuevo es valido porque tiene mas de los caracteres pedidos!
                if (isValidPassword(pass_nuevo,ctx)){
                    return true;
                }
            }
        }
        return false;
    }
}
