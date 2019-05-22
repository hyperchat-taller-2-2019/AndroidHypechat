package com.example.hypechat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<CardChatMensajes> {

    private List<ChatMensajeRecibir> listado_de_mensajes = new ArrayList<>();
    private Context ctx;

    public AdapterChat(Context ctx) {
        this.ctx = ctx;
    }

    public void agregarMensaje(ChatMensajeRecibir chatMensaje){
        listado_de_mensajes.add(chatMensaje);
        notifyItemInserted(listado_de_mensajes.size());
    }

    @NonNull
    @Override
    public CardChatMensajes onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.card_view_mensajes,viewGroup,false);
        return new CardChatMensajes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardChatMensajes cardChatMensajes, int i) {
        //Obtengo los datos del mensaje de la lista a completar en la tarjeta
        ChatMensajeRecibir mensaje = listado_de_mensajes.get(i);

        cardChatMensajes.getNickname().setText(mensaje.getNickname());
        cardChatMensajes.getMensaje_chat().setText(mensaje.getTexto());

        //Aca veo si es un mensaje de texto o una imagen y en funcion de eso seteo los parametros de la card
        if (mensaje.getUrl_foto_mensaje() != null){
            cardChatMensajes.getImagen_mensaje().setVisibility(View.VISIBLE);
            Glide.with(ctx).load(mensaje.getUrl_foto_mensaje()).into(cardChatMensajes.getImagen_mensaje());
        }
        else{
            //solo el mensaje de texto!
            cardChatMensajes.getImagen_mensaje().setVisibility(View.GONE);
        }
        if (mensaje.getUrl_foto_perfil().equals("")){
            cardChatMensajes.getFoto_perfil().setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(ctx).load(mensaje.getUrl_foto_perfil()).into(cardChatMensajes.getFoto_perfil());
        }

        //Aca se setea la hora de cada mensaje haciendo la conversion adecuada.
        Long codigoHora = mensaje.getHora();
        Date date = new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        cardChatMensajes.getHora().setText(sdf.format(date));



    }

    @Override
    public int getItemCount() {
        return listado_de_mensajes.size();
    }
}
