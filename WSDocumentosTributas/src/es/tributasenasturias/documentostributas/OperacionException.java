package es.tributasenasturias.documentostributas;

public class OperacionException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3375532458899578601L;

	/**
	 * 
	 */
	public OperacionException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OperacionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OperacionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OperacionException(Throwable cause) {
		super(cause);
	}

}
