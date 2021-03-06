package com.example.hypechat;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CardAgregarMiembroCanal extends RecyclerView.ViewHolder {

    CheckBox email;


    public CardAgregarMiembroCanal(@NonNull View itemView) {
        super(itemView);
        email = (CheckBox) itemView.findViewById(R.id.checkbox_miembro_org);
        email.setClickable(false);
        //id = (TextView) itemView.findViewById(R.id.id_de_la_organizacion);

        //Magia para el listener
        itemView.setTag(this);
      
    }

    public CheckBox getEmail() {
        return email;
    }

    public void setEmail(CheckBox email) {
        this.email = email;
    }

    public void setClickeable(Boolean edit_permition){this.email.setEnabled(edit_permition);}

    public void setOnClickListener(View.OnClickListener onClickListener) {
        itemView.setOnClickListener(onClickListener);
    }

}
