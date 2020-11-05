package com.example.mensajesactividad.controladores;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.controladores.Autenticacion;
import com.example.mensajesactividad.controladores.MainActivity;
import com.example.mensajesactividad.controladores.MostrarContactos;
import com.example.mensajesactividad.modelos.AdaptadorGrupos;
import com.example.mensajesactividad.modelos.Grupo;
import com.example.mensajesactividad.modelos.Usuario;
import com.example.mensajesactividad.services.RecyclerItemClickListener;

import java.util.ArrayList;

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


        myAdapter=new AdaptadorGrupos(this, listadogrupos);
        recyclerView.setAdapter(myAdapter);

    }




    public void crearIntent(int posicion) {
        Intent intent=new Intent(MostrarGrupos.this, MainActivity.class);
        intent.putExtra("chat_id", listadogrupos.get(posicion).getId().toString());

        intent.putExtra("grupo", true);

        intent.putExtra("grupoinfo", listadogrupos.get(posicion));

   //     intent.putExtra("tokenaenviar", usuario.getToken().toString());
        intent.putExtra("tokenorigen", Autenticacion.tokenorigen);
        intent.putExtra("nombreemisor", Autenticacion.nombredelemisor);

     //   intent.putExtra("nombrereceptor", usuario.getNombre().toString());

        intent.putExtra("numerodetelefono", Autenticacion.numerotelefono);
      //  intent.putExtra("numerodetelefonoreceptor", usuario.getTelefono().toString());

        intent.putExtra("usuarioemisor", new Usuario(Autenticacion.numerotelefono, Autenticacion.nombredelemisor, null, Autenticacion.tokenorigen));
      //  intent.putExtra("usuarioreceptor", usuario);


        intent.putExtra("contactos", MostrarContactos.contactos);


        startActivity(intent);
    }










}