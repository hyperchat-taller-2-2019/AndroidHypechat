package com.example.hypechat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AdapterOrganizaciones extends RecyclerView.Adapter<CardOrganizacionInfo> {

    private List<Organizacion> lista_organizaciones = new ArrayList<>();
    private Context ctx;
    private View.OnClickListener mOnItemClickListener;


    public AdapterOrganizaciones(Context ctx) {
        this.ctx = ctx;
    }

    public void agregarOrganizacion(Organizacion organizacion){
        lista_organizaciones.add(organizacion);
        notifyItemInserted(lista_organizaciones.size());
    }

    //Magia para el listener
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CardOrganizacionInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.card_view_organizacion,viewGroup,false);
        return new CardOrganizacionInfo(view,this.mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardOrganizacionInfo cardOrganizacionInfo, int i) {
        Organizacion organizacionActual = lista_organizaciones.get(i);

        cardOrganizacionInfo.getNombre().setText(organizacionActual.getNombre());
        cardOrganizacionInfo.getId().setText(organizacionActual.getId());

    }

    @Override
    public int getItemCount() {
        return lista_organizaciones.size();
    }

    public Organizacion obtenerItemPorPosicion(int position) {
        return lista_organizaciones.get(position);
    }

}
