package com.example.hypechat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ChatFragment extends Fragment {

    private EditText texto_mensaje;
    private TextView titulo_chat;
    private Button boton_enviar_mensaje;
    private RecyclerView chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        titulo_chat = (TextView) view.findViewById(R.id.titulo_chat);
        chat = (RecyclerView) view.findViewById(R.id.listado_chat_cards);
        texto_mensaje = (EditText) view.findViewById(R.id.texto_mensaje_a_enviar);
        boton_enviar_mensaje = (Button) view.findViewById(R.id.btn_enviar_mensaje);



        return view;
    }

}
