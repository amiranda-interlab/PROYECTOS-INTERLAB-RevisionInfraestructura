package com.interlab.revision.infraestructura.bean;

import java.util.ArrayList;
import java.util.List;

public class Esquema {
    private String nombre;
    private List<Tabla> tablas;

    public Esquema(String nombre) {
        this.nombre = nombre;
        this.tablas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Tabla> getTablas() {
        return tablas;
    }

    public void setTablas(List<Tabla> tablas) {
        this.tablas = tablas;
    }
}
