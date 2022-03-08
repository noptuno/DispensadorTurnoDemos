package com.gpp.devoluciondeenvases.dispensador;

import static com.gpp.devoluciondeenvases.dispensador.FileBrowseActivity.calculateInSampleSize;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gpp.devoluciondeenvases.R;

public class Configuracion extends AppCompatActivity {
    ImageView imagen;
    Button subir, guardar;
    private static final int REQUEST_PICK_FILE = 1; //for File browsing
    static final String FOLDER_NAME_KEY = "com.gpp.config.FolderName";
    static final String FOLDER_PATH_KEY = "com.gpp.config.FolderPath";


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


        //==========Start file browsing activity==================//
        Intent intent = new Intent("com.gpp.FileBrowseActivity");
        startActivityForResult(intent,REQUEST_PICK_FILE);

/*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Selecciona la image"),10);

        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_PICK_FILE:
            {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if(extras != null) {

                        String m_selectedPath = extras.getString(FOLDER_PATH_KEY);
                            Toast.makeText(getApplicationContext(),m_selectedPath,Toast.LENGTH_SHORT).show();

                            Bitmap bitmap = decodeSampledBitmapFromFile(m_selectedPath, 50, 50);
                        imagen.setImageBitmap(bitmap);
                        }
                    }
                }
                break;
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }
}