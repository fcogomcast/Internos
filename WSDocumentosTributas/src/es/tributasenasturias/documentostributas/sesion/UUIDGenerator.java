/**
 * 
 */
package es.tributasenasturias.documentostributas.sesion;

/**
 * @author crubencvs
 * Genera un nuevo UUID único.
 */
public class UUIDGenerator {

	/**
	 * Genera un id único en formato hexadecimal
	 * @return Id único
	 */
	public String generateHexUID() {
		
		return Integer.toHexString(generateUID());
	}
	/**
	 * Genera un id único en formato entero
	 * @return Id único
	 */
	public int generateUID() {
		java.util.UUID uid = java.util.UUID.randomUUID();
		return uid.hashCode();
	}

	
}
