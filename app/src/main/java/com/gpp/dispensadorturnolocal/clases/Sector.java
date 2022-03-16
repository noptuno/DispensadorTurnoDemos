package com.gpp.dispensadorturnolocal.clases;

public class Sector {

    private Integer idSector;
    private String nombreSector;
    private int numeroSector;
    private int habilitadoSector;
    private String colorSector;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private String ip;

    public String toString() {
        return "Sector{" +
                "idsector=" +         idSector +
                ", nombre='" +     nombreSector + '\'' +
                ", numero='" +        numeroSector + '\'' +
                ", habilitado='" +        habilitadoSector + '\'' +
                ", color='" +        colorSector + '\'' +
                '}';
    }

    public int getHabilitadoSector() {
        return habilitadoSector;
    }

    public void setHabilitadoSector(int habilitadoSector) {
        this.habilitadoSector = habilitadoSector;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public String getNombreSector() {
        return nombreSector;
    }

    public void setNombreSector(String nombreSector) {
        this.nombreSector = nombreSector;
    }

    public int getNumeroSector() {
        return numeroSector;
    }

    public void setNumeroSector(int numeroSector) {
        this.numeroSector = numeroSector;
    }



    public String getColorSector() {
        return colorSector;
    }

    public void setColorSector(String colorSector) {
        this.colorSector = colorSector;
    }



    public static final int LIMIT_CANTIDAD = 100;
    public static final int RESET_CANTIDAD = 0;


    public void addCantidad(int numeroSector){

        if (this.numeroSector<LIMIT_CANTIDAD){
            this.numeroSector += numeroSector;
        }else{
            this.numeroSector = RESET_CANTIDAD;
        }
    }
}
