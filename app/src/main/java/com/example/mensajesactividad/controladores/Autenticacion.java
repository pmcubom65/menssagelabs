package com.example.mensajesactividad.controladores;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.PendingIntent;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;

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




public class Autenticacion extends AppCompatActivity  implements RequestHandlerInterface {

    Button botonempezar;

    EditText textoponertelefono;

    View pantalla;

    EditText ponernombre;
    RequestQueue requestQueue;
    RequestHandlerInterface rh = this;

    Boolean haypreferencias=false;

    public static String numerotelefono;
    String urlcrearusuario= Rutas.urlcrearusuario;
    String urlmandarsms=Rutas.urlmandarsms;
    public static String nombredelemisor;
    public static String tokenorigen;

    public static String idpropietario;
    String appSmsToken;


    public static String rutafotoimportante="";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);


        textoponertelefono= findViewById(R.id.confirmar);
        ponernombre=findViewById(R.id.nombre);
        botonempezar=findViewById(R.id.botonempiece);

        pantalla=findViewById(R.id.autenticacionlayout);

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();


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
    public void smsToken() {

        SmsManager smsManager = SmsManager.getDefault();

        appSmsToken= smsManager.createAppSpecificSmsToken(createSmsTokenPendingIntent());
        mandarSmsEnlaApi(numerotelefono);


     /*   Intent intent=new Intent(this, Presentacion.class);

        intent.putExtra("wait", "wait");

        startActivity(intent);*/

    }


    private PendingIntent createSmsTokenPendingIntent() {


        Intent intent=new Intent(this, Presentacion.class);

        intent.putExtra("wait", "wait");

        return PendingIntent.getActivity(this, 1234, intent,0);
    }



   public void mostrarError(String error){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }


    private void mandarSmsEnlaApi(String telefono) {


       JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("destinatario", telefono);
            jsonBody.put("texto", appSmsToken);
        } catch (JSONException e) {

        }

        CrearRequests cr = new CrearRequests(urlmandarsms, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }



    private void guardarPreferencias(String telefono, String nombre, String token, String id){
        SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("telefono", telefono);
        editor.putString("id", id);
    //    editor.putString("token", token);
    //    editor.putString("nombre", nombre);
   //     editor.putString("id", id);
    //    System.out.println("guardando preferencias");

        editor.commit();

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



          smsToken();
            } catch (JSONException e) {
                e.printStackTrace();

                Snackbar.make(findViewById(R.id.autenticacionlayout), response.toString(), Snackbar.LENGTH_LONG).show();

            }


        }
        else if (urla.equals(Rutas.urlmandarsms)) {
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