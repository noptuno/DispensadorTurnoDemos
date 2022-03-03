package com.gpp.devoluciondeenvases.principal.principal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.gpp.devoluciondeenvases.R;

public class Configuracion extends AppCompatActivity {
    ImageView imagen;
    Button subir, guardar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);


        imagen = findViewById(R.id.imglogo);
        subir = findViewById(R.id.btnsubirimagen);


        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarimagen();
               // Toast.makeText(getApplicationContext(),"mensaje",Toast.LENGTH_SHORT).show();
            }
        });
        guardar = findViewById(R.id.btnguardar);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void cargarimagen() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Selecciona la image"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            Uri path = data.getData();
            imagen.setImageURI(path);
        }
    }
}