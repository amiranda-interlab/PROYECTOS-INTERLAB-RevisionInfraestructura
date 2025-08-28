package com.interlab.revision.infraestructura.bean;

public class Columna {
    private String nombre;
    private String tipoDato;
    private boolean esNullable;

    public Columna(String nombre, String tipoDato, boolean esNullable) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.esNullable = esNullable;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public boolean isEsNullable() {
        return esNullable;
    }

    public void setEsNullable(boolean esNullable) {
        this.esNullable = esNullable;
    }
}
