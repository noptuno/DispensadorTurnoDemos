package com.gpp.devoluciondeenvases.basededatos;

public class ConstantsDB {
    //General
    public static final String DB_NAME = "dbgeneral2.db";
    public static final int DB_VERSION = 2;



    //TABLAPRODUCTO

    public static final String TABLA_PRODUCTO = "producto";
    public static final String PRO_IDPRODUCTO = "_idproducto";
    public static final String PRO_DESCRIPCION = "descripcion";
    public static final String PRO_PRECIO = "precio";
    public static final String PRO_CANTIDAD = "cantidad";


    public static final String TABLA_PRODUCTO_SQL =
            "CREATE TABLE  " + TABLA_PRODUCTO + "(" +
                    PRO_IDPRODUCTO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PRO_DESCRIPCION + " TEXT," +
                    PRO_PRECIO + " DOUBLE," +
                    PRO_CANTIDAD  + " INTEGER);" ;

    public static final String TABLA_SECTOR = "sector";
    public static final String SEC_ID = "_idsector";
    public static final String SEC_NOMBRE = "nombreSector";
    public static final String SEC_NUMERO = "numeroSector";
    public static final String SEC_HABILITADO = "habilitadoSector";
    public static final String SEC_COLOR = "colorSector";
    public static final String SEC_IP = "ip";

    public static final String TABLA_SECTOR_SQL =
            "CREATE TABLE  " + TABLA_SECTOR + "(" +
                    SEC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SEC_NOMBRE + " TEXT," +
                    SEC_NUMERO + " INTEGER," +
                    SEC_HABILITADO + " INTEGER," +
                    SEC_COLOR + " TEXT," +
                    SEC_IP  + " TEXT);" ;



}
