package com.gpp.devoluciondeenvases.dispensador;

import static com.gpp.devoluciondeenvases.dispensador.FileBrowseActivity.calculateInSampleSize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.basededatos.SectorDB;
import com.gpp.devoluciondeenvases.clases.Config;
import com.gpp.devoluciondeenvases.clases.Sector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainDispensador extends AppCompatActivity {
    private SectorDB db;
    String ApplicationConfigFilename = "configuraciondispensador.dat";
    private Button btniniciar;
private int CantidadSectores = 0;
private ImageView imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dispensador);

        btniniciar = findViewById(R.id.btniniciar);
        imageButton = findViewById(R.id.logoempresa);
        btniniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CantidadSectores>0){
                    Intent in = new Intent(MainDispensador.this,DispensadorTurnoPrincipal.class);
                    in.putExtra("cantidadSectores", CantidadSectores);
                    startActivity(in);

                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }else{
                    Toast.makeText(MainDispensador.this, "Debe Registrar o Habilitar al menos 1 Sector", Toast.LENGTH_LONG).show();
                }

            }
        });

        CantidadSectores = cargarLista();

        if (CantidadSectores>0){

            Intent in = new Intent(MainDispensador.this,DispensadorTurnoPrincipal.class);
            in.putExtra("cantidadSectores", CantidadSectores);
            startActivity(in);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }else{

            Toast.makeText(MainDispensador.this, "Debe Registrar o Habilitar al menos 1 Sector", Toast.LENGTH_LONG).show();

        }




    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarConfiguraion();
    }

    private void cargarConfiguraion() {

        Config appSettings = ReadApplicationSettingFromFile();
        if (appSettings != null){
            cargarimagen(appSettings.getPathimagen());
        }
    }

    private void cargarimagen(String pathimagen) {

        Bitmap bitmap = decodeSampledBitmapFromFile(pathimagen, 100, 100);

        if (bitmap==null){
            bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logodiscopeque);
            imageButton.setImageBitmap(bitmap);

        }else{
            imageButton.setImageBitmap(bitmap);

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


    @Override
    protected void onPostResume() {
        super.onPostResume();
        CantidadSectores = cargarLista();

    }

    private int cargarLista() {
        int cantidad = 0;
        try {

            db = new SectorDB(this);
            ArrayList<Sector> list = db.loadSectorDispensador();

            ArrayList<Sector> list2;

            for (Sector sector : list) {

                if (sector.getHabilitadoSector()==1){
                    Log.i("---> Base de datos: ", sector.toString());
                    cantidad++;
                }
            }

        } catch (Exception e) {
            Log.e("error", "mensajed");
        }
        return cantidad;
    }


    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent in;
        switch (item.getItemId()) {

            case R.id.idconfigurar:
                in = new Intent(MainDispensador.this, Configuracion.class);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(in);
                break;

            case R.id.idsector:

                in = new Intent(MainDispensador.this, RegistroSectores.class);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(in);
                break;
            case R.id.idaydua:
                DisplayAboutDialog();
                break;

            case R.id.idsalir:
                finish();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    void DisplayAboutDialog() {
        final AppCompatDialog about = new AppCompatDialog(MainDispensador.this);
        about.setContentView(R.layout.doabout);
        about.setCancelable(true);
        about.setTitle("Sobre Nosotros");
        // get version of the application.
        PackageInfo pinfo;
        try
        {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (pinfo != null) {
                TextView descTextView = (TextView) about.findViewById(R.id.AboutDescription);
                String descString = " " + getString(R.string.app_name) + "\n"
                        + " Version Code:"
                        + String.valueOf(pinfo.versionCode) + "\n"
                        + " Version Name:" + pinfo.versionName+"\n"
                        + " Copyright: 2021" + "\n"
                        + " Lipiner S.A (DMR mil rollos)" + "\r\n"
                        + " Dirección: Convenio 828"  + "\r\n"
                        + " Teléfono: (+598) 2209 19 21"  + "\r\n"
                        + " Contacto: desarrollo@dmr.com.uy";

                if(descTextView != null)
                    descTextView.setText(descString);

                // set up the image view
                ImageView AboutImgView = (ImageView) about
                        .findViewById(R.id.AboutImageView);
                if (AboutImgView != null)
                    AboutImgView.setImageResource(R.mipmap.ic_launcher);

                // set up button
                Button closeButton = (Button) about.findViewById(R.id.AboutCloseButton);
                if (closeButton != null) {
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            about.dismiss();
                        }
                    });
                }

                about.show();
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    Config ReadApplicationSettingFromFile() {
        Config ret = null;
        InputStream instream;
        try {
           // showToast("Loading configuration");
            instream = openFileInput(ApplicationConfigFilename);
        } catch (FileNotFoundException e) {

            Log.e("DOPrint", e.getMessage(), e);

            showToast("No hay Configuración");

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

    public void showToast(final String toast) {
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }
}