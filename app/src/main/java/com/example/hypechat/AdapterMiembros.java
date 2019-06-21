package com.example.hypechat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AdapterMiembros extends RecyclerView.Adapter<CardMiembroInfo> {


    private List<String> lista_usuarios = new ArrayList<>();
    private Context ctx;
    private View.OnClickListener mOnItemClickListener;



    public AdapterMiembros(Context ctx) {

        this.ctx = ctx;

    }

    public void agregarMiembro(String user_email){
        lista_usuarios.add(user_email);
        notifyItemInserted(lista_usuarios.size());
    }

    //Magia para el listener
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CardMiembroInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.card_view_miembro,viewGroup,false);
        return new CardMiembroInfo(view,this.mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardMiembroInfo cardUsuarioInfo, int i) {
        String usuario_actual = lista_usuarios.get(i);

        cardUsuarioInfo.getEmail().setText(usuario_actual);
        //cardUsuarioInfo.getId().setText(organizacionActual.getId());

    }

    @Override
    public int getItemCount() {
        return lista_usuarios.size();
    }

    public String obtenerItemPorPosicion(int position) {
        return lista_usuarios.get(position);
    }

    public void vaciar_lista(){
        lista_usuarios.clear();
    }
}
