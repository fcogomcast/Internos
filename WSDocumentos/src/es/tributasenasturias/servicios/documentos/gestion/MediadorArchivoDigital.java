package es.tributasenasturias.servicios.documentos.gestion;

import javax.xml.ws.Holder;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;


import es.tributasenasturias.services.ws.archivodigital.archivodigital.ArchivoDigital;
import es.tributasenasturias.services.ws.archivodigital.archivodigital.ArchivoDigital_Service;
import es.tributasenasturias.services.ws.archivodigital.archivodigital.CSVType;
import es.tributasenasturias.services.ws.archivodigital.archivodigital.CertificadoType;
import es.tributasenasturias.servicios.documentos.custodia.types.FirmaType;
import es.tributasenasturias.servicios.documentos.exceptions.ArchivoDigitalException;
import es.tributasenasturias.servicios.documentos.impresion.soap.handler.HandlerUtil;
import es.tributasenasturias.servicios.documentos.impresion.utils.Base64;
import es.tributasenasturias.servicios.documentos.impresion.utils.XMLUtils;
import es.tributasenasturias.servicios.documentos.preferencias.Preferencias;

/**
 * Permite la invocación de operaciones del Archivo Digital
 * 
 * @author crubencvs
 * 
 */
public class MediadorArchivoDigital {
	
	public static class AltaArchivoDigitalResponse{
		
		public AltaArchivoDigitalResponse(Holder<Integer> idAdar,
										  Holder<String> csvAdar,
										  Holder<String> hashAdar,
										  Holder<String> mensajeError){
			if (mensajeError!=null && !"".equals(mensajeError.value) && mensajeError.value!=null ){
				error=true;
			} else if (idAdar!=null && idAdar.value==0){
				error=true;
				this.mensajeError="Se ha recibido identificador de custodia \"0\"";
			} else {
				if (idAdar!=null){
					this.idAdar=idAdar.value.toString();
				}
				if (csvAdar!=null){
					this.csvAdar= csvAdar.value;
				}
				if (hashAdar!=null){
					this.hashAdar= hashAdar.value;
				}
				this.mensajeError="";
				error=false;
			}
		}
		
		private String idAdar;
		private String csvAdar;
		private String hashAdar;
		private String mensajeError;
		private boolean error;
		
		public String getIdAdar() {
			return idAdar;
		}
		public void setIdAdar(String idAdar) {
			this.idAdar = idAdar;
		}
		public String getCsvAdar() {
			return csvAdar;
		}
		public void setCsvAdar(String csvAdar) {
			this.csvAdar = csvAdar;
		}
		public String getHashAdar() {
			return hashAdar;
		}
		public void setHashAdar(String hashAdar) {
			this.hashAdar = hashAdar;
		}
		public String getMensajeError() {
			return mensajeError;
		}
		public void setMensajeError(String mensajeError) {
			this.mensajeError = mensajeError;
		}
		public boolean esError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		
	}
	
	/**
	 * Recupera el contenido de un documento del archivo digital
	 * 
	 * @param idAdar
	 *            Identificador de custodia en el archivo digital
	 * @param pref
	 * @return
	 * @throws Exception
	 */
	public String recuperarDocumentoArchivoDigital(String idAdar)
			throws ArchivoDigitalException {
		Preferencias pref = new Preferencias();
		ArchivoDigital_Service srv = new ArchivoDigital_Service();
		ArchivoDigital port = srv.getArchivoDigitalSOAP();
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port;
		bpr.getRequestContext().put(
				javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				pref.getEndpointArchivoDigital());
		HandlerUtil.setHandlerClient((javax.xml.ws.BindingProvider) port);
		
		String obtenerSoloDatosArchivo = "N";
		Holder<byte[]> contenidoArchivo = new Holder<byte[]>(); // Salida
		Holder<String> datosArchivo = new Holder<String>();
		Holder<String> error = new Holder<String>();
		port.obtieneArchivoPorId("USU_WEB_SAC", Integer.parseInt(idAdar),
				obtenerSoloDatosArchivo, contenidoArchivo, datosArchivo, error);
		if (error != null && error.value != null && error.value != "") {
			throw new ArchivoDigitalException(
					"Error al recuperar el fichero del archivo digital:"
							+ error.value);
		}
		if (contenidoArchivo == null || "".equals(contenidoArchivo)) {
			throw new ArchivoDigitalException(
					"Error al recuperar el fichero del archivo digital, no hay contenido");
		}
		return new String(Base64.encode(contenidoArchivo.value));
	}
	
	/**
	 * Realiza el alta de un documento en archivo digital, con origen reimpresión
	 * @param idElemento Identificador de elemento Tributas
	 * @param codigoUsuario Código de usuario que realiza la operación
	 * @param Tipo de elemento
	 * @param contenidoDocumento Contenido del documento a custodiar
	 * @param paramFirma Opcional. Parámetros de firma:
	 *                   <br/>
	 *                   Firma por CSV (S/N)
	 *                   Firmante en la firma por CSV
	 *                   Firma por Certificado de sello(S/N)
	 *                   Firma LTV (S/N)
	 * @throws ArchivoDigitalException
	 */
	public AltaArchivoDigitalResponse altaArchivoDigital(String idElementoTributas,
								                         String codigoUsuario,
								                         String tipoElemento,
								                         byte[] contenidoDocumento,
								                         FirmaType paramFirma) throws ArchivoDigitalException{
		Preferencias pref = new Preferencias();
		
		Holder<Integer> idArchivo=new Holder<Integer>();
		Holder<String> csv = new Holder<String>();
		Holder<String> hash= new Holder<String>();
		Holder<String> error=new Holder<String>();
		
		try {
			ArchivoDigital_Service srv = new ArchivoDigital_Service();
			ArchivoDigital port = srv.getArchivoDigitalSOAP();
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port;
			bpr.getRequestContext().put(
					javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					pref.getEndpointArchivoDigital());
			HandlerUtil.setHandlerClient((javax.xml.ws.BindingProvider) port);
			
			port.custodia(codigoUsuario,
					      idElementoTributas, 
					      tipoElemento, 
					      idElementoTributas+".pdf",
					      "N", 
					      contenidoDocumento, 
					      "", // No se pasa hash de PDF. Ya no es necesario
					      preparaMetadatos(idElementoTributas),
					      prepararParametrosFirma(pref, paramFirma), 
					      idArchivo, csv, hash, error);
			return new AltaArchivoDigitalResponse(idArchivo, csv, hash, error);
		} catch (Exception e){
			throw new ArchivoDigitalException ("Error en interfaz con el alta de archivo digital:"+ e.getMessage(),e);
		}
		
	}
	
	/**
	 * Constructor de los parámetros de firma, en base a los parámetros de firma que hayan llegado
	 * al servicio, o bien a las preferencias de servicio
	 * @param pref
	 * @param paramFirma
	 * @return
	 */
	private es.tributasenasturias.services.ws.archivodigital.archivodigital.FirmaType prepararParametrosFirma(Preferencias pref, FirmaType paramFirma){
		es.tributasenasturias.services.ws.archivodigital.archivodigital.FirmaType firma= new es.tributasenasturias.services.ws.archivodigital.archivodigital.FirmaType();
		if (paramFirma!=null){
			CertificadoType certificadoType= new CertificadoType();
			certificadoType.setFirmaCertificado(paramFirma.getCertificado().getFirmaCertificado());
			certificadoType.setLTV(paramFirma.getCertificado().getLTV());
			
			CSVType csvType = new CSVType();
			csvType.setFirmaCSV(paramFirma.getCSV().getFirmaCSV());
			csvType.setFirmante(paramFirma.getCSV().getFirmante());
			firma.setCertificado(certificadoType);
			firma.setCSV(csvType);
		} else { //Se intenta sacar de preferencias
			CertificadoType certificadoType= new CertificadoType();
			certificadoType.setFirmaCertificado(pref.getFirmarCertificado());
			certificadoType.setLTV(pref.getFirmarLTV());
			
			CSVType csvType = new CSVType();
			csvType.setFirmaCSV(pref.getFirmarCSV());
			csvType.setFirmante(pref.getFirmanteCSV());
			firma.setCertificado(certificadoType);
			firma.setCSV(csvType);
		}
		return firma;
	}
	
	/**
	 * Prepara los metadatos a almacenar para el reimprimible.
	 * @param idGdre Identificador de reimprimible
	 * @return
	 * @throws Exception
	 */
	private String preparaMetadatos(String idGdre) throws Exception{
		MediadorBaseDatos bd= new MediadorBaseDatos();
		String payload=bd.recuperaMetadatosGdre(idGdre);
		//Puede venir escapado
		if (payload!=null){
			Document docMetadatos= XMLUtils.getXMLDoc(XMLUtils.unescapeXmlString(payload));
			//Hay que extraer el contenido del nodo, que será a su vez un xml
			String contenido = (String)XPathFactory.newInstance().newXPath().evaluate("/*[1]/text()", docMetadatos,XPathConstants.STRING);
			return contenido;
		} else {
			return "";
		}
	}
	/**
	 * Custodia de una versión de documento en el archivo digital
	 * @param idElementoTributas
	 * @param codigoUsuario
	 * @param tipoElemento
	 * @param idAdarPadre
	 * @param contenidoDocumento
	 * @param paramFirma
	 * @return
	 * @throws ArchivoDigitalException
	 */
	public AltaArchivoDigitalResponse custodiaVersion(
		    String idElementoTributas, 
		    String codigoUsuario,
			String tipoElemento,
			String idAdarPadre,
			byte[] contenidoDocumento, 
			FirmaType paramFirma)
			throws ArchivoDigitalException {
		Preferencias pref = new Preferencias();

		Holder<Integer> idArchivo = new Holder<Integer>();
		Holder<String> csv = new Holder<String>();
		Holder<String> hash = new Holder<String>();
		Holder<String> error = new Holder<String>();

		try {
			ArchivoDigital_Service srv = new ArchivoDigital_Service();
			ArchivoDigital port = srv.getArchivoDigitalSOAP();
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port;
			bpr.getRequestContext().put(
					javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					pref.getEndpointArchivoDigital());
			HandlerUtil.setHandlerClient((javax.xml.ws.BindingProvider) port);

			port.custodiaVersion(codigoUsuario, 
								 idElementoTributas, 
								 tipoElemento,
								 idElementoTributas + ".pdf", 
								 "N",
								 contenidoDocumento,
								 "", // No se pasa hash de PDF. Ya no es necesario
								 preparaMetadatos(idElementoTributas),
								 Integer.parseInt(idAdarPadre),
								 prepararParametrosFirma(pref, paramFirma), 
								 idArchivo, 
								 csv,
								 hash, 
								 error);
			return new AltaArchivoDigitalResponse(idArchivo, csv, hash, error);
		} catch (Exception e) {
			throw new ArchivoDigitalException(
					"Error en interfaz con el alta de archivo digital:"
							+ e.getMessage(), e);
		}

	}

}
