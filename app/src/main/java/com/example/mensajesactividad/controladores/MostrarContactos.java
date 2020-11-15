package com.example.mensajesactividad.controladores;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mensajesactividad.DialogoGrupo;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.modelos.AdaptadorContactos;
import com.example.mensajesactividad.modelos.Chat;
import com.example.mensajesactividad.modelos.Grupo;
import com.example.mensajesactividad.modelos.Usuario;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.services.MyService;
import com.example.mensajesactividad.services.MySingleton;
import com.example.mensajesactividad.services.RecyclerItemClickListener;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MostrarContactos extends AppCompatActivity  implements RequestHandlerInterface {


    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;




    String mostrarlistadochats=Rutas.rutamostrarlistadochats;


  //  String buscarusuario= Rutas.buscarusuario;


    String mostrargrupos=Rutas.rutamostrargrupos;


    RequestQueue requestQueue;
    RequestHandlerInterface rh = this;

    public static String chat_id_empiece;
    public static String telefono_chat;
    String idcodigo="";
    String inicio="";

    private Usuario usuario;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public static ArrayList<Usuario> contactos;

    String miembros="";

    public ArrayList<Chat> listadodechats;

    public ArrayList<Grupo> listadogrupos;

    public static ArrayList<Usuario> alGrupo;

    Bundle args;

    ImageView ivfoto, imageView5;

    TextView nombrepropheader, mibadge, textview2;

    public static Boolean nolocalizacion=true;


    private BroadcastReceiver onMessage= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


          Bundle args = intent.getBundleExtra("DATA");
            Usuario miusuario = (Usuario) args.getSerializable("receptor");

            Usuario llegada=new Usuario(miusuario.getTelefono(),miusuario.getNombre(),miusuario.getUri(), miusuario.getToken(), miusuario.getId());

            System.out.println("usuario llegada broadcast "+ llegada);

            int indice=0;
            if (!contactos.contains(llegada)){
                indice=contactos.size();
                contactos.add(indice, llegada);
            }else {
                indice=contactos.indexOf(llegada);
            }

            mibadge=(TextView) recyclerView.findViewHolderForAdapterPosition(indice).itemView.findViewById(R.id.badgecontacto);

            System.out.println("voy a añadir al badge al "+ contactos.get(indice).toString());

            String aumento=String.valueOf(Integer.valueOf(contactos.get(indice).getMensajesnoleidos())+1);



            System.out.println("aumento "+aumento);






            contactos.get(indice).setMensajesnoleidos(aumento);

            mibadge.setText(aumento);
            mibadge.setVisibility(View.VISIBLE);





            System.out.println("este es el broadcast "+miusuario);

            usuario=miusuario;

            idcodigo=miusuario.getUltimochat().toString();

            contactos.get(indice).setUltimochat(miusuario.getUltimochat());

            myAdapter.notifyItemChanged(indice);


        }


    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_contactos);

        toolbar=findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);

        drawerLayout=findViewById(R.id.midrawer);
        navigationView=findViewById(R.id.minavegacion);


        IntentFilter intentFilter= new IntentFilter("com.myApp.CUSTOM_EVENT");
        LocalBroadcastManager.getInstance(this).registerReceiver(onMessage, intentFilter);


        final ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamburguesa);

        alGrupo=new ArrayList<>();
        listadodechats=new ArrayList<>();
        listadogrupos=new ArrayList<>();

        View navHead =  navigationView.getHeaderView(0);
        ImageView iv=navHead.findViewById(R.id.fototest);

        Glide.with(getApplicationContext()).load(Autenticacion.rutafotoimportante)
                .placeholder(R.drawable.account_circle)
                .into(iv);

        nombrepropheader=(TextView) navHead.findViewById(R.id.texttest);
        imageView5=findViewById(R.id.imageView5);

        nombrepropheader.setText(Autenticacion.nombredelemisor);

        textview2=findViewById(R.id.textView2);


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

                        drawerLayout.closeDrawers();
                        recogerListadoChats();

                        return true;


                    case R.id.menugrupo:

                        drawerLayout.closeDrawers();
                        nolocalizacion=false;
                        recogerMisGruposChats();
                        return true;

                    case R.id.perfilicono:
                        System.out.println("profile");
                        drawerLayout.closeDrawers();
                        Intent i=new Intent(MostrarContactos.this, Perfil.class);
                        startActivity(i);
                        return true;


                    case R.id.localizacion:
                        textview2.setText("Compartir localización con...");
                        drawerLayout.closeDrawers();


                        nolocalizacion=false;
                        recyclerView.setAdapter(null);
                        recyclerView.setLayoutManager(null);
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setLayoutManager(layoutManager);
                        myAdapter.notifyDataSetChanged();

                        return true;
                }

                return false;
            }
        });

        Intent intent = getIntent();
        args = intent.getBundleExtra("BUNDLE");

            if (args!=null) {

                System.out.println("pasa por aqui");
                contactos=new ArrayList<>();

                contactos = (ArrayList<Usuario>) args.getSerializable("ARRAYLIST");
            }







        recyclerView=findViewById(R.id.miscontactos);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        usuario=null;

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemClick(View view, int position) {
                        System.out.println("click item");

                  //      telefono_chat=contactos.get(position).getTelefono().toString().replaceAll("[\\D]", "");

             //           buscarUsuario(telefono_chat);

                        System.out.println("pantalla de contactos "+contactos);

                        usuario=contactos.get(position);
                      //  usuario.setUltimochat(idcodigo);


                        System.out.println("este que datos tiene "+usuario);

                        System.out.println("localizacion "+nolocalizacion);

                        if (usuario.getUltimochat()!="" && nolocalizacion) {

                            System.out.println("aqui hay ulitmo chat "+usuario.getUltimochat());

                            crearIntent(usuario.getUltimochat(), Rutas.crearfechaHora(), usuario);

                        }else if (usuario.getUltimochat()!="" && !nolocalizacion){

                            crearServicio(usuario);

                            crearIntent(usuario.getUltimochat(), Rutas.crearfechaHora(), usuario);

                        } else if (usuario.getUltimochat()=="" && !nolocalizacion){

                            LocalDateTime fechaactual= LocalDateTime.now();
                            ZonedDateTime zdt = fechaactual.atZone(ZoneId.of("Europe/Madrid"));
                            idcodigo= String.valueOf(zdt.toInstant().toEpochMilli());

                            System.out.println("aqui nOOO hay ulitmo chat "+usuario.getUltimochat());
                            crearChat(idcodigo, Rutas.crearfechaHora());
                            crearIntent(idcodigo, inicio, usuario);
                        }else {
                       /*     LocalDateTime fechaactual= LocalDateTime.now();
                            ZonedDateTime zdt = fechaactual.atZone(ZoneId.of("Europe/Madrid"));
                            idcodigo= String.valueOf(zdt.toInstant().toEpochMilli());

                            System.out.println("aqui nOOO hay ulitmo chat "+usuario.getUltimochat());
                            crearChat(idcodigo, Rutas.crearfechaHora());*/

                            crearIntent(usuario.getUltimochat(), Rutas.crearfechaHora(), usuario);
                        }



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

                        nolocalizacion=false;
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


        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("codigo", id);
            jsonBody.put("inicio", inicio);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.insertchat, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }



    private void buscarUsuario(String telefonobuscar) {


        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("telefono", telefonobuscar);
            System.out.println("Busco este telefono "+jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.buscarusuario, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }




 public void crearIntent(String idct, String inicio, Usuario usuario1) {
     Intent intent=new Intent(getApplicationContext(), MainActivity.class);

     System.out.println("estoy pasandole el chat "+idct);
     intent.putExtra("chat_id", idct);
        intent.putExtra("tokenaenviar", usuario1.getToken().toString());
        intent.putExtra("tokenorigen", Autenticacion.tokenorigen);
        intent.putExtra("nombreemisor", Autenticacion.nombredelemisor);

        intent.putExtra("nombrereceptor", usuario1.getNombre().toString());

        intent.putExtra("numerodetelefono", Autenticacion.numerotelefono);
        intent.putExtra("numerodetelefonoreceptor", usuario1.getTelefono().toString());

       intent.putExtra("usuarioemisor", new Usuario(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, Autenticacion.rutafotoimportante, Autenticacion.tokenorigen));
       intent.putExtra("usuarioreceptor", usuario1);



       intent.putExtra("contactos", contactos);


        startActivity(intent);
     finish();
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
        args = intent.getBundleExtra("BUNDLE");
        System.out.println("on resuuuuume");
        nolocalizacion=true;

        if (args!=null) {
            contactos = (ArrayList<Usuario>) args.getSerializable("ARRAYLIST");
            System.out.println("ONRESUME " +contactos.toString());
            myAdapter=new AdaptadorContactos(this, contactos);
         //   recyclerView.setAdapter(myAdapter);

            myAdapter.notifyDataSetChanged();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    public void recogerListadoChats() {

        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("telefono", Autenticacion.numerotelefono);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(mostrarlistadochats, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());

    }









    public void recogerMisGruposChats() {

        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("telefono", Autenticacion.numerotelefono);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests( mostrargrupos, jsonBody, rh);

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

                String ruta=Rutas.construirRuta(rutap);

                System.out.println("desde contactos "+ ruta);

                System.out.println("usuario encontrado");
                usuario=new Usuario(telefono, nombre, ruta, token, id);
                System.out.println(usuario);

                crearChat(idcodigo, inicio);


            }catch (JSONException e) {

                Snackbar.make((View) findViewById(R.id.linearcontactos), "El usuario no está registrado", Snackbar.LENGTH_LONG).show();

                System.out.println(e.toString());
            }

            System.out.println(response);



        } else if (url.equals(Rutas.insertchat)) {
            try {

                JSONObject respuesta = new JSONObject(response);

                String codigo = respuesta.getString("codigo");
                String inicio = respuesta.getString("inicio");

                System.out.println("chat creado");

                if (!nolocalizacion){
                   crearServicio(usuario);

                }





            }catch (JSONException e) {

                Snackbar.make((View) findViewById(R.id.linearcontactos), response.toString(), Snackbar.LENGTH_LONG).show();
            }

            System.out.println(response);
        } else if (url.equals(Rutas.rutamostrarlistadochats)) {


            try {

                JSONObject respuesta = new JSONObject(response);


                JSONArray jsonArray=respuesta.getJSONArray("chats");

                JSONObject e = null;


                for (int j=0; j < jsonArray.length(); j++){
                    e = jsonArray.getJSONObject(j);

                    // String inicio, String codigo, String telefono, String nombre, String token

                    Chat chataincluir=new Chat(
                            e.getString("INICIO").toString(),
                            e.getString("CODIGO").toString(),

                            e.getString("TELEFONO").toString(),
                            e.getString("NOMBRE").toString(),
                            e.getString("TOKEN").toString(),
                            Rutas.construirRuta(e.getString("RUTA").toString())
                    );

                    if (!listadodechats.contains(chataincluir)){
                        listadodechats.add(chataincluir);
                    }



                }


            }catch (JSONException e) {

                System.out.println(e.toString());
            }

            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable) listadodechats);


            Intent intentlistachats=new Intent(MostrarContactos.this, MostrarListaChats.class);
            intentlistachats.putExtra("BUNDLE",args);
            startActivity(intentlistachats);




        } else if (url.equals(Rutas.rutamostrargrupos)) {
            try {

                JSONObject respuesta = new JSONObject(response);

                JSONArray jsonArray=respuesta.getJSONArray("GRUPOS");

                JSONObject e = null;
                String nombremiembros="Miembros: ";
                ArrayList<Usuario> mg=new ArrayList<>();

                for (int j=0; j < jsonArray.length(); j++){
                    e = jsonArray.getJSONObject(j);

                    JSONArray todoslosmiembros=e.getJSONArray("MIEMBROS");

                    for (int k=0; k<todoslosmiembros.length(); k++) {
                        JSONObject miembro=todoslosmiembros.getJSONObject(k);
                        nombremiembros=String.format("%s %s,", nombremiembros, miembro.getString("NOMBRE"));

                        //String telefono, String nombre, String uri, String token

                        String rutapp=miembro.getString("RUTA").toString();


                        Usuario usuariomiembro=new Usuario(miembro.getString("TELEFONO").toString(), miembro.getString("NOMBRE").toString(),
                                Rutas.construirRuta(rutapp)
                                , miembro.getString("TOKEN").toString(), miembro.getString("ID").toString());



                        if (!mg.contains(usuariomiembro) && usuariomiembro.getTelefono()!=Autenticacion.numerotelefono){
                            mg.add(usuariomiembro);
                        }
                    }

                    Grupo grupoaincluir =new Grupo(
                            e.getString("NOMBRE").toString(),
                            e.getString("ID").toString(),
                            nombremiembros,
                            mg

                    );


                    nombremiembros="Miembros: ";

                    if (!listadogrupos.contains(grupoaincluir)){
                        listadogrupos.add(grupoaincluir);
                    }



                }


            }catch (JSONException e) {

                System.out.println(e.toString());
            }

            Bundle args = new Bundle();
            args.putSerializable("ARRAYLISTGRUPO",(Serializable) listadogrupos);

            Intent intentlistagrupos=new Intent(MostrarContactos.this, MostrarGrupos.class);
            intentlistagrupos.putExtra("BUNDLE",args);
            startActivity( intentlistagrupos);
        }


    }


    public void crearServicio(Usuario usuarioamandar) {


        Intent intentservice=new Intent(MostrarContactos.this, MyService.class);

        intentservice.putExtra("usuarioreceptor", usuarioamandar);
        startService(intentservice);

    }



}