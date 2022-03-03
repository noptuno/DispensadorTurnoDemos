package com.gpp.devoluciondeenvases.principal.principal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.gpp.devoluciondeenvases.R;

public class MensajeActivity extends AppCompatActivity {
    ActionBar actionBar;
    ConstraintLayout constrain;
    private int CantidadSectores =0;
    private String nombreSector;
    TextView numero,nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);

        constrain = findViewById(R.id.constrainmensaje);
        numero = findViewById(R.id.txtnumeromensaje);

        nombre = findViewById(R.id.txtsector);

        constrain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        if (actionBar != null) {
            actionBar.hide();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            CantidadSectores = bundle.getInt("numeroSector");
            nombreSector = bundle.getString("nombreSector");
        }else {
            CantidadSectores = 0;
            nombreSector= "";
        }

        numero.setText(""+CantidadSectores);
        nombre.setText(""+nombreSector);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                MensajeActivity.this.finish();

            }


        }, 2000);
    }

}
