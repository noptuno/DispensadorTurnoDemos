package com.gpp.devoluciondeenvases.basededatos;

public class ConstantsDB {
    //General
    public static final String DB_NAME = "dbdevolucion6.db";
    public static final int DB_VERSION = 6;



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

//TABLASecuencia



}
