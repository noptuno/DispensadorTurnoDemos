package com.gpp.dispensadorturnolocal.clases;

import java.io.Serializable;

public class Config implements Serializable {

    private static final long serialVersionUID = 476212312412L;
    private String pathimagen;
    private int tipopael;
    private String otros;



    public Config(String pathimagen, int tipopael, String otros) {
        this.pathimagen = pathimagen;
        this.tipopael = tipopael;
        this.otros = otros;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPathimagen() {
        return pathimagen;
    }

    public void setPathimagen(String pathimagen) {
        this.pathimagen = pathimagen;
    }

    public int getTipopael() {
        return tipopael;
    }

    public void setTipopael(int tipopael) {
        this.tipopael = tipopael;
    }

    public String getOtros() {
        return otros;
    }

    public void setOtros(String otros) {
        this.otros = otros;
    }
}
