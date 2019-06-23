package com.example.hypechat;

public class Usuario {

    private static Usuario instancia;
    private String email;
    private String password;
    private String url_foto_perfil;
    private String nickname;
    private String token;
    private String nombre;
    private String token_notification;


    private Usuario() {
        email = "";
        token = "";
        nombre = "";
        password = "";
        url_foto_perfil = "";
        nickname = "";
        token_notification = "";
    }

    public static  Usuario getInstancia() {
        if (instancia == null) {
            instancia = new Usuario();
        }
        return instancia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTokenPush() {
        return token_notification;
    }

    public void setTokenPush(String token_push) {
        this.token_notification = token_push;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl_foto_perfil() {
        return url_foto_perfil;
    }

    public void setUrl_foto_perfil(String url_foto_perfil) {
        this.url_foto_perfil = url_foto_perfil;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getToken() {
        return token;
    }

}
