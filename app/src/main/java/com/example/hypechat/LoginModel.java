package com.example.hypechat;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;


import org.json.JSONException;
import org.json.JSONObject;

public class LoginModel implements Login.Model {

    private ValidadorDeCampos validador;
    private LoginPresenter presentador;

    private final Integer USUARIO_VALIDO = 1;

    //ESTA DESPUES DEBERIA SER LA DIRECCION DE DONDE ESTE EL SERVER REAL Y EL ENDPOINT CORRESPONDIENTE!
    private final String URL_LOGIN = "https://secure-plateau-18239.herokuapp.com/login";
    private final String URL_LOGIN_FACE = "https://virtserver.swaggerhub.com/taller2-hypechat/Hypechat/1.0.0/logFacebook";


    public LoginModel(LoginPresenter presentador){
        this.presentador = presentador;
        this.validador = new ValidadorDeCampos();


    }

    @Override
    public void login(String email, String password,Context ctx) {
        if (this.validador.isValidEmail(email, presentador) && this.validador.isValidPassword(password,presentador)){

            presentador.mostrarProcessDialog("Hypechat","Validando datos...");

            //Preparo Body del POST
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("email", email);
                requestBody.put("contraseña", password);
            }
            catch(JSONException except){
                presentador.showErrorMgs(except.getMessage());
            }

            JsonRequest_login(URL_LOGIN, requestBody,ctx);

        }
    }

    @Override
    public void loginConFacebook(Context ctx) {
        presentador.mostrarProcessDialog("Hypechat","Validando datos...");
        JSONObject requestBody = new JSONObject();
        try {
            presentador.limpiarProcessDialog();
            requestBody.put("token", AccessToken.getCurrentAccessToken().getToken());

        }
        catch(JSONException except){
            presentador.limpiarProcessDialog();
            presentador.showErrorMgs(except.getMessage());
        }

        JsonRequest_login(URL_LOGIN_FACE,requestBody,ctx);
    }

    public void JsonRequest_login(String URL, JSONObject requestBody, Context ctx) {
        // SE PUEDE HACER EL REQUEST AL SERVER PARA LOGUEARSE !
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        presentador.limpiarProcessDialog();
                        procesarResponse(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presentador.limpiarProcessDialog();
                        presentador.showErrorMgs("No fue posible conectarse al servidor, por favor intente de nuevo mas tarde");

                    }
                });


        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }

    private void procesarResponse(JSONObject response) {
        try{
            //Usuario valido?
            if (response.getInt("valido") == USUARIO_VALIDO){
                String token_response = response.getString("token");
                String apodo_response = response.getString("apodo");
                String nombre_response = response.getString("nombre");
                String email_response = response.getString("email");
                presentador.guardarDato("apodo",apodo_response);
                presentador.guardarDato("nombre",nombre_response);
                presentador.guardarDato("email",email_response);
                presentador.guardarDato("token",token_response);
                presentador.showHome();
            }
            else{
                presentador.showErrorMgs("Usuario o Contraseña Invalidos!");
            }
        }
        catch (JSONException error){
            presentador.showErrorMgs("Hubo un error con el Json de Respuesta");
        }
    }

}
