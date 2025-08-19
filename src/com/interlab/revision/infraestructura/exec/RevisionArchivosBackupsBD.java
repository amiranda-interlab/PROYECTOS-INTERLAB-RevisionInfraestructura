/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interlab.revision.infraestructura.exec;

import com.interlab.revision.infraestructura.base.DB;
import com.interlab.revision.infraestructura.bean.Proparametro;
import com.interlab.revision.infraestructura.bean.RevisionInfra;
import com.interlab.revision.infraestructura.dao.RevisionInfraDAO;
import com.interlab.revision.infraestructura.util.Util;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author amiranda
 */
public class RevisionArchivosBackupsBD {

    static final Logger logger = Logger.getLogger(RevisionArchivosBackupsBD.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Connection con_intranet = null;

        List<Proparametro> lst_correos = new ArrayList<>();
        Proparametro para = new Proparametro();
        Proparametro para1 = new Proparametro();
        para.setParvalor("amiranda@interlabsa.com");
        para1.setParvalor("soportetecnico@interlabsa.com");
        lst_correos.add(para);
        lst_correos.add(para1);

        try {
            //Inicializar conexiones
            DateTime dt = new DateTime();
            logger.info("*** INICIO DEL PROCESO DE REVISION DE BACKUPS DE BASE DE DATOS ***");
            logger.info(String.valueOf(dt.toDate()));
            System.out.println(dt.toDate());

            con_intranet = DB.getConnectionDbIntranet();
            con_intranet.setAutoCommit(false);

            RevisionInfraDAO dao = new RevisionInfraDAO();
            List<RevisionInfra> reinf = dao.obtenerBasesActivas(con_intranet);

            String fechaHoy = new SimpleDateFormat("yyyyMMdd").format(new Date());

            for (RevisionInfra base : reinf) {
                String ruta_carpeta_fecha = base.getRutaRespaldoBak();
                File carpetaFecha = new File(ruta_carpeta_fecha, fechaHoy);

                boolean tieneBackup = false;

                if (carpetaFecha.exists() && carpetaFecha.isDirectory()) {
                    File[] carpetasBase = carpetaFecha.listFiles();

                    if (carpetasBase != null) {

                        File carpetaBackup = new File(ruta_carpeta_fecha + "/" + fechaHoy, base.getNombreBase());
                        File[] archivos_baks = carpetaBackup.listFiles();

                        if (archivos_baks != null) {

                            for (File archivo : archivos_baks) {
                                logger.info("Archivos encontrados en: " + archivo.getAbsolutePath());
                                System.out.println("Archivos encontrados en: " + archivo.getAbsolutePath());
                                tieneBackup = true;
                                break;
//                                File[] archivos = carpetaBase.listFiles(File::isFile);
//
//                                if (archivos != null && archivos.length > 0) {
//                                    tieneBackup = true;
//                                    logger.info("Backup encontrado en: " + carpetaBase.getAbsolutePath());
//                                    break;
//                                } else {
//                                    logger.warn("No se encontraron archivos en: " + carpetaBase.getAbsolutePath());
//                                }
                            }

                        } else {
                            logger.warn("No hay archivos baks en: " + carpetaFecha.getAbsolutePath());
                            System.out.println("No hay archivos baks en: " + carpetaFecha.getAbsolutePath());
                        }

                    } else {
                        logger.warn("No hay carpetas de bases de datos en: " + carpetaFecha.getAbsolutePath());
                        System.out.println("No hay carpetas de bases de datos en: " + carpetaFecha.getAbsolutePath());
                    }
                } else {
                    logger.warn("No existe carpeta para la fecha: " + carpetaFecha.getAbsolutePath());
                    System.out.println("No existe carpeta para la fecha: " + carpetaFecha.getAbsolutePath());
                }

                base.setExisteBackup(tieneBackup);
            }

            con_intranet.commit();

            // Ordenar: primero los que NO tienen backup
            reinf.sort((a, b) -> Boolean.compare(a.isExisteBackup(), b.isExisteBackup()));
            // Construcción del HTML para correo
            StringBuilder tablaHtml = new StringBuilder();
            tablaHtml.append("<table border='1' cellpadding='5' cellspacing='0'>");
            tablaHtml.append("<tr>")
                    .append("<th></th>")
                    .append("<th>Codigo DB</th>")
                    .append("<th>Codigo Motor</th>")
                    .append("<th>Nombre Motor Base</th>")
                    .append("<th>IP Motor Base</th>")
                    .append("<th>Nombre Base</th>")
                    .append("<th>Ruta Respaldo</th>")
                    .append("<th>Estado</th>")
                    .append("<th>Existe Backups</th>")
                    .append("</tr>");

            int contador = 1;
            for (RevisionInfra base : reinf) {
                tablaHtml.append("<tr>")
                        .append("<td>").append(contador++).append("</td>") // Columna para numerar los datos
                        .append("<td>").append(base.getCodigoDb()).append("</td>")
                        .append("<td>").append(base.getCodigoMotor()).append("</td>")
                        .append("<td>").append(base.getNombreMotorBase()).append("</td>")
                        .append("<td>").append(base.getIpMotorBase()).append("</td>")
                        .append("<td>").append(base.getNombreBase()).append("</td>")
                        .append("<td>").append(base.getRutaRespaldoBak()).append("</td>")
                        .append("<td>").append(base.getEstado()).append("</td>")
                        .append("<td style='color:").append(base.isExisteBackup() ? "black" : "red").append(";'>")
                        .append(base.isExisteBackup() ? "SI" : "NO")
                        .append("</td>")
                        .append("</tr>");
            }

            tablaHtml.append("</table>");

            String asunto = "Reporte de Backups SQL - Estado de Respaldos";
            String cuerpoCorreo = "<p>Estimado equipo,</p>"
                    + "<p>Se adjunta el estado de los respaldos de las bases de datos activas:</p>"
                    + tablaHtml.toString()
                    + "<p>Saludos,<br/>Josue Miranda Villalta / Soporte Tecnico</p>";

            Util.enviarCorreo(lst_correos, "administrador@interlabsa.com", "smtp.gmail.com", "587", asunto, cuerpoCorreo);
            Util.tiempoEjecucion(dt);
            logger.info("*** PROCESO FINALIZADO ***");

        } catch (Exception ex) {
            logger.error(ex, ex);
            Util.enviarCorreo(lst_correos, "soportetecnico@interlabsa.com", "smtp.gmail.com", "587",
                    "Error en proceso de revisión de Archivos Backups SQL", ex.toString());
            try {
                if (con_intranet != null) {
                    con_intranet.rollback();
                }
            } catch (SQLException ex1) {
                logger.error(ex1, ex1);
            }
        } catch (Throwable ex) {
            logger.error(ex, ex);
            Util.enviarCorreo(lst_correos, "administrador@interlabsa.com", "smtp.gmail.com", "587",
                    "Error crítico en proceso de revisión de Archivos Backups SQL", ex.toString());
            try {
                if (con_intranet != null) {
                    con_intranet.rollback();
                }
            } catch (SQLException ex1) {
                logger.error(ex1, ex1);
            }
        } finally {
            DB.safeClose(con_intranet);
        }

    }

}
