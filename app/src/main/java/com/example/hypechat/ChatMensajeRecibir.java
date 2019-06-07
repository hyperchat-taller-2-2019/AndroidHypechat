package com.example.hypechat;

public class ChatMensajeRecibir extends ChatMensaje{
    private Long hora;

    public ChatMensajeRecibir() {
    }

    public ChatMensajeRecibir(Long hora) {
        this.hora = hora;
    }

    public ChatMensajeRecibir(String nickname, String texto, Long hora, String url_foto_perfil,String email) {
        super(nickname, texto, url_foto_perfil,email);
        this.hora = hora;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
