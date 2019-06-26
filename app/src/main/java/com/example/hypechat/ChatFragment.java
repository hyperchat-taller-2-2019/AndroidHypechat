package com.example.hypechat;


import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment {

    private EditText texto_mensaje;
    private TextView titulo_chat, titulo_orga;
    private Button boton_enviar_mensaje;
    private ImageButton volver;
    private RecyclerView chat;
    private ImageButton boton_enviar_imagen, editar_canal;
    private String id,name = "canal",org_id = "organizacion";
    private Boolean es_canal;

    private AdapterChat adaptador_para_chat;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int PHOTO_SEND = 1;
    private static final String URL_PALABRAS_PRO =  "https://secure-plateau-18239.herokuapp.com/message";
    private static final String URL_CANAL_MENCIONES =  "https://secure-plateau-18239.herokuapp.com/channel/mention";
    private static final String URL_PRIVADO_MENCIONES = "https://secure-plateau-18239.herokuapp.com/privateChat/mention";
    private static final String URL_TITO = "https://secure-plateau-18239.herokuapp.com/tito";

    @SuppressLint("ValidFragment")
    public ChatFragment(boolean canal) {
        this.es_canal = canal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);


        titulo_chat = (TextView) view.findViewById(R.id.titulo_chat);
        titulo_orga = (TextView) view.findViewById(R.id.titulo_orga);
        volver = (ImageButton) view.findViewById(R.id.volver_a_organizacion);
        chat = (RecyclerView) view.findViewById(R.id.listado_chat_cards);
        texto_mensaje = (EditText) view.findViewById(R.id.texto_mensaje_a_enviar);
        boton_enviar_mensaje = (Button) view.findViewById(R.id.btn_enviar_mensaje);
        boton_enviar_imagen = (ImageButton) view.findViewById(R.id.boton_enviar_imagen);
        editar_canal = (ImageButton) view.findViewById(R.id.info_canal);
        titulo_chat.setText(this.name.toUpperCase());
        titulo_orga.setText(this.org_id);

        if(!es_canal){
            editar_canal.setVisibility(View.INVISIBLE);
            editar_canal.setEnabled(false);
        }

        adaptador_para_chat = new AdapterChat(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        chat.setLayoutManager(l);
        chat.setAdapter(adaptador_para_chat);

        editar_canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new EditarCanal(org_id,name));
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();

                //Me traigo el fragmento sabiendo que es el de OrganizacionFragment para cargarle la información
                OrganizacionFragment org = (OrganizacionFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            }
        });


        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new OrganizacionFragment(org_id));
                //Esta es la linea clave para que vuelva al fragmento anterior!
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
                fragmentManager.executePendingTransactions();

                //Me traigo el fragmento sabiendo que es el de OrganizacionFragment para cargarle la información
                OrganizacionFragment org = (OrganizacionFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            }
        });

        boton_enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = texto_mensaje.getText().toString();
                if (!texto.isEmpty()) {
                    if(es_canal) envio_tito(texto);
                    else filtro_mensaje_palabras_prohibidas(texto);
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
                filtro_mensaje_palabras_prohibidas("");
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

    private void envio_tito(String texto) {
        final JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", Usuario.getInstancia().getToken());
            requestBody.put("id", this.org_id);
            requestBody.put("channel", this.name);
            requestBody.put("message", texto);

        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Envio el mensaje para chequeo de mencion de tito");

        Log.i("INFO", "Json Request , check http status codes" + requestBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_TITO, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String mensaje = response.getString("message");
                            filtro_mensaje_palabras_prohibidas(mensaje);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //agregarUser();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (error.networkResponse.statusCode){
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
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

    private void filtro_mensaje_palabras_prohibidas(String texto) {

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("userToken", Usuario.getInstancia().getToken());
            requestBody.put("organizationID", this.org_id);
            requestBody.put("channelName", this.name);
            requestBody.put("message", texto);

        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Envio el mensaje para chequear palabras prohibidas de la organizacion");

        Log.i("INFO", "Json Request , check http status codes" + requestBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_PALABRAS_PRO, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        try {
                            String mensaje = response.getString("message");
                            if(mensaje.compareTo("") != 0){
                                reference.push().setValue(new ChatMensajeEnviar(Usuario.getInstancia().getNickname(),mensaje,
                                        ServerValue.TIMESTAMP,Usuario.getInstancia().getUrl_foto_perfil(),Usuario.getInstancia().getEmail()));
                                mandar_menciones(mensaje);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //agregarUser();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (error.networkResponse.statusCode){
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
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

    private void mandar_menciones(String mensaje) {
        JSONObject requestBody = new JSONObject();
        String URL = URL_CANAL_MENCIONES;
        if(!es_canal) URL = URL_PRIVADO_MENCIONES;
        try {
            requestBody.put("token", Usuario.getInstancia().getToken());
            requestBody.put("message", mensaje);
            requestBody.put("id", this.org_id);
            if(es_canal) requestBody.put("channel", this.name);
            else requestBody.put("email", this.name);

        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Envio el mensaje para chequear menciones y enviar notificaciones");

        Log.i("INFO", "Json Request , check http status codes" + requestBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("INFO", "Notificaciones enviadas");

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


    //para que cuando se llene la pantalla de mensajes siempre vaya al ultimo
    private void setScrollBar() {
        chat.scrollToPosition(adaptador_para_chat.getItemCount()-1);
    }


    public void setSalaDeChat(String id, String name, String orga_id) {

        this.id = id;
        this.name = name;
        this.org_id = orga_id;
        //Aca se decide a que nodo de la base de datos voy a buscar y escribir los mensajes
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(id);
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
        titulo_chat.setText(this.name.toUpperCase());
        titulo_orga.setText(this.org_id);

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
                        ChatMensajeEnviar mensajeEnviar = new ChatMensajeEnviar(Usuario.getInstancia().getNickname(), "Te ha enviado una imagen...",
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
