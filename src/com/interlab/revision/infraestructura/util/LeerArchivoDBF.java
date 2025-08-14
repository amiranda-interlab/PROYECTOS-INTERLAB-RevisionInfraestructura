package com.interlab.revision.infraestructura.util;


import java.io.*; //Manipular objetos de archivos
import com.linuxense.javadbf.*; //Propiamente para manipular DBF's

public class LeerArchivoDBF {

    public static void main(String args[]) {
        leerDBF("\\\\172.30.0.87\\ciap\\PROVEED.dbf");
    }

    public static void leerDBF(String ruta) {
        try {
            // creacion del objeto DBFReader
            //nos permitira la lectura de un archivo dbf
            InputStream inputStream = new FileInputStream(ruta); // take dbf file as program argument
            DBFReader reader = new DBFReader(inputStream);
            // obtenemos el numero de filas
            int numDeColumnas = reader.getFieldCount();
            // impresion en pantalla de los nombres de las columnas
            for (int i = 0; i < numDeColumnas; i++) {
                DBFField columna = reader.getField(i); //DBFField: clase columna
                // obtenido el objeto columna
                //podemos acceder a mas propiedades aparte del nombre
                System.out.print(columna.getName());
                System.out.print("\t");
            }
            // Empezamos a la lectura de las filas
            Object[] filaObjectos; //para almacenar cada fila
            while ((filaObjectos = reader.nextRecord()) != null) {
                for (int i = 0; i < numDeColumnas; i++) {
                    System.out.print(filaObjectos[i] + "\t");
                }
                System.out.println("\t");
            }
            // Es hora de cerrar el archivo
            inputStream.close();
        } catch (DBFException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
