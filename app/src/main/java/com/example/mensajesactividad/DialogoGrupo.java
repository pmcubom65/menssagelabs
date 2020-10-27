package com.example.mensajesactividad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.mensajesactividad.modelos.Usuario;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogoGrupo extends DialogFragment {

    public interface Datoaactualizar {

        public void onNombreAActualizar(String s);
    }

    public DialogoGrupo(){}

    public DialogoGrupo(Boolean b){
        crear=b;
    }


    Datoaactualizar datoactualizar;
    EditText miedit;
    Boolean crear;


    String urlcreargrupo="http://10.0.2.2:54119/api/smartchat/creargrupo";

    String urlañadiraungrupo="http://10.0.2.2:54119/api/smartchat/anadirusuarioagrupo";

    TextView tv;
    View layoutactualizar;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();

        layoutactualizar=inflater.inflate(R.layout.dialogogrupo,null);
        tv=layoutactualizar.findViewById(R.id.textodialogo);

        if (MostrarContactos.alGrupo.size()==0) {
            layoutactualizar.findViewById(R.id.nombregrupo).setVisibility(View.INVISIBLE);


            tv.setText("Tiene que seleccionar miembros del grupo en su agenda para crear un grupo");
        }else {
            tv.setText("Escriba el nombre del grupo");
        }

        miedit=(EditText) layoutactualizar.findViewById(R.id.nombregrupo);
        builder.setView(layoutactualizar);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (miedit.getText().toString().length()>0) {

                    crearGrupo(miedit.getText().toString());
                }else {

                    String respuesta="El nombre del grupo no puede estar vacío. Grupo no creado";
                    final View viewPos = getActivity().findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(viewPos, respuesta, Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();

                }




            }
        });
        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
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
            datoactualizar=(Datoaactualizar) activity;
        }catch (ClassCastException cce) {}

    }


    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);

            if (MostrarContactos.alGrupo.size()==0){
                positiveButton.setEnabled(false);
            }


            positiveButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...

                    if (crear && miedit.getText().toString().length()>0) {
                        crearGrupo(miedit.getText().toString());
                    } else if (!crear && miedit.getText().toString().length()>0) {
                        anadirGrupo(miedit.getText().toString(), MostrarContactos.alGrupo);
                    }

                    if (wantToCloseDialog)
                        dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void crearGrupo(String nombre) {


            StringRequest request = new StringRequest(Request.Method.POST, urlcreargrupo, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {
                    final View viewPos = layoutactualizar.findViewById(R.id.dialogogruposlayout);
                    try {

                        JSONObject respuesta = new JSONObject(response);

                        String  nombre = respuesta.getString("nombre");


                        Snackbar snackbar = Snackbar.make(viewPos, "Grupo "+nombre+" creado con éxito", Snackbar.LENGTH_INDEFINITE);

                        snackbar.show();




                    }catch (JSONException e) {

                        Snackbar snackbar = Snackbar.make(viewPos, "Grupo "+nombre+" creado con éxito", Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
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
                        jsonBody.put("nombre", nombre);


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

            MySingleton.getInstance(getContext()).addToRequest(request);






    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    private void anadirGrupo(String nombre, ArrayList<Usuario> alGrupo) {

        for (int i=0; i<alGrupo.size(); i++) {
            meterMiembro(alGrupo.get(i).getTelefono().toString().replaceAll("[\\D]", ""), nombre);
        }

    }



    public void meterMiembro(String telefono, String nombre) {

        StringRequest request = new StringRequest(Request.Method.POST, urlañadiraungrupo, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final View viewPos = layoutactualizar.findViewById(R.id.dialogogruposlayout);

                try {

                    JSONObject respuesta = new JSONObject(response);

                    String  nombre = respuesta.getString("grupo");

                    Snackbar snackbar = Snackbar.make(viewPos, "Añadido al grupo con éxito", Snackbar.LENGTH_INDEFINITE);

                    snackbar.show();




                }catch (JSONException e) {


                    Snackbar snackbar = Snackbar.make(viewPos, "Usuario o Grupo no válido", Snackbar.LENGTH_INDEFINITE);

                    snackbar.show();

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
                    jsonBody.put("grupo", nombre);

                    jsonBody.put("telefono", telefono);


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

        MySingleton.getInstance(getContext()).addToRequest(request);


    }

}
