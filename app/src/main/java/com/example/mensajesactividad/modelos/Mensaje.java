package com.example.mensajesactividad.modelos;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Mensaje {

    private String contenido;
    private String fecha;
    private String telefono;
    private String nombre;

    private String rutaarchivo;


    public Mensaje(String contenido, String fecha, String telefono) {
        this.contenido = contenido;
        this.fecha = fecha;
        this.telefono=telefono;
    }


    public Mensaje(String contenido, String fecha, String telefono, String nombre) {
        this.contenido = contenido;
        this.fecha = fecha;
        this.telefono=telefono;
        this.nombre=nombre;
    }


    public Mensaje(String contenido, String fecha, String telefono, String nombre, String ruta) {
        this.contenido = contenido;
        this.fecha = fecha;
        this.telefono=telefono;
        this.nombre=nombre;
        this.rutaarchivo=ruta;
    }

    public String getRutaarchivo() {
        return rutaarchivo;
    }

    public void setRutaarchivo(String rutaarchivo) {
        this.rutaarchivo = rutaarchivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getContenido() {
        return contenido;
    }

    public String getFecha() {
        return fecha;
    }


    @Override
    public String toString() {
        return "Mensaje{" +
                "contenido='" + contenido + '\'' +
                ", fecha='" + fecha + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mensaje)) return false;
        Mensaje mensaje = (Mensaje) o;
        return getContenido().equals(mensaje.getContenido()) &&
                getFecha().equals(mensaje.getFecha()); //&&
          //      getTelefono().equals(mensaje.getTelefono());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(getContenido(), getFecha(), getTelefono());
    }
}
