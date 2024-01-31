package es.tributasenasturias.documentostributas.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import es.tributasenasturias.documentostributas.OperacionException;


/**
 * Utilidades para compresión y en su caso descompresión de datos 
 * @author crubencvs
 *
 */
public class Compresor {
	private Compresor()
	{
		
	}
	/**
	 * Compresión de datos
	 * @param datos Datos a comprimir, en forma de array de byte
	 * @return Datos comprimidor, como una cadena de Base64
	 * @throws OperacionException
	 */
	public static final String comprimir (byte[] datos) throws OperacionException
	{
		ByteArrayOutputStream resulByteArray = new ByteArrayOutputStream();
		try {
			resulByteArray.write(datos);
			return PdfComprimidoUtils.comprimirPDF(resulByteArray);
		} catch (IOException e) {
			throw new OperacionException ("Error en compresión de datos:"+ e.getMessage(),e);
		} catch (Exception e) {
			throw new OperacionException ("Error en compresión de datos:" + e.getMessage(),e);
		}
	}
	
	public static final byte[] descomprimir (String comprimido) throws OperacionException
	{
		try {
			ByteArrayOutputStream bo = PdfComprimidoUtils.descomprimirPDF(comprimido);
			return bo.toByteArray();
		} catch (Exception e) {
			throw new OperacionException ("Error en descompresión de datos:" + e.getMessage(),e);
		}
	}
}
