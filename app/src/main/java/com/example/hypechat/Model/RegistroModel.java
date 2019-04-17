package com.example.hypechat.Model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hypechat.Presenter.RegistroPresenter;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistroModel implements Registro.Model{


    private ValidadorDeCampos validador;
    private RegistroPresenter presentador;
    //CONSTANTES!!!
    private final String URL_REGISTRO = "https://secure-plateau-18239.herokuapp.com/registro";
    private final Integer REGISTRO_EXITOSO = 1;

    public RegistroModel(RegistroPresenter presentador){
        this.presentador = presentador;
        this.validador = new ValidadorDeCampos();


    }


    public void registrarUsuario(String nombre, String apodo, String email, String contraseña, Context ctx){

        if (this.validador.isValidName(nombre, presentador) && this.validador.isValidDisplayName(apodo, presentador) && this.validador.isValidEmail(email, presentador) && this.validador.isValidPassword(contraseña,presentador)){
            //Aviso al usuario lo que pasa
            presentador.mostrarProcessDialog("Hypechat","Registrando usuario...");
            //ARMO BODY DEL REQUEST POST
            JSONObject requestBody = new JSONObject();
            try{
                requestBody.put("nombre",nombre);
                requestBody.put("apodo",apodo);
                requestBody.put("email",email);
                requestBody.put("contraseña",contraseña);
            }
            catch (JSONException except){
                presentador.showErrorMgs(except.getMessage());
            }

            //HAGO REQUEST
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, URL_REGISTRO, requestBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            presentador.limpiarProcessDialog();
                            procesarResponseDeRegistro(response);
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            presentador.limpiarProcessDialog();
                            presentador.showErrorMgs("No fue posible conectarse al servidor, por favor intente de nuevo mas tarde");

                        }
                    });

            //Agrego request de registro a la cola
            HttpConexionSingleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
        }

    }
    private void procesarResponseDeRegistro(JSONObject response) {
        try{
            if (response.getInt("resultado") == REGISTRO_EXITOSO){
                presentador.showErrorMgs("El usuario ha sido registrado con exito!");

                //ACA VUELVE A LA PANTALLA DE LOGIN PARA QUE INGRESE CON LAS CREDENCIALES REGISTRADAS
                //HABRIA QUE VER SI MEJOR YA LO HACEMOS ENTRAR AL SISTEMA!
                presentador.showHome();
            }
            else{
                presentador.showErrorMgs("El usuario ya existe!");
            }
        }
        catch (JSONException except){
            presentador.showErrorMgs(except.getMessage());
        }
    }

}

