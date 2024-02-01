package es.tributasenasturias.servicios.documentos.impresion.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Clase con utilidades para comprimir y descomprimir texto
 * de la misma forma que lo hace la aplicación Tributas en la sección
 * de "Comprimir/Descomprimir" de las herramientas del gestor documental.
 * Este código no tiene en cuenta la codificación del texto original,
 * podría dar problemas con codificaciones diferentes.
 * @author CarlosRuben
 */
public class GZIPImplWeb {

    /**
     * Convierte de un array de bytes 
     * @param in Array de bytes
     * @return  Cadena equivalente en formato hexadecimal.
     */
    public static String byte2Hex(byte in[]) {

        byte ch = 0x00;

        int i = 0;

        if (in == null || in.length <= 0) {
            return null;
        }



        String pseudo[] = {"0", "1", "2",
            "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E",
            "F"};

        StringBuffer out = new StringBuffer(in.length * 2);



        while (i < in.length) {

            ch = (byte) (in[i] & 0xF0); // Strip off high nibble

            ch = (byte) (ch >>> 4);
            // shift the bits down

            ch = (byte) (ch & 0x0F);
// must do this is high order bit is on!

            out.append(pseudo[ (int) ch]); // convert the nibble to a String Character

            ch = (byte) (in[i] & 0x0F); // Strip off low nibble 

            out.append(pseudo[ (int) ch]); // convert the nibble to a String Character

            i++;

        }

        String rslt = new String(out);

        return rslt;

    }
    /**
     * Convierte de una cadena que contiene caracteres hexadecimales
     * a un array de bytes con los valores equivalentes.
     * @param str Cadena que contiene caracteres hexadecimales
     * @return Array de bytes equivalentes.
     */
    public static byte[] hex2Byte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
    
    /**
     * Descomprime un array de bytes y devuelve la cadena correspondiente
     * @param paramArrayOfByte Array de bytes
     * @return Cadena equivalente
     */
    private static String descomprimir(byte[] paramArrayOfByte) {
    	ByteArrayInputStream localByteArrayInputStream=null;
    	ByteArrayOutputStream localByteArrayOutputStream=null;
    	GZIPInputStream localGZIPInputStream=null;
        try {
            byte[] arrayOfByte = new byte[1024*50];
            localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
            localByteArrayOutputStream = new ByteArrayOutputStream();
            localGZIPInputStream = new GZIPInputStream(localByteArrayInputStream);
            int i;
            while ((i = localGZIPInputStream.read(arrayOfByte)) >= 0) {
            	localByteArrayOutputStream.write(arrayOfByte,0,i);
            }
            //Importante. El charset ha de ser el que se indica, que es con el que se insertaron
        	//los datos en Tributas y en la base de datos.
            return new String(localByteArrayOutputStream.toByteArray(),Charset.forName("ISO-8859-1"));
        } catch (IOException localIOException) {
            return localIOException.toString();
        }
        finally
        {
        	if (localByteArrayInputStream!=null)
        	{
        		try { localByteArrayInputStream.close();} catch (Exception e){}
        	}
        	if (localByteArrayOutputStream!=null)
        	{
        		try { localByteArrayOutputStream.close();} catch (Exception e){}
        	}
        	if (localGZIPInputStream!=null)
        	{
        		try { localGZIPInputStream.close();} catch (Exception e) {} 
        	}
        		
        }
    }
    /**
     * Comprime una cadena y devuelve un array de bytes
     * @param paramString Cadena a comprimir
     * @return Array de bytes que representa la cadena comprimida
     */
    private static byte[] comprimir(String paramString) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
            // CRUBENCVS 38243.
            //localGZIPOutputStream.write(paramString.getBytes(), 0, paramString.length());
            //Había un fallo en la sentencia, para cadenas multibytes, paramString.getBytes no
            //tiene por qué coincidir con paramString.length. Se basa en la longitud de la cadena.
            localGZIPOutputStream.write(paramString.getBytes(), 0, paramString.getBytes().length);
            //Fin CRUBENCVS 38243
            localGZIPOutputStream.close();
        } catch (IOException localIOException) {
            System.out.println(localIOException.toString());
        }
        return localByteArrayOutputStream.toByteArray();
    }
    /**
     * Comprime la cadena que se le pasa por parámetro, y devuelve la 
     * cadena comprimida y expresada como una cadena de caracteres hexadecimales
     * @param cadena Cadena a comprimir
     * @return Cadena comprimida en hexadecimal
     */
    public static String comprimeWeb(String cadena) {
        return byte2Hex(comprimir(cadena));
    }
    /**
     * Descomprime la cadena que se le pasa como parámetro, que será 
     * un texto comprimido y expresado en forma de caracteres hexadecimales.
     * @param comprimido Cadena comprimida en caracteres hexadecimales
     * @return Cadena descomprimida
     */
    public static String descomprimeWeb(String comprimido) {
        return descomprimir(hex2Byte(comprimido));
    }
}
