package com.interlab.revision.infraestructura.bean;

import java.util.ArrayList;
import java.util.List;

public class Tabla {
    private String nombre;
    private List<Columna> columnas;

    public Tabla(String nombre) {
        this.nombre = nombre;
        this.columnas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Columna> getColumnas() {
        return columnas;
    }

    public void setColumnas(List<Columna> columnas) {
        this.columnas = columnas;
    }
}
