package com.example.mensajesactividad;

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
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


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
import com.example.mensajesactividad.modelos.Usuario;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    static String numerotelefono;

    String urlcrearusuario= Rutas.urlcrearusuario;
    String urlmandarsms=Rutas.urlmandarsms;


    static String nombredelemisor;
    static String tokenorigen;
    String appSmsToken;

    ArrayList<Usuario> listacontactos;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);


        cargarPreferencias();



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
            jsonBody.put("nombre", nombre);
            jsonBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(urlcrearusuario, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());



   /*     StringRequest request = new StringRequest(Request.Method.POST, urlcrearusuario, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respuesta = new JSONObject(response);

                    String codigo = respuesta.getString("codigo");
                    String mensaje = respuesta.getString("mensaje");

                    Snackbar.make(findViewById(R.id.autenticacionlayout), response.toString(), Snackbar.LENGTH_LONG).show();



                } catch (JSONException e) {
                    e.printStackTrace();
                    guardarPreferencias(telefono, nombre, token);

                    System.out.println("grabado");
                    getContactPermission();

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

                Snackbar.make(findViewById(R.id.autenticacionlayout), error.toString(), Snackbar.LENGTH_LONG).show();

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
                    jsonBody.put("telefono", telefono);
                    jsonBody.put("nombre", nombre);
                    jsonBody.put("token", token);
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
        });*/

    //    MySingleton.getInstance(getBaseContext()).addToRequest(request);
   //   requestQueue.add(request);


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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS_REQUEST);

        }else {
            smsToken();
        }
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


                        listacontactos.add(new Usuario(phoneNo, name, my_contact_Uri.toString()));

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


        getPermissionToSendSMS();


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void smsToken() {

        if (haypreferencias) {

            Intent intent=new Intent(this, MostrarContactos.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable) listacontactos);
            intent.putExtra("BUNDLE2",args);
            startActivity(intent);

        }else {
       /*     SmsManager smsManager = SmsManager.getDefault();

            appSmsToken= smsManager.createAppSpecificSmsToken(createSmsTokenPendingIntent());

            smsManager.sendTextMessage(numerotelefono, null, "Hola!, autenticación correcta", null, null);
            smsManager.sendTextMessage(numerotelefono, null, appSmsToken, null, null);*/

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

            smsToken();

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





    /*    StringRequest request = new StringRequest(Request.Method.POST, urlmandarsms, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respuesta = new JSONObject(response);

                    String sms = respuesta.getString("sms");



                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(R.id.autenticacionlayout), response.toString(), Snackbar.LENGTH_LONG).show();

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

                Snackbar.make(findViewById(R.id.autenticacionlayout), error.toString(), Snackbar.LENGTH_LONG).show();

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
                    jsonBody.put("destinatario", telefono);
                    jsonBody.put("texto", appSmsToken);

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
        requestQueue.add(request);*/


    }




















    private void guardarPreferencias(String telefono, String nombre, String token){
        SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("telefono", telefono);
        editor.putString("token", token);
        editor.putString("nombre", nombre);
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

        if (telefono.length()>0 && token.length()>0 && nombre.length()>0){
            System.out.println("preferencias funcionan");
            numerotelefono=telefono;
            nombredelemisor=nombre;
            tokenorigen=token;
            haypreferencias=true;
            getContactList();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResponse(String response, String urla) {

        if (urla.equals(Rutas.urlcrearusuario)) {
            try {
                JSONObject respuesta = new JSONObject(response);

                String codigo = respuesta.getString("codigo");
                String mensaje = respuesta.getString("mensaje");

                Snackbar.make(findViewById(R.id.autenticacionlayout), response.toString(), Snackbar.LENGTH_LONG).show();



            } catch (JSONException e) {
                e.printStackTrace();
                guardarPreferencias(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, Autenticacion.tokenorigen);

                System.out.println("grabado");
                getContactPermission();

            }


        }else {
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