package es.tributasenasturias.servicios.documentos.exceptions;

public class DocumentoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 49766966639762602L;

	public DocumentoException(String message, Throwable cause) {
		super("["+DocumentoException.class.getName()+"]:"+message, cause);
	}

	public DocumentoException(String message) {
		super("["+DocumentoException.class.getName()+"]:"+message);

	}

}
