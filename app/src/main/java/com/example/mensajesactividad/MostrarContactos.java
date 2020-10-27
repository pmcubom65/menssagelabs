package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.controladores.Presentacion;
import com.example.mensajesactividad.modelos.Chat;
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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


public class MostrarContactos extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

 //   "http://10.0.2.2:54119/api/smartchat/crearusuario/"



    String insertchat="http://10.0.2.2:54119/api/smartchat/crearchat";

    String mostrarlistadochats="http://10.0.2.2:54119/api/smartchat/detallesmischats";

    //http://10.0.2.2:54119/api/smartchat/crearchat

    String buscarusuario="http://10.0.2.2:54119/api/smartchat/buscarusuario";


    String showchat="http://10.0.2.2:54119/api/chats_service.php";


    String mostrarlistadochat1s="http://localhost:54119/api/smartchat/listadochats";


    RequestQueue requestQueue;


    public static String chat_id_empiece;
    public static String telefono_chat;
    String id;
    String inicio;

    private Usuario usuario;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public ArrayList<Usuario> contactos;

    ArrayList<Chat> listadodechats;

    static ArrayList<Usuario> alGrupo;



    Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_contactos);

        toolbar=findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);

        drawerLayout=findViewById(R.id.midrawer);
        navigationView=findViewById(R.id.minavegacion);

        final ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamburguesa);

        alGrupo=new ArrayList<>();
        listadodechats=new ArrayList<>();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mihome:
                        Intent homeintent=new Intent(MostrarContactos.this, Presentacion.class);
                        drawerLayout.closeDrawers();
                        startActivity(homeintent);
                        return true;

                    case R.id.miexit:
                        System.exit(0);
                        return true;

                    case R.id.menuchat:
                        System.out.println("chats");
                        drawerLayout.closeDrawers();
                        recogerListadoChats();

                        return true;


                    case R.id.menugrupo:
                        System.out.println("grupos");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.perfilicono:
                        System.out.println("profile");
                        drawerLayout.closeDrawers();
                        Intent i=new Intent(MostrarContactos.this, Perfil.class);
                        startActivity(i);
                        return true;
                }

                return false;
            }
        });

        Intent intent = getIntent();
        args = intent.getBundleExtra("BUNDLE");

            if (args!=null) {
                contactos = (ArrayList<Usuario>) args.getSerializable("ARRAYLIST");

            }






        recyclerView=findViewById(R.id.miscontactos);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemClick(View view, int position) {
                        System.out.println("click item");

                        LocalDateTime fechaactual= LocalDateTime.now();
                        ZonedDateTime zdt = fechaactual.atZone(ZoneId.of("Europe/Madrid"));
                        id= String.valueOf(zdt.toInstant().toEpochMilli());

                        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        inicio=fechaactual.format(dtf);

                        telefono_chat=contactos.get(position).getTelefono().toString().replaceAll("[\\D]", "");

                        buscarUsuario(telefono_chat);



                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        if (view.findViewById(R.id.checkBox).getVisibility()==View.GONE) {
                            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                            view.findViewById(R.id.checkBox).setVisibility(View.VISIBLE);

                            alGrupo.add(contactos.get(position));
                        } else {
                            view.findViewById(R.id.checkBox).setVisibility(View.GONE);
                            view.setBackgroundColor(getResources().getColor(R.color.lightblanco));
                            alGrupo.remove(contactos.get(position));
                        }


                    }
                })
        );

        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        myAdapter=new AdaptadorContactos(this, contactos);
        recyclerView.setAdapter(myAdapter);

    }


    public void crearChat(String id, String inicio){

        StringRequest request = new StringRequest(Request.Method.POST, insertchat, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String codigo = respuesta.getString("codigo");
                    String inicio = respuesta.getString("inicio");

                    System.out.println("chat creado");

                    crearIntent(codigo, inicio);



                }catch (JSONException e) {

                    Snackbar.make((View) findViewById(R.id.linearcontactos), response.toString(), Snackbar.LENGTH_LONG).show();
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

                Snackbar.make((View) findViewById(R.id.linearcontactos), response.toString(), Snackbar.LENGTH_LONG).show();

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
                    jsonBody.put("codigo", id);
                    jsonBody.put("inicio", inicio);

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





    private void buscarUsuario(String telefonobuscar) {

        StringRequest request = new StringRequest(Request.Method.POST, buscarusuario, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String telefono = respuesta.getString("TELEFONO");
                    String token = respuesta.getString("TOKEN");
                    String nombre = respuesta.getString( "NOMBRE");

                    System.out.println("usuario encontrado");
                    usuario=new Usuario(telefono, nombre, null, token);
                    System.out.println(usuario);

                    crearChat(id, inicio);


                }catch (JSONException e) {

                    Snackbar.make((View) findViewById(R.id.linearcontactos), "El usuario no está registrado", Snackbar.LENGTH_LONG).show();
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
                    jsonBody.put("telefono", telefonobuscar);
                    System.out.println("Busco este telefono "+jsonBody.toString());

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
    //    requestQueue.add(request);


    }




 public void crearIntent(String id, String inicio) {
     Intent intent=new Intent(getApplicationContext(), MainActivity.class);
     intent.putExtra("chat_id", id);
        intent.putExtra("tokenaenviar", usuario.getToken().toString());
        intent.putExtra("tokenorigen", Autenticacion.tokenorigen);
        intent.putExtra("nombreemisor", Autenticacion.nombredelemisor);

        intent.putExtra("nombrereceptor", usuario.getNombre().toString());

        intent.putExtra("numerodetelefono", Autenticacion.numerotelefono);
        intent.putExtra("numerodetelefonoreceptor", usuario.getTelefono().toString());

       intent.putExtra("usuarioemisor", new Usuario(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, null, Autenticacion.tokenorigen));
       intent.putExtra("usuarioreceptor", usuario);


       intent.putExtra("contactos", contactos);


        startActivity(intent);
 }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;


            case R.id.Crear_grupo:

                DialogoGrupo dialogoGrupo=new DialogoGrupo(true);

                dialogoGrupo.show(getSupportFragmentManager(), " dialogoGrupo");



                break;

            case R.id.Anadir_grupo:

                DialogoGrupo dialogoGrupo2=new DialogoGrupo(false);

                dialogoGrupo2.show(getSupportFragmentManager(), " dialogoGrupo2");

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        args = intent.getBundleExtra("BUNDLE2");



        if (args!=null) {
            contactos = (ArrayList<Usuario>) args.getSerializable("ARRAYLIST");
            System.out.println("ONRESUME " +contactos.toString());
            myAdapter=new AdaptadorContactos(this, contactos);
            recyclerView.setAdapter(myAdapter);

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    public void recogerListadoChats() {

        StringRequest request = new StringRequest(Request.Method.POST, mostrarlistadochats, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);


                    JSONArray jsonArray=respuesta.getJSONArray("chats");

                    JSONObject e = null;


                    for (int j=0; j < jsonArray.length(); j++){
                        e = jsonArray.getJSONObject(j);

                       // String inicio, String codigo, String telefono, String nombre, String token
                        listadodechats.add(new Chat(
                                e.getString("INICIO").toString(),
                                e.getString("CODIGO").toString(),

                                e.getString("TELEFONO").toString(),
                                e.getString("NOMBRE").toString(),
                                e.getString("TOKEN").toString()
                                ));

                    }


                }catch (JSONException e) {

                    System.out.println(e.toString());
                }

                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable) listadodechats);


                Intent intentlistachats=new Intent(MostrarContactos.this, MostrarListaChats.class);
                intentlistachats.putExtra("BUNDLE",args);
                startActivity(intentlistachats);

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

                Snackbar.make((View) findViewById(R.id.linearcontactos), response.toString(), Snackbar.LENGTH_LONG).show();

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
                    jsonBody.put("telefono", Autenticacion.numerotelefono);


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