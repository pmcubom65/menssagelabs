package com.example.mensajesactividad;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    TextView tnombre;
    TextView ttelefono;
    TextView tchats;
    String url = "http://10.0.2.2:54119/api/smartchat/listadochats";

    String imagen_url="http://10.0.2.2:54119/api/smartchat/almacenarimagen";


    String buscarusuario="http://10.0.2.2:54119/api/smartchat/buscarusuario";

    RequestQueue requestQueue;
    private Toolbar toolbar;
    public static final int PICK_IMAGE = 1;

    TextView tv;
    ImageView iv;
    ProgressDialog progressDialog;
    Bitmap bitmap;

    String idusuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tv = findViewById(R.id.ruta);
        iv = findViewById(R.id.imageView4);

        tnombre = findViewById(R.id.perfilnombre);
        ttelefono = findViewById(R.id.perfiltelefono);
        tchats = findViewById(R.id.perfilchats);

        tnombre.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        ttelefono.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tchats.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        tnombre.setText("NOMBRE " + Autenticacion.nombredelemisor);
        ttelefono.setText("TELEFONO " + Autenticacion.numerotelefono);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        toolbar = findViewById(R.id.mitoolbarperfil);
        toolbar.setLogo(R.drawable.smart_prod);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numeroDeChats();


    }


    public void numeroDeChats() {


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsnobject = new JSONObject(response.toString());
                    tchats.setText("Chats creados: " + jsnobject.getString("numero"));


                } catch (JSONException e) {
                    e.printStackTrace();
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
                    jsonBody.put("telefono", Autenticacion.numerotelefono);


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
        requestQueue.add(request);


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
            } catch (IOException e) {
                e.printStackTrace();
            }

            String imgString = ","+Base64.encodeToString(getBytesFromBitmap(bitmap),
                    Base64.DEFAULT);



            String selectedImagePath = getPath(uri);
            iv.setImageURI(uri);

            buscarUsuario(Autenticacion.numerotelefono, imgString);



        }
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




    private void subirImagen(String imagen, String id) {

        StringRequest request = new StringRequest(Request.Method.POST, imagen_url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String id = respuesta.getString("GRABADO").toString();

                    progressDialog.dismiss();


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
        MySingleton.getInstance(getBaseContext()).addToRequest(request);

    }





    private void buscarUsuario(String telefonobuscar, String imagen) {

        StringRequest request = new StringRequest(Request.Method.POST, buscarusuario, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject respuesta = new JSONObject(response);

                    idusuario = respuesta.getString("ID").toString();
                    System.out.println("usuario encontrado");

                    subirImagen(imagen, idusuario);

                }catch (JSONException e) {

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
        MySingleton.getInstance(getBaseContext()).addToRequest(request);
        //    requestQueue.add(request);


    }

}