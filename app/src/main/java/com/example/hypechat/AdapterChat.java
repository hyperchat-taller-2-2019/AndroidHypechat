package com.example.hypechat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<CardChatMensajes> {

    private List<ChatMensajeRecibir> listado_de_mensajes = new ArrayList<>();
    private Context ctx;
    private final static int MENSAJE_PROPIO = 1;
    private static final int MENSAJE_DE_OTRO = -1;

    public AdapterChat(Context ctx) {
        this.ctx = ctx;
    }

    public void agregarMensaje(ChatMensajeRecibir chatMensaje){
        listado_de_mensajes.add(chatMensaje);
        notifyItemInserted(listado_de_mensajes.size());
    }

    @NonNull
    @Override
    public CardChatMensajes onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == MENSAJE_PROPIO){
            view = LayoutInflater.from(ctx).inflate(R.layout.card_view_mensajes_emisor,viewGroup,false);
        }
        else {
            view = LayoutInflater.from(ctx).inflate(R.layout.card_view_mensajes_receptor, viewGroup, false);
        }
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
        Date date = new Date(mensaje.getHora());
        PrettyTime prettyTime = new PrettyTime(new Date(), Locale.getDefault());
        cardChatMensajes.getHora().setText(prettyTime.format(date));



    }

    @Override
    public int getItemCount() {
        return listado_de_mensajes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!listado_de_mensajes.get(position).getNickname().equals(null)){
            if (listado_de_mensajes.get(position).getNickname().equals(Usuario.getInstancia().getNickname())){
                return MENSAJE_PROPIO;
            }
            else{
                return MENSAJE_DE_OTRO;
            }
        }
        return MENSAJE_DE_OTRO;
    }
}
