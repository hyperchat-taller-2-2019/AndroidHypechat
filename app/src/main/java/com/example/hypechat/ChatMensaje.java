package com.example.hypechat;

public abstract class ChatMensaje {

    private String nickname,texto,url_foto_perfil,url_foto_mensaje,email;

    public ChatMensaje() {
    }

    public ChatMensaje(String nickname, String texto, String url_foto_perfil,String email) {
        this.nickname = nickname;
        this.texto = texto;
        this.url_foto_perfil = url_foto_perfil;
        this.url_foto_mensaje = null;
        this.email = email;
    }

    public ChatMensaje(String nickname, String texto, String url_foto_perfil, String url_foto_mensaje,String email) {
        this.nickname = nickname;
        this.texto = texto;
        this.url_foto_perfil = url_foto_perfil;
        this.url_foto_mensaje = url_foto_mensaje;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getUrl_foto_perfil() {
        return url_foto_perfil;
    }

    public void setUrl_foto_perfil(String url_foto_perfil) {
        this.url_foto_perfil = url_foto_perfil;
    }

    public String getUrl_foto_mensaje() {
        return url_foto_mensaje;
    }

    public void setUrl_foto_mensaje(String url_foto_mensaje) {
        this.url_foto_mensaje = url_foto_mensaje;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
