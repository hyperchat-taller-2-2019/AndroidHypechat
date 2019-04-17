package com.example.hypechat.Model;

import android.content.Context;

public interface Login {


    interface View{
        void showHome();
        void showErrorMgs(String error);
        void mostrarProcessDialog(String titulo, String mensaje);
        void limpiarProcessDialog();
        void guardarDato(String nombre, String dato);
    }

    interface Presenter extends ErrorMsg {
        void showHome();
        void showErrorMgs(String error);
        void login(String email, String password,Context ctx);
        void mostrarProcessDialog(String titulo, String mensaje);
        void limpiarProcessDialog();
        void loginConFacebook(Context ctx);
        void guardarDato(String nombre, String dato);
    }

    interface Model{
        void login(String email, String password,Context ctx);
        void loginConFacebook(Context ctx);
    }
}
