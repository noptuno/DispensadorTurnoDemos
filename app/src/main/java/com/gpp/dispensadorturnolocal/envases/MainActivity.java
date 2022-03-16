package com.gpp.dispensadorturnolocal.envases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpp.dispensadorturnolocal.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int m_configurado;
    String fechainicio;
    EditText numero;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numero = findViewById(R.id.numeroeditable);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date2 = new Date();
        fechainicio = dateFormat.format(date2);

        pref = getSharedPreferences("DISPENSADORDEMO", Context.MODE_PRIVATE);
        numero.setText(pref.getString("NUMERO", "00"));
        numero.requestFocus();

    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.idconfigurar:
                Intent in = new Intent(MainActivity.this, RegistrarProducto.class);
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

    public void registrar_producto(View v){
    Intent in = new Intent(MainActivity.this, RegistrarProducto.class);
    startActivity(in);
}

    public void Dispensador_turno(View v){

            Intent i = new Intent(MainActivity.this, DispensadorTurno.class);
            pref = getSharedPreferences("DISPENSADORDEMO", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            if (numero.getText().toString().isEmpty() || numero.getText().toString().equals("") || numero.getText().toString().equals(" ") ){
                editor.putString("NUMERO", "0");
                numero.setText("0");
            }else{
                editor.putString("NUMERO", numero.getText().toString());
            }
            editor.apply();
            GuardarFecha();
            startActivity(i);

    }

    void GuardarFecha(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        String fechaactual = dateFormat.format(date);

        pref = getSharedPreferences("DISPENSADORDEMO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("FECHA", fechaactual);
        editor.apply();
    }



    public void devolver_producto(View v){
        Intent i = new Intent(MainActivity.this, DevoluciondeEnvases.class);
        startActivity(i);
    }

    void DisplayAboutDialog() {
        final AppCompatDialog about = new AppCompatDialog(MainActivity.this);
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
                        + " Copyright: 2018" + "\n"
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
}