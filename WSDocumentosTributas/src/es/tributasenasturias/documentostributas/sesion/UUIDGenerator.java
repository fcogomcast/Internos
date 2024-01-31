/**
 * 
 */
package es.tributasenasturias.documentostributas.sesion;

/**
 * @author crubencvs
 * Genera un nuevo UUID �nico.
 */
public class UUIDGenerator {

	/**
	 * Genera un id �nico en formato hexadecimal
	 * @return Id �nico
	 */
	public String generateHexUID() {
		
		return Integer.toHexString(generateUID());
	}
	/**
	 * Genera un id �nico en formato entero
	 * @return Id �nico
	 */
	public int generateUID() {
		java.util.UUID uid = java.util.UUID.randomUUID();
		return uid.hashCode();
	}

	
}
