package com.gpp.devoluciondeenvases.envases;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.adapter.AdapterProducto;
import com.gpp.devoluciondeenvases.basededatos.ProductoDB;
import com.gpp.devoluciondeenvases.clases.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarProducto extends AppCompatActivity {
    private AdapterProducto adapter;
    private ProductoDB db;
    private Button registrar;
    private EditText descripcion, precio;
    boolean estadoteclado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_producto);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        registrar = findViewById(R.id.btnregistrar);

        descripcion = findViewById(R.id.txtdescripcion);
        precio = findViewById(R.id.txtprecio);

        precio.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!descripcion.getText().toString().equals("") && descripcion.getText().length() > 0 && precio.getText().length() > 0 && !precio.getText().toString().equals("")) {

                    Producto producto = new Producto();
                    producto.setDescripcion(descripcion.getText().toString());

                    BigDecimal bd = new BigDecimal(precio.getText().toString());
                    bd = bd.setScale(4, RoundingMode.HALF_UP);
                    producto.setPrecio(bd.doubleValue());

                    if (registrarProdcuto(producto)) {
                        limpiar();
                    }
                    cargarLista();
                }

            }
        });

        adapter = new AdapterProducto();
        adapter.setOnNoteSelectedListener(new AdapterProducto.OnNoteSelectedListener() {
            @Override
            public void onClick(Producto productodetalle) {

                final Producto producto = productodetalle;

                AlertDialog.Builder build = new AlertDialog.Builder(RegistrarProducto.this);
                build.setMessage("Opciones").setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            db = new ProductoDB(RegistrarProducto.this);
                            db.eliminarProducto(producto.getIdProducto());
                            Toast.makeText(RegistrarProducto.this, "Se Elimino Producto" + producto.getDescripcion(), Toast.LENGTH_LONG).show();
                            cargarLista();
                        } catch (Exception e) {
                            Log.e("error", "mensaje");
                        }


                    }


                });
                AlertDialog alertDialog = build.create();
                alertDialog.show();

            }
        });



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recicler_view_productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        cargarLista();

    }


    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }


        private void limpiar() {

        descripcion.setText("");
        precio.setText("");

    }

    public boolean registrarProdcuto(Producto producto) {

        try {
            db = new ProductoDB(this);
            db.insertarProducto(producto);
            return true;

        } catch (Exception e) {
            Log.e("error", "mensajeb");
            return false;
        }
    }

    private void cargarLista() {

        try {
            db = new ProductoDB(this);
            ArrayList<Producto> list = db.loadProducto();
            for (Producto producto : list) {
                Log.i("---> Base de datos: ", producto.toString());

            }
            adapter.setNotes(list);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("error", "mensajed");
        }
    }


}