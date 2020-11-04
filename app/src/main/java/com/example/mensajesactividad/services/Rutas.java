package com.example.mensajesactividad.services;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Rutas {

    public static String urlcrearusuario="http://10.0.2.2:54119/api/smartchat/crearusuario/";
    public static String urlmandarsms="http://10.0.2.2:54119/api/smartchat/crearSMS";


    public static String urlbuscarfoto="http://10.0.2.2:54119/api/smartchat/buscarfoto";
    public static String subir_imagen_url = "http://10.0.2.2:54119/api/smartchat/almacenarimagen";

    public static String buscarusuario="http://10.0.2.2:54119/api/smartchat/buscarusuario";


    public static String insertchat="http://10.0.2.2:54119/api/smartchat/crearchat";


    public static String rutamostrarlistadochats="http://10.0.2.2:54119/api/smartchat/detallesmischats";


    public static String rutamostrargrupos="http://10.0.2.2:54119/api/smartchat/misgrupos";

    public static  String rutacrearmensaje="http://10.0.2.2:54119/api/smartchat/crearmensaje";


    public static String rutabuscargrupo="http://10.0.2.2:54119/api/smartchat/buscarGrupoPorID";



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String crearfechaHora() {
        LocalDateTime ahora= LocalDateTime.now();
        ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ahora.format(dtf);
    }

}
