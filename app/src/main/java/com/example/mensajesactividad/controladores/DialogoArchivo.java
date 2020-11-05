package com.example.mensajesactividad.controladores;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.mensajesactividad.R;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.services.MySingleton;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DialogoArchivo  extends DialogFragment  implements RequestHandlerInterface  {

    View layoutactualizar;
    Button archivo;
    Button imagen;
    ImageView imagencargada;

    TextView nombrearchivo;
    public static final int PICK_IMAGE = 1;

    public static final int PICK_FILE = 2;


    RequestHandlerInterface rh = this;

    Datoaactualizar datoactualizar;

    String imagen_url= Rutas.subir_imagen_url;

    HashMap<String, String> valores=new HashMap<>();


    String imagenenstring="";
    String extensionstring="";
    Boolean wantToCloseDialog = false;

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);



            positiveButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {

                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (imagenenstring.length()>0 && extensionstring.length()>0) {
                        subirImagen(imagenenstring, Autenticacion.idpropietario, extensionstring);
                    }


                    if (wantToCloseDialog)
                        dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

    @Override
    public void onResponse(String response, String url) {

        if (url.equals(Rutas.subir_imagen_url)) {

            final View viewPos = layoutactualizar.findViewById(R.id.dialogoarchivoslayout);
            try {

                JSONObject respuesta = new JSONObject(response);

                String id = respuesta.getString("GRABADO").toString();

                datoactualizar.onNombreAActualizar("actualizar");

               dismiss();

            }catch (JSONException e) {

                System.out.println(e.toString());

                Snackbar snackbar = Snackbar.make(viewPos, "Error en la grabación", Snackbar.LENGTH_INDEFINITE);

                snackbar.show();
            }

            System.out.println(response);

        }

    }


    public interface Datoaactualizar {

        public void onNombreAActualizar(String s);
    }

    public  DialogoArchivo(){}


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();

        layoutactualizar=inflater.inflate(R.layout.dialogo_archivo,null);
        archivo=layoutactualizar.findViewById(R.id.imageViewArchivo);
        imagen=layoutactualizar.findViewById(R.id.imageViewFoto);
        imagencargada=layoutactualizar.findViewById(R.id.imagencargada);

        nombrearchivo=layoutactualizar.findViewById(R.id.nombrearchivo);

        imagencargada.setVisibility(View.GONE);
        nombrearchivo.setVisibility(View.GONE);

        builder.setView(layoutactualizar);



        archivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirArchivo();
            }
        });




        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();
            }
        });





        builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Aceptar</font>"), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (imagenenstring.length()>0 && extensionstring.length()>0) {
                    subirImagen(imagenenstring, Autenticacion.idpropietario, extensionstring);
                }


            }
        });
        builder.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancelar</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });





        return builder.create();
    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity=(Activity) context;

        try {
            datoactualizar=(DialogoArchivo.Datoaactualizar) activity;
        }catch (ClassCastException cce) {}

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //error
                return;
            }
            Uri uri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imagenenstring = ","+ Base64.encodeToString(getBytesFromBitmap(bitmap),
                    Base64.DEFAULT);



            String selectedImagePath = getPath(uri);
            imagencargada.setVisibility(View.VISIBLE);
            imagencargada.setImageURI(uri);

         //   String filename=uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
            String rutaimagen=new File(uri.getPath()).getPath().split(":")[0];
            String filename=rutaimagen.substring(rutaimagen.lastIndexOf("/")+1);

            extensionstring=getMimeType(getContext(), uri);


            nombrearchivo.setVisibility(View.VISIBLE);
            nombrearchivo.setText(filename);

         //   buscarUsuario(Autenticacion.numerotelefono, imgString, extension);

        }



        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
               return;
            }
            Uri uri = data.getData();

            String mimeType = getActivity().getContentResolver().getType(uri);



            System.out.println(mimeType);

            String filename=uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
            extensionstring=filename.substring(filename.lastIndexOf(".")+1);

            nombrearchivo.setVisibility(View.VISIBLE);
            nombrearchivo.setText(filename);


            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                InputStream is= getActivity().getContentResolver().openInputStream(uri);

                int nRead;
                byte[] databyte = new byte[16384];

                while ((nRead = is.read( databyte , 0,  databyte .length)) != -1) {
                    buffer.write( databyte , 0, nRead);
                }



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imagenenstring = ","+ Base64.encodeToString(buffer.toByteArray(),
                    Base64.DEFAULT);


          //  System.out.println("filestring" +fileString);

        //    String selectedImagePath = getPath(uri);
            imagencargada.setVisibility(View.VISIBLE);
            imagencargada.setImageResource(R.drawable.clip);


        //   buscarUsuario(Autenticacion.numerotelefono, fileString, extension);

        }

    }


    private void subirImagen(String imagen, String id, String extension) {


        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("ID", id);
            jsonBody.put("IMAGEN", imagen);

            jsonBody.put("EXTENSION", extension);



            Iterator it = valores.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                jsonBody.put(pair.getKey().toString(), pair.getValue().toString());

                it.remove(); // avoids a ConcurrentModificationException
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(imagen_url, jsonBody, rh);

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequest(cr.crearRequest());




      /*  StringRequest request = new StringRequest(Request.Method.POST, imagen_url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String id = respuesta.getString("GRABADO").toString();


                    datoactualizar.onNombreAActualizar("actualizar");

                }catch (JSONException e) {

                    System.out.println(e.toString());
                }

                System.out.println(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("volley error");
                error.printStackTrace();

                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data

                        JSONObject obj = new JSONObject(res);
                        System.out.println(obj.toString());
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        Log.e("JSON Parser", "Error parsing data " + e1.toString());
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        Log.e("JSON Parser", "Error parsing data " + e2.toString());
                        e2.printStackTrace();
                    }
                }

                System.out.println(error.toString());

            }
        }) {


            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonBody = new JSONObject();




                try {
                    jsonBody.put("ID", id);
                    jsonBody.put("IMAGEN", imagen);

                    jsonBody.put("EXTENSION", extension);



                    Iterator it = valores.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        jsonBody.put(pair.getKey().toString(), pair.getValue().toString());

                        it.remove(); // avoids a ConcurrentModificationException
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {

                    return jsonBody == null ? null : jsonBody.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException uee) {

                    return null;
                }


            }
        };

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequest(request);*/

    }



    public void subirArchivo() {
        Intent intent = new Intent();
        intent.setType("text/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione archivo"), PICK_FILE);
    }


    public void subirImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione Imagen"), PICK_IMAGE);
    }



    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }



    public byte[] getBytesFromFile(File file) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
                System.out.print((char)b[i]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }

        return b;
    }


    public String getPath(Uri uri) {
        String fileName;

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        String s = cursor.getString(column_index);

        cursor.close();


        return s;
    }




    public String getPathDelTipoArchivo(Uri uri){
        String pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
        String docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
        String xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls");
        String xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
        String ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt");
        String pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx");
        String txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
        String rtx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx");
        String rtf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf");
        String html = MimeTypeMap.getSingleton().getMimeTypeFromExtension("html");


        Uri table = MediaStore.Files.getContentUri("external");
        //Column
        String[] column = {MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.MIME_TYPE};

        //Where
        String where = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                +" OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        //args
        String[] args = new String[]{pdf,doc,docx,xls,xlsx,ppt,pptx,txt,rtx,rtf,html};

        Cursor fileCursor = getActivity().getContentResolver().query(table, column, where, args, null);

        if (fileCursor == null) return null;
        String s="";
        fileCursor.moveToFirst();
           s = fileCursor.getString(MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT);



    //    fileCursor.moveToFirst();



        fileCursor.close();


        return s;



    }


    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));

        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }





    public void setValues(String vdia, String vchat_id, String vemisor, String vreceptor) {

        valores.put("DIA", vdia);
        valores.put("CHAT_ID", vchat_id);
        valores.put("EMISOR", vemisor);
        valores.put("RECEPTOR", vreceptor);
    }
}
