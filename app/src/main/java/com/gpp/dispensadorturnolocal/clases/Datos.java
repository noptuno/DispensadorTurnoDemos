package com.gpp.dispensadorturnolocal.clases;

public class Datos {

            int numeroactual;
            int ultimoNumero;
            int cantidadEspera;
            int limite;

    public Datos(int numeroactual, int ultimoNumero, int cantidadEspera, int limite) {
        this.numeroactual = numeroactual;
        this.ultimoNumero = ultimoNumero;
        this.cantidadEspera = cantidadEspera;
        this.limite = limite;
    }

    public Datos() {
    }

    public void sumar(){


        this.cantidadEspera++;

       // this.ultimoNumero = numeroactual+cantidadEspera;

    }

    public void restar(){

        this.numeroactual--;
        this.cantidadEspera++;
        this.ultimoNumero = numeroactual+cantidadEspera;

    }
    public void reset(){

        this.numeroactual=0;
        this.cantidadEspera=0;
        this.ultimoNumero = 0;

    }


    public int getNumeroactual() {
        return numeroactual;
    }

    public void setNumeroactual(int numeroactual) {
        this.numeroactual = numeroactual;
    }

    public int getUltimoNumero() {
        return ultimoNumero;
    }

    public void setUltimoNumero(int ultimoNumero) {
        this.ultimoNumero = ultimoNumero;
    }

    public int getCantidadEspera() {
        return cantidadEspera;
    }

    public void setCantidadEspera(int cantidadEspera) {
        this.cantidadEspera = cantidadEspera;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }
}
