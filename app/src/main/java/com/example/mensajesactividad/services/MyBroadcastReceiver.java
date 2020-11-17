package com.example.mensajesactividad.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.controladores.Autenticacion;
import com.example.mensajesactividad.modelos.Chat;
import com.example.mensajesactividad.modelos.Grupo;
import com.example.mensajesactividad.modelos.Mensaje;
import com.example.mensajesactividad.modelos.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class MyBroadcastReceiver extends BroadcastReceiver implements RequestHandlerInterface {

    String KEY_REPLY = "key_reply";


    String urlcrearmensaje= Rutas.rutacrearmensaje;
    String id_mensaje;
    RequestQueue requestQueue;
    RequestHandlerInterface rh = this;
    String chat_id;
    Usuario usuarioemisor;
    Usuario usuarioreceptor;
    CharSequence contenido;
    String url="https://fcm.googleapis.com/fcm/send";


    String buscargrupo=Rutas.rutabuscargrupo;

    boolean esgrupo=false;
    Grupo grupo;
    Context micontext;
    String grupoahora="false";


    private static final int notificationid=001;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("broadcast ");
        requestQueue= Volley.newRequestQueue(context.getApplicationContext());

        contenido = getMessageText(intent);
        chat_id=intent.getStringExtra("chat_id");


        micontext=context.getApplicationContext();
        micontext=context;


        buscarSiEsGrupo(chat_id, context);
        LocalDateTime ahora= LocalDateTime.now();
        ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dia=ahora.format(dtf);



//        Mensaje mensaje=new Mensaje(contenido.toString(), dia, usuarioemisor.getTelefono().toString(), usuarioemisor.getNombre().toString());


        Mensaje mensaje=new Mensaje(contenido.toString(), dia, Autenticacion.numerotelefono, Autenticacion.nombredelemisor);
        id_mensaje=String.valueOf(zdt.toInstant().toEpochMilli());

        if (esgrupo) {


            for (int g = 0; g < grupo.getDetallesmiembros().size(); g++) {
                usuarioreceptor = (Usuario) grupo.getDetallesmiembros().get(g);
                usuarioemisor=new Usuario(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, null, Autenticacion.tokenorigen);
                grabarMensaje(mensaje, id_mensaje, context);

            }
        }else {
            usuarioemisor=(Usuario) intent.getSerializableExtra("usuarioemisor");
            usuarioreceptor=(Usuario) intent.getSerializableExtra("usuarioreceptor");


            grabarMensaje(mensaje, id_mensaje, context);
        }



    }



    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }



    public void grabarMensaje(Mensaje m, String id, Context context){

        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("contenido", m.getContenido().toString());
            jsonBody.put("dia", m.getFecha().toString());
            jsonBody.put("usuarioid", m.getTelefono().toString());
            jsonBody.put("chatid", chat_id);
            jsonBody.put("idusuariorecepcion", usuarioreceptor.getTelefono().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.rutacrearmensaje, jsonBody, rh);

        MySingleton.getInstance(context).addToRequest(cr.crearRequest());



    }




    public void notificationFirebase(Context context)   {

        JSONObject mainObj=new JSONObject();
        String token="";
        try {

            mainObj.put("to", usuarioreceptor.getToken().toString());
            JSONObject notificationObj=new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("michatid", chat_id);
            jData.put("titulo", contenido.toString());

            jData.put("tokenaenviar", usuarioreceptor.getToken().toString());
            jData.put("tokenemisor", usuarioemisor.getToken().toString());


            jData.put("fotoemisor", usuarioemisor.getUri().toString());
            jData.put("fotoreceptor", usuarioreceptor.getUri().toString());


            jData.put("nombreemisor", usuarioemisor.getNombre().toString());
            jData.put("nombrereceptor", usuarioreceptor.getNombre().toString());


            jData.put("telefonoemisor", usuarioemisor.getTelefono().toString());
            jData.put("telefonoreceptor", usuarioreceptor.getTelefono().toString());


            mainObj.put("priority","high");

            mainObj.put("data", jData);
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    System.out.println("Notificación enviada");
                    broadcastIntent(context, usuarioemisor, usuarioreceptor);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Notificación erronea");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header=new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAafa8PTg:APA91bEafAQa2vygzlPALqd72Dik0BflDS7b-hCraAwZvzAkK-hLHsohWvsN1C5kHSSym3pdZx5M63COhYBPosP7Icu-JDXguENKkH3fvXco4CXroInSeLadlujJKpUrqoROt1ttGiW0");
                    return header;
                }
            };

            MySingleton.getInstance(context).addToRequest(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void broadcastIntent(Context context, Usuario emisora, Usuario receptora) {
        Intent intent = new Intent();
        intent.setAction("com.myApp.CUSTOM_EVENT");


        Bundle argsi = new Bundle();
        emisora.setUltimochat(chat_id);
        receptora.setUltimochat(chat_id);
        argsi.putSerializable("emisor", (Serializable) emisora);
        argsi.putSerializable("receptor", (Serializable) receptora);

        argsi.putString("esgrupo", grupoahora);

        intent.putExtra("DATA", argsi);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }



        }, 2000);




    }






    private void buscarSiEsGrupo(String idc, Context context) {

        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("ID", idc);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.rutabuscargrupo, jsonBody, rh);

        MySingleton.getInstance(context).addToRequest(cr.crearRequest());

    }







    @Override
    public void onResponse(String response, String url) {

        if (url.equals(Rutas.rutacrearmensaje)){


            try {
                JSONObject jsnobject = new JSONObject(response.toString());

                String contenido = jsnobject.getString("contenido");

                System.out.println("respuesta");
                System.out.println(response);
                NotificationManager notificationManager = (NotificationManager)micontext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(MyBroadcastReceiver.notificationid);



                notificationFirebase(micontext);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (url.equals(Rutas.rutabuscargrupo)){


            try {

                JSONObject respuesta = new JSONObject(response);

                String iddelgrupo = respuesta.getString("ID").toString();
                String nombregrupo = respuesta.getString("NOMBRE").toString();

                esgrupo=true;

                grupoahora="true";

                //    grupo=(Grupo) llegada.getSerializableExtra("grupoinfo");



                JSONArray jsonArray=respuesta.getJSONArray("MIEMBROS");
                JSONObject e = null;
                ArrayList<Usuario> usuariogrupo=new ArrayList<>();
                for (int j=0; j < jsonArray.length(); j++) {
                    e = jsonArray.getJSONObject(j);

                    //Usuario(String telefono, String nombre, String uri, String token, String id)

                    Usuario usuariomiembro=new Usuario(e.getString("TELEFONO").toString(), e.getString("NOMBRE").toString(), e.getString("RUTA").toString(), e.getString("TOKEN").toString(), e.getString("ID").toString());

                    usuariomiembro.setUri(Rutas.construirRuta(usuariomiembro.getUri().toString()));

                    usuariogrupo.add(usuariomiembro);
                }

                grupo=new Grupo(nombregrupo, iddelgrupo, "", usuariogrupo);


            }catch (JSONException e) {
                esgrupo=false;

                grupoahora="false";

            }



        }

    }





}
