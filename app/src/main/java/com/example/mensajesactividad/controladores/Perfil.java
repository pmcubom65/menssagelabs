package com.example.mensajesactividad.controladores;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mensajesactividad.services.MySingleton;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.services.CrearRequests;
import com.example.mensajesactividad.services.RequestHandlerInterface;
import com.example.mensajesactividad.services.Rutas;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity implements RequestHandlerInterface {

    TextView tnombre;
    TextView ttelefono;


    String imagen_url = Rutas.subir_imagen_url;


    String buscarusuario = "http://10.0.2.2:54119/api/smartchat/buscarusuario";

    RequestQueue requestQueue;
    RequestHandlerInterface rh = this;
    private Toolbar toolbar;
    public static final int PICK_IMAGE = 1;

    TextView tv;
    ImageView iv;
    ProgressDialog progressDialog;
    Bitmap bitmap;

    String idusuario;

    Button subirimagenbutton;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tv = findViewById(R.id.ruta);
        iv = findViewById(R.id.imageView4);

        tv.setVisibility(View.GONE);

        tnombre = findViewById(R.id.perfilnombre);
        ttelefono = findViewById(R.id.perfiltelefono);


        tnombre.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        ttelefono.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


        tnombre.setText("NOMBRE " + Autenticacion.nombredelemisor);
        ttelefono.setText("TELEFONO " + Autenticacion.numerotelefono);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        subirimagenbutton=findViewById(R.id.subirimagen);

        toolbar = findViewById(R.id.mitoolbarperfil);
        toolbar.setLogo(R.drawable.smart_prod);
        setSupportActionBar(toolbar);
  //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getApplicationContext().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        buscarFotoUsuario(Autenticacion.idpropietario);



    }


    public void subirimagenonclick(View view) {

        progressDialog = new ProgressDialog(Perfil.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();


        //converting image to base64 string
    /*    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);*/




    /*    StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if(s.equals("true")){
                    Toast.makeText(Perfil.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(Perfil.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Perfil.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", imageString);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(Perfil.this);
        rQueue.add(request);*/


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //error
                return;
            }
            Uri uri = data.getData();


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                Glide.with(getApplicationContext()).load(bitmap)
                        .placeholder(R.drawable.account_circle)
                        .into(iv);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String imgString = "," + Base64.encodeToString(getBytesFromBitmap(bitmap),
                    Base64.DEFAULT);


            String selectedImagePath = getPath(uri);
            iv.setImageURI(uri);


            String filename = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);

            String extension = getMimeType(getApplicationContext(), uri);

            //     buscarUsuario(Autenticacion.numerotelefono, imgString, extension);

        //    buscarFotoUsuario(Autenticacion.idpropietario);

            subirImagen(imgString, Autenticacion.idpropietario, extension);

        }
    }


    public void buscarFotoUsuario(String id) {
        System.out.println("buscando la foto");
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("ID", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(Rutas.urlbuscarfoto, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());
    }


    public String getPath(Uri uri) {
        String fileName;

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        String s = cursor.getString(column_index);

        cursor.close();


        return s;
    }


    private String queryName(Uri uri) {
        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }


    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


    private void subirImagen(String imagen, String id, String extension) {


        JSONObject jsonBody=new JSONObject();

        try {
            jsonBody.put("ID", id);
            jsonBody.put("IMAGEN", imagen);
            jsonBody.put("EXTENSION", extension);
            jsonBody.put("DIA", "");
            jsonBody.put("CHAT_ID", "");
            jsonBody.put("EMISOR", Autenticacion.numerotelefono);
            jsonBody.put("RECEPTOR", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CrearRequests cr = new CrearRequests(imagen_url, jsonBody, rh);

        MySingleton.getInstance(getBaseContext()).addToRequest(cr.crearRequest());




    /*    StringRequest request = new StringRequest(Request.Method.POST, imagen_url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String id = respuesta.getString("GRABADO").toString();

                    tv.setVisibility(View.VISIBLE);
                    tv.setText("FOTO SUBIDA CON ÉXITO");

                    progressDialog.dismiss();


                } catch (JSONException e) {

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
                    jsonBody.put("DIA", "");
                    jsonBody.put("CHAT_ID", "");
                    jsonBody.put("EMISOR", Autenticacion.numerotelefono);
                    jsonBody.put("RECEPTOR", "");



            /*        valores.put("DIA", vdia);
                    valores.put("CHAT_ID", vchat_id);
                    valores.put("EMISOR", vemisor);
                    valores.put("RECEPTOR", vreceptor);*/


            /*    } catch (JSONException e) {
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
        MySingleton.getInstance(getBaseContext()).addToRequest(request);*/

    }


    private void buscarUsuario(String telefonobuscar, String imagen, String extension) {

        StringRequest request = new StringRequest(Request.Method.POST, buscarusuario, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    idusuario = respuesta.getString("ID").toString();
                    System.out.println("usuario encontrado");

                    subirImagen(imagen, idusuario, extension);

                } catch (JSONException e) {

                    Snackbar.make((View) findViewById(R.id.linearcontactos), "El usuario no está registrado", Snackbar.LENGTH_LONG).show();
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

                Snackbar.make(findViewById(R.id.autenticacionlayout), error.toString(), Snackbar.LENGTH_LONG).show();

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
                    System.out.println("Busco este telefono " + jsonBody.toString());

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
        MySingleton.getInstance(getBaseContext()).addToRequest(request);
        //    requestQueue.add(request);


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

    @Override
    public void onResponse(String response, String url) {

        if (url.equals(Rutas.urlbuscarfoto)) {
            try {
                JSONObject respuesta = new JSONObject(response);

                String nuevaruta = respuesta.getString("RUTA").toString();

                if (nuevaruta.length()>0) {
                    nuevaruta=nuevaruta.replace('\\', '/');
                    String ruta="https://smartchat.smartlabs.es/"+nuevaruta.substring(nuevaruta.lastIndexOf("img"));

                    System.out.println(ruta);



                    Glide.with(getApplicationContext()).load(ruta)
                            .placeholder(R.drawable.account_circle)
                            .into(iv);
                }


            } catch (JSONException   e) {
                e.printStackTrace();

            }


        }else if (url.equals(Rutas.subir_imagen_url)) {


            try {

                JSONObject respuesta = new JSONObject(response);

                String id = respuesta.getString("GRABADO").toString();

                String miruta = respuesta.getString("RUTA").toString();

                tv.setVisibility(View.VISIBLE);
                tv.setText("FOTO SUBIDA CON ÉXITO");
                subirimagenbutton.setVisibility(View.GONE);

                guardarimagenpreferencias(miruta);

                progressDialog.dismiss();


            } catch (JSONException e) {

                System.out.println(e.toString());
            }

            System.out.println(response);

        }
    }

    public void guardarimagenpreferencias(String mr){

            SharedPreferences preferences=getSharedPreferences("com.example.mensajes.credenciales", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("foto", mr);
            System.out.println("guardando preferencias");

            editor.commit();


        Thread mithread=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1500);
                }catch (Exception e) {

                }finally {

                    Intent intent=new Intent(Perfil.this, Autenticacion.class);
                    startActivity(intent);
                }
            }
        };
        mithread.start();

    }
}