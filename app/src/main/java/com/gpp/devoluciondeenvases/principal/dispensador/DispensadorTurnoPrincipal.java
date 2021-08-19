package com.gpp.devoluciondeenvases.principal.dispensador;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.adapter.AdapterDispensador;
import com.gpp.devoluciondeenvases.basededatos.SectorDB;
import com.gpp.devoluciondeenvases.clases.Sector;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class DispensadorTurnoPrincipal extends AppCompatActivity {


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn = null;
    private UsbEndpoint usbEndpointOut = null;
    private UsbManager usbManager;
    private boolean permisosimpresora = false;
    private boolean impresoraactiva = false;
    private Context context;
    private int CantidadSectores =0;

    private SectorDB db;
    ConstraintLayout constrain;
    ActionBar actionBar;

    private AdapterDispensador adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensador_turno_principal);
        constrain = findViewById(R.id.constrainturnodos);
        context = getApplicationContext();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            CantidadSectores = bundle.getInt("cantidadSectores");
        }else {
            CantidadSectores = 0;
        }

        adapter = new AdapterDispensador(CantidadSectores);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerviewprincipal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        cargarLista();

        usb();

        adapter.setOnNoteSelectedListener(new AdapterDispensador.OnNoteSelectedListener() {
            @Override
            public void onClick(Sector note) {
                if (permisosimpresora){
                    if (impresoraactiva){
                        imprimirNumero(note);
                    }else{
                        Toast.makeText(DispensadorTurnoPrincipal.this, "No se ha conectado la impresora (recinicie la app)", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(DispensadorTurnoPrincipal.this, "Debe aceptar permisos", Toast.LENGTH_LONG).show();
                }

            }
        });


    }



    private void imprimirNumero(Sector sector) {

        byte[] printData = {0};
        String nombreSector = sector.getNombreSector();
        int numeroactual = sector.getNumeroSector();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        //animacion.setVisibility(View.VISIBLE);
        //btnimprimir.setEnabled(false);

        Charset encoding = Charset.forName("CP437");

        byte[] nombreproducto = "Su Turno es: ".getBytes(encoding);
        byte[] numeroimprimir = ("" + numeroactual).getBytes();

        Bitmap starLogoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_dmrcirculo);

        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPos);
        builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
        builder.beginDocument();
        //builder.appendBitmap(starLogoImage, false);
        // builder.appendLineFeed();

        //*********************************
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendMultiple(3, 3);
        builder.appendAbsolutePosition(nombreSector.getBytes(), 0);
        builder.appendLineFeed(1);

        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendMultiple(1, 1);
        builder.appendAbsolutePosition(nombreproducto, 0);
        builder.appendLineFeed();
        builder.appendLineSpace(50);
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendMultiple(10, 10);
        builder.appendAbsolutePosition(numeroimprimir, 0);
        builder.appendLineFeed();
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);

        builder.appendMultiple(0, 0);
        builder.appendAbsolutePosition(("Fecha: " + fecha).getBytes(), 0);
        builder.appendLineFeed();
        //**********************

        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        printData = builder.getCommands();


        try {

            if (connection != null) {
                int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                if (result != -1) {

                    int numero = imcrementar(numeroactual);
                    sector.setNumeroSector(numero);

                    if (actualziar(sector)){
                        cargarLista();
                    }

                } else {
                    Toast.makeText(DispensadorTurnoPrincipal.this, "ERROR A: imprimir", Toast.LENGTH_LONG).show();
                }
            }

            // btnimprimir.setEnabled(true);
            //animacion.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            Toast.makeText(DispensadorTurnoPrincipal.this, "ERROR B: imprimir", Toast.LENGTH_LONG).show();
            //btnimprimir.setEnabled(true);
            //animacion.setVisibility(View.INVISIBLE);
        }
    }

    private int imcrementar(int ultimonumero){
        int LIMIT_CANTIDAD = 100;
        int RESET_CANTIDAD = 0;
        int numero = ultimonumero;

        if (numero<LIMIT_CANTIDAD){

            numero++;

        }else{
            numero = RESET_CANTIDAD;
        }

        return numero;
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

    private void usb() {

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e("Dispositivos", device.getDeviceName() + " + " + device.getVendorId() + " + " + device.getProductId());

            if (device.getVendorId() == 1155  && device.getProductId() == 22304) {

                if (usbManager.hasPermission(device)) {
                    permisosimpresora = true;
                    conectarImpresora(device);
                } else {
                    permisosimpresora = false;
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                    DispensadorTurnoPrincipal.this.registerReceiver(usbReceiver, filter);
                    usbManager.requestPermission(device, mPermissionIntent);
                }

            }
        }


    }


    private void conectarImpresora(UsbDevice device) {
        try {

            UsbInterface usbInterface = device.getInterface(0);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint end = usbInterface.getEndpoint(i);
                if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                    usbEndpointIn = end;
                } else {
                    usbEndpointOut = end;
                }
            }
            connection = usbManager.openDevice(device);

            if (connection != null && connection.claimInterface(usbInterface, true)) {
                impresoraactiva = true;
                Toast.makeText(DispensadorTurnoPrincipal.this, "Conectado", Toast.LENGTH_SHORT).show();
            }else{
                impresoraactiva = false;
                Toast.makeText(DispensadorTurnoPrincipal.this, "ERROR C conectar", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception var2) {

            impresoraactiva = false;
            Toast.makeText(DispensadorTurnoPrincipal.this, "ERROR D conectar", Toast.LENGTH_SHORT).show();
        }



    }


    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    DispensadorTurnoPrincipal.this.unregisterReceiver(usbReceiver);
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(DispensadorTurnoPrincipal.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                        permisosimpresora = true;
                        conectarImpresora(device);
                    } else {
                        Toast.makeText(DispensadorTurnoPrincipal.this, "Permiso no aceptado, OBLIGATORIO", Toast.LENGTH_LONG).show();
                        permisosimpresora = false;
                        finish();
                    }

                }
            }
        }
    };


    private void cargarLista() {

        try {
            db = new SectorDB(this);
            ArrayList<Sector> list = db.loadSectorDispensador();

            ArrayList<Sector> list2;

            for (Sector sector : list) {
                Log.i("---> Base de datos: ", sector.toString());
            }

            adapter.setNotes(list);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("error", "mensajed");
        }
    }

    void hidebarras() {
        constrain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (actionBar != null) {
            actionBar.hide();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        hidebarras();

    }


}
