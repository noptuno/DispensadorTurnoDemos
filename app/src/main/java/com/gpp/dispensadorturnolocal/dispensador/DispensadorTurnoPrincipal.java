package com.gpp.dispensadorturnolocal.dispensador;

import static com.gpp.dispensadorturnolocal.dispensador.FileBrowseActivity.calculateInSampleSize;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gpp.dispensadorturnolocal.R;
import com.gpp.dispensadorturnolocal.adapter.AdapterDispensador;
import com.gpp.dispensadorturnolocal.basededatos.SectorDB;
import com.gpp.dispensadorturnolocal.clases.Config;
import com.gpp.dispensadorturnolocal.clases.Sector;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
private Button configurarnuevamente;
    MediaPlayer click, click2;
    Context contexto;
    static final int MENSAJERESULT = 0;
    private SectorDB db;
    ConstraintLayout constrain;
    ActionBar actionBar;
    ImageView logoempresa;
    int tipopael= 0;
    Bitmap logoempresabitmap;

    private AdapterDispensador adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensador_turno_principal);
        constrain = findViewById(R.id.constrainturnodos);
        context = getApplicationContext();
        contexto = this;
        click = MediaPlayer.create(contexto, R.raw.fin);
        click2 = MediaPlayer.create(contexto, R.raw.ckickk);
        logoempresa = findViewById(R.id.fondoarriba);

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

        cargarConfiguraion();
        adapter.setOnNoteSelectedListener(new AdapterDispensador.OnNoteSelectedListener() {
            @Override
            public void onClick(Sector note) {
                if (permisosimpresora){

                    if (impresoraactiva){

                        click2.start();
                        mostrarEspera(note);
                        imprimirNumero(note);

                    }else{
                        Toast.makeText(DispensadorTurnoPrincipal.this, "No se ha conectado la impresora (recinicie la app)", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(DispensadorTurnoPrincipal.this, "Debe aceptar permisos", Toast.LENGTH_LONG).show();
                }

            }
        });


        configurarnuevamente = findViewById(R.id.btn_salir);

        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                botonregresar();


            }
        });


    }

    //código
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }




    @Override
    public void onBackPressed() {

        //botonregresar();
        // super.onBackPressed();

    }

    private void cargarConfiguraion() {

        Config appSettings = ReadApplicationSettingFromFile();
        if (appSettings != null){

            cargarimagen(appSettings.getPathimagen());
            tipopael = appSettings.getTipopael();
        }
    }

    private void cargarimagen(String pathimagen) {

        Bitmap bitmaptemp = decodeSampledBitmapFromFile(pathimagen, 150, 150);

        if (bitmaptemp!=null){


            logoempresa.setImageBitmap(bitmaptemp);
            byte[] bitmapData = convertTo1BPP(bitmaptemp, 128);
            logoempresabitmap= BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);


        }else{

            logoempresabitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_dmr);
            logoempresa.setImageBitmap(logoempresabitmap);

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

    private void botonregresar() {

        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
        View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DispensadorTurnoPrincipal.this);

        alertDialogBuilder.setView(promptUserView);

        final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);

        alertDialogBuilder.setTitle("Usuario Administrador: ");

        // prompt for username
        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // and display the username on main activity layout


                if (!userAnswer.equals("") && userAnswer.getText().length()>0){

                    if (validaryguardar(userAnswer.getText().toString())){

                        DispensadorTurnoPrincipal.this.finish();

                    }else{

                        Toast.makeText(getApplicationContext(), "Contraseña Incorrecta", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        // all set and time to build and show up!
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        userAnswer.requestFocus();


    }

    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals("dmr")){
            v = true;
        }

        return v;
    }
    private void imprimirNumero(final Sector sector) {

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                try {
                    byte[] printData = {0};
                    String nombreSector = sector.getNombreSector();
                    int numeroactual = sector.getNumeroSector();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
                    Date date = new Date();
                    String fecha = dateFormat.format(date);

                    //animacion.setVisibility(View.VISIBLE);
                    //btnimprimir.setEnabled(false);

                    Charset encoding = Charset.forName("CP437");


                    byte[] sectorname ;
                    byte[] nombreproducto ;
                    byte[] numeroimprimir ;
                    byte[] fechaimprimir;

                    ICommandBuilder.AlignmentPosition  align;

                    if (tipopael==0){
                        sectorname = (" "+nombreSector).getBytes();
                        nombreproducto = "   Su Turno es: ".getBytes(encoding);
                        if (numeroactual>9){
                            numeroimprimir = (" " + numeroactual).getBytes();
                        }else{
                            numeroimprimir = (" " + "0"+numeroactual).getBytes();
                        }

                        fechaimprimir = ("   Fecha: " + fecha).getBytes();
                        align =  ICommandBuilder.AlignmentPosition.Left;
                    }else{
                        sectorname = nombreSector.getBytes();
                        nombreproducto = "Su Turno es: ".getBytes(encoding);

                        if (numeroactual>9){
                            numeroimprimir = (""+numeroactual).getBytes();
                        }else{
                            numeroimprimir = ("0"+numeroactual).getBytes();
                        }

                        fechaimprimir = ("Fecha: " + fecha).getBytes();
                        align =  ICommandBuilder.AlignmentPosition.Center;
                    }



                  //  Bitmap starLogoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.logodiscopeque);

                    ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPos);


                    builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
                    builder.beginDocument();
                    builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
                    builder.appendBitmap(logoempresabitmap, false);
                    builder.appendLineFeed();

                    //*********************************
                    builder.appendAlignment(align);
                    builder.appendMultiple(3, 3);
                    builder.appendAbsolutePosition(sectorname, 0);
                    builder.appendLineFeed(1);

                    builder.appendAlignment(align);
                    builder.appendMultiple(1, 1);
                    builder.appendAbsolutePosition(nombreproducto, 0);
                    builder.appendLineFeed();
                    builder.appendLineSpace(50);
                    builder.appendAlignment(align);
                    builder.appendMultiple(10, 10);
                    builder.appendAbsolutePosition(numeroimprimir, 0);
                    builder.appendLineFeed();
                    builder.appendAlignment(align);

                    builder.appendMultiple(0, 0);
                    builder.appendAbsolutePosition((fechaimprimir), 0);
                    builder.appendLineFeed();
                    //**********************

                    builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
                    builder.endDocument();
                    printData = builder.getCommands();

                    if (connection != null) {
                        int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                        if (result != -1) {

                            int numero = imcrementar(numeroactual);
                            sector.setNumeroSector(numero);

                            if (actualziar(sector)){

                                cargarLista();

                            }

                        } else {
                            Toast.makeText(DispensadorTurnoPrincipal.this, "Impresora no conectada", Toast.LENGTH_LONG).show();
                            usb();
                        }
                    }


                } catch (Exception e) {
                    Toast.makeText(DispensadorTurnoPrincipal.this, "ERROR impresión de archivos", Toast.LENGTH_LONG).show();

                }

            }

        }, 1500);

    }


    private void mostrarEspera(Sector nota) {
        Intent v = new Intent(DispensadorTurnoPrincipal.this, MensajeActivity.class);
        v.putExtra("numeroSector", nota.getNumeroSector());
        v.putExtra("nombreSector", nota.getNombreSector());
        v.putExtra("colorSector", nota.getColorSector());
        startActivityForResult(v, MENSAJERESULT);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case MENSAJERESULT: {
                if (resultCode == RESULT_OK) {

                    Log.e("RECIBIDO", "OK");
                }
                break;
            }
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
                Toast.makeText(DispensadorTurnoPrincipal.this, "Impresora Conectada", Toast.LENGTH_SHORT).show();
            }else{

                impresoraactiva = false;
                Toast.makeText(DispensadorTurnoPrincipal.this, "No Impresora Conectada", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception var2) {

            impresoraactiva = false;
            Toast.makeText(DispensadorTurnoPrincipal.this, "Error USB conexión", Toast.LENGTH_SHORT).show();
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
      // hidebarras();

    }
    String ApplicationConfigFilename = "configuraciondispensador.dat";
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


}
