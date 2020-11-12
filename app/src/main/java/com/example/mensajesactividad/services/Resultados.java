package com.example.mensajesactividad.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Resultados extends SQLiteOpenHelper {

    public Resultados(@Nullable Context context) {
        super(context, "resultados.db", null, 1);
    }



    //miusuario.getTelefono(),miusuario.getNombre(),miusuario.getUri(), miusuario.getToken(), miusuario.getId()


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usuario (telefono text, nombre text, uri text, token text, usuarioid text, contador int, ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, unique(usuarioid))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists usuario");


        onCreate(db);
    }
}
