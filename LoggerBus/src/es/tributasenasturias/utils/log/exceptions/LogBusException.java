package es.tributasenasturias.utils.log.exceptions;

public class LogBusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6856391534922267581L;

	public LogBusException() {
		super();
	}

	public LogBusException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogBusException(String message) {
		super(message);
	}
}
