package com.gpp.devoluciondeenvases.principal.dispensador;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.adapter.AdapterProducto;
import com.gpp.devoluciondeenvases.adapter.AdapterSector;
import com.gpp.devoluciondeenvases.basededatos.ProductoDB;
import com.gpp.devoluciondeenvases.basededatos.SectorDB;
import com.gpp.devoluciondeenvases.clases.Producto;
import com.gpp.devoluciondeenvases.clases.Sector;
import com.gpp.devoluciondeenvases.principal.RegistrarProducto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class RegistroSectores extends AppCompatActivity {
    private AdapterSector adapter;
    private SectorDB db;
    private Button btnregistrar, btnnuevosector, btnred, btnblue, btnorange, btngreen, btnpurple;
    private EditText nombre, numero;
    private String color = "#B30D0D";
    private LinearLayout layoutPrincipal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resgistro_sectores);



        layoutPrincipal = findViewById(R.id.layouprincipal);
        botones();


        btnregistrar= findViewById(R.id.btnregistarsector);
        nombre= findViewById(R.id.etxtnombre);

        numero= findViewById(R.id.etxtnumero);

        btnnuevosector= findViewById(R.id.btnnuevosector);

        btnnuevosector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           limpiar();

            }
        });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!nombre.getText().toString().equals(" ")) {

                    Sector sector = new Sector();
                    sector.setNombreSector(nombre.getText().toString());
                    sector.setColorSector(color);
                    sector.setNumeroSector(0);

                    if (registrarProdcuto(sector)) {
                        limpiar();
                    }

                    cargarLista();
                }

            }
        });

        adapter = new AdapterSector();
        adapter.setOnNoteSelectedListener(new AdapterSector.OnNoteSelectedListener() {
            @Override
            public void onClick(Sector productodetalle) {

                final Sector sector = productodetalle;

                AlertDialog.Builder build = new AlertDialog.Builder(RegistroSectores.this);
                build.setMessage("Opciones").setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            nombre.setText(sector.getNombreSector());
                            numero.setText(sector.getNumeroSector());
                            color = (sector.getColorSector());
                            asignarColor(color);
                            btnregistrar.setText("Modificar");

                    }


                });
                AlertDialog alertDialog = build.create();
                alertDialog.show();

            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSectores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        cargarLista();


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                try {
                    db = new SectorDB(RegistroSectores.this);
                    db.eliminarSector(adapter.getposicionactual(viewHolder.getAdapterPosition()).getIdSector());
                    cargarLista();
                } catch (Exception e) {

                }
            }
        }).attachToRecyclerView(recyclerView);





    }

    private void botones() {
        final String red = "#B30D0D";
        final String blue = "#2196F3";
        final String orange = "#FF9800";
        final String green = "#4CAF50";
        final String purple = "#673AB7";

        btnred= findViewById(R.id.btnred);
        btnblue= findViewById(R.id.btnblue);
        btnorange= findViewById(R.id.btnorange);
        btngreen= findViewById(R.id.btngreen);
        btnpurple= findViewById(R.id.btnpurple);

        btnred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                asignarColor(red);
            }
        });
        btnblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                asignarColor(blue);
            }
        });
        btnorange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                asignarColor(orange);
            }
        });
        btngreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asignarColor(green);
            }
        });
        btnpurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asignarColor(purple);
            }
        });
    }

   private void asignarColor(String colorelegido) {
       color = colorelegido;
       layoutPrincipal.setBackgroundColor(Color.parseColor(colorelegido));

    }

    private void cargarLista() {

        try {
            db = new SectorDB(this);
            ArrayList<Sector> list = db.loadSector();

            for (Sector sector : list) {

                Log.i("---> Base de datos: ", sector.toString());
            }

            adapter.setNotes(list);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("error", "mensajed");
        }
    }


    public boolean registrarProdcuto(Sector sector) {

        try {
            db = new SectorDB(this);
            db.insertarSector(sector);
            return true;

        } catch (Exception e) {
            Log.e("error", "mensajeb");
            return false;
        }
    }

    private void limpiar() {

        nombre.setText("");
        numero.setText("0");
        color = "#B30D0D";
        btnregistrar.setText("AÑADIR");
    }
}