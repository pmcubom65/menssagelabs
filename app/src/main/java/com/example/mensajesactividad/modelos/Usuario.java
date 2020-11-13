package com.example.mensajesactividad.modelos;


import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.Objects;


@SuppressWarnings("serial")
public class Usuario implements Serializable {

    private String telefono;
    private String nombre;
    private String uri;
    private String token;
    private String id;

    private String mensajesnoleidos;
    private String ultimochat;


    public Usuario(String telefono, String nombre, String uri) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.uri=uri;
    }

    public Usuario(String telefono, String nombre, String uri, String token) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.uri=uri;
        this.token=token;
    }



    public Usuario(String telefono, String nombre, String uri, String token, String id) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.uri=uri;
        this.token=token;
        this.id=id;
    }


    public String getMensajesnoleidos() {
        return mensajesnoleidos;
    }

    public void setMensajesnoleidos(String mensajesnoleidos) {
        this.mensajesnoleidos = mensajesnoleidos;
    }

    public String getUltimochat() {
        return ultimochat;
    }

    public void setUltimochat(String ultimochat) {
        this.ultimochat = ultimochat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNombre() {
        return nombre;
    }


    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(telefono, usuario.telefono);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(telefono);
    }


    @Override
    public String toString() {
        return "Usuario{" +
                "telefono='" + telefono + '\'' +
                ", nombre='" + nombre + '\'' +
                ", uri='" + uri + '\'' +
                ", token='" + token + '\'' +
                ", id='" + id + '\'' +
                ", mensajesnoleidos='" + mensajesnoleidos + '\'' +
                ", ultimochat='" + ultimochat + '\'' +
                '}';
    }
}
