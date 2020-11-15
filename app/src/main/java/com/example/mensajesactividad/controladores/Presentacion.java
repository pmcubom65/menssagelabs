package com.example.mensajesactividad.controladores;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.modelos.Usuario;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.services.MySingleton;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Presentacion extends AppCompatActivity implements RequestHandlerInterface {


    Boolean haypreferencias=false;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    RequestQueue requestQueue;
    RequestHandlerInterface rh = this;

    ArrayList<Usuario> listacontactos;


    private final int REQUEST_READ_PHONE_STATE=1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int BIND_ACCESSIBILITY_SERVICE=1;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final int SEND_SMS_PERMISSIONS_REQUEST=1;
    ProgressDialog progressDialog;
    TextView wait;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        wait=(TextView) findViewById(R.id.waiting);
        progressDialog = new ProgressDialog(Presentacion.this);


       if (getIntent().getExtras()!=null){
           wait.setVisibility(View.VISIBLE);
           wait.setText("Cargando Contactos...");

           progressDialog.setMessage("Espere...");
           progressDialog.show();
       }



        requestQueue= Volley.newRequestQueue(getApplicationContext());
        cargarPreferencias();


        if (haypreferencias){

            getContactList();

            Intent intent=new Intent(this, MostrarContactos.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable) listacontactos);
            intent.putExtra("BUNDLE",args);
            progressDialog.dismiss();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(intent);
                }
            }, 3000);


        }else {

            pedirPermisos();


        }


    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarPreferencias(){
        System.out.println("cargando preferencias");
        SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
        String telefono=preferences.getString("telefono", "");
        String idd=preferences.getString("id", "");


        if (telefono.length()>0){

            Autenticacion.numerotelefono=telefono;
            Autenticacion.idpropietario=idd;
            haypreferencias=true;

            buscarUsuario(Autenticacion.numerotelefono, Autenticacion.idpropietario);

         //   getContactList();
        }

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

                        buscarUsuario(phoneNo, Autenticacion.idpropietario);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
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



    @Override
    public void onResponse(String response, String url) {
        if (url.equals(Rutas.buscarusuario)){
            try {

                JSONObject respuesta = new JSONObject(response);

                String telefono = respuesta.getString("TELEFONO");
                String token = respuesta.getString("TOKEN");
                String nombre = respuesta.getString( "NOMBRE");

                String id = respuesta.getString( "ID");

                String rutap=respuesta.getString("RUTA");

                String mensajesnoleidos=respuesta.getString("MENSAJES");
                String ultimochat=respuesta.getString("ULTIMOCHAT");

                if (telefono.equals(Autenticacion.numerotelefono)){
                    Autenticacion.tokenorigen=token;
                    Autenticacion.nombredelemisor=nombre;
                    Autenticacion.rutafotoimportante=Rutas.construirRuta(rutap);


                }else {

                    Usuario usuarioagenda=new Usuario(telefono, nombre, Rutas.construirRuta(rutap), token, id);
                    usuarioagenda.setMensajesnoleidos(mensajesnoleidos);
                    usuarioagenda.setUltimochat(ultimochat);

                    listacontactos.add(usuarioagenda);
                    Set<Usuario> set = new HashSet<>(listacontactos);

                    listacontactos.clear();
                    listacontactos.addAll(set);
                }

            }catch (JSONException e) {

                System.out.println(e.toString());

            }

        }  else  if (url.equals(Rutas.urlbuscarfoto)) {
            try {
                JSONObject respuesta = new JSONObject(response);

                String nuevaruta = respuesta.getString("RUTA").toString();

                if (nuevaruta.length()>0) {

                    Autenticacion.rutafotoimportante=Rutas.construirRuta(nuevaruta);

                }


            } catch (JSONException   e) {
                e.printStackTrace();

            }
            System.out.println("ruta foto "+response);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pedirPermisos() {
        getPermisosLocalizacion();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){


           pedirPermisos2();
        }

    }


    public void getPermisosLocalizacion(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pedirPermisos2() {
        getContactPermission();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){


            getPermissionToSendSMS();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getPermissionToSendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Presentacion.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS_REQUEST);

        }else {
            terminarIntent();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            pedirPermisos2();

        }else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){


            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPermissionToSendSMS();
                }
            }, 500);


        }  else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED){

                terminarIntent();

        }else {
            Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_LONG).show();

        }
    }


    public void terminarIntent() {
        Thread mithread=new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {

                    sleep(1000);
                }catch (Exception e) {

                }finally {

                    Intent intent=new Intent(Presentacion.this, Autenticacion.class);
                    startActivity(intent);
                }
            }
        };
        mithread.start();
    }





}