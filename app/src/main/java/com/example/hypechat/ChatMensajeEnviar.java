package com.example.hypechat;

import java.util.Map;

public class ChatMensajeEnviar extends ChatMensaje {
    private Map hora;

    public ChatMensajeEnviar() {
    }

    public ChatMensajeEnviar(Map hora) {
        this.hora = hora;
    }

    public ChatMensajeEnviar(String nickname, String texto, Map hora, String url_foto_perfil,String email) {
        super(nickname, texto, url_foto_perfil,email);
        this.hora = hora;
    }

    public ChatMensajeEnviar(String nickname, String texto, String url_foto_perfil, String url_foto_mensaje, Map hora,String email) {
        super(nickname, texto, url_foto_perfil, url_foto_mensaje,email);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
