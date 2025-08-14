/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interlab.revision.infraestructura.exec;

import com.interlab.revision.infraestructura.base.DB;
import com.interlab.revision.infraestructura.bean.Proparametro;
import com.interlab.revision.infraestructura.util.Util;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

        List<Proparametro> lst_correos = new ArrayList<Proparametro>();
        Proparametro para = new Proparametro();
        Proparametro para1 = new Proparametro();
        para.setParvalor("amiranda@interlabsa.com");
        para1.setParvalor("soportetecnico@interlabsa.com");
        lst_correos.add(para);
        lst_correos.add(para1);
        try {
            //Inicializar conexiones
            DateTime dt = new DateTime();
            logger.info("*** INICIO DEL PROCESO DE REVISION DE BACKUPS DE BASE DE DATOS  ***");
            logger.info(String.valueOf(dt.toDate()));
            System.out.println(dt.toDate());
            con_intranet = DB.getConnectionDbIntranet();
            con_intranet.setAutoCommit(false);
            
            
            

            con_intranet.commit();
            Util.tiempoEjecucion(dt);
            logger.info("*** PROCESO FINALIZADO ***");
        } catch (Exception ex) {
            logger.error(ex, ex);
            Util.enviarCorreo(lst_correos, "administrador@interlabsa.com", "smtp.gmail.com", "587", "Error en proceso de revision de Archivos Backups SQL", ex.toString());
            try {
                con_intranet.rollback();
            } catch (SQLException ex1) {
                logger.error(ex1, ex1);
            }
        } catch (Throwable ex) {
            logger.error(ex, ex);
            Util.enviarCorreo(lst_correos, "administrador@interlabsa.com", "smtp.gmail.com", "587", "Error en proceso de revision de Archivos Backups SQL", ex.toString());
            try {
                con_intranet.rollback();
            } catch (SQLException ex1) {
                logger.error(ex1, ex1);
            }
        } finally {
            DB.safeClose(con_intranet);
        }

    }

}
