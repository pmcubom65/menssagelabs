package com.example.mensajesactividad.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.controladores.Autenticacion;
import com.example.mensajesactividad.modelos.Grupo;
import com.example.mensajesactividad.modelos.Mensaje;
import com.example.mensajesactividad.modelos.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    broadcastIntent(context);
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
      //      requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void broadcastIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.myApp.CUSTOM_EVENT");
        // We should use LocalBroadcastManager when we want INTRA app
        // communication
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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





     /*   StringRequest request = new StringRequest(Request.Method.POST, buscargrupo, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String iddelgrupo = respuesta.getString("ID").toString();
                    String nombregrupo = respuesta.getString("NOMBRE").toString();


                    System.out.println("grupo encontrado");
                    esgrupo=true;

                    //       grupo=(Grupo) llegada.getSerializableExtra("grupoinfo");



                    JSONArray jsonArray=respuesta.getJSONArray("MIEMBROS");
                    JSONObject e = null;
                    ArrayList<Usuario> usuariogrupo=new ArrayList<>();
                    for (int j=0; j < jsonArray.length(); j++) {
                        e = jsonArray.getJSONObject(j);

                        Usuario usuariomiembro=new Usuario(e.getString("TELEFONO").toString(), e.getString("NOMBRE").toString(), null, e.getString("TOKEN").toString());
                        usuariogrupo.add(usuariomiembro);
                    }

                    grupo=new Grupo(nombregrupo, iddelgrupo, "", usuariogrupo);

                }catch (JSONException e) {

                    System.out.println("No es grupo");
                }

                System.out.println(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("volley error");
                error.printStackTrace();

                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data

                        JSONObject obj = new JSONObject(res);
                        System.out.println(obj.toString());
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        Log.e("JSON Parser", "Error parsing data " + e1.toString());
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        Log.e("JSON Parser", "Error parsing data " + e2.toString());
                        e2.printStackTrace();
                    }
                }

                System.out.println(error.toString());

            }
        }) {


            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("ID", idc);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {

                    return jsonBody == null ? null : jsonBody.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException uee) {

                    return null;
                }


            }
        };

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        MySingleton.getInstance(context).addToRequest(request);*/

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

            System.out.println("mensaje grabado "+ response.toString());
        } else if (url.equals(Rutas.rutabuscargrupo)){


            try {

                JSONObject respuesta = new JSONObject(response);

                String iddelgrupo = respuesta.getString("ID").toString();
                String nombregrupo = respuesta.getString("NOMBRE").toString();


                System.out.println("grupo encontrado");
                esgrupo=true;

                //    grupo=(Grupo) llegada.getSerializableExtra("grupoinfo");



                JSONArray jsonArray=respuesta.getJSONArray("MIEMBROS");
                JSONObject e = null;
                ArrayList<Usuario> usuariogrupo=new ArrayList<>();
                for (int j=0; j < jsonArray.length(); j++) {
                    e = jsonArray.getJSONObject(j);

                    //Usuario(String telefono, String nombre, String uri, String token, String id)

                    Usuario usuariomiembro=new Usuario(e.getString("TELEFONO").toString(), e.getString("NOMBRE").toString(), e.getString("RUTA").toString(), e.getString("TOKEN").toString(), e.getString("ID").toString());

                    System.out.println("usuariomiembro broadcast "+usuariomiembro);

                    usuariomiembro.setUri(Rutas.construirRuta(usuariomiembro.getUri().toString()));

                    usuariogrupo.add(usuariomiembro);
                }

                grupo=new Grupo(nombregrupo, iddelgrupo, "", usuariogrupo);


                System.out.println("este es mi grupo "+grupo);

            }catch (JSONException e) {

                System.out.println("No es grupo");
            }

            System.out.println(response);





        }

    }



}
