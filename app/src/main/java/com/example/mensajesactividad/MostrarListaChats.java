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


    //https://stackoverflow.com/questions/36095691/android-navigationdrawer-multiple-activities-same-menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

   //     FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
    //    getLayoutInflater().inflate(R.layout.activity_mostrar_lista_chats, contentFrameLayout);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        listadodechats = (ArrayList<Chat>) args.getSerializable("ARRAYLIST");


        setContentView(R.layout.activity_mostrar_lista_chats);
        requestQueue= Volley.newRequestQueue(getApplicationContext());




        recyclerView=findViewById(R.id.milistadechats);



        toolbar=findViewById(R.id.mitoolbar2);
        setSupportActionBar(toolbar);

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


     //   intent.putExtra("contactos", contactos);


        startActivity(intent);
    }
}