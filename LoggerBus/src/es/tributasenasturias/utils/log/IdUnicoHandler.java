package es.tributasenasturias.utils.log;

import java.util.UUID;

/**
 * Manejador de los id único. 
 * @author crubencvs
 *
 */
public class IdUnicoHandler {
	/**
	 * Genera un id único.
	 * @return Cadena con un id único.
	 */
	protected String getUniqueId ()
	{
		UUID uid= UUID.randomUUID();
		return Integer.toHexString(uid.hashCode());
	}
}
