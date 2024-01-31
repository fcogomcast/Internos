package es.tributasenasturias.documentostributas.utils.logging;

import es.tributasenasturias.documentostributas.utils.Preferencias;

/**
 * 
 * @author noelianbb
 *
 */
public class Logger extends GenericAppLogger
{

	private final String LOG_FILE = "Application.log";
	public Logger(String idSesion)
	{
		super (idSesion);
		this.setLogFile(LOG_FILE);
		try {
			this.setLogDir(Preferencias.getPreferencias().getDirectorioLog());
		} catch (Exception e) {
			System.err.println ("Error al establecer el directorio de log en base a par�metros. Se utiliza el directorio de aplicaci�n:" + Preferencias.getDirectorioAplicacion());
			e.printStackTrace();
			this.setLogDir(Preferencias.getDirectorioAplicacion());
		}
		this.setNombre(Preferencias.getNombreAplicacion()); //Nombre de proceso que aparecer� en el log.
	}
}
