package com.interlab.revision.infraestructura.dao;

import com.interlab.revision.infraestructura.bean.Columna;
import com.interlab.revision.infraestructura.bean.Tabla;
import com.interlab.revision.infraestructura.bean.Esquema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ComparadorDAO {

    public Esquema obtenerEsquema(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase); // Establece base activa
        Esquema esquema = new Esquema(nombreBase);

        String queryTablas = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'";
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
            String queryColumnas = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE "
                    + "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";
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

    public List<String> obtenerStoredProcedures(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> procedures = new ArrayList<>();
        String query = "SELECT name FROM sys.procedures";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                procedures.add(rs.getString("name"));
            }
        }
        return procedures;
    }

    public List<String> obtenerVistas(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> vistas = new ArrayList<>();
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vistas.add(rs.getString("TABLE_NAME"));
            }
        }
        return vistas;
    }

    public List<String> obtenerFunciones(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> funciones = new ArrayList<>();
        String query = "SELECT name FROM sys.objects WHERE type IN ('FN', 'IF', 'TF')";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                funciones.add(rs.getString("name"));
            }
        }
        return funciones;
    }

    public List<String> obtenerTriggers(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> triggers = new ArrayList<>();
        String query = "SELECT name FROM sys.triggers WHERE parent_id > 0";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                triggers.add(rs.getString("name"));
            }
        }
        return triggers;
    }

    public List<String> obtenerSynonyms(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> synonyms = new ArrayList<>();
        String query = "SELECT name FROM sys.synonyms";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                synonyms.add(rs.getString("name"));
            }
        }
        return synonyms;
    }

    public List<String> obtenerUsuarios(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> usuarios = new ArrayList<>();
        String query = "SELECT name FROM sys.database_principals WHERE type = 'S' AND name NOT IN ('dbo', 'guest', 'INFORMATION_SCHEMA', 'sys')";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(rs.getString("name"));
            }
        }
        return usuarios;
    }

    public List<String> obtenerRoles(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> roles = new ArrayList<>();
        String query = "SELECT name FROM sys.database_principals WHERE type = 'R' AND name NOT IN ('public')";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("name"));
            }
        }
        return roles;
    }

    public List<String> obtenerPermisos(Connection conn, String nombreBase) throws Exception {
        conn.setCatalog(nombreBase);
        List<String> permisos = new ArrayList<>();
        String query = "SELECT dp.name AS principal_name, perm.permission_name, perm.class_desc, perm.state_desc "
                + "FROM sys.database_permissions perm "
                + "JOIN sys.database_principals dp ON perm.grantee_principal_id = dp.principal_id";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String linea = rs.getString("principal_name") + " - "
                        + rs.getString("permission_name") + " - "
                        + rs.getString("class_desc") + " - "
                        + rs.getString("state_desc");
                permisos.add(linea);
            }
        }
        return permisos;
    }

    public List<String> obtenerLogins(Connection conn) throws Exception {
        List<String> logins = new ArrayList<>();
        String query = "SELECT name FROM sys.server_principals WHERE type_desc = 'SQL_LOGIN' AND name NOT LIKE '##%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logins.add(rs.getString("name"));
            }
        }
        return logins;
    }

}
