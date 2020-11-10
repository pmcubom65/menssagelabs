package com.example.mensajesactividad.services;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Rutas {

  /*  public static String urlcrearusuario="https://sdi.smartlabs.es/api/smartchat/crearusuario/";
    public static String urlmandarsms="https://sdi.smartlabs.es/api/smartchat/crearSMS";


    public static String urlbuscarfoto="https://sdi.smartlabs.es/api/smartchat/buscarfoto";
    public static String subir_imagen_url = "https://sdi.smartlabs.es/api/smartchat/almacenarimagen";



    public static String buscarusuario="https://sdi.smartlabs.es/api/smartchat/buscarusuario";


    public static String insertchat="https://sdi.smartlabs.es/api/smartchat/crearchat";




    public static String rutamostrarlistadochats="https://sdi.smartlabs.es/api/smartchat/detallesmischats";


    public static String rutamostrargrupos="https://sdi.smartlabs.es/api/smartchat/misgrupos";

    public static  String rutacrearmensaje="https://sdi.smartlabs.es/api/smartchat/crearmensaje";

    public static String rutaurlcargarmensajeschat="https://sdi.smartlabs.es/api/smartchat/buscarmensajeschat";


    public static String rutabuscargrupo="https://sdi.smartlabs.es/api/smartchat/buscarGrupoPorID";


    public static String rutaanadirusuarioagrupo="https://sdi.smartlabs.es/api/smartchat/anadirusuarioagrupo";

    public static String rutacreargrupo="https://sdi.smartlabs.es/api/smartchat/creargrupo";*/


    public static String urlcrearusuario="http://10.0.2.2:54119/api/smartchat/crearusuario/";
    public static String urlmandarsms="http://10.0.2.2:54119/api/smartchat/crearSMS";


    public static String urlbuscarfoto="http://10.0.2.2:54119/api/smartchat/buscarfoto";
    public static String subir_imagen_url = "http://10.0.2.2:54119/api/smartchat/almacenarimagen";



    public static String buscarusuario="http://10.0.2.2:54119/api/smartchat/buscarusuario";


    public static String insertchat="http://10.0.2.2:54119/api/smartchat/crearchat";




    public static String rutamostrarlistadochats="http://10.0.2.2:54119/api/smartchat/detallesmischats";


    public static String rutamostrargrupos="http://10.0.2.2:54119/api/smartchat/misgrupos";

    public static  String rutacrearmensaje="http://10.0.2.2:54119/api/smartchat/crearmensaje";

    public static String rutaurlcargarmensajeschat="http://10.0.2.2:54119/api/smartchat/buscarmensajeschat";


    public static String rutabuscargrupo="http://10.0.2.2:54119/api/smartchat/buscarGrupoPorID";


    public static String rutaanadirusuarioagrupo="http://10.0.2.2:54119/api/smartchat/anadirusuarioagrupo";

    public static String rutacreargrupo="http://10.0.2.2:54119/api/smartchat/creargrupo";



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String crearfechaHora() {
        LocalDateTime ahora= LocalDateTime.now();
        ZonedDateTime zdt = ahora.atZone(ZoneId.of("Europe/Madrid"));

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ahora.format(dtf);
    }




    public static String construirRuta(String rutajson) {
        if (rutajson.length()>0) {
            String nuevaruta=rutajson.replace('\\', '/');


            String salida="https://smartchat.smartlabs.es/"+nuevaruta.substring(nuevaruta.lastIndexOf("img"));

            return salida;
        }else {
            return "";
        }
    }
}
