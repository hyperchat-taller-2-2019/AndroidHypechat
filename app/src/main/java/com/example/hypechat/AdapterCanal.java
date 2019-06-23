package com.example.hypechat;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AdapterCanal extends RecyclerView.Adapter<CardAgregarCanal> {

    private List<String> lista_canales = new ArrayList<>();
    private Context ctx;
    private View.OnClickListener mOnItemClickListener;



    public AdapterCanal(Context ctx) {

        this.ctx = ctx;

    }

    public void agregarCanal(String nombre_canal){
        lista_canales.add(nombre_canal);
        notifyItemInserted(lista_canales.size());
    }

    //Magia para el listener
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CardAgregarCanal onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.card_view_canal,viewGroup,false);
        return new CardAgregarCanal(view,this.mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAgregarCanal cardCanal, int i) {
        String canal_actual = lista_canales.get(i);

        cardCanal.getNombre().setText(canal_actual);
        //cardUsuarioInfo.getId().setText(organizacionActual.getId());

    }

    @Override
    public int getItemCount() {
        return lista_canales.size();
    }

    public String obtenerItemPorPosicion(int position) {
        return lista_canales.get(position);
    }

    public void vaciar_lista(){
        lista_canales.clear();
    }
}