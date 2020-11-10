package com.example.mensajesactividad.modelos;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Grupo implements Serializable {

        private String nombre;
        private String id;
        private String miembros;

        private ArrayList<Usuario> detallesmiembros;


        public Grupo() {}

    public Grupo(String nombre, String id, String miembros) {
        this.nombre = nombre;
        this.id = id;
        this.miembros = miembros;
    }


    public Grupo(String nombre, String id, String miembros, ArrayList<Usuario> detallesmiembros) {
        this.nombre = nombre;
        this.id = id;
        this.miembros = miembros;
        this.detallesmiembros=detallesmiembros;
    }


    public ArrayList<Usuario> getDetallesmiembros() {
        return detallesmiembros;
    }

    public void setDetallesmiembros(ArrayList<Usuario> detallesmiembros) {
        this.detallesmiembros = detallesmiembros;
    }

    public String getMiembros() {
        return miembros;
    }

    public void setMiembros(String miembros) {
        this.miembros = miembros;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Grupo{" +
                "nombre='" + nombre + '\'' +
                ", id='" + id + '\'' +
                ", miembros='" + miembros + '\'' +
                ", detallesmiembros=" + detallesmiembros +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grupo)) return false;
        Grupo grupo = (Grupo) o;
        return getNombre().equals(grupo.getNombre()) &&
                getId().equals(grupo.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(getNombre(), getId());
    }
}
