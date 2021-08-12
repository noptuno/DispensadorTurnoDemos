package com.gpp.devoluciondeenvases.basededatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.gpp.devoluciondeenvases.clases.Producto;

import java.util.ArrayList;

public class ProductoDB {
    private SQLiteDatabase db;
    private ProductoDB.DBHelper dbHelper;

    public ProductoDB(Context context) {
        dbHelper = new ProductoDB.DBHelper(context);
    }


    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if(db!=null){
            db.close();
        }
    }

    private ContentValues clienteMapperContentValues(Producto producto) {
        ContentValues cv = new ContentValues();

        cv.put(ConstantsDB.PRO_IDPRODUCTO, producto.getIdProducto());
        cv.put(ConstantsDB.PRO_DESCRIPCION, producto.getDescripcion());
        cv.put(ConstantsDB.PRO_PRECIO, producto.getPrecio());
        cv.put(ConstantsDB.PRO_CANTIDAD, producto.getCantidad());

        return cv;
    }




    public ArrayList loadProducto() {

        ArrayList<Producto> list = new ArrayList<>();
        this.openReadableDB();
        String[] campos = new String[]{ConstantsDB.PRO_IDPRODUCTO, ConstantsDB.PRO_DESCRIPCION, ConstantsDB.PRO_PRECIO,ConstantsDB.PRO_CANTIDAD};
        Cursor c = db.query(ConstantsDB.TABLA_PRODUCTO, campos, null, null, null, null, null);

        try {
            while (c.moveToNext()) {
                Producto producto = new Producto();
                producto.setIdProducto(c.getInt(0));
                producto.setDescripcion(c.getString(1));
                producto.setPrecio(c.getDouble(2));
                producto.setCantidad(c.getInt(3));
                list.add(producto);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }

    public long insertarProducto(Producto producto) {
        this.openWriteableDB();
        long rowID = db.insert(ConstantsDB.TABLA_PRODUCTO, null, clienteMapperContentValues(producto));
        this.closeDB();
        return rowID;
    }

    public void eliminarProducto(int codigoProducto) {
        this.openWriteableDB();
        String where = ConstantsDB.PRO_IDPRODUCTO + "= ?";
        db.delete(ConstantsDB.TABLA_PRODUCTO, where, new String[]{String.valueOf(codigoProducto)});
        this.closeDB();
    }

    public void eliminarAll() {
        this.openWriteableDB();
        db.delete(ConstantsDB.TABLA_PRODUCTO, null, null);
        this.closeDB();

    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, ConstantsDB.DB_NAME, null, ConstantsDB.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ConstantsDB.TABLA_PRODUCTO_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
