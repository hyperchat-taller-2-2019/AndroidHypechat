package com.example.hypechat;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UbicacionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String id_orga;
    private final String URL_OBTENER_UBICACIONES = "https://secure-plateau-18239.herokuapp.com/locations/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        id_orga = getIntent().getStringExtra("id_organizacion");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String Url = URL_OBTENER_UBICACIONES+Usuario.getInstancia().getToken()+"/"+id_orga;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, Url, null , new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       completarMapa(response);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(UbicacionActivity.this,
                                        "Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(UbicacionActivity.this,
                                        "Server error!", Toast.LENGTH_LONG).show();
                            case (404):
                                Toast.makeText(UbicacionActivity.this,
                                        "No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                            default:
                                Toast.makeText(UbicacionActivity.this, "Ocurrio un error!!!", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void completarMapa(JSONObject response) {
        //ACA DEBO COMPLETAR EL MAPA CON LAS UBICACIONES DEL RESPONSE
        //Esto para cada elemento del array.
        Log.i("INFO",response.toString());
        try{
            JSONArray usuarios = response.getJSONArray("users");
            Log.i("INFO", String.valueOf(usuarios.length()));

            for (int i=0; i<usuarios.length(); i++){
                JSONObject usuario = usuarios.getJSONObject(i);
                LatLng ubicacion = new LatLng(usuario.getDouble("latitud"),usuario.getDouble("longitud"));
                MarkerOptions marcador = new MarkerOptions().position(ubicacion).title(usuario.getString("nickname"));
                mMap.addMarker(marcador);
                if (usuario.getString("nickname").equals(Usuario.getInstancia().getNickname())){
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(ubicacion)      // Sets the center of the map to Mountain View
                            .zoom(12)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(20)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }


        }catch(JSONException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
