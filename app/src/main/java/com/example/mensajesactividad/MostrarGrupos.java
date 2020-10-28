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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MostrarGrupos extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    RequestQueue requestQueue;

    public ArrayList<Grupo> listadogrupos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        listadogrupos = (ArrayList<Grupo>) args.getSerializable("ARRAYLISTGRUPO");



        setContentView(R.layout.activity_mostrar_grupos);

        requestQueue= Volley.newRequestQueue(getApplicationContext());


        recyclerView=findViewById(R.id.misgrupos);



        toolbar=findViewById(R.id.mitoolbar3);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setLogo(R.drawable.smart_prod);



        requestQueue= Volley.newRequestQueue(getApplicationContext());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onItemClick(View view, int position) {
                        System.out.println("click item");
                    //    crearIntent(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );


        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        myAdapter=new AdaptadorGrupos(this, listadogrupos);
        recyclerView.setAdapter(myAdapter);

    }




  /*  public void crearIntent(String id, String inicio) {
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
    }*/

}