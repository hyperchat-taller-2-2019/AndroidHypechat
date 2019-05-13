package com.example.hypechat;

public abstract class ChatMensaje {

    private String nickname,texto,url_foto_perfil;

    public ChatMensaje() {
    }

    public ChatMensaje(String nickname, String texto, String url_foto_perfil) {
        this.nickname = nickname;
        this.texto = texto;
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

    public String getUrl_foto_perfil() {
        return url_foto_perfil;
    }

    public void setUrl_foto_perfil(String url_foto_perfil) {
        this.url_foto_perfil = url_foto_perfil;
    }
}
