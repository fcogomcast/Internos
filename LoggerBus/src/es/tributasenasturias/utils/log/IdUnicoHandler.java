package es.tributasenasturias.utils.log;

import java.util.UUID;

/**
 * Manejador de los id �nico. 
 * @author crubencvs
 *
 */
public class IdUnicoHandler {
	/**
	 * Genera un id �nico.
	 * @return Cadena con un id �nico.
	 */
	protected String getUniqueId ()
	{
		UUID uid= UUID.randomUUID();
		return Integer.toHexString(uid.hashCode());
	}
}
