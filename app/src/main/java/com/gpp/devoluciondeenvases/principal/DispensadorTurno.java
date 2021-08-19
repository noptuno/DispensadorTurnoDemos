package com.gpp.devoluciondeenvases.principal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.gpp.devoluciondeenvases.R;

import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

@SuppressLint("HandlerLeak")
public class DispensadorTurno extends AppCompatActivity {

    public static boolean isConnected = false;// 蓝牙连接状态

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private boolean permisosimpresora = false;
    private boolean impresoraactiva = false;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn = null;
    private UsbEndpoint usbEndpointOut = null;


    private Button btnimprimir,conectar;
    ConstraintLayout constrain;
    ActionBar actionBar;
    byte[] printData;
    private ProgressDialog mProgressDialog;
    SharedPreferences pref;
    private int numero = 00;
    private TextView numeromostrar;
    private Context context;
    private LottieAnimationView animacion;
    private  UsbManager usbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_dispensador_turno);

        constrain = findViewById(R.id.constrainturno);
        btnimprimir = findViewById(R.id.btnimprimir);
        numeromostrar = findViewById(R.id.txtNum1);
        animacion = findViewById(R.id.animation_view2);
        conectar = findViewById(R.id.btnconectar);
        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usb();
            }
        });

        btnimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        if (permisosimpresora){
            if (impresoraactiva){
                 imprimirNumero();
                 }else{
                 Toast.makeText(DispensadorTurno.this, "No se ha conectado la impresora (recinicie la app)", Toast.LENGTH_LONG).show();
             }
            }else{
                 Toast.makeText(DispensadorTurno.this, "Debe aceptar permisos", Toast.LENGTH_LONG).show();
            }

            }
        });
        actionBar = getSupportActionBar();
        hidebarras();
        constrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidebarras();
            }
        });
        context = getApplicationContext();

        usb();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        pref = getSharedPreferences("DISPENSADORDEMO", Context.MODE_PRIVATE);
        numero = Integer.parseInt(pref.getString("NUMERO", "0"));
        numeromostrar.setText(""+numero);
        hidebarras();



    }

    void imcrementar(){

        if (numero<99){

            numero++;
            pref = getSharedPreferences("DISPENSADORDEMO", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("NUMERO", String.valueOf(numero));
            editor.apply();
            numeromostrar.setText(""+ numero);

        }else{

            numero = 0;
            pref = getSharedPreferences("DISPENSADORDEMO", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("NUMERO", String.valueOf(numero));
            editor.apply();
            numeromostrar.setText(""+ numero);
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

    private void imprimirNumero() {

        byte[] printData = {0};

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        animacion.setVisibility(View.VISIBLE);
        btnimprimir.setEnabled(false);

        Charset encoding = Charset.forName("CP437");

        byte[] nombreproducto= "Su Turno es: ".getBytes(encoding);
        byte[] numeroimprimir = (""+numero).getBytes();

        Bitmap starLogoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_dmrcirculo);

        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPos);
        builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
        builder.beginDocument();
        //builder.appendBitmap(starLogoImage, false);
       // builder.appendLineFeed();

        //*********************************
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendMultiple(1, 1);
        builder.appendAbsolutePosition(nombreproducto,0);
        builder.appendLineFeed();
        builder.appendLineSpace(50);
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendMultiple(10, 10);
        builder.appendAbsolutePosition(numeroimprimir,0);
        builder.appendLineFeed();
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);

        builder.appendMultiple(0, 0);
        builder.appendAbsolutePosition(("Fecha: " + fecha).getBytes(),0);
        builder.appendLineFeed();
        //**********************

        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        printData = builder.getCommands();


        try {

            if (connection!=null){
                int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                if (result != -1) {
                    imcrementar();
                } else {
                    Toast.makeText(DispensadorTurno.this, "ERROR A: imprimir", Toast.LENGTH_LONG).show();
                }
            }

            btnimprimir.setEnabled(true);
            animacion.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            Toast.makeText(DispensadorTurno.this, "ERROR B: imprimir", Toast.LENGTH_LONG).show();
            btnimprimir.setEnabled(true);
            animacion.setVisibility(View.INVISIBLE);
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
                    DispensadorTurno.this.registerReceiver(usbReceiver, filter);
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
                Toast.makeText(DispensadorTurno.this, "Conectado", Toast.LENGTH_SHORT).show();
            }else{
                impresoraactiva = false;
                Toast.makeText(DispensadorTurno.this, "ERROR C conectar", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception var2) {

            impresoraactiva = false;
            Toast.makeText(DispensadorTurno.this, "ERROR D conectar", Toast.LENGTH_SHORT).show();
        }



    }

    void close(){

        try {
            if (this.connection != null) {
                this.connection.releaseInterface(this.usbInterface);
                this.connection.close();
                this.connection = null;
            }

        } catch (Exception var2) {
            var2.printStackTrace();
        }


    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    DispensadorTurno.this.unregisterReceiver(usbReceiver);
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            Toast.makeText(DispensadorTurno.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                        permisosimpresora = true;
                        conectarImpresora(device);
                    } else {
                        Toast.makeText(DispensadorTurno.this, "Permiso no aceptado, OBLIGATORIO", Toast.LENGTH_LONG).show();
                        permisosimpresora = false;
                        finish();
                    }

                }
            }
        }
    };

    @Override
    public void onDestroy() {

        super.onDestroy();
    }




}