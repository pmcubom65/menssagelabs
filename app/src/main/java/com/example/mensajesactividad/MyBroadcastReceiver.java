package com.example.mensajesactividad;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.example.mensajesactividad.modelos.Mensaje;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;



public class MyBroadcastReceiver extends BroadcastReceiver {

    String KEY_REPLY = "key_reply";
  //  String urlcrearmensaje="http://10.0.2.2/api/crearmensaje.php";

    String urlcrearmensaje="http://10.0.2.2:54119/api/smartchat/crearmensaje";
    String id_mensaje;
    RequestQueue requestQueue;
    String chat_id;
    Usuario usuarioemisor;
    Usuario usuarioreceptor;
    CharSequence contenido;
    String url="https://fcm.googleapis.com/fcm/send";

    private static final int notificationid=001;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("broadcast ");
        requestQueue= Volley.newRequestQueue(context.getApplicationContext());

        contenido = getMessageText(intent);
        chat_id=intent.getStringExtra("chat_id");

       usuarioemisor=(Usuario) intent.getSerializableExtra("usuarioemisor");
       usuarioreceptor=(Usuario) intent.getSerializableExtra("usuarioreceptor");

        LocalDateTime ahora= LocalDateTime.now();
        ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dia=ahora.format(dtf);

        Mensaje mensaje=new Mensaje(contenido.toString(), dia, usuarioemisor.getTelefono().toString(), usuarioemisor.getNombre().toString());
        id_mensaje=String.valueOf(zdt.toInstant().toEpochMilli());
        grabarMensaje(mensaje, id_mensaje, context);

    }



    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }



    public void grabarMensaje(Mensaje m, String id, Context context){


        StringRequest request = new StringRequest(Request.Method.POST, urlcrearmensaje, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsnobject = new JSONObject(response.toString());

                    String contenido = jsnobject.getString("contenido");

                    System.out.println("respuesta");
                    System.out.println(response);
                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MyBroadcastReceiver.notificationid);



                notificationFirebase(context);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("mensaje grabado "+ response.toString());

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
                    jsonBody.put("contenido", m.getContenido().toString());
                    jsonBody.put("dia", m.getFecha().toString());
                    jsonBody.put("usuarioid", m.getTelefono().toString());
                    jsonBody.put("chatid", chat_id);
                    jsonBody.put("idusuariorecepcion", usuarioreceptor.getTelefono().toString());


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
        MySingleton.getInstance(context).addToRequest(request);
    //    requestQueue.add(request);









   /*     StringRequest request=new StringRequest(Request.Method.POST, urlcrearmensaje, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {
                System.out.println("respuesta");
                System.out.println(response);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(MyBroadcastReceiver.notificationid);
                notificationFirebase();

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters=new HashMap<>();
                parameters.put("mensaje_id",id);
                parameters.put("contenido", m.getContenido());
                parameters.put("dia", m.getFecha());
                parameters.put("chat_id", chat_id);
                parameters.put("telefono", m.getTelefono());
                return parameters;
            }
        };

        requestQueue.add(request);*/
    }




    public void notificationFirebase(Context context)   {

        //https://stackoverflow.com/questions/37731275/display-specific-activity-when-firebase-notification-is-tapped/38195369


 /*       Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, michatid);
        //     bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "String");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/



        //our json object
        JSONObject mainObj=new JSONObject();
        String token="";
        try {


            //      String tokensegundo="dbMjzbyeRvK7H7X0JPFRml:APA91bGnxgY1r1waKY2Knmbc5kiSjtK12Z_IJkDmjKsJ7YuDvSN5w6phiWoIhGbTeEMOx89_78FUbluUr9CxMdb-vhnpp61IYwaJipBh4m0O66n0SSDlKl4hQT57uhdllhmL6rJacmFB";
            //       mainObj.put("to", tokenaenviarlosmensajes);

            mainObj.put("to", usuarioreceptor.getToken().toString());
            JSONObject notificationObj=new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("michatid", chat_id);
            jData.put("titulo", contenido.toString());

            jData.put("tokenaenviar", usuarioreceptor.getToken().toString());
            jData.put("tokenemisor", usuarioemisor.getToken().toString());


            jData.put("nombreemisor", usuarioemisor.getNombre().toString());
            jData.put("nombrereceptor", usuarioreceptor.getNombre().toString());


            jData.put("telefonoemisor", usuarioemisor.getTelefono().toString());
            jData.put("telefonoreceptor", usuarioreceptor.getTelefono().toString());

     /*       notificationObj.put("title", mensaje.getContenido());

            notificationObj.put("body", mensaje.getContenido());
            notificationObj.put("sound", "default");
            notificationObj.put("click_action", "CLICK_ACTION");*/


            //       mainObj.put("notification", notificationObj);
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

}
