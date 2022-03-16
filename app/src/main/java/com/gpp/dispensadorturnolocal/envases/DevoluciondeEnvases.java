package com.gpp.dispensadorturnolocal.envases;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gpp.dispensadorturnolocal.R;
import com.gpp.dispensadorturnolocal.adapter.MyAdapter;
import com.gpp.dispensadorturnolocal.basededatos.ProductoDB;
import com.gpp.dispensadorturnolocal.clases.Producto;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class DevoluciondeEnvases extends AppCompatActivity {
    private ProductoDB db;
    private RecyclerView.Adapter mAdapter;
    private List<Producto> Listproductos;
    private RecyclerView mRecyclerview;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView txttotal;
    double totalactual = 0.00;
    private double totalsumado;
    int secuencial;
    SharedPreferences preferencesecuencial;

    private boolean isReceiverRegistered = false;
    private Thread readThread;
    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private UsbDevice device;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private boolean impresora = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devolucionde_envases);

        txttotal = findViewById(R.id.txttotal);

        mRecyclerview = findViewById(R.id.recyclerView01);
        mLayoutManager = new LinearLayoutManager(this);

        preferencesecuencial = getSharedPreferences("BASE", Context.MODE_PRIVATE);
        secuencial = preferencesecuencial.getInt("secuancia", 1);

        cargaradapter();


        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();


        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e("Dispositivos", device.getDeviceName() + " + " + device.getVendorId() + " + " + device.getProductId());
            if (device.getVendorId() == 1155  && device.getProductId() == 22304) {

                usbManager.requestPermission(device, mPermissionIntent);
                usbInterface = device.getInterface(0);
                connection = usbManager.openDevice(device);
                connection.claimInterface(usbInterface, false);

                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    UsbEndpoint end = usbInterface.getEndpoint(i);
                    if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                        usbEndpointIn = end;
                    } else {
                        usbEndpointOut = end;
                    }
                }
            }
        }
    }



    private void cargaradapter(){
        Listproductos = this.cargarLista();
        mAdapter = new MyAdapter(Listproductos, R.layout.item_note_devolucion, this, new MyAdapter.OnclickListener() {
            @Override
            public void OnitemClick(Producto productos, int position) {

                productos.addCantidad(1);
                double precioproducto = productos.getPrecio();
                totalactual = totalactual + precioproducto;

                BigDecimal bd = new BigDecimal(totalactual);
                bd = bd.setScale(4, RoundingMode.HALF_UP);

                txttotal.setText("" + bd.doubleValue());
                mAdapter.notifyItemChanged(position);

            }

            @Override
            public void OnitemClickrestar(Producto productos, int position) {

                double precioproducto = productos.getPrecio();
                productos.ressCantidad(1);

                totalactual = totalactual - precioproducto;
                BigDecimal bd = new BigDecimal(totalactual);
                bd = bd.setScale(4, RoundingMode.HALF_UP);

                txttotal.setText("" + bd.doubleValue());
                mAdapter.notifyItemChanged(position);

            }
        });
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setAdapter(mAdapter);

    }


    @Override
    public void onDestroy() {
        unregisterReceiver(usbReceiver);
        if (connection != null) {
            if (usbInterface != null) {
                connection.releaseInterface(usbInterface);
                usbInterface = null;
                usbEndpointIn = null;
                usbEndpointOut = null;

                Log.e("ERROR", "se limpio A");
            }
            connection.close();
            usbInterface = null;
            connection = null;
            Log.e("ERROR", "se limpio B");
        }
        super.onDestroy();
    }


    public void Preparardatosaimprimir(View v) {

        //crear codigobarra


            String codigo = "";
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
            String formattedDate = df.format(c);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String date = df2.format(c);

            if (totalactual<99999){
                String precio = txttotal.getText().toString();
                if (precio.length() > 2) {

                    int intNumber = Integer.parseInt(precio.substring(0, precio.indexOf('.')));
                    int decNumberInt = Integer.parseInt(precio.substring(precio.indexOf('.') + 1));

                    Formatter fmt = new Formatter();
                    fmt.format("%05d", intNumber);

                    Formatter fmt2 = new Formatter();
                    fmt2.format("%02d", decNumberInt);


                   secuencial = preferencesecuencial.getInt("secuancia", 1);
                    Formatter fmtsecuencia = new Formatter();
                    fmtsecuencia.format("%04d", secuencial);


                    String tipovaucher = "01";                      //TT debe tener el valor 01 fijo
                    String idinterno = fmtsecuencia.toString();    //C   id interno secuencial. Cada voucher debe tener un nro diferente. Cuando llega al máximo (9999) comienza en 1 nuevamente
                    String montoentero = fmt.toString();            //M=Monto entero del valor del envase
                    String montodecimal = fmt2.toString();               //D=Monto decimal del valor del envase
                    String fecha = formattedDate;                      //F=Fecha en formato DDMMAAAA
                    codigo = tipovaucher + idinterno + montoentero + montodecimal + fecha;
                    System.out.println(codigo);

                    if (txttotal.getText().toString().length() > 0) {

                        preparaescpos(codigo, date, precio);

                    }
                    // TT CCCC MMMMM DD FFFFFFFF
                } else {
                    Toast.makeText(DevoluciondeEnvases.this, "Debe seleccionar Productos", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(DevoluciondeEnvases.this, "Ha Excedido el limite del Monto total en pesos", Toast.LENGTH_LONG).show();
            }








    }


    private void reiniciar() {

        totalactual = 0.00;
        txttotal.setText(""+totalactual);
        cargaradapter();
    }


    public void regresar(View v) {
        finish();
    }


    private void preparaescpos(String codigo, String fecha, String precio) {

        byte[] printData = {0};
        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPos);
        Charset encoding;
        encoding = StandardCharsets.UTF_8;
        builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.append((
                "Devolucion de Envases \n" +
                        "Distribuidora de la Costa\n" +
                        " Fecha: " + fecha + "\n" +
                        "\n").getBytes(encoding));
        builder.appendLineFeed();
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendBarcode(codigo.getBytes(), ICommandBuilder.BarcodeSymbology.Code93, ICommandBuilder.BarcodeWidth.Mode1, 80, false);
        builder.append(("\n").getBytes(encoding));
        builder.append((codigo).getBytes(encoding));
        builder.appendLineFeed();
        builder.append((
                "Total en $: " + precio + "\n").getBytes(encoding));
        builder.appendLineFeed();
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        printData = builder.getCommands();

        imprimirusb(printData);


    }

    private void imprimirusb(final byte[] printData) {
        if (impresora) {

            AlertDialog.Builder build = new AlertDialog.Builder(DevoluciondeEnvases.this);
            build.setMessage("Realmente Desea Imprimir?").setPositiveButton("Si - Imprimir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {

                        int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                        Log.e("SEND RESULT", result + "");
                        if (result != -1) {
                            SharedPreferences.Editor editor = preferencesecuencial.edit();

                            if (secuencial <= 9999) {
                                editor.putInt("secuancia", secuencial + 1);
                                editor.apply();

                            } else {
                                editor.putInt("secuancia", 1);
                                editor.apply();
                            }
                            reiniciar();
                        } else {

                                Toast.makeText(DevoluciondeEnvases.this, "No se reconoce la impresora, Reinicie la app", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        Toast.makeText(DevoluciondeEnvases.this, "Hubo un error al imprimir", Toast.LENGTH_LONG).show();
                        Log.e("error", "mensajed");
                    }

                }


            }).setNegativeButton("No - Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            AlertDialog alertDialog = build.create();
            alertDialog.show();

        } else {

            Toast.makeText(DevoluciondeEnvases.this, "No hay impresora", Toast.LENGTH_LONG).show();
        }


    }


    private List<Producto> cargarLista() {

        ArrayList<Producto> list = null;
        try {
            db = new ProductoDB(this);
            list = db.loadProducto();

        } catch (Exception e) {
            list = null;
            Log.e("error", "mensajed");
        }
        return list;
    }

    private void limpiar() {


    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();



            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.e("ERROR", "SE LLAMO DESDE BROADCAST");
                            Toast.makeText(DevoluciondeEnvases.this, "Dispositivo Habilitado", Toast.LENGTH_LONG).show();
                            impresora = true;
                        }
                    } else {
                        Toast.makeText(DevoluciondeEnvases.this, "Dispositivo Deshabilitado", Toast.LENGTH_LONG).show();
                        Log.d("TAG", "permission denied for device " + device);
                        impresora = false;
                    }

                }
            }
        }
    };

}