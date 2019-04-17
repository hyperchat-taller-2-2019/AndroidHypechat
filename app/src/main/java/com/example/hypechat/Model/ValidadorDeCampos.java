package com.example.hypechat.Model;

public class ValidadorDeCampos {

    private final int MIN_PASS_LENGTH = 8;



    public boolean isValidName(String name, ErrorMsg error) {
        if (name.isEmpty()){
            error.showErrorMgs("Olvidó completar su Nombre !");
            return false;
        }
        return true;
    }

    public boolean isValidDisplayName(String display_name,ErrorMsg error) {
        if (display_name.isEmpty()){
            error.showErrorMgs("Olvidó completar su Apodo !");
            return false;
        }
        return true;
    }

    public boolean isValidPassword(String password,ErrorMsg error){
        if (password.isEmpty()){
            error.showErrorMgs("Olvidó completar su contraseña !");
            return false;
        }
        else if (password.length()  < MIN_PASS_LENGTH){
            error.showErrorMgs("El password debe tener como mínimo "+ MIN_PASS_LENGTH + " caracteres !");
            return false;
        }
        return true;
    }
    
    public boolean isValidEmail(String email,ErrorMsg error){
        if (email.isEmpty()){
            error.showErrorMgs("Olvidó completar el email !");
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            error.showErrorMgs("El mail tiene formato invalido !");
            return false;
        }
        return true;
    }
}
