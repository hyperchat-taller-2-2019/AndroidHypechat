package com.example.hypechat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class AdapterMiembrosCanal extends RecyclerView.Adapter<CardAgregarMiembroCanal> {

    private List<String> lista_usuarios = new ArrayList<>();
    private Context ctx;
    //private View.OnClickListener mOnItemClickListener;

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    interface OnItemCheckListener {
        void onItemCheck(String item);
        void onItemUncheck(String item);
    }

    public AdapterMiembrosCanal(Context ctx) {

        this.ctx = ctx;

    }

    public void agregarMiembro(String user_email){
        lista_usuarios.add(user_email);
        notifyItemInserted(lista_usuarios.size());
    }

    //Magia para el listener
    public void setOnItemClickListener(OnItemCheckListener itemClickListener) {
        onItemCheckListener = itemClickListener;
    }

    @NonNull
    @Override
    public CardAgregarMiembroCanal onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.card_view_agregar_usuario_canal,viewGroup,false);
        return new CardAgregarMiembroCanal(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardAgregarMiembroCanal cardUsuarioInfo, int i) {
        final String usuario_actual = lista_usuarios.get(i);

        cardUsuarioInfo.getEmail().setText(usuario_actual);

        cardUsuarioInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardUsuarioInfo.email.setChecked(!cardUsuarioInfo.email.isChecked());
                if (cardUsuarioInfo.email.isChecked()) {
                    onItemCheckListener.onItemCheck(usuario_actual);
                } else {
                    onItemCheckListener.onItemUncheck(usuario_actual);
                }
            }
        });
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

    public ArrayList<String> getChequeados() {
        ArrayList<String> lista = new ArrayList<String>();



        return lista;
    }
}
