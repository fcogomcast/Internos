package es.tributasenasturias.servicios.documentos.gestion;

import es.tributasenasturias.servicios.documentos.impresion.soap.handler.HandlerUtil;
import es.tributasenasturias.servicios.documentos.impresion.utils.ConversorParametrosLanzador;
import es.tributasenasturias.servicios.documentos.log.Logger;
import es.tributasenasturias.servicios.documentos.preferencias.Preferencias;
import es.tributasenasturias.webservices.lanzador.clients.LanzaPLMasivo;
import es.tributasenasturias.webservices.lanzador.clients.LanzaPLMasivoService;

/**
 * Clase para accesos a base de datos
 * @author crubencvs
 *
 */
public class MediadorBaseDatos {

	private Preferencias pref; 
	public MediadorBaseDatos(){
		this.pref= new Preferencias();
	}
	/** Recupera metadatos de un reimprimible
	 * 
	 * @param idGdre Identificador del reimprimible
	 * @return Cadena de texto con los metadatos (xml como texto) o bien cadena vacía si no hubiese, en principio siempre hay
	 * @throws Exception
	 */
	public String recuperaMetadatosGdre(String idGdre) throws Exception
	{
		String metadatos="";
		try{
			ConversorParametrosLanzador cpl;
			cpl = new ConversorParametrosLanzador();
			cpl.setProcedimientoAlmacenado(pref.getPaMetadatosGdre());
	        cpl.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
	        cpl.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
	        cpl.setParametro("USU_WEB_SAC", ConversorParametrosLanzador.TIPOS.String);
	        cpl.setParametro("33", ConversorParametrosLanzador.TIPOS.Integer);
	        cpl.setParametro(idGdre, ConversorParametrosLanzador.TIPOS.String);
			
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
	        	metadatos=cpl.getNodoResultado("STRING_MEMO");
	        	
	        }catch (Exception ex) {
	        		Logger.error("Error en lanzador al recuperar los metadatos desde BD: "+ex.getMessage(),Logger.LOGTYPE.APPLOG);
	        		Logger.trace(ex.getStackTrace(), Logger.LOGTYPE.APPLOG);
	        		throw ex;
	        }
		} catch (Exception e) {
			Logger.error("Excepcion generica al recuperar los metadatos desde BD: "+e.getMessage(),Logger.LOGTYPE.APPLOG);
			throw e;
		}
		return metadatos;
	}
	
	/**
	 * Actualiza en GDRE_REIMPRESIONES para el id_gdre que se pasa, con el id_adar que se indica, así como el csv y el hash
	 * @param idGdre Identificador de reimprimible
	 * @param idAdar Identificador de custodia en el archivo digital
	 * @param csvAdar Csv de archivo en el archivo digital
	 * @param hashAdar hash de archivo en el archivo digital
	 * @throws Exception
	 */
	public void actualizaIdadarGdre(String idGdre, String idAdar, String csvAdar, String hashAdar) throws Exception {
		try{
			ConversorParametrosLanzador cpl;
			cpl = new ConversorParametrosLanzador();
			cpl.setProcedimientoAlmacenado(pref.getPaActualizarAdarReimpresion());
			cpl.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
			cpl.setParametro("1", ConversorParametrosLanzador.TIPOS.Integer);
	        cpl.setParametro("USU_WEB_SAC", ConversorParametrosLanzador.TIPOS.String);
	        cpl.setParametro("33", ConversorParametrosLanzador.TIPOS.Integer);
	        cpl.setParametro(idGdre, ConversorParametrosLanzador.TIPOS.Integer);
	        cpl.setParametro(idAdar, ConversorParametrosLanzador.TIPOS.Integer);
	        cpl.setParametro(hashAdar, ConversorParametrosLanzador.TIPOS.String);
	        cpl.setParametro(csvAdar, ConversorParametrosLanzador.TIPOS.String);
	        
	        LanzaPLMasivoService lanzaderaWS = new LanzaPLMasivoService();					
			LanzaPLMasivo lanzaderaPort;			
			lanzaderaPort = lanzaderaWS.getLanzaPLMasivoSoapPort();
	        
			// enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			// Cambiamos el endpoint
			bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,pref.getEndpointLanzador());

	        //Vinculamos con el Handler	        
	        HandlerUtil.setHandlerClient((javax.xml.ws.BindingProvider) lanzaderaPort);
	        try {	        	
	        	lanzaderaPort.executePL(pref.getEntorno(), cpl.Codifica(), "", "", "", "");
	        	//En principio no devuelve nada a web. O termina, o da error. Sólo para reimpresiones archivadas puede no funcionar, pero esta es nueva, no puede estar archivada.
	        	
	        }catch (Exception ex) {
	        		Logger.error("Error en lanzador al actualizar con id_adar " +idAdar +  " en GDRE_REIMPRESIONES para la reimpresión " + idGdre + ": "+ex.getMessage(),Logger.LOGTYPE.APPLOG);
	        		Logger.trace(ex.getStackTrace(), Logger.LOGTYPE.APPLOG);
	        		throw ex;
	        }
		} catch (Exception e) {
			Logger.error("Excepcion generica al actualizar con id_adar "+ idAdar + " en GDRE_REIMPRESIONES para la reimpresión " + idGdre + ": "+e.getMessage(),Logger.LOGTYPE.APPLOG);
			throw e;
		}
	}
}
