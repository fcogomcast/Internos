package es.tributasenasturias.documentostributas.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

//
public class Preferencias {
	// Se hace instancia privada y estática.
	static private Preferencias _pref = new Preferencias();
	private Preferences m_preferencias;

	private final static String FICHERO_PREFERENCIAS = "prefsDocumentosTributas.xml";
	private final static String NOMBRE_APLICACION = "WSDocumentosTributas";
	private final static String DIRECTORIO_APLICACION = "proyectos/"+NOMBRE_APLICACION;
	private final static String DIRECTORIO_PREFERENCIAS = DIRECTORIO_APLICACION;
	private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();

	// nombres de las preferencias

	private final static String KEY_PREF_PROCALMACENADO_CONSULTA = "pAConsultaDocumento";
	private final static String KEY_PREF_ENDPOINT_LANZADOR = "EndPointLanzador";
	private final static String KEY_PREF_LOG = "ModoLog";
	private final static String KEY_PREF_ESQUEMA = "Esquema";
	private final static String KEY_PREF_LOG_DIR = "DirectorioLogs";

	private final String NOMBRE_PREF_PROCALMACENADO_ALTADOC = "pAAltaDocumento";


	private Preferencias() {
		try {
			CargarPreferencias();
		} catch (Exception e) {
			
		}
	}

	@SuppressWarnings("static-access")
	protected synchronized void CargarPreferencias() throws Exception {
		if (CompruebaFicheroPreferencias()) {

			FileInputStream inputStream = new FileInputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
			Preferences.systemNodeForPackage(this.getClass()).importPreferences(inputStream);
			inputStream.close();

			m_preferencias = Preferences.systemNodeForPackage(this.getClass());

			String[] keys = m_preferencias.keys();
			String msgKeys = "Leyendo las siguientes claves -> ";
			for (int i = 0; i < keys.length; i++) {
				msgKeys += "[" + keys[i] + "] ";
			}

			for (int i = 0; i < keys.length; i++) {
				String value = m_preferencias.get(keys[i], "");

				tablaPreferencias.put(keys[i], value);
			}
		}
	}

	private void InicializaTablaPreferencias() {
		// Logger.debug("Cargando tabla con preferencias por defecto");

		tablaPreferencias.clear();
		tablaPreferencias.put(KEY_PREF_ENDPOINT_LANZADOR,"http://bus:7101/WSInternos/ProxyServices/PXLanzadorMasivo");
		tablaPreferencias.put(KEY_PREF_PROCALMACENADO_CONSULTA,"GESTION_DOCUMENTOS_TRIBUTAS.consulta_documento");
		tablaPreferencias.put(KEY_PREF_LOG, "INFO");
		tablaPreferencias.put(KEY_PREF_ESQUEMA, "EXPLOTACION");
		tablaPreferencias.put(KEY_PREF_LOG_DIR, "proyectos/"+NOMBRE_APLICACION);

		tablaPreferencias.put(NOMBRE_PREF_PROCALMACENADO_ALTADOC, "GESTION_DOCUMENTOS_TRIBUTAS.alta_documento");
	}

	private boolean CompruebaFicheroPreferencias() {
		boolean existeFichero = false;

		File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		existeFichero = f.exists();
		if (existeFichero == false) {
			// Logger.debug("El fichero de preferencias
			// ("+DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS+") no
			// existe!");
			CrearFicheroPreferencias();
		}

		return existeFichero;
	}

	/***************************************************************************
	 * 
	 * Creamos el fichero de preferencias con los valores por defecto
	 * 
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private synchronized void CrearFicheroPreferencias() {
		// preferencias por defecto
		m_preferencias = Preferences.systemNodeForPackage(this.getClass());

		InicializaTablaPreferencias();

		// recorremos la tabla cargada con las preferencias por defecto
		Iterator itr = tablaPreferencias.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, String> e = (Map.Entry) itr.next();

			m_preferencias.put(e.getKey(), e.getValue());
		}

		FileOutputStream outputStream = null;
		File fichero;
		try {
			fichero = new File(DIRECTORIO_PREFERENCIAS);
			if (fichero.exists() == false)
				if (fichero.mkdirs() == false) {
					throw new java.io.IOException(
							"No se puede crear el directorio de las preferencias.");
				}

			outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "/"
					+ FICHERO_PREFERENCIAS);
			m_preferencias.exportNode(outputStream);
		} catch (Exception e) {
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
			}
		}
	}

	public void recargaPreferencias() throws Exception {
		CargarPreferencias();
	}

	private String getValueFromTablaPreferencias(String key) {
		String toReturn = "";

		if (tablaPreferencias.containsKey(key)) {
			toReturn = tablaPreferencias.get(key);
		}
		return toReturn;
	}

	private synchronized void setValueIntoTablaPreferencias(String key,
			String value) {
		tablaPreferencias.put(key, value);
	}

	// Este método devolverá la instancia de clase.
	public synchronized static Preferencias getPreferencias() throws Exception {
		if (_pref == null) {
			throw new Exception("No se han podido recuperar las preferencias.");
		}
		_pref.CargarPreferencias();
		return _pref;
	}

	public String getEsquemaBaseDatos() {
		return getValueFromTablaPreferencias(KEY_PREF_ESQUEMA);
	}

	public void setEsquemaBaseDatos(String esquema) {
		setValueIntoTablaPreferencias(KEY_PREF_ESQUEMA, esquema);
	}

	public String getpAConsulta() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCALMACENADO_CONSULTA);
	}

	public void setpAConsulta(String nomProcedimiento) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCALMACENADO_CONSULTA,
				nomProcedimiento);
	}

	public String getEndPointLanzador() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR);
	}

	public void setEndPointLanzador(String endPointLanzador) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR, endPointLanzador);
	}

	public String getModoLog() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG);
	}

	public void setModoLog(String modo) {
		setValueIntoTablaPreferencias(KEY_PREF_LOG, modo);
	}

	public String getDirectorioLog()
	{
		return getValueFromTablaPreferencias(KEY_PREF_LOG_DIR);
	}
	
	public static String getNombreAplicacion()
	{
		return NOMBRE_APLICACION;
	}
	public static String getDirectorioAplicacion()
	{
		return DIRECTORIO_APLICACION;
	}
    public String getPAAltaDocumento () {
    	return getValueFromTablaPreferencias(NOMBRE_PREF_PROCALMACENADO_ALTADOC);
    };
}