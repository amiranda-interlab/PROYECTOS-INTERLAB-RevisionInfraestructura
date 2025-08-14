/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interlab.revision.infraestructura.bean;

/**
 *
 * @author User
 */
public class Proparametro {
    
    private long parid;
    private int parmodulo;
    private String pardescripcion;
    private String parclave;
    private String parvalor;
    
    public Proparametro(){}
    public Proparametro(String parclave){
        this.parclave = parclave;
    }

    /**
     * @return the parid
     */
    public long getParid() {
        return parid;
    }

    /**
     * @param parid the parid to set
     */
    public void setParid(long parid) {
        this.parid = parid;
    }

    /**
     * @return the parmodulo
     */
    public int getParmodulo() {
        return parmodulo;
    }

    /**
     * @param parmodulo the parmodulo to set
     */
    public void setParmodulo(int parmodulo) {
        this.parmodulo = parmodulo;
    }

    /**
     * @return the pardescripcion
     */
    public String getPardescripcion() {
        return pardescripcion;
    }

    /**
     * @param pardescripcion the pardescripcion to set
     */
    public void setPardescripcion(String pardescripcion) {
        this.pardescripcion = pardescripcion;
    }

    /**
     * @return the parclave
     */
    public String getParclave() {
        return parclave;
    }

    /**
     * @param parclave the parclave to set
     */
    public void setParclave(String parclave) {
        this.parclave = parclave;
    }

    /**
     * @return the parvalor
     */
    public String getParvalor() {
        return parvalor;
    }

    /**
     * @param parvalor the parvalor to set
     */
    public void setParvalor(String parvalor) {
        this.parvalor = parvalor;
    }
    
    
}
