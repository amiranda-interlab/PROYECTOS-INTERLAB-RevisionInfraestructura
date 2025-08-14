/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interlab.revision.infraestructura.util;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.interlab.revision.infraestructura.bean.Proparametro;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import javax.mail.PasswordAuthentication;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.PrinterName;
import org.apache.log4j.Logger;

public abstract class Util {

    public static final Logger logger = Logger.getLogger(Util.class);

    public static java.sql.Date convertirFecha(java.util.Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }

    public static void enviarCorreo(List<Proparametro> para, String de, String servidor, String puerto, String asunto, String mensaje) {
        Properties props = new Properties();
        String username = "administrador@interlabsa.com";
        String password = "kgqg aiqj xfou arue";//Contraseña de aplicacion PROCESOS-INTERLAB
        props.put("mail.smtp.host", servidor);
        props.put("mail.smtp.port", puerto);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(de));
            InternetAddress[] address = new InternetAddress[para.size()];
            int i = 0;
            for (Proparametro bean : para) {
                address[i] = new InternetAddress(bean.getParvalor());
                i++;
            }
//            msg.setRecipients(Message.RecipientType.TO, address);
//            msg.setSubject(asunto);
//            msg.setSentDate(new Date());
//            msg.setText(mensaje);
            
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(asunto);
            msg.setSentDate(new Date());
            
            Multipart multipart = new MimeMultipart();
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(mensaje, "text/html; charset=utf-8");
            multipart.addBodyPart(textPart, 0);//PRIMERO EL TEXTO 
            msg.setContent(multipart);            
            
            Transport.send(msg);
        } catch (Throwable e) {
            System.out.println("Error al enviar correo: " + e);
        }
    }

    public static void sendMail(String[] to, String from, String server, String subject, String message) {//No funciona como en gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", server);
        Session session = Session.getInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = new InternetAddress[to.length];

            for (int i = 0; i < to.length; i++) {
                address[i] = new InternetAddress(to[i]);
            }

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(message);
            Transport.send(msg);
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    public static Properties readProperties(String resource) {
        Properties prop = new Properties();
        InputStream input = Util.class.getResourceAsStream("/com/interlab/resources/" + resource);
        try {
            if (input == null) {
                input = new URL(resource).openStream();
            }
            prop.load(input);
            return prop;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("El recurso /com/interlab/resources/" + resource + " no se encuentra");
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            Closeables.closeQuietly(input);
        }
    }

    public static String quitarCaracteresEspeciales(String cadena) {
//        cadena = cadena.replaceAll("ñ", "n");
//        cadena = cadena.replaceAll("Ñ", "N");
//        
        cadena = Normalizer.normalize(cadena, Normalizer.Form.NFD);
        cadena = cadena.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        
        
        String charsToRemove = "!'";
 
        for (char c : charsToRemove.toCharArray()) {
            cadena = cadena.replace(String.valueOf(c), "");
        }       

        return cadena.trim();
    }

    public static String getHoy(String DATE_FORMAT) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c1 = Calendar.getInstance(); // today
        return sdf.format(c1.getTime());
    }

    public static Date convertStringToDate(String fecha, String StrSimpleDateFormat) {
        Date date = null;

        SimpleDateFormat sdf = new SimpleDateFormat(StrSimpleDateFormat);
        try {
            date = sdf.parse(fecha);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    public static String convertDateToString(Date fechaent, String StrSimpleDateFormat) {
        Date dateNow = fechaent;
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat(StrSimpleDateFormat);
        String nowYYYYMMDD = dateformatYYYYMMDD.format(dateNow);
        String fecha = nowYYYYMMDD;
        return fecha;
    }

    public static String tiempoEjecucion(DateTime ini) {
        String time = new String();
        long segundosTranscurridos = Seconds.secondsBetween(ini, DateTime.now()).getSeconds();
        int day = (int) TimeUnit.SECONDS.toDays(segundosTranscurridos);
        long hours = TimeUnit.SECONDS.toHours(segundosTranscurridos) - day * 24;
        long minute = TimeUnit.SECONDS.toMinutes(segundosTranscurridos) - TimeUnit.SECONDS.toHours(segundosTranscurridos) * 60L;
        long second = TimeUnit.SECONDS.toSeconds(segundosTranscurridos) - TimeUnit.SECONDS.toMinutes(segundosTranscurridos) * 60L;
        time = "Tiempo de ejecucion del proceso: \ndias:" + day + " horas:" + hours + " minutos:" + minute + " segundos:" + second + "\n";
        System.out.println(time);
        return time;
    }

    public static boolean copyFile(File origen, File destino, boolean eliminar, String tipo) {
        try {
//            if (destino.exists()) {
//                return true;
//            }
            FileChannel in = (new FileInputStream(origen)).getChannel();
            FileChannel out = (new FileOutputStream(destino)).getChannel();
            in.transferTo(0, origen.length(), out);
            in.close();
            out.close();

            if (eliminar && tipo.compareTo("MI") == 0) {
                origen.delete();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error al enviar correo: " + e);
            return false;
        }
    }

    public static boolean imprimirCodigosMSPHoneywellNO(String nombre_codigo, int codigo, int numero_tickets_por_impresion, String nombre_impresora, String id_muestra) {
        boolean flag = false;
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

        Formatter fmt = new Formatter();
        fmt.format("%07d", codigo);

        String codigo_numerico = "";
        if (nombre_codigo.trim().compareTo("HEMOGRAMA") == 0) {
            codigo_numerico = fmt.toString() + "02";
        } else if (nombre_codigo.trim().compareTo("INMUNOLOGIA 2") == 0) {
            codigo_numerico = fmt.toString() + "10";
        } else if (nombre_codigo.trim().compareTo("BIOQUIMICA") == 0) {
            codigo_numerico = fmt.toString() + "01";
        } else if (nombre_codigo.trim().compareTo("INMUNO-QUIMICA") == 0) {
            codigo_numerico = fmt.toString() + "01";
        } else if (nombre_codigo.trim().compareTo("ORINA") == 0) {
            codigo_numerico = fmt.toString() + "03";
        } else if (nombre_codigo.trim().compareTo("HECES") == 0) {
            codigo_numerico = fmt.toString() + "04";
        } else if (nombre_codigo.trim().compareTo("NEFELOMETRIA") == 0) {
            codigo_numerico = fmt.toString() + "01";
        } else if (nombre_codigo.trim().compareTo("INMUNOLOGIA 1") == 0) {
            codigo_numerico = fmt.toString() + "09";
        } else {
            codigo_numerico = fmt.toString() + "";
        }

        String zplCommand = "#10\n"
                + "N\n"
                + "#10\n"
                + "B290,40,0,1,2,8,120,N,\"" + codigo_numerico + "\"#10#13\n"
                + "A315,07,0,3,1,1,N,\"INTERLAB\"#10#13\n"
                + "A600,34,1,2,1,1,R,\"" + nombre_codigo + "\"#10#13\n"
                + "A572,40,1,3,1,1,N,\"ID\"#10#13\n"
                + "A572,99,1,3,1,1,N,\"MUESTRA\"#10#13\n"
                + "A545,33,1,4,1,1,N,\" " + id_muestra + "\"#10#13\n"
                + "\n"
                + "//AX,Y  X  LO MUEVE DE ARRIBA ABAJO, Y LO MUEVO DE IZQUIERDA A DERCHA\n"
                + "P1#10#13\n";

        try {
            // convertimos el comando a bytes          
            byte[] by = zplCommand.getBytes();
            for (int j = 0; j < numero_tickets_por_impresion; j++) {
                DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                Doc doc = new SimpleDoc(by, flavor, null);

                //Inclusion del nombre de impresora y sus atributos
                AttributeSet attributeSet = new HashAttributeSet();
                attributeSet.add(new PrinterName(nombre_impresora, null));
                attributeSet = new HashAttributeSet();
                //Soporte de color o no
                attributeSet.add(ColorSupported.NOT_SUPPORTED);

                // Formato de Documento
                DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
                //Busqueda de la impresora por el nombre asignado en attributeSet
                PrintService[] services = PrintServiceLookup.lookupPrintServices(docFormat, attributeSet);

                //En caso de que tengamos varias impresoras configuradas
                PrintService myPrinter = null;
                for (int i = 0; i < services.length; i++) {
                    System.out.println("impresora: " + services[i].getName());
                    if (services[i].getName().equals(nombre_impresora)) {
                        myPrinter = services[i];
                        System.out.println("Imprimiendo en : " + services[i].getName() + "codigo:" + fmt);
                        break;
                    }
                }
                DocPrintJob printJob = myPrinter.createPrintJob();
                //Envio a la impresora
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                printJob.print(doc, aset);
                flag = true;
            }
        } catch (Exception ex) {
            logger.error(ex, ex);
            flag = false;
        }
        return flag;
    }

    public static boolean imprimirCodigosMSPHoneywell(String nombre_codigo, int codigo, int numero_tickets_por_impresion, String nombre_impresora, String apellido_materno, String id_muestra) {
        boolean flag = false;
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

        System.out.println("Numero de orden : " + codigo);

        Formatter fmt = new Formatter();
        fmt.format("%07d", codigo);
        String codigo_numerico = "";
        if (nombre_codigo.trim().compareTo("HEMOGRAMA") == 0) {
            codigo_numerico = fmt.toString() + "02";
        } else if (nombre_codigo.trim().compareTo("INMUNOLOGIA 2") == 0) {
            codigo_numerico = fmt.toString() + "10";
        } else if (nombre_codigo.trim().compareTo("INMUNO-QUIMICA") == 0) {
            codigo_numerico = fmt.toString() + "01";
        } else if (nombre_codigo.trim().compareTo("ORINA") == 0) {
            codigo_numerico = fmt.toString() + "03";
        } else if (nombre_codigo.trim().compareTo("HECES") == 0) {
            codigo_numerico = fmt.toString() + "04";
        } else if (nombre_codigo.trim().compareTo("NEFELOMETRIA") == 0) {
            codigo_numerico = fmt.toString() + "01";
        } else if (nombre_codigo.trim().compareTo("INMUNOLOGIA 1") == 0) {
            codigo_numerico = fmt.toString() + "09";
        } else {
            codigo_numerico = fmt.toString() + "";
        }
        String zplCommand = "#10\n"
                + "N\n"
                + "#10\n"
                + "B300,40,0,1,2,8,120,N,\"" + codigo_numerico + "\"#10#13\n"
                + "A315,07,0,3,1,1,N,\"INTERLAB\"#10#13\n"
                + "A600,34,1,2,1,1,R,\"" + nombre_codigo + "\"#10#13\n"
                + "A572,40,1,3,1,1,N,\"ID:\"#10#13\n"
                + "A572,99,1,3,1,1,N,\" " + id_muestra + "\"#10#13\n"
                + "A545,33,1,4,1,1,N,\" " + codigo_numerico + "\"#10#13\n"
                + "A265,30,1,2,1,1,N,\"Apellido:\"#10#13\n"
                + "A240,30,1,2,1,1,N,\"" + apellido_materno + "\"#10#13\n"
                + "\n"
                + "//AX,Y  X  LO MUEVE DE ARRIBA ABAJO, Y LO MUEVO DE IZQUIERDA A DERCHA\n"
                + "P1#10#13\n";

        try {
            // convertimos el comando a bytes          
            byte[] by = zplCommand.getBytes();
            for (int j = 0; j < numero_tickets_por_impresion; j++) {
                DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                Doc doc = new SimpleDoc(by, flavor, null);

                //Inclusion del nombre de impresora y sus atributos
                AttributeSet attributeSet = new HashAttributeSet();
                attributeSet.add(new PrinterName(nombre_impresora, null));
                attributeSet = new HashAttributeSet();
                //Soporte de color o no
                attributeSet.add(ColorSupported.NOT_SUPPORTED);

                // Formato de Documento
                DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
                //Busqueda de la impresora por el nombre asignado en attributeSet
                PrintService[] services = PrintServiceLookup.lookupPrintServices(docFormat, attributeSet);

                //En caso de que tengamos varias impresoras configuradas
                PrintService myPrinter = null;
                for (int i = 0; i < services.length; i++) {
                    //System.out.println("impresora: " + services[i].getName());
                    if (services[i].getName().equals(nombre_impresora)) {
                        myPrinter = services[i];
                        //System.out.println("Imprimiendo en : " + services[i].getName());
                        break;
                    }
                }
                DocPrintJob printJob = myPrinter.createPrintJob();
                //Envio a la impresora
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                printJob.print(doc, aset);
                flag = true;
            }
        } catch (Exception ex) {
            logger.error(ex, ex);
            flag = false;
        }
        return flag;
    }

    public static String quitarSaltos(String cadena) {
        // Para el reemplazo usamos un string vacío 
        String tmp = cadena;
        if (cadena.length() > 401) {
            tmp = cadena.substring(0, 400);
        }
        return tmp.replaceAll("\n", "");
    }

    public static Date sumarRestarAnios(Date fecha, int anios) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(calendar.YEAR, anios);
        return calendar.getTime();
    }
    
    public static Date sumarRestarDias(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }    
}
