package com.interlab.revision.infraestructura.dao;

import com.interlab.revision.infraestructura.bean.Columna;
import com.interlab.revision.infraestructura.bean.Tabla;
import com.interlab.revision.infraestructura.bean.Esquema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ComparadorDAO {

    public Esquema obtenerEsquema(Connection conn, String nombreBase) throws Exception {
        Esquema esquema = new Esquema(nombreBase);

        // Obtener tablas
        String queryTablas = "SELECT TABLE_NAME FROM " + nombreBase + ".INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'";
        try (PreparedStatement stmt = conn.prepareStatement(queryTablas)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombreTabla = rs.getString("TABLE_NAME");
                Tabla tabla = new Tabla(nombreTabla);
                esquema.getTablas().add(tabla);
            }
        }

        // Obtener columnas por cada tabla
        for (Tabla tabla : esquema.getTablas()) {
            String queryColumnas = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE " +
                                   "FROM " + nombreBase + ".INFORMATION_SCHEMA.COLUMNS " +
                                   "WHERE TABLE_NAME = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryColumnas)) {
                stmt.setString(1, tabla.getNombre());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String nombreColumna = rs.getString("COLUMN_NAME");
                    String tipoDato = rs.getString("DATA_TYPE");
                    boolean esNullable = rs.getString("IS_NULLABLE").equalsIgnoreCase("YES");

                    Columna columna = new Columna(nombreColumna, tipoDato, esNullable);
                    tabla.getColumnas().add(columna);
                }
            }
        }

        return esquema;
    }
}
