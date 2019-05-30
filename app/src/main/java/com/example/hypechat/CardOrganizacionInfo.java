package com.example.hypechat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CardOrganizacionInfo extends RecyclerView.ViewHolder {

    private TextView nombre;
    private TextView id;

    public CardOrganizacionInfo(@NonNull View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nombre_de_la_organizacion);
        id = (TextView) itemView.findViewById(R.id.id_de_la_organizacion);

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

    public TextView getId() {
        return id;
    }

    public void setId(TextView id) {
        this.id = id;
    }
}
