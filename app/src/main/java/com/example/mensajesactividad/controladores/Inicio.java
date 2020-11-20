package com.example.mensajesactividad.controladores;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mensajesactividad.R;

import android.content.Intent;
import android.os.Bundle;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);



        Thread mithread=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                }catch (Exception e) {

                }finally {
                    //       Intent intent=new Intent(NuevaActividad.this, MainActivity.class);
                    Intent intent=new Intent(Inicio.this, Presentacion.class);
                    startActivity(intent);
                }
            }
        };
        mithread.start();



    }
}