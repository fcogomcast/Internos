package es.tributasenasturias.servicios.documentos.operaciones;

import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import es.tributasenasturias.servicios.documentos.exceptions.ImpresionGDException;
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
 * Permite la impresión de un documento utilizando el gestor Documental.
 * @author crubencvs
 *
 */
public class ImpresionGD {

	/**
	 * No se llama directamente.
	 */
	private ImpresionGD()
	{
	}
	/**
	 * Clase para mantener la información del documento de impresión.
	 * xmlDatos : {@link Document} Xml con los datos necesarios para imprimir el documento.
	 * xmlAltaDocumento: {@link Document} Xml con los datos necesarios para realizar el alta de documento en base de datos
	 * docb64: Documento (PDF) en formato base 64.
	 * @author crubencvs
	 *
	 */
	public static class InfoDocImpresion
	{
		private Document xmlDatos;
		private Node xmlAltaDocumento;
		private String docb64;
		public String getDocb64() {
			return docb64;
		}
		public void setDocb64(String docb64) {
			this.docb64 = docb64;
		}
		public Document getXmlDatos() {
			return xmlDatos;
		}
		public void setXmlDatos(Document xmlDatos) {
			this.xmlDatos = xmlDatos;
		}
		public Node getXmlAltaDocumento() {
			return xmlAltaDocumento;
		}
		public void setXmlAltaDocumento(Node xmlAltaDocumento) {
			this.xmlAltaDocumento = xmlAltaDocumento;
		}
	}
	/**
	 * Utilizado para devolver una nueva instancia del objeto.
	 * @return
	 */
	public static ImpresionGD newImpresionGD()
	{
		return new ImpresionGD();
	}
	
	/**
	 * Recupera el PDF impreso.
	 * @param origenDatos la cadena de petición a base de datos que devolverá los datos del informe.
	 * @param codigoVerificacion El código de verificación a imprimir en el documento, o null si no hay ninguno.
	 * @return {@link InfoDocImpresion} con el contenido del PDF, el XML que lo originó y en su caso la información necesaria para almacenamiento en base de datos.
	 * @throws ImpresionGDException
	 */
	public InfoDocImpresion getPDFImpresion(String origenDatos, String codigoVerificacion, boolean altaDocumento) throws ImpresionGDException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InfoDocImpresion info= getXmlImpresion (origenDatos);

		//Creamos el objeto DocumentoGD y generamos el PDF
		DocumentoGD gd = new DocumentoGD(info.getXmlDatos(), buffer);
		//Si no se da de alta el documento, se tiene en cuenta el código de verificación que se haya
		//pasado (que total será inútil porque no quedará el documento guardado)
		if (!altaDocumento || (altaDocumento && !"".equals(codigoVerificacion)))
		{
			gd.setCodigoVerificacion(codigoVerificacion);
		}
		else if (altaDocumento && "".equals(codigoVerificacion)) //Si se va a dar de alta, es necesario que coincida lo guardado con el generado. Se añade al PDF
		{
			//El código de verificación será el tipo + nombre + mac
			//Vendrá en el campo "LIBRE" de la estructura
			String codVerif =XMLUtils.getNodeText(XMLUtils.selectSingleNode(info.getXmlAltaDocumento(), "LIBRE")); 
			gd.setCodigoVerificacion(codVerif);
		}
		byte [] res = gd.Generar();
		char[] c=null;
		if (res!=null)
		{
			c = Base64.encode(res);
		}
		info.setDocb64(new String(c));
		return info;
	}
	private InfoDocImpresion getXmlImpresion (String origenDatos)
	{
	 Document xmlDatos=null;
	 InfoDocImpresion info = new InfoDocImpresion();
	 try{
		ConversorParametrosLanzador cpl;
		Preferencias pref = new Preferencias();
		cpl = new ConversorParametrosLanzador();
		cpl.setProcedimientoAlmacenado(pref.getPaImpresion());
        //Origen
		//Se ha de pasar como base64 para que no haya problemas, ya que la petición
		//al lanzador se pasa como texto, y puede haber problemas por tener xml codificado en texto
		// (el origen de datos) dentro de otro xml Codificado en texto (petición).
		//Ejemplo: &lt;peti&gt;&lt;proc nombre="ImpresionGDSW.getxmlImpresion"&gt;&lt;param id="1"&gt;&lt;valor&gt;&lt;peti&gt; ...
        cpl.setParametro(GZIPImplWeb.comprimeWeb(origenDatos),ConversorParametrosLanzador.TIPOS.Clob);
        cpl.setParametro("S",ConversorParametrosLanzador.TIPOS.String); // Comprimido
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
        	//Convertimos la respuesta en un XML
        	String clob=cpl.getNodoResultado("CLOB_DATA");
        	xmlDatos= XMLUtils.getXMLDoc(GZIPImplWeb.descomprimeWeb(clob));
        	//Se recupera sólo la primera fila de la estructura, ya que es la que debería tener los datos.
        	//Si hay más, no tienen información que nos interese.
        	info.setXmlAltaDocumento(cpl.getFila(cpl.getEstruct("EDIN_ESTR_DOCU_INTERNET"),1));
        	info.setXmlDatos(xmlDatos);
        }catch (Exception ex) {
        		Logger.error("Error en lanzadera al recuperar datos del xml para impresión de gestor documental: "+ex.getMessage(),Logger.LOGTYPE.APPLOG);
        		Logger.trace(ex.getStackTrace(), Logger.LOGTYPE.APPLOG);
        }
	} catch (Exception e) {
		Logger.error("Excepcion generica al recuperar los datos del xml para impresión de gestor documental: "+e.getMessage(),Logger.LOGTYPE.APPLOG);
	}
		return info;
	}
}
