package com.example.hypechat;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;


public class Perfil extends Fragment {

    private View vista;

    private EditText nombre_perfil, apodo_perfil;
    private TextView email_perfil;
    private Button cambiarContraseña, modificarPerfil;
    private Dialog dialog_cambiar_psw;
    private ValidadorDeCampos validador;
    private ProgressDialog progressDialog;
    private ImageButton btn_cambiar_foto_perfil;
    private ImageView perfil_foto;
    private ImageButton btn_actualizar_ubicacion;
    private static final int PHOTO_PERFIL = 2;
    StorageReference storageReference;
    FirebaseStorage storage;
    private FusedLocationProviderClient fusedLocationClient;


    private View header;


    private final String URL_CAMBIAR_PASSWORD = "https://secure-plateau-18239.herokuapp.com/psw";
    private final String URL_CAMBIAR_PERFIL = "https://secure-plateau-18239.herokuapp.com/profile";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.perfil, container, false);

        dialog_cambiar_psw = new Dialog(getActivity());
        modificarPerfil = (Button) view.findViewById(R.id.boton_modificar_perfil);
        cambiarContraseña = (Button) view.findViewById(R.id.boton_cambiar_contraseña);
        btn_cambiar_foto_perfil = (ImageButton) view.findViewById(R.id.boton_cambiar_foto_perfil);
        btn_actualizar_ubicacion = (ImageButton) view.findViewById(R.id.btn_actualizar_ubicacion);
        storage = FirebaseStorage.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        validador = new ValidadorDeCampos();

        modificarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Apretaste para modificar perfil");
                nombre_perfil = (EditText) view.findViewById(R.id.nombre_perfil);
                apodo_perfil = (EditText) view.findViewById(R.id.apodo_perfil);
                email_perfil = (TextView) view.findViewById(R.id.email_perfil);

                String nombre_perfil_string = nombre_perfil.getText().toString();
                String apodo_perfil_string = apodo_perfil.getText().toString();
                String email_perfil_string = email_perfil.getText().toString();

                if (validador.isValidProfileChange(nombre_perfil_string, apodo_perfil_string, email_perfil_string, getActivity())) {
                    Log.i("TO DO:", "Se puede mandar el request para modificar los datos del usuario!");

                    JSONObject cambiar_perfil_body = new JSONObject();
                    try {
                        String user_token = Usuario.getInstancia().getToken();

                        cambiar_perfil_body.put("token", user_token);
                        cambiar_perfil_body.put("name", nombre_perfil_string);
                        cambiar_perfil_body.put("nickname", apodo_perfil_string);
                        cambiar_perfil_body.put("email", email_perfil_string);

                    } catch (JSONException except) {
                        Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog = ProgressDialog.show(getContext(), "Hypechat", "Modificando Perfil...",
                            true);
                    cambiarPerfilRequest(cambiar_perfil_body);
                }
            }
        });

        cambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "Apretaste para cambiar contraseña");
                dialog_cambiar_psw.setContentView(R.layout.popup_cambiar_password);

                Button b_cambiar_contrasenia = (Button) dialog_cambiar_psw.findViewById(R.id.aceptar_cambio_contrasenia);
                ImageView b_cancelar_cambio = (ImageView) dialog_cambiar_psw.findViewById(R.id.boton_cancelar_cambio_contrasenia);

                final EditText pass_viejo = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_viejo);
                final EditText pass_nuevo = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_nuevo);
                final EditText pass_nuevo_bis = (EditText) dialog_cambiar_psw.findViewById(R.id.et_password_nuevo_bis);

                b_cancelar_cambio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_cambiar_psw.dismiss();
                    }
                });

                b_cambiar_contrasenia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = Usuario.getInstancia().getPassword();

                        Log.i("INFO: ", "Validando que los datos sean correctos!");

                        Log.i("INFO:", "passwordActual:" + password);
                        Log.i("INFO:", "passwordActualInsertado:" + pass_viejo.getText().toString());
                        Log.i("INFO:", "passwordNuevoInsertado:" + pass_nuevo.getText().toString());
                        Log.i("INFO:", "passwordNuevoRepetido:" + pass_nuevo_bis.getText().toString());

                        if (validador.isValidPasswordChange(password, pass_viejo.getText().toString(), pass_nuevo.getText().toString(), pass_nuevo_bis.getText().toString(), getActivity())) {

                            Log.i("INFO: ", "Los datos son correctos!");
                            Log.i("INFO", "hacer el request para cambiar el password!");

                            String token_usuario = Usuario.getInstancia().getToken();

                            JSONObject cambiar_psw_body = new JSONObject();
                            try {
                                cambiar_psw_body.put("token", token_usuario);
                                cambiar_psw_body.put("psw", pass_nuevo.getText().toString());
                            } catch (JSONException except) {
                                Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            progressDialog = ProgressDialog.show(getContext(), "Hypechat", "Cambiando Contraseña...",
                                    true);

                            cambiarPswRequest(cambiar_psw_body);
                        }
                    }
                });


                dialog_cambiar_psw.show();
            }
        });

        btn_cambiar_foto_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO", "APRETASTE PARA CAMBIAR UNA FOTO DE PERFIL!");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar una foto de perfil"), PHOTO_PERFIL);
            }
        });

        btn_actualizar_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Aca hay que hacer el request para actualizar la ubicacion del usuario que apreta
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(getActivity(), "No tenes los permisos necesarios", Toast.LENGTH_LONG).show();
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                Log.i("INFO","VAS A DESCUBRIR TU UBICACION");
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.i("INFO","Tu LONGITUD es: "+location.getLongitude());
                                    Log.i("INFO","Tu LATITUD es: "+location.getLatitude());
                                }else{
                                    Toast.makeText(getActivity(), "Algo falló, intenta de nuevo activando el GPS", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
        });


        this.nombre_perfil = (EditText) view.findViewById(R.id.nombre_perfil);
        this.apodo_perfil = (EditText) view.findViewById(R.id.apodo_perfil);
        this.email_perfil = (TextView) view.findViewById(R.id.email_perfil);
        this.perfil_foto = (ImageView) view.findViewById(R.id.perfil_foto);

        if (!Usuario.getInstancia().getUrl_foto_perfil().equals("")){
            Glide.with(getContext()).load(Usuario.getInstancia().getUrl_foto_perfil()).into(perfil_foto);
        }


        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("INFO","Se va a cargar la foto a la base de datos!");
        if (requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            progressDialog = ProgressDialog.show(getContext(),"Hypechat","Modificando Foto de perfil",
                    true);
            Uri url_foto = data.getData();
            storageReference = storage.getReference("imagenes_perfil");
            final StorageReference foto_referencia = storageReference.child(url_foto.getLastPathSegment());
            //MAGIA QUE PIDE LA NUEVA DOCUMENTACION PARA OBTENER LA DOWNLOAD URL
            foto_referencia.putFile(url_foto).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
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
                        Usuario.getInstancia().setUrl_foto_perfil(downloadUri.toString());
                        //Tomo la referencia de la imagen del header
                        ImageView header_foto_perfil = (ImageView) header.findViewById(R.id.header_foto_perfil);
                        //Cargo la foto en el perfil y en el header
                        Glide.with(getContext()).load(Usuario.getInstancia().getUrl_foto_perfil()).into(perfil_foto);
                        Glide.with(getContext()).load(Usuario.getInstancia().getUrl_foto_perfil()).into(header_foto_perfil);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "La foto de perfil se modificó con exito!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Fallo la carga de la imagen" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void cambiarPerfilRequest(final JSONObject cambiar_perfil_body) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, URL_CAMBIAR_PERFIL, cambiar_perfil_body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        try{
                            setApodoPerfil(cambiar_perfil_body.getString("nickname"));
                            setEmailPerfil(cambiar_perfil_body.getString("email"));
                            setNombrePerfil(cambiar_perfil_body.getString("name"));
                            Usuario.getInstancia().setNombre(cambiar_perfil_body.getString("name"));
                            Usuario.getInstancia().setEmail(cambiar_perfil_body.getString("email"));
                            Usuario.getInstancia().setNickname(cambiar_perfil_body.getString("nickname"));

                            TextView header_nombre_usuario = (TextView) header.findViewById(R.id.header_user_name);
                            header_nombre_usuario.setText(cambiar_perfil_body.getString("name"));
                            Toast.makeText(getContext(), "El perfil se modifico Correctamente!", Toast.LENGTH_LONG).show();
                        }
                        catch (JSONException exception){
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),
                                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),
                                        "Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                Toast.makeText(getActivity(),
                                        "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void cambiarPswRequest(final JSONObject cambiar_psw_body) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, URL_CAMBIAR_PASSWORD, cambiar_psw_body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("INFO", "La contraseña se modifico correctamente!");
                        progressDialog.dismiss();
                        try{
                            Usuario.getInstancia().setPassword(cambiar_psw_body.getString("psw"));
                            Toast.makeText(getActivity(), "La contraseña ha sido modificada con Exito!", Toast.LENGTH_LONG).show();
                        }catch (JSONException exception){
                            Log.i("INFO", exception.getMessage());
                        }
                        dialog_cambiar_psw.dismiss();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),
                                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),
                                        "Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                Toast.makeText(getActivity(),
                                        "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void completarDatosPerfil(String nombre, String apodo, String email,String url_foto, Boolean  soy_yo){
        this.setNombrePerfil(nombre);
        this.setApodoPerfil(apodo);
        this.setEmailPerfil(email);


        //Tomo la referencia de la imagen del header
        ImageView header_foto_perfil = (ImageView) header.findViewById(R.id.header_foto_perfil);
        if (!Usuario.getInstancia().getUrl_foto_perfil().equals("")) {
            //Cargo la foto en el perfil y en el header
            Glide.with(getContext()).load(Usuario.getInstancia().getUrl_foto_perfil()).into(header_foto_perfil);
            Glide.with(getContext()).load(url_foto).into(perfil_foto);
        }

        if (soy_yo){
            mostrarBotones();
            setEditTextNormales();
        }
        else{
            ocultarBotones();
            setearEditTextComoNoEditables();
        }
    }

    private void setearEditTextComoNoEditables() {
        this.nombre_perfil.setEnabled(false);
        this.apodo_perfil.setEnabled(false);
    }

    private void setEditTextNormales() {
        this.nombre_perfil.setEnabled(true);
        this.apodo_perfil.setEnabled(true);
    }

    private void mostrarBotones() {
        this.modificarPerfil.setVisibility(View.VISIBLE);
        this.cambiarContraseña.setVisibility(View.VISIBLE);
    }

    private void ocultarBotones() {
        this.modificarPerfil.setVisibility(View.GONE);
        this.cambiarContraseña.setVisibility(View.GONE);
    }

    private void setEmailPerfil(String email) {
        this.email_perfil.setText(email);
    }

    private void setNombrePerfil(String nombre){
        this.nombre_perfil.setText(nombre);
    }

    private void setApodoPerfil(String apodo){
        this.apodo_perfil.setText(apodo);
    }

    public void setHeader (View header_recibido){
        this.header = header_recibido;
    }
}
