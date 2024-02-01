package es.tributasenasturias.servicios.documentos.operaciones;

import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;

import es.tributasenasturias.servicios.documentos.exceptions.ArchivoDigitalException;
import es.tributasenasturias.servicios.documentos.exceptions.ImpresionGDException;
import es.tributasenasturias.servicios.documentos.gestion.MediadorArchivoDigital;
import es.tributasenasturias.servicios.documentos.impresion.DocumentoGD;
import es.tributasenasturias.servicios.documentos.impresion.soap.handler.HandlerUtil;
import es.tributasenasturias.servicios.documentos.impresion.utils.Base64;
import es.tributasenasturias.servicios.documentos.impresion.utils.ConversorParametrosLanzador;
import es.tributasenasturias.servicios.documentos.impresion.utils.GZIPImplWeb;
import es.tributasenasturias.servicios.documentos.impresion.utils.XMLUtils;
import es.tributasenasturias.servicios.documentos.log.Logger;
import es.tributasenasturias.servicios.documentos.preferencias.Preferencias;
import es.tributasenasturias.webservices.lanzador.clients.LanzaPLMasivo;
import es.tributasenasturias.webservices.lanzador.clients.LanzaPLMasivoService;


/**
 * Permite la reimpresión de un documento de tributas. Recuperará el xml de documento
 * preparado para la impresión que será ofrecido por la base de datos.
 * 14/01/2021. 41361: si la reimpresión se encuentra en archivo digital, se recupera 
 * de ahí el contenido, y es el que se muestra.
 * @author crubencvs
 *
 */
public class Reimpresion {

	private Reimpresion()
	{}
	/**
	 * 41361
	 * Clase para almacenar la recuperación de datos de la reimpresión
	 * desde base de datos. Puede contener o bien el XML con datos de la plantilla,
	 * o el id_adar del archivo digital, para poder consultarlo. 
	 */
	private static class RespuestaDatosReimpresion{
		private boolean custodiadoAD;
		private String idAdar;
		private Document xmlDatos;
		public RespuestaDatosReimpresion(boolean custodiadoAD, String idAdar, Document xmlDatos){
			this.custodiadoAD= custodiadoAD;
			this.idAdar= idAdar;
			this.xmlDatos= xmlDatos;
		}
		public boolean estaCustodiadoAD() {
			return custodiadoAD;
		}
		public void setEstaCustodiadoAD(boolean custodiadoAD) {
			this.custodiadoAD = custodiadoAD;
		}
		public String getIdAdar() {
			return idAdar;
		}
		public void setIdAdar(String idAdar) {
			this.idAdar = idAdar;
		}
		public Document getXmlDatos() {
			return xmlDatos;
		}
		public void setXmlDatos(Document xmlDatos) {
			this.xmlDatos = xmlDatos;
		}
		
	}
	/**
	 * Método generador. No se utilizará directamente el constructor, por si en el futuro 
	 * fuera necesario alguna inicialización compleja.
	 * @return
	 */
	public static Reimpresion newReimpresion()
	{
		return new Reimpresion();
	}
	
	/**
	 * Recupera el PDF reimprimido.
	 * @param elemento Elemento identificador del reimprimible
	 * @param tipo Tipo del reimprimible
	 * @param codigoVerificacion Código de verificación a imprimir en cada página.
	 * @param consultarAD Indica si se consultará el documento en archivo digital, si está.
	 * @return Una cadena en base 64 con el contenido del pdf.
	 * @throws ImpresionGDException
	 */
	public String getPDFReimprimido(String elemento, String tipo, String codigoVerificacion, boolean consultarAD) throws ImpresionGDException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		RespuestaDatosReimpresion r;
		try {
			r = getXmlReimpresion (elemento, tipo);
		}catch (Exception e){
			throw new ImpresionGDException ("Error al recuperar el XML de base de datos:"+ e.getMessage(),e);
		}
		String pdf="";
		if (r.estaCustodiadoAD() && consultarAD){
			MediadorArchivoDigital ad= new MediadorArchivoDigital();
			try {
				pdf= ad.recuperarDocumentoArchivoDigital(r.getIdAdar());
			} catch (ArchivoDigitalException ade){
				throw new ImpresionGDException ("Error al recuperar documento custodiado"+ade.getMessage(), ade);
			}
		} else {
			//Creamos el objeto DocumentoGD y generamos el PDF
			DocumentoGD gd = new DocumentoGD(r.getXmlDatos(), buffer);
			gd.setCodigoVerificacion(codigoVerificacion);
			byte [] res = gd.Generar();
			char[] c=null;
			if (res!=null)
			{
				c = Base64.encode(res);
			}
			pdf= new String(c);
		}
		return pdf;
	}
	
	/**
	 * Recupera el PDF reimprimido.
	 * @param idGdre Reimprimible.
	 * @param tipo Tipo del reimprimible
	 * @param codigoVerificacion Código de verificación a imprimir en cada página.
	 * @param consultarAD
	 * @return Una cadena en base 64 con el contenido del pdf.
	 * @throws ImpresionGDException
	 */
	public String getPDFReimprimido(String idGdre, String codigoVerificacion, boolean consultarAD) throws ImpresionGDException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		RespuestaDatosReimpresion r;
		try {
			r = getXmlReimpresion (idGdre);
		} catch (Exception e){
			throw new ImpresionGDException ("Error al recuperar el XML de base de datos:" + e.getMessage(),e);
		}
		
		String pdf="";
		if (r.estaCustodiadoAD() && consultarAD){
			MediadorArchivoDigital ad= new MediadorArchivoDigital();
			try {
				pdf= ad.recuperarDocumentoArchivoDigital(r.getIdAdar());
			} catch (ArchivoDigitalException ade){
				throw new ImpresionGDException ("Error al recuperar documento custodiado"+ade.getMessage(), ade);
			}
		}  else {
			//Creamos el objeto DocumentoGD y generamos el PDF
			DocumentoGD gd = new DocumentoGD(r.getXmlDatos(), buffer);
			gd.setCodigoVerificacion(codigoVerificacion);
			byte [] res = gd.Generar();
			char[] c=null;
			if (res!=null)
			{
				c = Base64.encode(res);
			}
			pdf= new String(c);
		}
		return pdf;
	}
	
	/**
	 * Recupera el documento xml con los datos necesarios para la reimpresión.
	 * @param elemento Elemento identificador del reimprimible
	 * @param tipo Tipo del reimprimible.
	 * @return
	 */
	private RespuestaDatosReimpresion getXmlReimpresion (String elemento, String tipo) throws Exception
	{
		
	 String idAdar=null;
     String clob=null;
     boolean custodiadoAD=false;	
	 Document doc=null;

	 try{
		ConversorParametrosLanzador cpl;
		Preferencias pref = new Preferencias();
		cpl = new ConversorParametrosLanzador();
        cpl.setProcedimientoAlmacenado(pref.getPaGetReimprimible());
        //Elemento
        cpl.setParametro(elemento,ConversorParametrosLanzador.TIPOS.String);
        // Tipo
        cpl.setParametro(tipo,ConversorParametrosLanzador.TIPOS.String);
        //Conexion
        cpl.setParametro("P",ConversorParametrosLanzador.TIPOS.String);

        LanzaPLMasivoService lanzaderaWS = new LanzaPLMasivoService();					
		LanzaPLMasivo lanzaderaPort;			
		lanzaderaPort = lanzaderaWS.getLanzaPLMasivoSoapPort();
        
		// enlazador de protocolo para el servicio.
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
		// Cambiamos el endpoint
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointLanzador());
        String respuesta = "";	

        //Vinculamos con el Handler	        
        HandlerUtil.setHandlerClient((javax.xml.ws.BindingProvider) lanzaderaPort);
        try {	        	
        	respuesta = lanzaderaPort.executePL(pref.getEntorno(), cpl.Codifica(), "", "", "", "");
        	cpl.setResultado(respuesta);	
        	// 41361. Comprobamos si lo que nos llega es un aviso de que la reimpresión
        	// está custodiada en Archivo Digital. En ese caso, hay que recuperar
        	// el documento del archivo digital.
        	String almacenReimpresion= cpl.getNodoResultado("STRING1_CANU");
        	if ("AD".equalsIgnoreCase(almacenReimpresion)){
        		//Está custodiado en Archivo Digital
        		idAdar= cpl.getNodoResultado("NUME1_CANU");
        		custodiadoAD=true;
        	} else { 
        		//Estará sólo en reimpresión
        		custodiadoAD=false;
        		//Convertimos la respuesta en un XML
            	clob=cpl.getNodoResultado("CLOB_DATA");
            	if ("".equals(clob)){
            		throw new Exception ("El XML a imprimir que llega de Base de Datos está vacío");
            	}
            	doc= XMLUtils.getXMLDoc(GZIPImplWeb.descomprimeWeb(clob));
        	}
        }catch (Exception ex) {
        		Logger.error("Error en lanzadera al recuperar datos del xml para reimprimir: "+ex.getMessage(),Logger.LOGTYPE.APPLOG);
        		Logger.trace(ex.getStackTrace(), Logger.LOGTYPE.APPLOG);
        		throw ex;
        }
	} catch (Exception e) {
		Logger.error("Excepcion generica al recuperar los datos del xml para reimprimir: "+e.getMessage(),Logger.LOGTYPE.APPLOG);
		throw e;
	}
	    RespuestaDatosReimpresion r = new RespuestaDatosReimpresion(custodiadoAD, idAdar, doc);
	    
		return r;
	}
	
	/**
	 * Recupera el documento xml con los datos necesarios para la reimpresión.
	 * @param idGdre Elemento identificador del reimprimible
	 * @return
	 */
	private RespuestaDatosReimpresion getXmlReimpresion (String idGdre) throws Exception
	{
		
	 String idAdar=null;
	 String clob=null;
	 boolean custodiadoAD=false;
	 Document doc=null;

	 try{
		ConversorParametrosLanzador cpl;
		Preferencias pref = new Preferencias();
		cpl = new ConversorParametrosLanzador();
        cpl.setProcedimientoAlmacenado(pref.getPaGetReimprimibleGDRE());
        //Id Gdre
        cpl.setParametro(idGdre,ConversorParametrosLanzador.TIPOS.Integer);
        //Conexion
        cpl.setParametro("P",ConversorParametrosLanzador.TIPOS.String);

        LanzaPLMasivoService lanzaderaWS = new LanzaPLMasivoService();					
		LanzaPLMasivo lanzaderaPort;			
		lanzaderaPort = lanzaderaWS.getLanzaPLMasivoSoapPort();
        
		// enlazador de protocolo para el servicio.
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
		// Cambiamos el endpoint
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointLanzador());
        String respuesta = "";	

        //Vinculamos con el Handler	        
        HandlerUtil.setHandlerClient((javax.xml.ws.BindingProvider) lanzaderaPort);
        try {	        	
        	respuesta = lanzaderaPort.executePL(pref.getEntorno(), cpl.Codifica(), "", "", "", "");
        	cpl.setResultado(respuesta);	
        	// 41361. Comprobamos si lo que nos llega es un aviso de que la reimpresión
        	// está custodiada en Archivo Digital. En ese caso, hay que recuperar
        	// el documento del archivo digital.
        	String almacenReimpresion= cpl.getNodoResultado("STRING1_CANU");
        	if ("AD".equalsIgnoreCase(almacenReimpresion)){
        		//Está custodiado en Archivo Digital
        		idAdar= cpl.getNodoResultado("NUME1_CANU");
        		custodiadoAD=true;
        	} else {
        		custodiadoAD=false;
        	}
        	//CRUBENCVS  25/01/2022 44233. Sólo recuperamos datos de reimpresión si no está custodiado en archivo digital
        	if (!custodiadoAD){
	        	//Convertimos la respuesta en un XML
	        	clob=cpl.getNodoResultado("CLOB_DATA");
	        	if ("".equals(clob)){
	        		throw new Exception ("El XML a imprimir que llega de Base de Datos está vacío");
	        	}
	        	doc= XMLUtils.getXMLDoc(GZIPImplWeb.descomprimeWeb(clob));
        	}
        	//FIN CRUBENCVS 25/01/2022 44233
        }catch (Exception ex) {
        		Logger.error("Error en lanzadera al recuperar datos del xml para reimprimir: "+ex.getMessage(),Logger.LOGTYPE.APPLOG);
        		Logger.trace(ex.getStackTrace(), Logger.LOGTYPE.APPLOG);
        		throw ex;
        }
	} catch (Exception e) {
		Logger.error("Excepcion generica al recuperar los datos del xml para reimprimir: "+e.getMessage(),Logger.LOGTYPE.APPLOG);
		throw e;
	}
	    RespuestaDatosReimpresion r = new RespuestaDatosReimpresion(custodiadoAD, idAdar, doc);
		return r;
	}
	
	/** Construye un PDF a partir de la un XML formado por la plantilla de gestor documental con los datos ya preparados para imprimir 
	 * 
	 * @param gzippedData Cadena en hexadecimal con el XML de plantilla rellena con datos y comprimida, para que ocupe menos espacio en los mensajes, y no puedan modificarse los caracteres por cambio de codificación
	 * @return
	 * @throws ImpresionGDException
	 */
	public String construirPDFFromData(String gzippedData) throws ImpresionGDException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
		Document xml = XMLUtils.getXMLDoc(GZIPImplWeb.descomprimeWeb(gzippedData));
		
		String pdf="";
		
		//Creamos el objeto DocumentoGD y generamos el PDF
		DocumentoGD gd = new DocumentoGD(xml, buffer);
		byte [] res = gd.Generar();
		char[] c=null;
		if (res!=null)
		{
			c = Base64.encode(res);
		}
		pdf= new String(c);
		
		return pdf;
		} catch (Exception ex){
			throw new ImpresionGDException ("Error en  impresión de pdf ", ex);
		}
	}
}
