package com.example.mensajesactividad.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.mensajesactividad.controladores.MainActivity;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.controladores.MostrarContactos;
import com.example.mensajesactividad.modelos.Chat;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MyFirebaseInstanceService extends FirebaseMessagingService {


    private final String canal="5555";
    private final int notificationid=001;
    String KEY_REPLY = "key_reply";
    public static boolean flag=false;

    String chat_id;
    String titulo;

    Usuario emisor;
    Usuario receptor;

    Boolean esgrupo=false;

    public static Chat michat;

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TOKEN", "Refreshed token: " + token);

    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       System.out.println( "From: " + remoteMessage.toString());

        Map<String, String> data = remoteMessage.getData();

        chat_id=(String) data.get("michatid");
        titulo=(String) data.get("titulo");

        String tokenemisor=(String) data.get("tokenaenviar");
        String nombreemisor=(String) data.get("nombrereceptor");
        String telefonoemisor=(String) data.get("telefonoreceptor");

        String fotoemisor=(String) data.get("fotoemisor");
        String fotoreceptor=(String) data.get("fotoreceptor");

        emisor=new Usuario(telefonoemisor, nombreemisor, fotoreceptor, tokenemisor);

        String tokenreceptor=(String) data.get("tokenemisor");
        String nombrereceptor=(String) data.get("nombreemisor");
        String telefonoreceptor=(String) data.get("telefonoemisor");

        receptor=new Usuario(telefonoreceptor, nombrereceptor, fotoemisor, tokenreceptor);

        notificationChannel();
        crearNotificacion();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void crearNotificacion() {

        RemoteViews normal=new RemoteViews(getPackageName(), R.layout.cerrada);
        RemoteViews expandida=new RemoteViews(getPackageName(), R.layout.expandida);


        if (receptor.getUri()!=null && receptor.getUri().toString().length()>0){


            try {

                if (!(receptor.getUri() instanceof String)){
                    Bitmap bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(R.drawable.account_circle)
                            .submit(48, 48)
                            .get();
                }else {
                    String rutamal=receptor.getUri().toString();
                    String fotohacia = rutamal.replaceAll("(?<!(http:|https:))/+", "/");

                    if (fotohacia.lastIndexOf('.')+5!=-1){
                        fotohacia=fotohacia.substring(0, fotohacia.lastIndexOf('.')+4);
                    }

                    Bitmap bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(fotohacia)
                            .submit(48, 48)
                            .get();

                    normal.setImageViewBitmap(R.id.imagennotificacion, bitmap);
                    expandida.setImageViewBitmap(R.id.imagennotificacion, bitmap);
                }





            } catch (ExecutionException e) {
                e.printStackTrace();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }else {
            normal.setImageViewResource(R.id.imagennotificacion, R.drawable.account_circle);
            expandida.setImageViewResource(R.id.imagennotificacion, R.drawable.account_circle);
        }


        normal.setTextViewText(R.id.ttitulo, receptor.getNombre().toString());
        expandida.setTextViewText(R.id.ttitulo, receptor.getNombre().toString());
        normal.setTextViewText(R.id.tinfo, titulo);
        expandida.setTextViewText(R.id.tinfo, titulo);

        NotificationCompat.Builder notification=new NotificationCompat.Builder(getApplicationContext(), canal);
        notification.setSmallIcon(R.drawable.smartlabs);
        notification.setContentTitle(receptor.getNombre().toString());
        notification.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(titulo));
        notification.setContentText(titulo);
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);





        notification.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        notification.setCustomContentView(normal);
        notification.setCustomBigContentView(expandida);


        notification.setAutoCancel(true);

        String replyLabel = "Respuesta: ";

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();


        Intent resultIntent = new Intent(this, MyBroadcastReceiver.class);
        resultIntent.putExtra("chat_id", chat_id);
        resultIntent.putExtra("usuarioemisor", emisor);
        resultIntent.putExtra("usuarioreceptor", receptor);


        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);




        //Notification Action with RemoteInput instance added.
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat, "RESPONDER", replyPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        //Notification.Action instance added to Notification Builder.
        notification.addAction(replyAction);


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notificationId", notificationid);
        intent.putExtra("chat_id", chat_id);
        intent.putExtra("usuarioemisor", emisor);
        intent.putExtra("usuarioreceptor", receptor);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Ver mensaje", dismissIntent);


        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(notificationid, notification.build());


        broadcastIntent(emisor, receptor);
  }

    public void broadcastIntent(Usuario emisora, Usuario receptora) {
        Intent intent = new Intent();
        intent.setAction("com.myApp.CUSTOM_EVENT");
   //     intent.putExtra("chat_id", chat_id);
        Bundle argsi = new Bundle();
        emisora.setUltimochat(chat_id);
        receptora.setUltimochat(chat_id);
        argsi.putSerializable("emisor",(Serializable) emisora);
        argsi.putSerializable("receptor",(Serializable) receptora);
        intent.putExtra("DATA",argsi);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }


    public void notificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            String indicar= "mas cosas";
            CharSequence personal=titulo;
            String descripcion=titulo;
            int importancia= NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel=new NotificationChannel(canal, personal, importancia);
            notificationChannel.setDescription(indicar);
            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }

    }


    @Override
    public void onCreate() {
        System.out.println("notificacion recibida");

    }
}
