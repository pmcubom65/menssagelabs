package com.example.mensajesactividad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MostrarListaChats extends AppCompatActivity {

    String mostrarlistadochats="http://10.0.2.2:54119/api/smartchat/detallesmischats";

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_lista_chats);

        recyclerView=findViewById(R.id.milistadechats);
        requestQueue= Volley.newRequestQueue(getApplicationContext());


        toolbar=findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);
        toolbar.setLogo(R.drawable.smart_prod);
    }
}