package es.tributasenasturias.documentostributas.sesion;

/**
 * Genera id de sesión único.
 * 
 * @author crubencvs
 *
 */
public class GeneradorIdSesion {
	private GeneradorIdSesion(){};
	/**
	 * Genera un id de sesión único.
	 * @return
	 */
	public static String generaIdSesion()
	{
		return new UUIDGenerator().generateHexUID(); // UID para esta petición. Se añadirá al log.
	}
}

