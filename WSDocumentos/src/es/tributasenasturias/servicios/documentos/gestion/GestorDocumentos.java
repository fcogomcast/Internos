package es.tributasenasturias.servicios.documentos.gestion;

import javax.xml.ws.Holder;


import es.tributasenasturias.servicios.client.doinDocumentos.WSConsultaDoinDocumentos;
import es.tributasenasturias.servicios.client.doinDocumentos.WSConsultaDoinDocumentos_Service;
import es.tributasenasturias.servicios.documentos.exceptions.DocumentoException;
import es.tributasenasturias.servicios.documentos.impresion.soap.handler.HandlerUtil;
import es.tributasenasturias.servicios.documentos.log.Logger;
import es.tributasenasturias.servicios.documentos.preferencias.Preferencias;

public class GestorDocumentos {

	public static boolean altaDocumento(String nombre,String tipo, String codigoVerificacion,String nifSP, String nifPR, String tipoDocumento, String idSesion,boolean comprimido,String documento ) throws DocumentoException
	{
		 Holder<String> docOut= new Holder<String>();
		 Holder<String> error = new Holder<String>();
		 Holder<String> resultado = new Holder<String>();
		 boolean altaCorrecta=false;
		 try{
			Preferencias pref = new Preferencias();
			WSConsultaDoinDocumentos_Service srv = new WSConsultaDoinDocumentos_Service();
			WSConsultaDoinDocumentos port = srv.getWSConsultaDoinDocumentosSOAP();
			// enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port;
			// Cambiamos el endpoint
			bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointAltaDocumento());

	        //Vinculamos con el Handler	        
	        HandlerUtil.setHandlerClient((javax.xml.ws.BindingProvider) port);
	        try {	        	
	        	port.altaDoinDocumento(nombre, tipo, codigoVerificacion, nifSP, nifPR, documento, tipoDocumento, idSesion,comprimido, docOut, error, resultado);
	        	if (!"0000".equals(error.value))
	        	{
	        		Logger.error("El alta de documento ("+nombre+"): ha devuelto error:"+error.value+"-"+resultado.value,Logger.LOGTYPE.APPLOG);
	        	}
	        	else
	        	{
	        		altaCorrecta=true;
	        	}
	        }catch (Exception ex) {
	        		throw new DocumentoException ("Error al dar de alta el documento ("+nombre+"): "+ex.getMessage(), ex);
	        }
		} catch (Exception e) {
			throw new DocumentoException ("Error al dar de alta el documento ("+nombre+"): "+e.getMessage(), e);
		}
		return altaCorrecta;
	}
}
