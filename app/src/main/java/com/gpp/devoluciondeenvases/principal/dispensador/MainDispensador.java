package com.gpp.devoluciondeenvases.principal.dispensador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.principal.MainActivity;
import com.gpp.devoluciondeenvases.principal.RegistrarProducto;

public class MainDispensador extends AppCompatActivity {


    private Button btnconfigurar, btniniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dispensador);

        btnconfigurar = findViewById(R.id.btnconfigurar);
        btniniciar = findViewById(R.id.btniniciar);


        btniniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnconfigurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainDispensador.this, RegistroSectores.class);
                startActivity(in);
            }
        });
    }






    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.idregistrar:
                Intent in = new Intent(MainDispensador.this, RegistroSectores.class);
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