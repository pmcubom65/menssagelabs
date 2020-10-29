package com.example.mensajesactividad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.modelos.Chat;
import com.example.mensajesactividad.modelos.Grupo;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MostrarListaChats extends AppCompatActivity{
//
//public class MostrarListaChats extends MostrarContactos {



    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    RequestQueue requestQueue;


    public ArrayList<Chat> listadodechats;

    String buscargrupo="http://10.0.2.2:54119/api/smartchat/buscarGrupoPorID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        listadodechats = (ArrayList<Chat>) args.getSerializable("ARRAYLIST");

        for (int k=0; k<listadodechats.size(); k++) {
            buscarSiEsGrupo(listadodechats.get(k).getCodigo().toString(), k);
        }


        setContentView(R.layout.activity_mostrar_lista_chats);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

        toolbar=findViewById(R.id.mitoolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(null);
        toolbar.setLogo(R.drawable.smart_prod);

        recyclerView=findViewById(R.id.milistadechats);






        requestQueue= Volley.newRequestQueue(getApplicationContext());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemClick(View view, int position) {
                        System.out.println("click item");
                        crearIntent(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {




                    }
                })
        );

        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        myAdapter=new AdaptadorListadoChats(this, listadodechats);

        recyclerView.setAdapter(myAdapter);

    }




    public void crearIntent(int position) {
        System.out.println(listadodechats);
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("chat_id", listadodechats.get(position).getCodigo().toString());
        intent.putExtra("tokenaenviar",listadodechats.get(position).getToken().toString());
        intent.putExtra("tokenorigen", Autenticacion.tokenorigen);
        intent.putExtra("nombreemisor", Autenticacion.nombredelemisor);

        intent.putExtra("nombrereceptor", listadodechats.get(position).getNombre().toString());

        intent.putExtra("numerodetelefono", Autenticacion.numerotelefono);
        intent.putExtra("numerodetelefonoreceptor", listadodechats.get(position).getTelefono().toString());

        intent.putExtra("usuarioemisor", new Usuario(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, null, Autenticacion.tokenorigen));
        intent.putExtra("usuarioreceptor",  new Usuario(listadodechats.get(position).getTelefono().toString(), listadodechats.get(position).getNombre().toString(), null, listadodechats.get(position).getToken().toString()));

        System.out.println("usuarioreceptor"+new Usuario(listadodechats.get(position).getTelefono().toString(), listadodechats.get(position).getNombre().toString(), null, listadodechats.get(position).getToken().toString()));
        intent.putExtra("contactos", MostrarContactos.contactos);


        startActivity(intent);
    }







    private void buscarSiEsGrupo(String idc, int posicion) {

        StringRequest request = new StringRequest(Request.Method.POST, buscargrupo, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String iddelgrupo = respuesta.getString("ID").toString();
                    String nombregrupo = respuesta.getString("NOMBRE").toString();


                    System.out.println("grupo encontrado");

                    listadodechats.remove(posicion);



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
        MySingleton.getInstance(getBaseContext()).addToRequest(request);

    }
}