/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interlab.revision.infraestructura.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import com.google.common.base.Objects;
import com.interlab.revision.infraestructura.util.Util;
import java.util.Properties;

public final class DB {

    private static final Logger logger = Logger.getLogger(DB.class);
    private static DataSource dsDbIntranet = null;
    private static DataSource dsDbInterERP = null;
    private static DataSource dsLabcorePrueba = null;

    private static String DIR_FILE_SERVER;

    private static String FILE_PROD_SERVER;
    private static String FILE_PROD_USER;
    private static String FILE_PROD_PASS;
    private static String FILE_PROD_DIRECTORY;

    private static String FILE_PRUB_SERVER;
    private static String FILE_PRUB_USER;
    private static String FILE_PRUB_PASS;
    private static String FILE_PRUB_DIRECTORY;

    public static synchronized void init() {
        logger.debug("Inicializando DataSources...");
        ComboPooledDataSource cpds = new ComboPooledDataSource("dsDbIntranet");
        dsDbIntranet = cpds;
        ComboPooledDataSource cpds1 = new ComboPooledDataSource("dsLabcorePrueba");
        dsLabcorePrueba = cpds1;
        ComboPooledDataSource cpds2 = new ComboPooledDataSource("dsDbInterERP");
        dsDbInterERP = cpds2;
        logger.debug("DataSources inicializados");
        initStaticVars();
    }

    private static void initStaticVars() {
        String config = (String) Objects.firstNonNull(System.getProperty("com.interlab.base.config.properties"), "config.properties");
        Properties prop = Util.readProperties(config);
        DIR_FILE_SERVER = prop.getProperty("dir.file.server");

        FILE_PROD_SERVER = prop.getProperty("ftp.prod.server");
        FILE_PROD_USER = prop.getProperty("ftp.prod.user");
        FILE_PROD_PASS = prop.getProperty("ftp.prod.password");
        FILE_PROD_DIRECTORY = prop.getProperty("ftp.prod.directory");

        FILE_PRUB_SERVER = prop.getProperty("ftp.prub.server");
        FILE_PRUB_USER = prop.getProperty("ftp.prub.user");
        FILE_PRUB_PASS = prop.getProperty("ftp.prub.password");
        FILE_PRUB_DIRECTORY = prop.getProperty("ftp.prub.directory");

    }

    public static Connection getConnectionDbIntranet() throws SQLException {
        if (dsDbIntranet == null) {
            init();
        }
        Connection con = dsDbIntranet.getConnection();
        return con;
    }

    public static Connection getConnectionLacorePrueba() throws SQLException {
        if (dsLabcorePrueba == null) {
            init();
        }
        Connection con = dsLabcorePrueba.getConnection();
        return con;
    }

    public static Connection getConnectionDbInterERP() throws SQLException {
        if (dsDbInterERP == null) {
            init();
        }
        Connection con = dsDbInterERP.getConnection();
        return con;
    }

    public static void safeClose(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error(e, e);
        }
    }

    /**
     * @return the DIR_FILE_SERVER
     */
    public static String getDIR_FILE_SERVER() {
        return DIR_FILE_SERVER;
    }

    /**
     * @param aDIR_FILE_SERVER the DIR_FILE_SERVER to set
     */
    public static void setDIR_FILE_SERVER(String aDIR_FILE_SERVER) {
        DIR_FILE_SERVER = aDIR_FILE_SERVER;
    }

    /**
     * @return the FILE_PROD_SERVER
     */
    public static String getFILE_PROD_SERVER() {
        return FILE_PROD_SERVER;
    }

    /**
     * @param aFILE_PROD_SERVER the FILE_PROD_SERVER to set
     */
    public static void setFILE_PROD_SERVER(String aFILE_PROD_SERVER) {
        FILE_PROD_SERVER = aFILE_PROD_SERVER;
    }

    /**
     * @return the FILE_PROD_USER
     */
    public static String getFILE_PROD_USER() {
        return FILE_PROD_USER;
    }

    /**
     * @param aFILE_PROD_USER the FILE_PROD_USER to set
     */
    public static void setFILE_PROD_USER(String aFILE_PROD_USER) {
        FILE_PROD_USER = aFILE_PROD_USER;
    }

    /**
     * @return the FILE_PROD_PASS
     */
    public static String getFILE_PROD_PASS() {
        return FILE_PROD_PASS;
    }

    /**
     * @param aFILE_PROD_PASS the FILE_PROD_PASS to set
     */
    public static void setFILE_PROD_PASS(String aFILE_PROD_PASS) {
        FILE_PROD_PASS = aFILE_PROD_PASS;
    }

    /**
     * @return the FILE_PROD_DIRECTORY
     */
    public static String getFILE_PROD_DIRECTORY() {
        return FILE_PROD_DIRECTORY;
    }

    /**
     * @param aFILE_PROD_DIRECTORY the FILE_PROD_DIRECTORY to set
     */
    public static void setFILE_PROD_DIRECTORY(String aFILE_PROD_DIRECTORY) {
        FILE_PROD_DIRECTORY = aFILE_PROD_DIRECTORY;
    }

    /**
     * @return the FILE_PRUB_SERVER
     */
    public static String getFILE_PRUB_SERVER() {
        return FILE_PRUB_SERVER;
    }

    /**
     * @param aFILE_PRUB_SERVER the FILE_PRUB_SERVER to set
     */
    public static void setFILE_PRUB_SERVER(String aFILE_PRUB_SERVER) {
        FILE_PRUB_SERVER = aFILE_PRUB_SERVER;
    }

    /**
     * @return the FILE_PRUB_USER
     */
    public static String getFILE_PRUB_USER() {
        return FILE_PRUB_USER;
    }

    /**
     * @param aFILE_PRUB_USER the FILE_PRUB_USER to set
     */
    public static void setFILE_PRUB_USER(String aFILE_PRUB_USER) {
        FILE_PRUB_USER = aFILE_PRUB_USER;
    }

    /**
     * @return the FILE_PRUB_PASS
     */
    public static String getFILE_PRUB_PASS() {
        return FILE_PRUB_PASS;
    }

    /**
     * @param aFILE_PRUB_PASS the FILE_PRUB_PASS to set
     */
    public static void setFILE_PRUB_PASS(String aFILE_PRUB_PASS) {
        FILE_PRUB_PASS = aFILE_PRUB_PASS;
    }

    /**
     * @return the FILE_PRUB_DIRECTORY
     */
    public static String getFILE_PRUB_DIRECTORY() {
        return FILE_PRUB_DIRECTORY;
    }

    /**
     * @param aFILE_PRUB_DIRECTORY the FILE_PRUB_DIRECTORY to set
     */
    public static void setFILE_PRUB_DIRECTORY(String aFILE_PRUB_DIRECTORY) {
        FILE_PRUB_DIRECTORY = aFILE_PRUB_DIRECTORY;
    }

}
