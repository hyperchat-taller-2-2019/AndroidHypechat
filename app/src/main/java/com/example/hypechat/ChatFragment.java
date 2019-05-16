package com.example.hypechat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment {

    private EditText texto_mensaje;
    private TextView titulo_chat;
    private Button boton_enviar_mensaje;
    private RecyclerView chat;
    private ImageButton boton_enviar_imagen;

    private AdapterChat adaptador_para_chat;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;

    private static final int PHOTO_SEND = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        titulo_chat = (TextView) view.findViewById(R.id.titulo_chat);
        chat = (RecyclerView) view.findViewById(R.id.listado_chat_cards);
        texto_mensaje = (EditText) view.findViewById(R.id.texto_mensaje_a_enviar);
        boton_enviar_mensaje = (Button) view.findViewById(R.id.btn_enviar_mensaje);
        boton_enviar_imagen = (ImageButton) view.findViewById(R.id.boton_enviar_imagen);

        adaptador_para_chat = new AdapterChat(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        chat.setLayoutManager(l);
        chat.setAdapter(adaptador_para_chat);

        //SharedPref para almacenar datos de sesión
        this.sharedPref = getActivity().getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        this.sharedEditor = sharedPref.edit();

        boton_enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = texto_mensaje.getText().toString();
                if (!texto.isEmpty()) {
                    reference.push().setValue(new ChatMensajeEnviar(sharedPref.getString("apodo", "NO NICK"),texto, ServerValue.TIMESTAMP,"www.example.com/imagen.png"));
                }
                else{
                    Toast.makeText(getContext(), "No podes mandar un mensaje Vacio!", Toast.LENGTH_LONG).show();
                }
            }
        });

        boton_enviar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar una foto a enviar"),PHOTO_SEND);
            }
        });

        adaptador_para_chat.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });
        return view;
    }


    //para que cuando se llene la pantalla de mensajes siempre vaya al ultimo
    private void setScrollBar() {
        chat.scrollToPosition(adaptador_para_chat.getItemCount()-1);
    }


    public void setSalaDeChat(String nombre_de_sala) {
        //Aca se decide a que nodo de la base de datos voy a buscar y escribir los mensajes
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(nombre_de_sala);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMensajeRecibir mensaje = dataSnapshot.getValue(ChatMensajeRecibir.class);
                adaptador_para_chat.agregarMensaje(mensaje);
                texto_mensaje.setText("");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        titulo_chat.setText(nombre_de_sala.toUpperCase());

        //Seteo de la configuracion para manejar Imagenes en la base de datos
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("imagenes_chat");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("INFO","Se va a cargar la foto a la base de datos!");
        if (requestCode == PHOTO_SEND && resultCode == RESULT_OK){
            Uri url_foto = data.getData();
            final StorageReference foto_referencia = storageReference.child(url_foto.getLastPathSegment());
            //MAGIA QUE PIDE LA NUEVA DOCUMENTACION PARA OBTENER LA DOWNLOAD URL
            foto_referencia.putFile(url_foto).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return foto_referencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.i("INFO","La url de la foto es: " + downloadUri.toString());
                        ChatMensajeEnviar mensajeEnviar = new ChatMensajeEnviar(sharedPref.getString("apodo", "NO NICK"), "Te ha enviado una imagen...",
                                "www.example.com/imagen.png", downloadUri.toString(), ServerValue.TIMESTAMP);
                        reference.push().setValue(mensajeEnviar);
                    } else {
                        Toast.makeText(getActivity(), "Fallo la carga de la imagen" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
