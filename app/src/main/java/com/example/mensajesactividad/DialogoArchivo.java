package com.example.mensajesactividad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DialogoArchivo  extends DialogFragment {

    View layoutactualizar;
    Button archivo;
    Button imagen;
    ImageView imagencargada;
    public static final int PICK_IMAGE = 1;

    public static final int PICK_FILE = 2;

    DialogoGrupo.Datoaactualizar datoactualizar;

    String imagen_url="http://10.0.2.2:54119/api/smartchat/almacenarimagen";


    String buscarusuario="http://10.0.2.2:54119/api/smartchat/buscarusuario";

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
        imagencargada.setVisibility(View.GONE);

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
            datoactualizar=(DialogoGrupo.Datoaactualizar) activity;
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

            String imgString = ","+ Base64.encodeToString(getBytesFromBitmap(bitmap),
                    Base64.DEFAULT);



            String selectedImagePath = getPath(uri);
            imagencargada.setVisibility(View.VISIBLE);
            imagencargada.setImageURI(uri);

     //       buscarUsuario(Autenticacion.numerotelefono, imgString);

        }



        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
               return;
            }
            Uri uri = data.getData();

            String mimeType = getActivity().getContentResolver().getType(uri);



            System.out.println(mimeType);
            System.out.println(uri.getPath());
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

            String fileString = ","+ Base64.encodeToString(buffer.toByteArray(),
                    Base64.DEFAULT);


            System.out.println("filestring" +fileString);

        //    String selectedImagePath = getPath(uri);
            imagencargada.setVisibility(View.VISIBLE);
            imagencargada.setImageResource(R.drawable.clip);


                   buscarUsuario(Autenticacion.numerotelefono, fileString);

        }

    }


    private void subirImagen(String imagen, String id) {

        StringRequest request = new StringRequest(Request.Method.POST, imagen_url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String id = respuesta.getString("GRABADO").toString();




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
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequest(request);

    }





    private void buscarUsuario(String telefonobuscar, String imagen) {

        StringRequest request = new StringRequest(Request.Method.POST, buscarusuario, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String idusuario = respuesta.getString("ID").toString();
                    System.out.println("usuario encontrado");

                    subirImagen(imagen, idusuario);

                }catch (JSONException e) {

              //      Snackbar.make((View) findViewById(R.id.linearcontactos), "El usuario no está registrado", Snackbar.LENGTH_LONG).show();

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
                    jsonBody.put("telefono", telefonobuscar);
                    System.out.println("Busco este telefono "+jsonBody.toString());

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
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequest(request);
        //    requestQueue.add(request);


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
}
