package com.example.hypechat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<CardChatMensajes> {

    private List<ChatMensaje> listado_de_mensajes = new ArrayList<>();
    private Context ctx;

    public AdapterChat(Context ctx) {
        this.ctx = ctx;
    }

    public void agregarMensaje(ChatMensaje chatMensaje){
        listado_de_mensajes.add(chatMensaje);
        notifyItemInserted(listado_de_mensajes.size());
    }

    @NonNull
    @Override
    public CardChatMensajes onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.card_view_mensajes,viewGroup,false);
        CardChatMensajes tarjeta = new CardChatMensajes(view);
        return new CardChatMensajes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardChatMensajes cardChatMensajes, int i) {
        //Obtengo los datos del mensaje de la lista a completar en la tarjeta
        ChatMensaje mensaje = listado_de_mensajes.get(i);

        cardChatMensajes.getNickname().setText(mensaje.getNickname());
        cardChatMensajes.getMensaje_chat().setText(mensaje.getTexto());
        cardChatMensajes.getHora().setText(mensaje.getHora());

    }

    @Override
    public int getItemCount() {
        return listado_de_mensajes.size();
    }
}
