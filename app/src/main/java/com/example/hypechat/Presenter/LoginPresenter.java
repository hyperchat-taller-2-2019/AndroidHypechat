package com.example.hypechat.Presenter;

import android.content.Context;

import com.example.hypechat.Model.Login;
import com.example.hypechat.Model.LoginModel;
import com.example.hypechat.View.LoginView;

public class LoginPresenter implements Login.Presenter {

    private LoginView view;
    private LoginModel model;

    public LoginPresenter(LoginView view){
        this.view = view;
        this.model = new LoginModel(this);
    }

    @Override
    public void showHome() {
        this.view.showHome();
    }

    @Override
    public void showErrorMgs(String error) {
        this.view.showErrorMgs(error);
    }

    @Override
    public void login(String email, String password,Context ctx) {
        this.model.login(email,password,ctx);
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
    public void loginConFacebook(Context ctx) {
        model.loginConFacebook(ctx);
    }

    @Override
    public void guardarDato(String nombre, String dato) {
        view.guardarDato(nombre, dato);
    }

}
