package com.example.mensajesactividad.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.controladores.Autenticacion;
import com.example.mensajesactividad.modelos.Mensaje;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service implements RequestHandlerInterface  {


    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;
    LocationListener locationListener;
    Polyline line;
    String url = "https://fcm.googleapis.com/fcm/send";
    String score;
    int flag=0;
    LatLng anterior = new LatLng(0, 0);
    Usuario amandarlocalizacion;
    private FirebaseAnalytics mFirebaseAnalytics;

    RequestQueue requestQueue;

    RequestHandlerInterface rh = this;

    String latitud="";
    String longitud="";


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        amandarlocalizacion = (Usuario)intent.getSerializableExtra("usuarioreceptor");


        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        final boolean gpsenable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsenable) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            Toast.makeText(getApplicationContext(), "Active el GPS y vuelva a intentarlo", Toast.LENGTH_LONG).show();
        }

        locationListener = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng actual = new LatLng(latitude, longitude);

                System.out.println("latitude y longitude "+latitude+longitude);

                if (amandarlocalizacion!=null && location!=null){
                    crearMensaje(amandarlocalizacion.getUltimochat(), amandarlocalizacion);

                    latitud=String.valueOf(latitude);
                    longitud=String.valueOf(longitude);
                }
                //insertar

                Geocoder geocoder = new Geocoder(getApplicationContext());


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void crearMensaje(String chatid, Usuario usuarioamandar) {
        Mensaje mensaje = new Mensaje("Localizacion", Rutas.crearfechaHora(), Autenticacion.numerotelefono, Autenticacion.nombredelemisor);


        grabarMensaje(mensaje, chatid, usuarioamandar);

        notificationFirebase(mensaje.getContenido(), chatid, usuarioamandar);
    }


    public void grabarMensaje(Mensaje m, String idchat, Usuario u) {


        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("contenido", m.getContenido().toString());
            jsonBody.put("dia", m.getFecha().toString());
            if (m.getTelefono() != null) {
                jsonBody.put("usuarioid", m.getTelefono().toString());
            } else {
                jsonBody.put("usuarioid", Autenticacion.numerotelefono);
            }

            jsonBody.put("chatid", idchat);
            jsonBody.put("idusuariorecepcion", u.getTelefono().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.rutacrearmensaje, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());
        locationManager.removeUpdates(locationListener);
        this.stopSelf();

    }




    public void crearLocalizacion(String idmensaje) {


        JSONObject jsonBody = new JSONObject();

        try {


            jsonBody.put("latitud", latitud);
            jsonBody.put("longitud", longitud);
            jsonBody.put("mensajeid", idmensaje);

            System.out.println("mando esto "+jsonBody.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.insertarlocalizacion, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }










    @Override
    public void onDestroy() {
        System.out.println("Servicio destruiiido");
    }

    public void notificationFirebase(String m, String chat, Usuario u) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, chat);


        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "String");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        JSONObject mainObj = new JSONObject();
        String token = "";

        try {

            mainObj.put("to", u.getToken().toString());
            JSONObject notificationObj = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("michatid", chat);

            jData.put("titulo", m);
            jData.put("fotoemisor", Autenticacion.rutafotoimportante);
            jData.put("fotoreceptor", u.getUri().toString());

            jData.put("tokenaenviar", u.getToken());
            jData.put("tokenemisor", Autenticacion.tokenorigen);

            jData.put("nombreemisor", Autenticacion.nombredelemisor);
            jData.put("nombrereceptor", u.getNombre().toString());


            jData.put("telefonoemisor", Autenticacion.numerotelefono);
            jData.put("telefonoreceptor", u.getTelefono().toString());

            mainObj.put("priority", "high");

            mainObj.put("data", jData);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    System.out.println("Notificación enviada");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Notificación erronea");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAafa8PTg:APA91bEafAQa2vygzlPALqd72Dik0BflDS7b-hCraAwZvzAkK-hLHsohWvsN1C5kHSSym3pdZx5M63COhYBPosP7Icu-JDXguENKkH3fvXco4CXroInSeLadlujJKpUrqoROt1ttGiW0");
                    return header;
                }
            };


            MySingleton.getInstance(getBaseContext()).addToRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResponse(String response, String url) {

        if (url.equals(Rutas.rutacrearmensaje)){
            try {
                System.out.println("mensaje creado");
                JSONObject respuesta = new JSONObject(response);

                String idmensajecrado = respuesta.getString("ID");


                System.out.println("id mensaje creado "+idmensajecrado);

                crearLocalizacion(idmensajecrado);


            }catch (JSONException e) {



                System.out.println(e.toString());
            }

            System.out.println(response);



        } else  if (url.equals(Rutas.insertarlocalizacion)){
            try {

                JSONObject respuesta = new JSONObject(response);

                String idmensajecrado = respuesta.getString("MENSAJEID");

            }catch (JSONException e) {
                System.out.println(e.toString());
            }

            System.out.println(response);



        }

    }





}
