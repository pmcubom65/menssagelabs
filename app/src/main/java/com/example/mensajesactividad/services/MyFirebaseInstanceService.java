package com.example.mensajesactividad.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mensajesactividad.MainActivity;
import com.example.mensajesactividad.MyBroadcastReceiver;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Map;

public class MyFirebaseInstanceService extends FirebaseMessagingService {


    private final String canal="5555";
    private final int notificationid=001;
    String KEY_REPLY = "key_reply";
    public static boolean flag=false;

    String chat_id;
    String titulo;

    Usuario emisor;
    Usuario receptor;



    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TOKEN", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
      //  sendRegistrationToServer(token);
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

        emisor=new Usuario(telefonoemisor, nombreemisor, null, tokenemisor);

        String tokenreceptor=(String) data.get("tokenemisor");
        String nombrereceptor=(String) data.get("nombreemisor");
        String telefonoreceptor=(String) data.get("telefonoemisor");

        receptor=new Usuario(telefonoreceptor, nombrereceptor, null, tokenreceptor);


        notificationChannel();
        crearNotificacion();
    }








    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void crearNotificacion() {

        //https://stackoverflow.com/questions/41888161/how-to-create-a-custom-notification-layout-in-android

   //     RemoteViews normal=new RemoteViews(getPackageName(), R.layout.cerrada);
    //    RemoteViews expandida=new RemoteViews(getPackageName(), R.layout.expandida);

        NotificationCompat.Builder notification=new NotificationCompat.Builder(getApplicationContext(), canal);
        notification.setSmallIcon(R.drawable.smartlabs);
        notification.setContentTitle(receptor.getNombre().toString());
        notification.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(titulo));
        notification.setContentText(titulo);
        notification.setPriority(NotificationCompat.PRIORITY_HIGH);
        //notification.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        //notification.setCustomContentView(normal);
        //notification.setCustomBigContentView(expandida);


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


        broadcastIntent();
  }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.myApp.CUSTOM_EVENT");
        // We should use LocalBroadcastManager when we want INTRA app
        // communication
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
