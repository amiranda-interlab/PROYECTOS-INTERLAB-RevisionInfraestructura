/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interlab.revision.infraestructura.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ManejoDeArchivos {

    /**
     * Copia todo el contenido de un directorio a otro directorio
     * @param srcDir
     * @param dstDir
     * @throws IOException
     */
    public static void copyDirectory(File srcDir, File dstDir) {
        boolean eliminar = false;
        try {
            if (srcDir.isDirectory()) {
                if (!dstDir.exists()) {
                    dstDir.mkdir();
                }
                String[] children = srcDir.list();
                for (int i = 0; i < children.length; i++) {
                    copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
                }
            } else {
                copyFile(srcDir, dstDir, eliminar);
            }
        } catch (Exception e) {
            ////System.out.println(e);
        }
    }

    /**
     * Copia un solo archivo
     * @param File origen
     * @param File destino
     * @return boolean
     * @throws IOException
     */
    public static boolean copyFile(File origen, File destino, boolean eliminar) {
        try {
            if (destino.exists() && !eliminar) {
                return true;
            } else if (destino.exists() && eliminar) {
                destino.delete();
            }
            FileChannel in = (new FileInputStream(origen)).getChannel();
            FileChannel out = (new FileOutputStream(destino)).getChannel();
            in.transferTo(0, origen.length(), out);
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            ////System.out.println(e);
            return false;
        }
    }

    /**
     * Copia un solo archivo
     * @param File origen
     * @param File destino
     * @return boolean
     * @throws IOException
     */
    public static boolean cutFile(File origen, File destino) {
        try {
            FileChannel in = (new FileInputStream(origen)).getChannel();
            FileChannel out = (new FileOutputStream(destino)).getChannel();
            in.transferTo(0, origen.length(), out);
            in.close();
            out.close();
            origen.delete();
            return true;
        } catch (Exception e) {
            ////System.out.println(e);
            return false;
        }
    }

    /**
     * Copia un archivo y busca y sustituye un String dado
     * @param source_file
     * @param destination_file
     * @param toFind
     * @param toReplace
     * @throws IOException
     */
    public static void copyFindAndReplace(String source_file, String destination_file, String toFind, String toReplace) {
        String str;
        try {
            FileInputStream fis2 = new FileInputStream(source_file);
            DataInputStream input = new DataInputStream(fis2);
            FileOutputStream fos2 = new FileOutputStream(destination_file);
            DataOutputStream output = new DataOutputStream(fos2);

            while (null != ((str = input.readLine()))) {
                String s2 = toFind;
                String s3 = toReplace;

                int x = 0;
                int y = 0;
                String result = "";
                while ((x = str.indexOf(s2, y)) > -1) {
                    result += str.substring(y, x);
                    result += s3;
                    y = x + s2.length();
                }
                result += str.substring(y);
                str = result;

                if (str.indexOf("'',") != -1) {
                    continue;
                } else {
                    str = str + "\n";
                    output.writeBytes(str);
                }
            }
        } catch (IOException ioe) {
            System.err.println("I/O Error - " + ioe);
        }
    }

    /**
     * Devuelve un string con los archivos de un directorio
     * @param Directorio
     * @return archivos
     */
    public static String[] getArchivos(String Directorio) {
        File srcDir = new File(Directorio);
        String[] archivos = srcDir.list();
        return archivos;
    }

    /**
     * Elimina el contenido de un directorio y el directorio si le especifica true en @eliminarContenedor
     * @param Directorio
     * @param eliminarContenedor
     */
    public static void deleteDirectorio(String Directorio, boolean eliminarContenedor) {
        File srcDir = new File(Directorio);
        if (srcDir.exists()) {
            String[] children = srcDir.list();
            for (int i = 0; i < children.length; i++) {
                File Archivo = new File(srcDir, children[i]);
                if (Archivo.exists()) {
                    Archivo.delete();
                }
            }
            if (eliminarContenedor) {
                srcDir.delete();
            }
        }
    }

    /**
     * Elimina el contenido de un directorio y el directorio si le especifica true en @eliminarContenedor
     * @param Directorio
     * @param eliminarContenedor
     */
    public static void deleteDirectorioAndSudDirectorios(String Directorio, String archivo_cheque, boolean eliminarContenedor) {
        File srcDir = new File(Directorio);
        if (srcDir.exists()) {
            String[] children = srcDir.list();
            for (int i = 0; i < children.length; i++) {
                File Archivo = new File(srcDir, children[i]);
                if (Archivo.exists()) {
                    if (Archivo.isDirectory()) {
                        ManejoDeArchivos.deleteDirectorioAndSudDirectorios(Archivo.getPath(), archivo_cheque, eliminarContenedor);
                    } else {                        
                        int inicial = Archivo.getName().lastIndexOf(".");
                        String nombre = Archivo.getName().substring(0, inicial);                        
                        if (nombre.compareTo(archivo_cheque) != 0) {
                            Archivo.delete();
                        }
                    }
                }
            }
            if (eliminarContenedor) {
                srcDir.delete();
            }
        }
    }


    /**
     * Quita los archivos que no esten en el string ruta y que pertenecen al directorio
     * @param ruta
     * @param archivos
     * @param borrar indica si en el caso de no tener archivos se puede eliminar el directorio
     */
    public static void limpiarDirectorio(String ruta, String archivos, boolean borrar) {
        File srcDir = new File(ruta);
        int bandera = 0;
        if (srcDir.exists()) {
            if (archivos.length() > 0) {
                String[] children = srcDir.list();
                String[] array_archivos = archivos.split(",");
                for (int i = 0; i < children.length; i++) {
                    //String[] nombre_ext = children[i].split("[.]");
                    //String nombre = nombre_ext[0];
                    int inicial = children[i].lastIndexOf(".");
                    String nombre = children[i].substring(0, inicial);
                    //String ext = children[i].substring(inicial + 1);
                    bandera = 0;
                    for (int j = 0; j < array_archivos.length; j++) {
                        if (array_archivos[j].compareTo(nombre) == 0) {
                            bandera = 1;//Significa que este archivo se queda en el directorio
                        }
                    }
                    if (bandera == 0) {
                        File Archivo = new File(srcDir, children[i]);
                        if (Archivo.exists()) {
                            Archivo.delete();
                        }
                    }
                }
            } else {
                ManejoDeArchivos.deleteDirectorio(ruta, borrar);//Elimino el directorio ya que no tiene ningun archivo
            }
        }
    }

}
