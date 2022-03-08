package com.gpp.devoluciondeenvases.dispensador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.adapter.AdapterSector;
import com.gpp.devoluciondeenvases.basededatos.SectorDB;
import com.gpp.devoluciondeenvases.clases.Sector;

import java.util.ArrayList;

public class RegistroSectores extends AppCompatActivity {
    private AdapterSector adapter;
    private SectorDB db;
    private Button btnregistrar, btnnuevosector, btnred, btnblue, btnorange, btngreen, btnpurple;
    private EditText nombre, numero;
    private String color = "#B30D0D";
    private LinearLayout layoutPrincipal;
    private int idsector, mostrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resgistro_sectores);

        layoutPrincipal = findViewById(R.id.layoutbotones);

        botones();

        CloseTeclado();

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
                if (!nombre.getText().toString().isEmpty() && !numero.getText().toString().isEmpty()) {

                 if (btnregistrar.getText().equals("MODIFICAR")){

                     Sector sector = new Sector();
                     sector.setIdSector(idsector);
                     sector.setNombreSector(nombre.getText().toString());
                     sector.setColorSector(color);
                     sector.setHabilitadoSector(1);
                     sector.setNumeroSector(Integer.parseInt(numero.getText().toString()));
                     actualziar(sector);
                     limpiar();

                 }else{
                    Sector sector = new Sector();
                    sector.setNombreSector(nombre.getText().toString());
                    sector.setColorSector(color);
                    sector.setHabilitadoSector(1);
                    sector.setNumeroSector(Integer.parseInt(numero.getText().toString()));
                    registrarProdcuto(sector);
                    limpiar();
                 }

                    cargarLista();

                }else{
                    Toast.makeText(getApplicationContext(), "Faltan Datos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        adapter = new AdapterSector();

        adapter.setOnNoteSelectedListener(new AdapterSector.OnNoteSelectedListener() {
            @Override
            public void onClick(Sector dectordetalle) {

                final Sector sector = dectordetalle;
                            nombre.setText(sector.getNombreSector());
                            numero.setText(""+sector.getNumeroSector());
                            color = (sector.getColorSector());
                            idsector = sector.getIdSector();
                            asignarColor(color);
                            btnregistrar.setText("MODIFICAR");
            }

        });


        adapter.setOnDetailListener(new AdapterSector.OnNoteDetailListener() {
            @Override
            public void onDetail(Sector note) {

                actualziar(note);
                limpiar();

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

    private void CloseTeclado() {

        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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

    public boolean actualziar(Sector sector) {

        try {
            db = new SectorDB(this);
            db.updateSector(sector);
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
        idsector = 0;

        //boton mejor
        layoutPrincipal.setBackgroundColor(Color.parseColor(color));


        btnregistrar.setText("AÑADIR");
        CloseTeclado();

    }
}