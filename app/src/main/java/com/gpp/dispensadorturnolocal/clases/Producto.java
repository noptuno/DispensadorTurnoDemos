package com.gpp.dispensadorturnolocal.clases;

public class Producto {

    private Integer idProducto;
    private String descripcion;
    private Double precio;
    private int cantidad;

    public static final int LIMIT_CANTIDAD = 50;
    public static final int RESET_CANTIDAD = 0;



    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Producto() {
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public Producto( String descripcion, Double precio) {
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String toString() {
        return "Producto{" +
                "idproducto=" +         idProducto +
                ", descripcion='" +     descripcion + '\'' +
                ", pesofijo='" +        precio + '\'' +
                '}';
    }


    public void addCantidad(int cantidad){

        if (this.cantidad<LIMIT_CANTIDAD){
            this.cantidad += cantidad;
        }


    }

    public void ressCantidad(int cantidad){

        if (this.cantidad>0){
            this.cantidad -= cantidad;
        }


    }

}
