package com.gpp.devoluciondeenvases.dispensador;

import static com.gpp.devoluciondeenvases.dispensador.FileBrowseActivity.calculateInSampleSize;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.clases.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Configuracion extends AppCompatActivity {
    ImageView imagen;
    Button subir, guardar;
    private static final int REQUEST_PICK_FILE = 1; //for File browsing
    static final String FOLDER_NAME_KEY = "com.gpp.config.FolderName";
    static final String FOLDER_PATH_KEY = "com.gpp.config.FolderPath";
    String pathimagen;
    int tipopael;
    String otros;
    private RadioButton tresplg, cuatroplg;
    private TextView txtpath;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1;
    private Config g_appSettings = new Config("path",0,"otros");
    String ApplicationConfigFilename = "configuraciondispensador.dat";
    String NamefileImagen = "imagen.dat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);


        int readExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED || readExternalPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }




        imagen = findViewById(R.id.imglogo);
        subir = findViewById(R.id.btnsubirimagen);
        txtpath = findViewById(R.id.txtpathimg);

        tresplg = findViewById(R.id.radioB3plg);
        cuatroplg= findViewById(R.id.radioB4plg);

        cuatroplg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g_appSettings.setTipopael(1);
            }
        });
        tresplg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g_appSettings.setTipopael(0);
            }
        });

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


                SaveApplicationSettingToFile();
                finish();
            }
        });



        
        Config appSettings = ReadApplicationSettingFromFile();
        if (appSettings != null){
            g_appSettings = appSettings;
            pathimagen = g_appSettings.getPathimagen();
            tipopael = g_appSettings.getTipopael();
            otros = g_appSettings.getOtros();
            txtpath.setText(pathimagen);
            if(tipopael == 0)
            {
                tresplg.setChecked(true);
            }else {
                cuatroplg.setChecked(true);
            }

           cargarimagen(pathimagen);

        }

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
                        cargarimagen(m_selectedPath);

                        }
                    }
                }
                break;
        }
    }


    private void cargarimagen(String path){

        Bitmap bitmap = decodeSampledBitmapFromFile(path, 100, 100);

        if (bitmap==null){
            bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_dmr_milrollos);
            imagen.setImageBitmap(bitmap);
            g_appSettings.setPathimagen("error");
            txtpath.setText("No se encontro la imagen");

        }else{
            imagen.setImageBitmap(bitmap);
            g_appSettings.setPathimagen(path);
            txtpath.setText(path);
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

    Config ReadApplicationSettingFromFile() {
        Config ret = null;
        InputStream instream;
        try {
            showToast("Loading configuration");
            instream = openFileInput(ApplicationConfigFilename);
        } catch (FileNotFoundException e) {

            Log.e("DOPrint", e.getMessage(), e);
            showToast("No configuration loaded");
            return null;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(instream);

            try {
                ret = (Config) ois.readObject();
            } catch (ClassNotFoundException e) {
                Log.e("DOPrint", e.getMessage(), e);
                ret = null;
            }
        } catch (Exception e) {
            Log.e("DOPrint", e.getMessage(), e);
            ret = null;
        } finally {
            try {
                if (instream != null)
                    instream.close();
            } catch (IOException ignored) { }
        }
        return ret;
    }

    public boolean SaveApplicationSettingToFile() {

        boolean bRet = true;
        FileOutputStream fos = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // write the object to the output stream object.
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(g_appSettings);

            // convert the output stream object to array of bytes
            byte[] buf = bos.toByteArray();

            // write the array of bytes to file output stream
            fos = openFileOutput(ApplicationConfigFilename,
                    Context.MODE_PRIVATE);
            fos.write(buf);
            File f = getDir(ApplicationConfigFilename, 0);
            Log.e("DOPrint", "Save Application settings to file: " + f.getName());
            showToast("Application Settings saved");
        } catch (IOException ioe) {
            Log.e("DOPrint", "error", ioe);
            showToast(ioe.getMessage());
            bRet = false;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ioe) {

                showToast(ioe.getMessage());
            }
        }
        return bRet;
    }// SaveApplicationSettingTo




    public void showToast(final String toast) {
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
    }
}