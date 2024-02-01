package es.tributasenasturias.servicios.documentos.preferencias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.prefs.Preferences;

public final class Preferencias {

	private Preferences m_preferencias;
	private String m_debug;
	private String m_entorno;

	private String m_endpoint_lanzador;
	private String m_namespace_lanzador;
	private String m_servicename_lanzador;
	private String m_pa_get_reimprimible;
	private String m_dir_imagenes;
	private String m_pa_impresion;
	private String m_endpoint_alta;
    // CRUBENCVS 39496
	private String m_pa_get_reimprimible_gdre;
	//41361
	private String m_pa_actualizar_adar_reimpresion;
	private String m_endpoint_archivo_digital;
	private String m_firmar_csv;
	private String m_firmante_csv;
	private String m_firmar_certificado;
	private String m_firmar_ltv;
	private String m_pa_metadatos_gdre;
	
	// Variables relacionads con la firma.
	
	// constantes para trabajar con las preferencias
	private final String FICHERO_PREFERENCIAS = "prefsDocumentos.xml";
	private final String DIRECTORIO_PREFERENCIAS = "proyectos/WSDocumentos";
	private final String NOMBRE_PREF_DEBUG = "Debug";
	private final String NOMBRE_PREF_ENTORNO = "Entorno";
	private final String NOMBRE_PREF_ENDPOINT_LANZADOR = "EndPointLanzador";
	private final String NOMBRE_PREF_PA_GET_REIMPRIMIBLE = "pAGetReimprimible";
	private final String NOMBRE_PREF_DIR_IMAGENES = "dirImagenes";
	private final String NOMBRE_PREF_WSLANZADOR_NAMESPACE = "NameSpaceLanzador";
	private final String NOMBRE_PREF_WSLANZADOR_SERVICE_NAME = "ServiceNameLanzador";				
	private final String NOMBRE_PREF_PAIMPRESION = "pAImpresionGD";
	private final String NOMBRE_PREF_ENDPOINT_ALTA = "EndPointAltaDocumento";

	// CRUBENCVS 39496
	private final String NOMBRE_PREF_PA_GET_REIMPRIMIBLE_GDRE= "paGetReimprimibleGDRE";
	
	// 41361
	private final String NOMBRE_PREF_PA_ACTUALIZAR_ADAR_REIMPRESION="pAActualizarAdarReimpresion";
	private final String NOMBRE_PREF_ENDPOINT_ARCHIVO_DIGITAL = "EndpointArchivoDigital";
	private final String NOMBRE_PREF_FIRMAR_CSV = "CustodiaFirmarCSV";
	private final String NOMBRE_PREF_FIRMANTE_CSV = "CustodiaFirmanteCSV";
	private final String NOMBRE_PREF_FIRMAR_CERTIFICADO = "CustodiaFirmarCertificado";
	private final String NOMBRE_PREF_FIRMAR_LTV = "CustodiaFirmarLTV";
	private final String NOMBRE_PREF_PA_METADATOS_GDRE = "pAMetadatosGdre";
	
	
	
	private final String VALOR_INICIAL_WSLANZADOR_NAMESPACE = "http://stpa/services";
	private final String VALOR_INICIAL_WSLANZADOR_SERVICE_NAME = "lanzaPLService";
	private final String VALOR_INICIAL_PA_GET_REIMPRIMIBLE = "ReimpresionSWGD.getXMLReimprimible";
	// Escribe aqui el valor inicial del debug (0 = no existe debug, 1 = existe
	// debug)
	private final String VALOR_INICIAL_PREF_DEBUG = "1";
	private final String VALOR_INICIAL_PREF_ENTORNO = "EXPLOTACION";
	private final String VALOR_INICIAL_PREF_ENDPOINT_LANZADOR = "http://bus.explo.epst.pa:7101/WSInternos/ProxyServices/PXLanzador";
	private final String VALOR_INICIAL_DIR_IMAGENES = "proyectos/WSDocumentos/imagenes";
	private final String VALOR_INICIAL_ENDPOINT_ALTA = "http://bus.explo.epst.pa:7101/WSInternos/ProxyServices/PXConsultaDoinDocumentos";
	private final String VALOR_INICIAL_PREF_PAIMPRESION = "impresionGDSW.getXMLImpresion";
	
	// CRUBENCVS 39496
	private final String VALOR_INICIAL_PA_GET_REIMPRIMIBLE_GDRE = "ReimpresionSWGD.getXMLReimprimibleGDRE";

	// CRUBENCVS 41361
	private final String VALOR_INICIAL_PA_ACTUALIZAR_ADAR_REIMPRESION = "GD_UTILIDADES.AD_ActualizarReimpresionGDRE";
	private final String VALOR_INICIAL_PREF_ENDPOINT_ARCHIVO_DIGITAL = "http://bus:7101/WSInternos/ProxyServices/PXArchivoDigital";
	private final String VALOR_INICIAL_PREF_FIRMAR_CSV ="S";
	private final String VALOR_INICIAL_PREF_FIRMANTE_CSV ="";
	private final String VALOR_INICIAL_PREF_FIRMAR_CERTIFICADO ="N";
	private final String VALOR_INICIAL_PREF_FIRMAR_LTV ="N";
	private final String VALOR_INICIAL_PREF_PA_METADATOS_GDRE= "METADATOS.obtenerXmlMetadatosGDRE";
	
	public Preferencias() {
		try {
			CargarPreferencias();
		} catch (Exception e) {		
			e.printStackTrace();
		} 
	}

	public void CompruebaFicheroPreferencias() {

		File f = new File(DIRECTORIO_PREFERENCIAS + "//" + FICHERO_PREFERENCIAS);
		if (f.exists() == false) {
			CrearFicheroPreferencias();
		}
	}

	/***************************************************************************
	 * 
	 * Creamos el fichero de preferencias con los valores por defecto
	 * 
	 **************************************************************************/
	private void CrearFicheroPreferencias() {
		// preferencias por defecto
		m_preferencias = Preferences.systemNodeForPackage(this.getClass());
		m_preferencias.put(NOMBRE_PREF_DEBUG, VALOR_INICIAL_PREF_DEBUG);
		m_preferencias.put(NOMBRE_PREF_ENTORNO, VALOR_INICIAL_PREF_ENTORNO);

		m_preferencias.put(NOMBRE_PREF_ENDPOINT_LANZADOR,VALOR_INICIAL_PREF_ENDPOINT_LANZADOR);					
		m_preferencias.put(NOMBRE_PREF_WSLANZADOR_NAMESPACE, VALOR_INICIAL_WSLANZADOR_NAMESPACE);
		m_preferencias.put(NOMBRE_PREF_WSLANZADOR_SERVICE_NAME, VALOR_INICIAL_WSLANZADOR_SERVICE_NAME);
		m_preferencias.put(NOMBRE_PREF_WSLANZADOR_SERVICE_NAME, VALOR_INICIAL_WSLANZADOR_SERVICE_NAME);
		m_preferencias.put(NOMBRE_PREF_PA_GET_REIMPRIMIBLE, VALOR_INICIAL_PA_GET_REIMPRIMIBLE);
		m_preferencias.put(NOMBRE_PREF_DIR_IMAGENES, VALOR_INICIAL_DIR_IMAGENES);
		m_preferencias.put(NOMBRE_PREF_DIR_IMAGENES, VALOR_INICIAL_DIR_IMAGENES);
		m_preferencias.put(NOMBRE_PREF_PAIMPRESION, VALOR_INICIAL_PREF_PAIMPRESION);
		m_preferencias.put(NOMBRE_PREF_ENDPOINT_ALTA, VALOR_INICIAL_ENDPOINT_ALTA);
		m_preferencias.put(NOMBRE_PREF_PA_GET_REIMPRIMIBLE_GDRE, VALOR_INICIAL_PA_GET_REIMPRIMIBLE_GDRE);
		// 41361
		m_preferencias.put(NOMBRE_PREF_PA_ACTUALIZAR_ADAR_REIMPRESION, VALOR_INICIAL_PA_ACTUALIZAR_ADAR_REIMPRESION);
		m_preferencias.put(NOMBRE_PREF_ENDPOINT_ARCHIVO_DIGITAL, VALOR_INICIAL_PREF_ENDPOINT_ARCHIVO_DIGITAL);
		m_preferencias.put(NOMBRE_PREF_FIRMAR_CSV, VALOR_INICIAL_PREF_FIRMAR_CSV);
		m_preferencias.put(NOMBRE_PREF_FIRMANTE_CSV, VALOR_INICIAL_PREF_FIRMANTE_CSV);
		m_preferencias.put(NOMBRE_PREF_FIRMAR_CERTIFICADO, VALOR_INICIAL_PREF_FIRMAR_CERTIFICADO);
		m_preferencias.put(NOMBRE_PREF_FIRMAR_LTV, VALOR_INICIAL_PREF_FIRMAR_LTV);
		m_preferencias.put(NOMBRE_PREF_PA_METADATOS_GDRE, VALOR_INICIAL_PREF_PA_METADATOS_GDRE);
		
		
		
		FileOutputStream outputStream = null;
		File fichero;
		try {
			fichero = new File(DIRECTORIO_PREFERENCIAS);
			if (fichero.exists() == false)
				fichero.mkdirs();

			outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "//"
					+ FICHERO_PREFERENCIAS);
			m_preferencias.exportNode(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				System.out.println("Error cerrando fichero de preferencias -> "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	// Obtencion de las preferencias que especificaran el almacen y su
	// contraseña
	public void CargarPreferencias() throws Exception {
		File f = new File(DIRECTORIO_PREFERENCIAS + "//" + FICHERO_PREFERENCIAS);
		if (f.exists()) {
			// si existe el fichero de preferencias lo cargamos
			FileInputStream inputStream = new FileInputStream(
					DIRECTORIO_PREFERENCIAS + "//" + FICHERO_PREFERENCIAS);
			Preferences.importPreferences(inputStream);
			inputStream.close();

			m_preferencias = Preferences.systemNodeForPackage(this.getClass());
			// obtenemos las preferencias
			
			m_debug = m_preferencias.get(NOMBRE_PREF_DEBUG, "");
			
			m_entorno = m_preferencias.get(NOMBRE_PREF_ENTORNO, "");
			m_endpoint_lanzador = m_preferencias.get(NOMBRE_PREF_ENDPOINT_LANZADOR, "");
			m_pa_get_reimprimible = m_preferencias.get(NOMBRE_PREF_PA_GET_REIMPRIMIBLE, "");
			m_namespace_lanzador = m_preferencias.get(NOMBRE_PREF_WSLANZADOR_NAMESPACE, "");
			m_servicename_lanzador= m_preferencias.get(NOMBRE_PREF_WSLANZADOR_SERVICE_NAME, "");
			m_dir_imagenes= m_preferencias.get(NOMBRE_PREF_DIR_IMAGENES, "");
			m_pa_impresion= m_preferencias.get(NOMBRE_PREF_PAIMPRESION, "");
			m_endpoint_alta = m_preferencias.get(NOMBRE_PREF_ENDPOINT_ALTA, "");
			// CRUBENCVS 39496
			m_pa_get_reimprimible_gdre = m_preferencias.get(NOMBRE_PREF_PA_GET_REIMPRIMIBLE_GDRE, "");
			// 41361
			m_pa_actualizar_adar_reimpresion = m_preferencias.get(NOMBRE_PREF_PA_ACTUALIZAR_ADAR_REIMPRESION, "");
			m_endpoint_archivo_digital= m_preferencias.get(NOMBRE_PREF_ENDPOINT_ARCHIVO_DIGITAL, "");
			m_firmar_csv = m_preferencias.get(NOMBRE_PREF_FIRMAR_CSV, "");
			m_firmante_csv = m_preferencias.get(NOMBRE_PREF_FIRMANTE_CSV, "");
			m_firmar_certificado= m_preferencias.get(NOMBRE_PREF_FIRMAR_CERTIFICADO, "");
			m_firmar_ltv= m_preferencias.get(NOMBRE_PREF_FIRMAR_LTV, "");
			m_pa_metadatos_gdre= m_preferencias.get(NOMBRE_PREF_PA_METADATOS_GDRE,"");
			
		} else {
			// si no existe el fichero de preferencias lo crearemos
			CrearFicheroPreferencias();

			throw new Exception(
					"Debe especificar primero las preferencias en el fichero: "
							+ f.getAbsolutePath() + " (parar el servicio)");
		}
	}

	public String getDebug() {
		return this.m_debug;
	};

	public String getPaGetReimprimible() {
		return this.m_pa_get_reimprimible;
	};
	
	public String getEntorno() {
		return this.m_entorno;
	};

	public String getEndpointLanzador() {
		return this.m_endpoint_lanzador;
	};
	
	public String getNameSpaceLanzador() {
		return this.m_namespace_lanzador;
	};
	
	public String getServiceNameLanzador () {
		return this.m_servicename_lanzador;
	};
	public String getDirImagenes() {
		return this.m_dir_imagenes;
	};
	public String getPaImpresion() {
		return this.m_pa_impresion;
	};
	public String getEndpointAltaDocumento() {
		return this.m_endpoint_alta;
	};
	// CRUBENCVS 39496
	public String getPaGetReimprimibleGDRE() {
		return this.m_pa_get_reimprimible_gdre;
	};
	// 41361
	
	public String getPaActualizarAdarReimpresion() {
		return this.m_pa_actualizar_adar_reimpresion;
	};
	
	public String getEndpointArchivoDigital() {
		return this.m_endpoint_archivo_digital;
	};
	
	public String getFirmarCSV() {
		return this.m_firmar_csv;
	};
	
	public String getFirmanteCSV() {
		return this.m_firmante_csv;
	};
	
	public String getFirmarCertificado() {
		return this.m_firmar_certificado;
	};
	
	public String getFirmarLTV() {
		return this.m_firmar_ltv;
	};
	
	public String getPaMetadatosGdre() {
		return this.m_pa_metadatos_gdre;
	};
}
