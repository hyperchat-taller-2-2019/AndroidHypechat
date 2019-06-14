package com.example.hypechat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CardMiembroInfo extends RecyclerView.ViewHolder {

    private TextView email;
    private TextView id;

    public CardMiembroInfo(@NonNull View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        email = (TextView) itemView.findViewById(R.id.email_del_miembro);
        //id = (TextView) itemView.findViewById(R.id.id_de_la_organizacion);

        //Magia para el listener
        itemView.setTag(this);
        itemView.setOnClickListener(onClickListener);
    }

    public TextView getEmail() {
        return email;
    }

    public void setEmail(TextView email) {
        this.email = email;
    }

    public TextView getId() {
        return id;
    }

    public void setId(TextView id) {
        this.id = id;
    }
}
