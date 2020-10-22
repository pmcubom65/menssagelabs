package com.example.mensajesactividad.controladores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mensajesactividad.Autenticacion;
import com.example.mensajesactividad.R;

public class Presentacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        Thread mithread=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                }catch (Exception e) {

                }finally {
                    //       Intent intent=new Intent(NuevaActividad.this, MainActivity.class);
                    Intent intent=new Intent(Presentacion.this, Autenticacion.class);
                    startActivity(intent);
                }
            }
        };
        mithread.start();
    }
}