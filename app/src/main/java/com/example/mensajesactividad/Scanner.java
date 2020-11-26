package com.example.mensajesactividad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.mensajesactividad.controladores.MostrarContactos;
import com.example.mensajesactividad.controladores.Perfil;
import com.example.mensajesactividad.controladores.Presentacion;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Scanner extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView textviewscanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        scannView = findViewById(R.id.scannerView);
        textviewscanner=findViewById(R.id.textviewscanner);
        codeScanner = new CodeScanner(this,scannView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("codigo leido "+result);
                        textviewscanner.setText("Código leido con éxito");




                        Thread mithread=new Thread(){
                            @Override
                            public void run() {
                                try {
                                    sleep(1500);
                                }catch (Exception e) {

                                }finally {

                                    Intent intentc=new Intent(Scanner.this, MostrarContactos.class);

                                    startActivity(intentc);
                                }
                            }
                        };
                        mithread.start();
                    }
                });
            }
        });



        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();

    }




    public void requestForCamera() {
        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Snackbar.make(scannView, "Se necesitan los permisos de la cámara para continuar", Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).check();
    }
}