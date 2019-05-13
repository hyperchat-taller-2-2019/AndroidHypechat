package com.example.hypechat;

import java.util.Map;

public class ChatMensajeEnviar extends ChatMensaje {
    private Map hora;

    public ChatMensajeEnviar() {
    }

    public ChatMensajeEnviar(Map hora) {
        this.hora = hora;
    }

    public ChatMensajeEnviar(String nickname, String texto, Map hora, String url_foto_perfil) {
        super(nickname, texto, url_foto_perfil);
        this.hora = hora;
    }


    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
