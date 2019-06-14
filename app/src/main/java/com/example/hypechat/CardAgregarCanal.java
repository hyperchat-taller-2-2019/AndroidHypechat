package com.example.hypechat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CardAgregarCanal extends RecyclerView.ViewHolder {

    private TextView nombre;

    public CardAgregarCanal(@NonNull View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nombre_canal_agregar);
        //id = (TextView) itemView.findViewById(R.id.id_de_la_organizacion);

        //Magia para el listener
        itemView.setTag(this);
        itemView.setOnClickListener(onClickListener);
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }


}
