package com.interlab.revision.infraestructura.bean;

public class RevisionInfra {

    private String codigoDb;
    private String codigoMotor;
    private String nombreMotorBase;
    private String ipMotorBase;
    private String nombreBase;
    private String rutaRespaldoBak;
    private int estado;
    private boolean existeBackup; 

    // Getters y Setters
    public String getCodigoDb() {
        return codigoDb;
    }

    public void setCodigoDb(String codigoDb) {
        this.codigoDb = codigoDb;
    }

    public String getCodigoMotor() {
        return codigoMotor;
    }

    public void setCodigoMotor(String codigoMotor) {
        this.codigoMotor = codigoMotor;
    }

    public String getNombreMotorBase() {
        return nombreMotorBase;
    }

    public void setNombreMotorBase(String nombreMotorBase) {
        this.nombreMotorBase = nombreMotorBase;
    }

    public String getIpMotorBase() {
        return ipMotorBase;
    }

    public void setIpMotorBase(String ipMotorBase) {
        this.ipMotorBase = ipMotorBase;
    }

    public String getNombreBase() {
        return nombreBase;
    }

    public void setNombreBase(String nombreBase) {
        this.nombreBase = nombreBase;
    }

    public String getRutaRespaldoBak() {
        return rutaRespaldoBak;
    }

    public void setRutaRespaldoBak(String rutaRespaldoBak) {
        this.rutaRespaldoBak = rutaRespaldoBak;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public boolean isExisteBackup() {
        return existeBackup;
    }

    public void setExisteBackup(boolean existeBackup) {
        this.existeBackup = existeBackup;
    }
}
