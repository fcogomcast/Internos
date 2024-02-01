package es.tributasenasturias.servicios.documentos.exceptions;

public class ImpresionGDException extends Exception{

	public ImpresionGDException(String message, Throwable cause) {
		super("[" + ImpresionGDException.class.getName()+"]:"+message, cause);
	}

	public ImpresionGDException(String message) {
		super("["+ImpresionGDException.class.getName()+"]:"+message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -237501031482790379L;

}
