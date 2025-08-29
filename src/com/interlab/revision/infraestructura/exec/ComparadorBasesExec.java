package com.interlab.revision.infraestructura.exec;

import com.interlab.revision.infraestructura.base.DB;
import com.interlab.revision.infraestructura.bean.Columna;
import com.interlab.revision.infraestructura.bean.Tabla;
import com.interlab.revision.infraestructura.bean.Esquema;
import com.interlab.revision.infraestructura.dao.ComparadorDAO;
import java.io.File;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

public class ComparadorBasesExec {

    public static void main(String[] args) {
        Connection con = null;

        String base1 = "Labcore";
        String base2 = "LabcoreMatriz";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Comparación de Bases");

        // Crear estilos
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setFontName("Arial");
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 11);
        bodyFont.setFontName("Arial");

        CellStyle bodyCellStyle = workbook.createCellStyle();
        bodyCellStyle.setFont(bodyFont);
        bodyCellStyle.setBorderBottom(BorderStyle.THIN);
        bodyCellStyle.setBorderTop(BorderStyle.THIN);
        bodyCellStyle.setBorderLeft(BorderStyle.THIN);
        bodyCellStyle.setBorderRight(BorderStyle.THIN);
        bodyCellStyle.setAlignment(HorizontalAlignment.LEFT);
        bodyCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        try {
            //con = DB.getConnectionDbInterERP();
            con = DB.getConnectionLacorePrueba();
            ComparadorDAO dao = new ComparadorDAO();

            // Obtener esquemas de ambas bases
            Esquema esquema1 = dao.obtenerEsquema(con, base1);
            Esquema esquema2 = dao.obtenerEsquema(con, base2);

            // Crear encabezado del Excel con estilo y mayúsculas
            int rowNum = 0;
            Row header = sheet.createRow(rowNum++);
            String[] titles = {"Base Comparada", "Tipo", "Nombre Objeto", "Observación"};

            for (int i = 0; i < titles.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(titles[i].toUpperCase());
                cell.setCellStyle(headerCellStyle);
            }

            // Tablas y columnas
            List<Tabla> tablas1 = esquema1.getTablas();
            List<Tabla> tablas2 = esquema2.getTablas();

            // Tablas exclusivas en base2
            for (Tabla t2 : tablas2) {
                boolean existeEn1 = tablas1.stream().anyMatch(t -> t.getNombre().equalsIgnoreCase(t2.getNombre()));
                if (!existeEn1) {
                    agregarFila(sheet, rowNum++, base2, "Tabla", t2.getNombre(),
                            "Tabla '" + t2.getNombre() + "' está en " + base2 + " pero no en " + base1, bodyCellStyle);
                }
            }

            // Tablas exclusivas en base1
            for (Tabla t1 : tablas1) {
                boolean existeEn2 = tablas2.stream().anyMatch(t -> t.getNombre().equalsIgnoreCase(t1.getNombre()));
                if (!existeEn2) {
                    agregarFila(sheet, rowNum++, base1, "Tabla", t1.getNombre(),
                            "Tabla '" + t1.getNombre() + "' está en " + base1 + " pero no en " + base2, bodyCellStyle);
                }
            }

            // Columnas exclusivas en base2
            for (Tabla t2 : tablas2) {
                Tabla t1 = tablas1.stream()
                        .filter(tb -> tb.getNombre().equalsIgnoreCase(t2.getNombre()))
                        .findFirst().orElse(null);
                if (t1 != null) {
                    for (Columna c2 : t2.getColumnas()) {
                        boolean existeEn1 = t1.getColumnas().stream()
                                .anyMatch(c -> c.getNombre().equalsIgnoreCase(c2.getNombre()));
                        if (!existeEn1) {
                            agregarFila(sheet, rowNum++, base2, "Columna", t2.getNombre() + "." + c2.getNombre(),
                                    "Columna está en " + base2 + " pero no en " + base1, bodyCellStyle);
                        }
                    }
                }
            }

            // Columnas exclusivas en base1
            for (Tabla t1 : tablas1) {
                Tabla t2 = tablas2.stream()
                        .filter(tb -> tb.getNombre().equalsIgnoreCase(t1.getNombre()))
                        .findFirst().orElse(null);
                if (t2 != null) {
                    for (Columna c1 : t1.getColumnas()) {
                        boolean existeEn2 = t2.getColumnas().stream()
                                .anyMatch(c -> c.getNombre().equalsIgnoreCase(c1.getNombre()));
                        if (!existeEn2) {
                            agregarFila(sheet, rowNum++, base1, "Columna", t1.getNombre() + "." + c1.getNombre(),
                                    "Columna está en " + base1 + " pero no en " + base2, bodyCellStyle);
                        }
                    }
                }
            }

            // Comparaciones para otros objetos (Procedimientos, Vistas, Funciones, Triggers, Synonyms)
            // Stored Procedure
            rowNum = compararObjetosGenericos(sheet, dao.obtenerStoredProcedures(con, base1),
                    dao.obtenerStoredProcedures(con, base2), base1, base2, "Stored Procedure", sheet, rowNum, bodyCellStyle);

            // Vistas
            rowNum = compararObjetosGenericos(sheet, dao.obtenerVistas(con, base1),
                    dao.obtenerVistas(con, base2), base1, base2, "Vista", sheet, rowNum, bodyCellStyle);

            // Funciones
            rowNum = compararObjetosGenericos(sheet, dao.obtenerFunciones(con, base1),
                    dao.obtenerFunciones(con, base2), base1, base2, "Función", sheet, rowNum, bodyCellStyle);

            // Triggers
            rowNum = compararObjetosGenericos(sheet, dao.obtenerTriggers(con, base1),
                    dao.obtenerTriggers(con, base2), base1, base2, "Trigger", sheet, rowNum, bodyCellStyle);

            // Synonyms
            rowNum = compararObjetosGenericos(sheet, dao.obtenerSynonyms(con, base1),
                    dao.obtenerSynonyms(con, base2), base1, base2, "Synonym", sheet, rowNum, bodyCellStyle);

            // Usuarios
            rowNum = compararObjetosGenericos(sheet,
                    dao.obtenerUsuarios(con, base1),
                    dao.obtenerUsuarios(con, base2),
                    base1, base2, "Usuario", sheet, rowNum, bodyCellStyle);

            // Roles
            rowNum = compararObjetosGenericos(sheet,
                    dao.obtenerRoles(con, base1),
                    dao.obtenerRoles(con, base2),
                    base1, base2, "Rol", sheet, rowNum, bodyCellStyle);

            // Permisos
            rowNum = compararObjetosGenericos(sheet,
                    dao.obtenerPermisos(con, base1),
                    dao.obtenerPermisos(con, base2),
                    base1, base2, "Permiso", sheet, rowNum, bodyCellStyle);

            // Inicios de sesión
            // OJO: logins se comparan a nivel de servidor, no por base
            rowNum = compararObjetosGenericos(sheet,
                    dao.obtenerLogins(con),
                    dao.obtenerLogins(con),
                    "Servidor", "Servidor", "Login", sheet, rowNum, bodyCellStyle);

            // Ajustar ancho de columnas automáticamente
            for (int i = 0; i < titles.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardar archivo Excel en ruta específica
            String rutaArchivo = "C:\\ArchivoComparacion\\ComparacionBases.xlsx";
            File directorio = new File("C:\\ArchivoComparacion");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
                System.out.println("*** Archivo Excel generado correctamente en: " + rutaArchivo + " ***");
            }

        } catch (Exception e) {
            System.err.println("Error en la comparación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.safeClose(con);
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void agregarFila(Sheet sheet, int rowNum, String base, String tipo, String nombre, String observacion, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(base);
        row.createCell(1).setCellValue(tipo);
        row.createCell(2).setCellValue(nombre);
        row.createCell(3).setCellValue(observacion);
        for (int i = 0; i < 4; i++) {
            row.getCell(i).setCellStyle(style);
        }
        // Log en consola
        System.out.println("[LOG] Base: " + base + " | Tipo: " + tipo + " | Objeto: " + nombre + " | Observación: " + observacion);
    }

    private static int compararObjetosGenericos(Sheet sheet, List<String> lista1, List<String> lista2,
            String base1, String base2, String tipo,
            Sheet hoja, int rowNum, CellStyle style) {
        for (String obj : lista2) {
            if (!lista1.contains(obj)) {
                agregarFila(hoja, rowNum++, base2, tipo, obj, tipo + " está en " + base2 + " pero no en " + base1, style);
            }
        }
        for (String obj : lista1) {
            if (!lista2.contains(obj)) {
                agregarFila(hoja, rowNum++, base1, tipo, obj, tipo + " está en " + base1 + " pero no en " + base2, style);
            }
        }
        return rowNum;
    }
}
