package es.tributasenasturias.documentostributas.utils.soap;

import es.tributasenasturias.documentostributas.utils.Preferencias;

public class ServerLogHandler extends LogHandler {

	private final String logFile ="SOAP_SERVER.log";
	
	public ServerLogHandler() {
		try {
			Preferencias pref = Preferencias.getPreferencias();
			if (!"DEBUG".equalsIgnoreCase(pref.getModoLog()) && !"ALL".equalsIgnoreCase(pref.getModoLog()))
			{
				this.setDoLog(false);
			}
			else
			{
				this.setDoLog(true);
			}
			this.setLOG_DIR(pref.getDirectorioLog());
		} catch (Exception e) {
			System.err.println ("Error al establecer el directorio de log de mensajes " + logFile + " en base a parámetros. Se utiliza el directorio de aplicación:" + Preferencias.getDirectorioAplicacion());
			e.printStackTrace();
			this.setLOG_DIR(Preferencias.getDirectorioAplicacion());
		}
		this.setLOG_FILE(logFile);
	}
}
