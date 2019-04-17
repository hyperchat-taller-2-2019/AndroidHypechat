package com.example.hypechat.Presenter;

import android.content.Context;

import com.example.hypechat.Model.Registro;
import com.example.hypechat.Model.RegistroModel;
import com.example.hypechat.View.RegistroView;

public class RegistroPresenter implements Registro.Presenter {

    private RegistroView view;
    private RegistroModel model;

    public RegistroPresenter(RegistroView view) {
        this.view = view;
        this.model = new RegistroModel(this);
    }

    @Override
    public void registrarUsuario(String nombre, String apodo, String email, String contraseña, Context ctx) {
        model.registrarUsuario(nombre, apodo, email, contraseña,ctx);
    }

    @Override
    public void showErrorMgs(String error) {
        view.showErrorMgs(error);
    }

    @Override
    public void mostrarProcessDialog(String titulo, String mensaje) {
        view.mostrarProcessDialog(titulo,mensaje);
    }

    @Override
    public void limpiarProcessDialog() {
        view.limpiarProcessDialog();
    }


    @Override
    public void showHome() {
        view.showHome();
    }
}
