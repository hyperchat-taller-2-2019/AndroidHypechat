package com.example.hypechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;


class ChatPrivateFragment extends Fragment {


    private EditText texto_mensaje;
    private TextView titulo_chat, titulo_orga;
    private Button boton_enviar_mensaje;
    private ImageButton volver, look;
    private RecyclerView chat;
    private ImageButton boton_enviar_imagen, boton_enviar_archivo, boton_enviar_snippet;;
    private String id_chat,email_chat;
    private Boolean snippet = false;


    private AdapterChat adaptador_para_chat;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int PHOTO_SEND = 1;
    private static final int FILE_SEND = 2;
    private static final String URL_PRIVADO_MENCIONES = "https://secure-plateau-18239.herokuapp.com/privateChat/mention";

    public ChatPrivateFragment(String id, String name) {
        this.id_chat = id;
        this.email_chat = name;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        titulo_chat = (TextView) view.findViewById(R.id.titulo_chat);
        titulo_chat.setText(email_chat);
        titulo_orga = (TextView) view.findViewById(R.id.titulo_orga);
        titulo_orga.setText("Chats privados");
        volver = (ImageButton) view.findViewById(R.id.volver_a_organizacion);
        chat = (RecyclerView) view.findViewById(R.id.listado_chat_cards);
        texto_mensaje = (EditText) view.findViewById(R.id.texto_mensaje_a_enviar);
        boton_enviar_mensaje = (Button) view.findViewById(R.id.btn_enviar_mensaje);
        boton_enviar_imagen = (ImageButton) view.findViewById(R.id.boton_enviar_imagen);
        boton_enviar_archivo = (ImageButton) view.findViewById(R.id.boton_enviar_archivo);
        boton_enviar_snippet = (ImageButton) view.findViewById(R.id.boton_snippet);
        look = (ImageButton) view.findViewById(R.id.info_canal);
        look.setVisibility(View.INVISIBLE);
        look.setEnabled(false);


        texto_mensaje.setText("");

        adaptador_para_chat = new AdapterChat(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        chat.setLayoutManager(l);
        chat.setAdapter(adaptador_para_chat);


        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Volver a los chats privados");

                getFragmentManager().popBackStackImmediate();
            }
        });

        boton_enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = texto_mensaje.getText().toString();
                if (!texto.isEmpty()) {
                    reference.push().setValue(new ChatMensajeEnviar(Usuario.getInstancia().getNickname(),texto,
                            ServerValue.TIMESTAMP,Usuario.getInstancia().getUrl_foto_perfil(),Usuario.getInstancia().getEmail()));
                    mandar_menciones(texto);
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
        boton_enviar_archivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar un archivo a enviar"),FILE_SEND);

            }
        });

        boton_enviar_snippet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!snippet){
                    snippet = true;
                    boton_enviar_snippet.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_snippet_selected));
                }else{
                    snippet = false;
                    boton_enviar_snippet.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_snippet));
                }

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

    private void mandar_menciones(String mensaje) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("token", Usuario.getInstancia().getToken());
            requestBody.put("message", mensaje);
            requestBody.put("email", this.email_chat);

        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Envio el mensaje para chequear menciones y enviar notificaciones");

        Log.i("INFO", "Json Request , check http status codes" + requestBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_PRIVADO_MENCIONES, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("INFO", "Notificaciones enviadas");
                        //agregarUser();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (error.networkResponse.statusCode){
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible enviar las notificaciones, por favor intente mas tarde", Toast.LENGTH_LONG).show();
                                break;
                            case (400):
                                Toast.makeText(getActivity(),"El ID ya existe, intente con otro.", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                Toast.makeText(getActivity(),"El usuario o organizacion es invalido", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void setSalaDeChat() {


        //Aca se decide a que nodo de la base de datos voy a buscar y escribir los mensajes
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(this.id_chat);
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


        //Seteo de la configuracion para manejar Imagenes en la base de datos
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("imagenes_chat");
    }

    //para que cuando se llene la pantalla de mensajes siempre vaya al ultimo
    private void setScrollBar() {
        chat.scrollToPosition(adaptador_para_chat.getItemCount()-1);
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
                        ChatMensajeEnviar mensajeEnviar = new ChatMensajeEnviar(Usuario.getInstancia().getNickname(), "Te ha enviado una imagen...",
                                Usuario.getInstancia().getUrl_foto_perfil(), downloadUri.toString(), ServerValue.TIMESTAMP,Usuario.getInstancia().getEmail());
                        reference.push().setValue(mensajeEnviar);
                    } else {
                        Toast.makeText(getActivity(), "Fallo la carga de la imagen" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if (requestCode == FILE_SEND && resultCode == RESULT_OK){
            Uri url_foto = data.getData();

            final StorageReference foto_referencia = storage.getReference("Archivos").child(url_foto.getLastPathSegment());
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
                        Log.i("INFO","La url del archivo es: " + downloadUri.toString());
                        ChatMensajeEnviar mensajeEnviar = new ChatMensajeEnviar(Usuario.getInstancia().getNickname(), "Te ha enviado un archivo...",
                                Usuario.getInstancia().getUrl_foto_perfil(), downloadUri.toString(), ServerValue.TIMESTAMP,Usuario.getInstancia().getEmail());
                        reference.push().setValue(mensajeEnviar);
                    } else {
                        Toast.makeText(getActivity(), "Fallo la carga de la imagen" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

