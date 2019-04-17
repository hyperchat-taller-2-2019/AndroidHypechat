package com.example.hypechat.Model;

import android.content.Context;

public interface Registro {

    interface View{
        void showHome();
        void showErrorMgs(String error);
        void mostrarProcessDialog(String titulo, String mensaje);
        void limpiarProcessDialog();
    }

    interface Presenter extends ErrorMsg {
        void registrarUsuario(String nombre, String apodo, String email, String contraseña, Context ctx);
        void showErrorMgs(String error);
        void mostrarProcessDialog(String titulo, String mensaje);
        void limpiarProcessDialog();
        void showHome();

    }

    interface Model{
        void registrarUsuario(String nombre, String apodo, String email, String contraseña, Context ctx);
    }
}
