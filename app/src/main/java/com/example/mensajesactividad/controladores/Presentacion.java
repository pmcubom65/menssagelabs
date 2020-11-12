package com.example.mensajesactividad.controladores;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.modelos.Usuario;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.services.MySingleton;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Presentacion extends AppCompatActivity implements RequestHandlerInterface {


    Boolean haypreferencias=false;

    RequestQueue requestQueue;
    RequestHandlerInterface rh = this;

    ArrayList<Usuario> listacontactos;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        cargarPreferencias();


        if (haypreferencias){

            getContactList();

            Intent intent=new Intent(this, MostrarContactos.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable) listacontactos);
            intent.putExtra("BUNDLE2",args);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, 3000);


        }else {
            Thread mithread=new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(3000);
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
            Autenticacion.numerotelefono=telefono;
            Autenticacion.nombredelemisor=nombre;
            Autenticacion.tokenorigen=token;
            Autenticacion.idpropietario=id;
            haypreferencias=true;
            buscarFotoUsuario(id);

            getContactList();
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

                        buscarUsuario(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }



    }




    private void buscarUsuario(String telefonobuscar) {


        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("telefono", telefonobuscar.toString().replaceAll("[\\D]", ""));
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

                listacontactos.add(new Usuario(telefono, nombre, Rutas.construirRuta(rutap), token, id));
                Set<Usuario> set = new HashSet<>(listacontactos);

                listacontactos.clear();
                listacontactos.addAll(set);


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


        }

    }
}