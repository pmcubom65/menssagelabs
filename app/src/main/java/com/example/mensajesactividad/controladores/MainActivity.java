package com.example.mensajesactividad.controladores;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.TextView;

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
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.modelos.Grupo;
import com.example.mensajesactividad.modelos.Mensaje;
import com.example.mensajesactividad.modelos.Usuario;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.modelos.MyAdapter;
import com.example.mensajesactividad.services.MyBroadcastReceiver;
import com.example.mensajesactividad.services.MySingleton;
import com.example.mensajesactividad.services.RecyclerItemClickListener;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;


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



public class MainActivity extends AppCompatActivity implements DialogoArchivo.Datoaactualizar, RequestHandlerInterface   {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Mensaje> datosAmostrar;
    private FloatingActionButton botonenviar;
    private FloatingActionButton botonadjuntar;

    private TextView textoenviar;

    private final String canal="5555";
    private final int notificationid=001;
    String KEY_REPLY = "key_reply";
    public static int datos;

    String insertchat= Rutas.insertchat;

    String urlcrearmensaje=Rutas.rutacrearmensaje;

    String urlcargarmensajeschat=Rutas.rutaurlcargarmensajeschat;

    String buscargrupo=Rutas.rutabuscargrupo;

    String url="https://fcm.googleapis.com/fcm/send";

    RequestQueue requestQueue;

    RequestHandlerInterface rh = this;

    String michatid;
    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    Mensaje mensaje;
    ArrayList<Usuario> contactos;

 /*   String tokenaenviarlosmensajes;
    String tokenemisor;
    String nombreemisor;
    String nombrereceptor;

    String telefonoreceptor;*/
    Usuario usuarioemisor;
    Usuario usuarioreceptor;

    private Toolbar toolbar;

    boolean esgrupo=false;
    Grupo grupo;


    private BroadcastReceiver onMessage= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("cambio el recycler");
            cargarMensajesChat();
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Intent llegada=getIntent();
        michatid=(String) llegada.getExtras().get("chat_id");

        contactos=(ArrayList<Usuario>) llegada.getExtras().get("contactos");


        usuarioemisor=(Usuario) llegada.getSerializableExtra("usuarioemisor");
        usuarioreceptor=(Usuario) llegada.getSerializableExtra("usuarioreceptor");


        esgrupo=llegada.getExtras().getBoolean("grupo");




        requestQueue= Volley.newRequestQueue(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        toolbar=findViewById(R.id.mitoolbarmensajes);

        if (esgrupo) {
            grupo=(Grupo) llegada.getSerializableExtra("grupoinfo");
            toolbar.setTitle("Conversando con Grupo "+grupo.getNombre().toString());
        }else {
            toolbar.setTitle("Conversando con "+usuarioreceptor.getNombre());
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(false);

        ViewGroup.LayoutParams params=recyclerView.getLayoutParams();

        params.height=  getScreenWidth(MainActivity.this) -400;
        recyclerView.setLayoutParams(params);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        datosAmostrar = new ArrayList<>();
        cargarMensajesChat();

        mAdapter = new MyAdapter(this, datosAmostrar);
        recyclerView.setAdapter(mAdapter);

        requestQueue= Volley.newRequestQueue(getApplicationContext());

        botonenviar=(FloatingActionButton) findViewById(R.id.botonmandarmensaje);
        botonadjuntar=(FloatingActionButton) findViewById(R.id.botonadjunto);
        textoenviar=(TextView) findViewById(R.id.textoanadir);






        botonenviar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if (textoenviar.getText().toString().length() > 0) {


                    LocalDateTime ahora = LocalDateTime.now();
                    ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String dia = ahora.format(dtf);

                    if (esgrupo) {

                        mensaje = new Mensaje(textoenviar.getText().toString(), dia, usuarioemisor.getTelefono().toString(), usuarioemisor.getNombre().toString());
                        datosAmostrar.add(datosAmostrar.size(), mensaje);
                        mAdapter.notifyItemChanged(datosAmostrar.size());
                        String id_mensaje = String.valueOf(zdt.toInstant().toEpochMilli());

                        cargarMensajesChat();


                        for (int g = 0; g < grupo.getDetallesmiembros().size(); g++) {
                            usuarioreceptor = (Usuario) grupo.getDetallesmiembros().get(g);
                            grabarMensaje(mensaje, id_mensaje);
                            notificationFirebase();
                        }


                    } else {

                        mensaje = new Mensaje(textoenviar.getText().toString(), dia, usuarioemisor.getTelefono().toString(), usuarioemisor.getNombre().toString());
                        datosAmostrar.add(datosAmostrar.size(), mensaje);
                        mAdapter.notifyItemChanged(datosAmostrar.size());
                        String id_mensaje = String.valueOf(zdt.toInstant().toEpochMilli());
                        grabarMensaje(mensaje, id_mensaje);
                        cargarMensajesChat();

                        notificationFirebase();


                    }


                }
            }
        });


        botonadjuntar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                DialogoArchivo dialogoarchivo=new  DialogoArchivo();

                LocalDateTime ahora= LocalDateTime.now();
                ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

                DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dia=ahora.format(dtf);



                //String vdia, String vchat_id, String vemisor, String vreceptor

                System.out.println("usuarios emisor"+ usuarioemisor);
                System.out.println("usuarios receptor"+ usuarioreceptor);

                if (esgrupo){
                    usuarioemisor=new Usuario(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, null, Autenticacion.tokenorigen);

                    System.out.println("en grupo "+usuarioemisor.toString());

                    mensaje=new Mensaje("Mensaje Enviado", dia, usuarioemisor.getTelefono().toString(), usuarioemisor.getNombre().toString());

                    String id_mensaje=String.valueOf(zdt.toInstant().toEpochMilli());



                    for (int g=0; g<grupo.getDetallesmiembros().size(); g++) {
                        usuarioreceptor=(Usuario) grupo.getDetallesmiembros().get(g);
                        grabarMensaje(mensaje, id_mensaje);

                        dialogoarchivo.setValues(dia, michatid, usuarioemisor.getTelefono().toString(), usuarioreceptor.getTelefono().toString());
                    //    notificationFirebase();
                    }




                }else {
                    dialogoarchivo.setValues(dia, michatid, usuarioemisor.getTelefono().toString(), usuarioreceptor.getTelefono().toString());
                }


                dialogoarchivo.show(getSupportFragmentManager(), " dialogoArchivo");
            }
        });






        IntentFilter intentFilter= new IntentFilter("com.myApp.CUSTOM_EVENT");
        LocalBroadcastManager.getInstance(this).registerReceiver(onMessage, intentFilter);




        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemClick(View view, int position) {
                        System.out.println("click item");

                        if (datosAmostrar.get(position).getRutaarchivo() instanceof  String) {

                            descargarArchivo(datosAmostrar.get(position).getRutaarchivo().toString());
                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })

        );
    }


    public void descargarArchivo(String ruta) {
        String name=ruta.substring(ruta.lastIndexOf("\\")+1);

        String nuevaruta=ruta.substring(2).replace('\\', '/');

        String rutamodificada="https://"+nuevaruta.replace("SRVWEB-01/inetpub/wwwroot/SmartChat", "smartchat.smartlabs.es");



        System.out.println(rutamodificada);


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(rutamodificada));
        startActivity(i);


    }






    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void crearNotificacion() {

        NotificationCompat.Builder notification=new NotificationCompat.Builder(this, canal);
        notification.setSmallIcon(R.drawable.smartlabs);
        notification.setContentTitle(textoenviar.getText().toString());
        notification.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(textoenviar.getText().toString()));
        notification.setContentText(textoenviar.getText().toString());
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification.setAutoCancel(true);

        String replyLabel = "Respuesta: ";

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();

        // Build a PendingIntent for the reply action to trigger.

        Intent resultIntent = new Intent(this, MyBroadcastReceiver.class);
        resultIntent.putExtra("chat_id", michatid);
        resultIntent.putExtra("telefono", usuarioreceptor.getTelefono().toString());

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
        intent.putExtra("chat_id", michatid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Rechazar", dismissIntent);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(notificationid, notification.build());
    }


    public void notificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            String indicar= textoenviar.getText().toString();
            CharSequence personal=indicar;
            String descripcion=indicar;
            int importancia= NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel=new NotificationChannel(canal, personal, importancia);
            notificationChannel.setDescription(indicar);
            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }


    public void grabarMensaje(Mensaje m, String id){


        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("contenido", m.getContenido().toString());
            jsonBody.put("dia", m.getFecha().toString());
            jsonBody.put("usuarioid", m.getTelefono().toString());
            jsonBody.put("chatid", michatid);
            jsonBody.put("idusuariorecepcion", usuarioreceptor.getTelefono().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(urlcrearmensaje, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }




    public void  cargarMensajesChat() {



        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("codigo", michatid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(urlcargarmensajeschat, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent volveracontactos=new Intent(this, MostrarContactos.class);
                volveracontactos.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable) contactos);


                volveracontactos.putExtra("BUNDLE",args);
                startActivity(volveracontactos);

        }




        return super.onOptionsItemSelected(item);
    }

    public void notificationFirebase()   {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, michatid);


        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "String");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        JSONObject mainObj=new JSONObject();
        String token="";

        try {

            mainObj.put("to", usuarioreceptor.getToken().toString());
            JSONObject notificationObj=new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("michatid", michatid);

            if (mensaje!=null) {
                jData.put("titulo", mensaje.getContenido());
            }else {
                jData.put("titulo", "Archivo enviado");
            }
            jData.put("fotoemisor", usuarioemisor.getUri().toString());
            jData.put("fotoreceptor", usuarioreceptor.getUri().toString());

            jData.put("tokenaenviar", usuarioreceptor.getToken().toString());
            jData.put("tokenemisor", usuarioemisor.getToken().toString());


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


            MySingleton.getInstance(getBaseContext()).addToRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }







    private void buscarSiEsGrupo(String id) {

        StringRequest request = new StringRequest(Request.Method.POST, buscargrupo, new Response.Listener<String>() {
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
                    toolbar.setTitle("Conversando con Grupo "+nombregrupo);


                    JSONArray jsonArray=respuesta.getJSONArray("MIEMBROS");
                    JSONObject e = null;
                    ArrayList<Usuario> usuariogrupo=new ArrayList<>();
                    for (int j=0; j < jsonArray.length(); j++) {
                        e = jsonArray.getJSONObject(j);

                        //Usuario(String telefono, String nombre, String uri, String token, String id)


                        Usuario usuariomiembro=new Usuario(e.getString("TELEFONO").toString(), e.getString("NOMBRE").toString(), e.getString("RUTA").toString(), e.getString("TOKEN").toString(), e.getString("ID").toString());

                        usuariomiembro.setUri(Rutas.construirRuta(usuariomiembro.getUri()));

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
                    jsonBody.put("ID", michatid);


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
        MySingleton.getInstance(getBaseContext()).addToRequest(request);

    }


    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onNombreAActualizar(String s) {
        if (s.equals("actualizar")){


            String dia=Rutas.crearfechaHora();


            if (esgrupo) {

           //     mensaje=new Mensaje("Archivo Enviado", dia, usuarioemisor.getTelefono().toString(), usuarioemisor.getNombre().toString());
        /*        datosAmostrar.add(datosAmostrar.size(), mensaje);
                mAdapter.notifyItemChanged(datosAmostrar.size());
                String id_mensaje=String.valueOf(zdt.toInstant().toEpochMilli());*/

                cargarMensajesChat();


                for (int g=0; g<grupo.getDetallesmiembros().size(); g++) {
             //       usuarioreceptor=(Usuario) grupo.getDetallesmiembros().get(g);
              //      grabarMensaje(mensaje, id_mensaje);
                    notificationFirebase();
                }





            }else {
                cargarMensajesChat();
                notificationFirebase();
            }


        }
    }

    @Override
    public void onResponse(String response, String url) {

        if (url.equals(urlcrearmensaje)){
            try {
                JSONObject jsnobject = new JSONObject(response.toString());

                String contenido = jsnobject.getString("contenido");
                textoenviar.setText("");

                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("mensaje grabado "+ response.toString());
        }else if (url.equals(urlcargarmensajeschat)) {

            try {
                JSONObject jsnobject = new JSONObject(response.toString());
                JSONArray jsonArray = jsnobject.getJSONArray("mensajes");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    Mensaje m=new Mensaje(explrObject.getString("CONTENIDO"), explrObject.getString("DIA").replace('T', ' '), explrObject.getString("TELEFONO"), explrObject.getString("NOMBRE"));
                    JSONArray archivos=explrObject.getJSONArray("ARCHIVOS");

                    for (int arc=0; arc< archivos.length(); arc++) {

                        JSONObject archivodelmensaje=archivos.getJSONObject(arc);
                        m.setRutaarchivo(archivodelmensaje.getString("RUTA").toString());
                    }





                    if (!datosAmostrar.contains(m)) {
                        datosAmostrar.add(m);
                    }


                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

    }


}

