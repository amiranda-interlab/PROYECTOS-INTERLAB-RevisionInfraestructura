package com.interlab.revision.infraestructura.exec;

import com.interlab.revision.infraestructura.base.DBInterERP;
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
        Connection con_intererp = null;

        String base1 = "dbInterERP";
        String base2 = "dbInterERPPRU7";

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
            con_intererp = DBInterERP.getConnectionDbInterERP();

            ComparadorDAO dao = new ComparadorDAO();

            Esquema esquema1 = dao.obtenerEsquema(con_intererp, base1);
            Esquema esquema2 = dao.obtenerEsquema(con_intererp, base2);

            // Crear encabezado del Excel con estilo y mayúsculas
            int rowNum = 0;
            Row header = sheet.createRow(rowNum++);
            String[] titles = {"Base Comparada", "Tipo", "Nombre Objeto", "Observación"};

            for (int i = 0; i < titles.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(titles[i].toUpperCase());
                cell.setCellStyle(headerCellStyle);
            }

            List<Tabla> tablas1 = esquema1.getTablas();
            List<Tabla> tablas2 = esquema2.getTablas();

            // Tablas exclusivas en base2
            for (Tabla t2 : tablas2) {
                boolean existeEn1 = tablas1.stream()
                        .anyMatch(t -> t.getNombre().equalsIgnoreCase(t2.getNombre()));
                if (!existeEn1) {
                    String observacion = "Tabla '" + t2.getNombre() + "' está en " + base2 + " pero no en " + base1;
                    agregarFila(sheet, rowNum++, base2, "Tabla", t2.getNombre(), observacion, bodyCellStyle);
                }
            }

            // Tablas exclusivas en base1
            for (Tabla t1 : tablas1) {
                boolean existeEn2 = tablas2.stream()
                        .anyMatch(t -> t.getNombre().equalsIgnoreCase(t1.getNombre()));
                if (!existeEn2) {
                    String observacion = "Tabla '" + t1.getNombre() + "' está en " + base1 + " pero no en " + base2;
                    agregarFila(sheet, rowNum++, base1, "Tabla", t1.getNombre(), observacion, bodyCellStyle);
                }
            }

            // Columnas exclusivas en base2
            for (Tabla t2 : tablas2) {
                Tabla t1 = tablas1.stream()
                        .filter(tb -> tb.getNombre().equalsIgnoreCase(t2.getNombre()))
                        .findFirst()
                        .orElse(null);
                if (t1 != null) {
                    List<Columna> cols2 = t2.getColumnas();
                    List<Columna> cols1 = t1.getColumnas();

                    for (Columna c2 : cols2) {
                        boolean existeEn1 = cols1.stream()
                                .anyMatch(c -> c.getNombre().equalsIgnoreCase(c2.getNombre()));
                        if (!existeEn1) {
                            String nombreObjeto = t2.getNombre() + "." + c2.getNombre();
                            String observacion = "Columna '" + c2.getNombre() + "' en tabla '" + t2.getNombre() + "' está en " + base2 + " pero no en " + base1;
                            agregarFila(sheet, rowNum++, base2, "Columna", nombreObjeto, observacion, bodyCellStyle);
                        }
                    }
                }
            }

            // Columnas exclusivas en base1
            for (Tabla t1 : tablas1) {
                Tabla t2 = tablas2.stream()
                        .filter(tb -> tb.getNombre().equalsIgnoreCase(t1.getNombre()))
                        .findFirst()
                        .orElse(null);
                if (t2 != null) {
                    List<Columna> cols1 = t1.getColumnas();
                    List<Columna> cols2 = t2.getColumnas();

                    for (Columna c1 : cols1) {
                        boolean existeEn2 = cols2.stream()
                                .anyMatch(c -> c.getNombre().equalsIgnoreCase(c1.getNombre()));
                        if (!existeEn2) {
                            String nombreObjeto = t1.getNombre() + "." + c1.getNombre();
                            String observacion = "Columna '" + c1.getNombre() + "' en tabla '" + t1.getNombre() + "' está en " + base1 + " pero no en " + base2;
                            agregarFila(sheet, rowNum++, base1, "Columna", nombreObjeto, observacion, bodyCellStyle);
                        }
                    }
                }
            }

            // Ajustar el tamaño de las columnas automáticamente
            for (int i = 0; i < titles.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardar archivo Excel en ruta específica
            String rutaArchivo = "C:\\Users\\kpesantez\\Desktop\\ArchivoComparacion\\ComparacionBases.xlsx";
            File directorio = new File("C:\\Users\\kpesantez\\Desktop\\ArchivoComparacion");
            if (!directorio.exists()) {
                directorio.mkdirs(); // Crea la carpeta si no existe
            }
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
                System.out.println("*** Archivo Excel generado correctamente en: " + rutaArchivo + " ***");
            }

        } catch (Exception e) {
            System.err.println("Error en la comparación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBInterERP.safeClose(con_intererp);
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void agregarFila(Sheet sheet, int rowNum, String base, String tipo, String nombre, String observacion, CellStyle style) {
        Row row = sheet.createRow(rowNum);

        Cell cell0 = row.createCell(0);
        cell0.setCellValue(base);
        cell0.setCellStyle(style);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(tipo);
        cell1.setCellStyle(style);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(nombre);
        cell2.setCellStyle(style);

        Cell cell3 = row.createCell(3);
        cell3.setCellValue(observacion);
        cell3.setCellStyle(style);
    }
}
