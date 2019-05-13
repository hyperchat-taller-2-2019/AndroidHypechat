package com.example.hypechat;

public class ChatMensaje {

    private String nickname,texto,hora,url_foto_perfil;

    public ChatMensaje() {
    }

    public ChatMensaje(String nickname, String texto, String hora, String url_foto_perfil) {
        this.nickname = nickname;
        this.texto = texto;
        this.hora = hora;
        this.url_foto_perfil = url_foto_perfil;
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

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUrl_foto_perfil() {
        return url_foto_perfil;
    }

    public void setUrl_foto_perfil(String url_foto_perfil) {
        this.url_foto_perfil = url_foto_perfil;
    }
}
