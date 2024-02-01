/**
 * 
 */
package stpa.utils.ControlHorario.preferencias;

/**
 * @author crubencvs
 */
public class PreferenciasException extends Exception {

	private static final long serialVersionUID = -1692014303709275239L;

	public PreferenciasException(String msg){
		super(msg);
	}

	@Override
	public String toString() {
		return "PreferenciasException: " + super.getMessage();
	}
}