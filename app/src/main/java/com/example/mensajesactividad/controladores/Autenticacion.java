package com.example.mensajesactividad.controladores;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.services.MySingleton;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.modelos.Usuario;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



public class Autenticacion extends AppCompatActivity  implements RequestHandlerInterface {

    Button botonempezar;

    EditText textoponertelefono;

    View pantalla;

    EditText ponernombre;
    RequestQueue requestQueue;
    RequestHandlerInterface rh = this;

    Boolean haypreferencias=false;


    private final int REQUEST_READ_PHONE_STATE=1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int BIND_ACCESSIBILITY_SERVICE=1;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final int SEND_SMS_PERMISSIONS_REQUEST=1;
    public static String numerotelefono;
    String urlcrearusuario= Rutas.urlcrearusuario;
    String urlmandarsms=Rutas.urlmandarsms;
    public static String nombredelemisor;
    public static String tokenorigen;

    public static String idpropietario;
    String appSmsToken;
    ArrayList<Usuario> listacontactos;

    String imagen_url = Rutas.subir_imagen_url;

    public static String rutafotoimportante="";

    Boolean esusuario=false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);


      //  cargarPreferencias();



        textoponertelefono= findViewById(R.id.confirmar);
        ponernombre=findViewById(R.id.nombre);
        botonempezar=findViewById(R.id.botonempiece);

        pantalla=findViewById(R.id.autenticacionlayout);

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();

      //  getPermissionToSendSMS();



        botonempezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textoponertelefono.getText().toString().length()>0 && ponernombre.getText().toString().length()>0) {
                    numerotelefono=textoponertelefono.getText().toString();
                    nombredelemisor=ponernombre.getText().toString();


                    getTokenTelefono();


                }else {
                    String salida="Número o nombre no válido";
                    mostrarError(salida);
                }
            }
        });


    }


    private void getTokenTelefono() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }


                        tokenorigen = task.getResult().getToken();


                        guardarUsuario(numerotelefono.replaceAll("[\\D]", ""), ponernombre.getText().toString(), tokenorigen.toString());
                    }
                });
    }

    private void guardarUsuario(String telefono, String nombre, String token) {

        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("telefono", telefono);
            jsonBody.put("nombre", nombre.toUpperCase());
            jsonBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(urlcrearusuario, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);

        } else {
            getContactList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getPermissionToSendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Autenticacion.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS_REQUEST);

        }else {
            smsToken();
        }
    }



    private void buscarUsuario(String telefonobuscar, String idowner) {


        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("telefono", telefonobuscar.toString().replaceAll("[\\D]", ""));
            jsonBody.put("id", idowner);
            System.out.println("Busco este telefono "+jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.buscarusuario, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }









    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getContactList() {
       listacontactos=new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                            buscarUsuario(phoneNo, idpropietario);
        //                    listacontactos.add(new Usuario(phoneNo, name, my_contact_Uri.toString()));



                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        Set<Usuario> set = new HashSet<>(listacontactos);


        listacontactos.clear();
        listacontactos.addAll(set);


    //    getFotoUsuario();

        getPermissionToSendSMS();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void smsToken() {

        if (haypreferencias) {

            Intent intent=new Intent(this, MostrarContactos.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable) listacontactos);
            intent.putExtra("BUNDLE2",args);




            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, 200);





     //       startActivity(intent);

        }else  {
            SmsManager smsManager = SmsManager.getDefault();



         //   smsManager.sendTextMessage(numerotelefono, null, "Hola!, autenticación correcta", null, null);
      //      smsManager.sendTextMessage(numerotelefono, null, appSmsToken, null, null);
            appSmsToken= smsManager.createAppSpecificSmsToken(createSmsTokenPendingIntent());
            mandarSmsEnlaApi(numerotelefono);
        }




    }


    private PendingIntent createSmsTokenPendingIntent() {


        Intent intent=new Intent(this, MostrarContactos.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable) listacontactos);
        intent.putExtra("BUNDLE",args);
       startActivity(intent);
        return PendingIntent.getActivity(this, 1234, new Intent(this, MostrarContactos.class),0);
    }



   public void mostrarError(String error){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){

            getContactList();
        }  else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED){

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    smsToken();
                }
            }, 3000);


        }else {
            String salida="Permisos Rechazados";
            mostrarError(salida);
        }
    }



    private void mandarSmsEnlaApi(String telefono) {


       JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("destinatario", telefono);
            jsonBody.put("texto", appSmsToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(urlmandarsms, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());


    }





    public void buscarFotoUsuario(String id) {
        System.out.println("buscando la foto");
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("ID", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.urlbuscarfoto, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());
    }














    private void guardarPreferencias(String telefono, String nombre, String token, String id){
        SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("telefono", telefono);
        editor.putString("token", token);
        editor.putString("nombre", nombre);
        editor.putString("id", id);
        System.out.println("guardando preferencias");

        editor.commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarPreferencias(){
        System.out.println("cargando preferencias");
        SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
        String telefono=preferences.getString("telefono", "");
        String token=preferences.getString("token", "");
        String nombre=preferences.getString("nombre", "");

        String id=preferences.getString("id", "");

        if (telefono.length()>0 && token.length()>0 && nombre.length()>0){
            System.out.println("preferencias funcionan");
            numerotelefono=telefono;
            nombredelemisor=nombre;
            tokenorigen=token;
            idpropietario=id;
            haypreferencias=true;
            buscarFotoUsuario(idpropietario);

            getContactList();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResponse(String response, String urla) {

        if (urla.equals(Rutas.urlcrearusuario)) {
            try {
                JSONObject respuesta = new JSONObject(response);

                idpropietario=respuesta.getString("id").toString();

                guardarPreferencias(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, Autenticacion.tokenorigen, idpropietario);

                System.out.println("grabado");
                getContactPermission();





            } catch (JSONException e) {
                e.printStackTrace();

                Snackbar.make(findViewById(R.id.autenticacionlayout), response.toString(), Snackbar.LENGTH_LONG).show();

            }


        }   else if (urla.equals(Rutas.buscarusuario)){
            try {

                JSONObject respuesta = new JSONObject(response);

                String telefono = respuesta.getString("TELEFONO");
                String token = respuesta.getString("TOKEN");
                String nombre = respuesta.getString( "NOMBRE");

                String id = respuesta.getString( "ID");

                String rutap=respuesta.getString("RUTA");

                String mensajesnoleidos=respuesta.getString("MENSAJES");
                String ultimochat=respuesta.getString("ULTIMOCHAT");

                Usuario usuarioagenda=new Usuario(telefono, nombre, Rutas.construirRuta(rutap), token, id);
                usuarioagenda.setMensajesnoleidos(mensajesnoleidos);
                usuarioagenda.setUltimochat(ultimochat);



                listacontactos.add(usuarioagenda);
                System.out.println(listacontactos);

            }catch (JSONException e) {

                System.out.println(e.toString());

            }

            System.out.println(response);



        }















        else  if (urla.equals(Rutas.urlbuscarfoto)) {
            try {
                JSONObject respuesta = new JSONObject(response);

                String nuevaruta = respuesta.getString("RUTA").toString();

                if (nuevaruta.length()>0) {

                    rutafotoimportante=Rutas.construirRuta(nuevaruta);





                }


            } catch (JSONException   e) {
                e.printStackTrace();

            }


        }





        else {
            try {
                JSONObject respuesta = new JSONObject(response);

                String sms = respuesta.getString("sms");



            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.autenticacionlayout), response.toString(), Snackbar.LENGTH_LONG).show();

            }
        }


        System.out.println(response);
    }





}