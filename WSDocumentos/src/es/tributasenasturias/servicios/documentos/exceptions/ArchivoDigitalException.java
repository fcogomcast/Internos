package es.tributasenasturias.servicios.documentos.exceptions;

/**
 * Excepción a lanzar cuando se produzcan problemas en el acceso a Archivo Digital
 * @author crubencvs
 *
 */
public class ArchivoDigitalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5270320682191966868L;
	private static final String prefijo="Error en acceso a archivo digital::";
	public ArchivoDigitalException(String message, Throwable cause) {
		super(prefijo+message, cause);
		
	}

	public ArchivoDigitalException(String message) {
		super(prefijo+message);
		
	}

}
