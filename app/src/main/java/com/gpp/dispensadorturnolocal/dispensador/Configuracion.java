package com.gpp.dispensadorturnolocal.dispensador;

import static com.gpp.dispensadorturnolocal.dispensador.FileBrowseActivity.calculateInSampleSize;
import static com.starmicronics.starioextension.ae.e;

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
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gpp.dispensadorturnolocal.R;
import com.gpp.dispensadorturnolocal.clases.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Configuracion extends AppCompatActivity {
    ImageView imagen;
    Button subir, guardar,cancelar;
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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        int readExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED || readExternalPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }

        cancelar = findViewById(R.id.btncancelar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

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

        }else{
            cargarimagen("null");
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    public void cargarimagen() {


        //==========Start file browsing activity==================//
       // Intent intent = new Intent("com.gpp.FileBrowseActivity");
       // startActivityForResult(intent,REQUEST_PICK_FILE);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().toString());
        intent.setDataAndType(uri, "*/*");
        startActivityForResult(Intent.createChooser(intent, "Open folder"),REQUEST_PICK_FILE);



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

                    Uri file_uri = data.getData();

                    cargarimagenUri(file_uri);
                    /*
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        String m_selectedPath = extras.getString(FOLDER_PATH_KEY);
                        cargarimagen(m_selectedPath);
                        }
                    */

                    }
                }
                break;
        }
    }


    private void cargarimagen(String path){


        Bitmap bitmap = decodeSampledBitmapFromFile(path , 100, 100);


        if (bitmap==null){
            bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_dmr);
            imagen.setImageBitmap(bitmap);
            g_appSettings.setPathimagen("error");
            txtpath.setText("No se encontro la imagen");

        }else{
            imagen.setImageBitmap(bitmap);
            g_appSettings.setPathimagen(path);
            txtpath.setText(path);
        }


    }


    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


    private void cargarimagenUri(Uri path){

        try {



            File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + "/temp3.png");

            InputStream inputStream = this.getContentResolver().openInputStream(path);

            FileOutputStream out = new FileOutputStream(f);

            if (inputStream != null) {
                copy(inputStream, out);
                inputStream.close();
            }
            if (out != null) {
                out.close();
            }




/*
            int c;
            while( (c = in.read() ) != -1)
                out.write(c);

            in.close();
            out.close();

*/
            Bitmap bitmap = decodeSampledBitmapFromFile(f.getPath() , 100, 100);


            if (bitmap==null){
                bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_dmr);
                imagen.setImageBitmap(bitmap);
                g_appSettings.setPathimagen("error");
                txtpath.setText("No se encontro la imagen");

            }else{
                imagen.setImageBitmap(bitmap);
                g_appSettings.setPathimagen(f.getPath());
                txtpath.setText(f.getPath());
            }

        } catch(IOException e) {
            System.err.println("Hubo un error de entrada/salida!!!");
        }



    }




    private byte[] intToDWord(int parValue) {
        byte[] retValue = new byte[]{(byte) (parValue & 255), (byte) (parValue >> 8 & 255), (byte) (parValue >> 16 & 255), (byte) (parValue >> 24 & 255)};
        return retValue;
    }

    private byte[] intToWord(int parValue) {
        byte[] retValue = new byte[]{(byte) (parValue & 255), (byte) (parValue >> 8 & 255)};
        return retValue;
    }


    private byte[] convertTo1BPP(Bitmap inputBitmap, int darknessThreshold) {
        int width = inputBitmap.getWidth();
        int height = inputBitmap.getHeight();
        ByteArrayOutputStream mImageStream = new ByteArrayOutputStream();
        int BITMAPFILEHEADER_SIZE = 14;
        int BITMAPINFOHEADER_SIZE = 40;
        short biPlanes = 1;
        short biBitCount = 1;
        int biCompression = 0;
        int biSizeImage = (width * biBitCount + 31 & -32) / 8 * height;
        int biXPelsPerMeter = 0;
        int biYPelsPerMeter = 0;
        int biClrUsed = 2;
        int biClrImportant = 2;
        byte[] bfType = new byte[]{66, 77};
        short bfReserved1 = 0;
        short bfReserved2 = 0;
        int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE + 8;
        int bfSize = bfOffBits + biSizeImage;
        byte[] colorPalette = new byte[]{0, 0, 0, -1, -1, -1, -1, -1};
        int monoBitmapStride = (width + 31 & -32) / 8;
        byte[] newBitmapData = new byte[biSizeImage];

        try {
            mImageStream.write(bfType);
            mImageStream.write(this.intToDWord(bfSize));
            mImageStream.write(this.intToWord(bfReserved1));
            mImageStream.write(this.intToWord(bfReserved2));
            mImageStream.write(this.intToDWord(bfOffBits));
            mImageStream.write(this.intToDWord(BITMAPINFOHEADER_SIZE));
            mImageStream.write(this.intToDWord(width));
            mImageStream.write(this.intToDWord(height));
            mImageStream.write(this.intToWord(biPlanes));
            mImageStream.write(this.intToWord(biBitCount));
            mImageStream.write(this.intToDWord(biCompression));
            mImageStream.write(this.intToDWord(biSizeImage));
            mImageStream.write(this.intToDWord(biXPelsPerMeter));
            mImageStream.write(this.intToDWord(biYPelsPerMeter));
            mImageStream.write(this.intToDWord(biClrUsed));
            mImageStream.write(this.intToDWord(biClrImportant));
            mImageStream.write(colorPalette);
            int[] imageData = new int[height * width];
            inputBitmap.getPixels(imageData, 0, width, 0, 0, width, height);

            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int pixelIndex = y * width + x;
                    int mask = 128 >> (x & 7);
                    int pixel = imageData[pixelIndex];
                    int R = Color.red(pixel);
                    int G = Color.green(pixel);
                    int B = Color.blue(pixel);
                    int A = Color.alpha(pixel);
                    boolean set = A < darknessThreshold || R + G + B > darknessThreshold * 3;
                    if (set) {
                        int index = (height - y - 1) * monoBitmapStride + (x >>> 3);
                        newBitmapData[index] = (byte) (newBitmapData[index] | mask);
                    }
                }
            }

            mImageStream.write(newBitmapData);
        } catch (Exception var36) {
            var36.printStackTrace();
        }

        return mImageStream.toByteArray();
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