package com.example.mensajesactividad.modelos;

public class Localizacion {

    String latitud;
    String longitud;

    public Localizacion(String latitud, String longitud) {

        this.latitud = latitud;
        this.longitud = longitud;
    }


    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
