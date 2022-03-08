package com.gpp.devoluciondeenvases.basededatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gpp.devoluciondeenvases.clases.Producto;
import com.gpp.devoluciondeenvases.clases.Sector;

import java.util.ArrayList;

public class SectorDB {
    private SQLiteDatabase db;
    private SectorDB.DBHelper dbHelper;

    public SectorDB(Context context) {
        dbHelper = new SectorDB.DBHelper(context);
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


    private ContentValues clienteMapperContentValues(Sector sectores) {

        ContentValues cv = new ContentValues();
        cv.put(ConstantsDB.SEC_ID, sectores.getIdSector());
        cv.put(ConstantsDB.SEC_NOMBRE, sectores.getNombreSector());
        cv.put(ConstantsDB.SEC_NUMERO, sectores.getNumeroSector());
        cv.put(ConstantsDB.SEC_HABILITADO, sectores.getHabilitadoSector());
        cv.put(ConstantsDB.SEC_COLOR, sectores.getColorSector());
        cv.put(ConstantsDB.SEC_IP   , sectores.getIp());
        return cv;
    }

    public long insertarSector(Sector sector) {
        this.openWriteableDB();
        long rowID = db.insert(ConstantsDB.TABLA_SECTOR, null, clienteMapperContentValues(sector));
        this.closeDB();
        return rowID;
    }

    public void eliminarSector(int codigo) {
        this.openWriteableDB();
        String where = ConstantsDB.SEC_ID + "= ?";
        db.delete(ConstantsDB.TABLA_SECTOR, where, new String[]{String.valueOf(codigo)});
        this.closeDB();
    }


    public void updateSector(Sector sector) {

        this.openWriteableDB();
        String where = ConstantsDB.SEC_ID + "= ?";
        db.update(ConstantsDB.TABLA_SECTOR, clienteMapperContentValues(sector), where, new String[]{String.valueOf(sector.getIdSector())});
        db.close();
    }




    public ArrayList loadSector() {

        ArrayList<Sector> list = new ArrayList<>();
        this.openReadableDB();
        String[] campos = new String[]{ConstantsDB.SEC_ID, ConstantsDB.SEC_NOMBRE, ConstantsDB.SEC_NUMERO,ConstantsDB.SEC_COLOR,ConstantsDB.SEC_HABILITADO,ConstantsDB.SEC_IP};
        Cursor c = db.query(ConstantsDB.TABLA_SECTOR, campos, null, null, null, null, null);

        try {
            while (c.moveToNext()) {
                Sector sector = new Sector();
                sector.setIdSector(c.getInt(0));
                sector.setNombreSector(c.getString(1));
                sector.setNumeroSector(c.getInt(2));
                sector.setColorSector(c.getString(3));
                sector.setHabilitadoSector(c.getInt(4));
                sector.setIp(c.getString(5));
                list.add(sector);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }

    public ArrayList loadSectorDispensador() {

        ArrayList<Sector> list = new ArrayList<>();
        this.openReadableDB();
        String desc = ConstantsDB.SEC_NOMBRE + "= ?";
        String where = ConstantsDB.SEC_HABILITADO + "= ?";
        String[] campos = new String[]{ConstantsDB.SEC_ID, ConstantsDB.SEC_NOMBRE, ConstantsDB.SEC_NUMERO,ConstantsDB.SEC_COLOR,ConstantsDB.SEC_HABILITADO,ConstantsDB.SEC_IP};
        Cursor c = db.query(ConstantsDB.TABLA_SECTOR, campos, where, new String[]{String.valueOf(1)}, null, null, desc +" DESC LIMIT 3");

        try {
            while (c.moveToNext()) {
                Sector sector = new Sector();
                sector.setIdSector(c.getInt(0));
                sector.setNombreSector(c.getString(1));
                sector.setNumeroSector(c.getInt(2));
                sector.setColorSector(c.getString(3));
                sector.setHabilitadoSector(c.getInt(4));
                sector.setIp(c.getString(5));
                list.add(sector);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }



    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, ConstantsDB.DB_NAME, null, ConstantsDB.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(ConstantsDB.TABLA_SECTOR_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
