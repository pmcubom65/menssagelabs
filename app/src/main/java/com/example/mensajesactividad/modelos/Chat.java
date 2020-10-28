package com.example.mensajesactividad.modelos;

import java.io.Serializable;
import java.util.Objects;

public class Chat implements Serializable {
    private String inicio;

    private String codigo;


    public Chat(String inicio, String codigo, String telefono, String nombre, String token) {
        this.inicio = inicio;
        this.codigo = codigo;
        this.telefono = telefono;
        this.nombre = nombre;
        this.token = token;
    }

    private String telefono;
    private String nombre;
    private String token;

    public Chat(){}

    public Chat(String inicio) {
        this.inicio = inicio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    @Override
    public String toString() {
        return "Chat{" +
                "inicio='" + inicio + '\'' +
                ", codigo='" + codigo + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chat)) return false;
        Chat chat = (Chat) o;
        return getCodigo().equals(chat.getCodigo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCodigo());
    }
}
