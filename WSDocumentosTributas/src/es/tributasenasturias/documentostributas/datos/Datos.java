package es.tributasenasturias.documentostributas.datos;


import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import es.tributasenasturias.documentostributas.utils.Preferencias;
import es.tributasenasturias.documentostributas.utils.logging.Logger;
import es.tributasenasturias.documentostributas.utils.soap.ClientLogHandler;

import stpa.services.LanzaPL;
import stpa.services.LanzaPLService;



public class Datos {
	private Preferencias preferencias;
	private Logger logger = null;

	private ConversorParametrosLanzador cpl;
	
	private String idSesion=""; 

	// Definición de Constantes
	private final String STRING_CADE = "STRING_CADE";
	private final String STRING1_CANU = "STRING1_CANU";
	private final String STRING2_CANU = "STRING2_CANU";
	private final String STRING3_CANU = "STRING3_CANU";
	private final String PDF_DATA = "pdf";
	private final String ERROR = "error";
	private final String ALTA_OK="0000";
	private final String ALTA_ERROR="0999";
	private final String CONSULTA_OK="0000";
	private final String CONSULTA_NO_DATOS="0001";
	private final String CONSULTA_ERROR="0999";

	public enum ResultadoOperacionBD
	{
		ALTA_OK,
		ALTA_ERROR,
		CONSULTA_OK,
		CONSULTA_NO_DATOS,
		CONSULTA_ERROR
	}
	private static class ResultadoBD
	{
		private ResultadoOperacionBD resultadoOperacion;
		private String mensajeResultado;

		/**
		 * @return the resultadoOperacion
		 */
		public final ResultadoOperacionBD getResultadoOperacion() {
			return resultadoOperacion;
		}

		/**
		 * @param resultadoOperacion the resultadoOperacion to set
		 */
		public final void setResultadoOperacion(ResultadoOperacionBD resultadoOperacion) {
			this.resultadoOperacion = resultadoOperacion;
		}

		/**
		 * @return the mensajeResultado
		 */
		public final String getMensajeResultado() {
			return mensajeResultado;
		}

		/**
		 * @param mensajeResultado the mensajeResultado to set
		 */
		public final void setMensajeResultado(String mensajeResultado) {
			this.mensajeResultado = mensajeResultado;
		}
		
		
	}
	
	//Resultado del Alta
	public static class ResultadoAlta extends ResultadoBD
	{
		
	}
	//ResultadoConsulta
	public static class ResultadoConsulta extends ResultadoBD
	{
		private String contenidoDocumento;
		private String descripcion;
		private String nombre;
		private boolean comprimido;

		/**
		 * @return the comprimido
		 */
		public final boolean isComprimido() {
			return comprimido;
		}

		/**
		 * @param comprimido the comprimido to set
		 */
		public final void setComprimido(boolean comprimido) {
			this.comprimido = comprimido;
		}

		/**
		 * @return the descripcion
		 */
		public final String getDescripcion() {
			return descripcion;
		}

		/**
		 * @param descripcion the descripcion to set
		 */
		public final void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		/**
		 * @return the nombre
		 */
		public final String getNombre() {
			return nombre;
		}

		/**
		 * @param nombre the nombre to set
		 */
		public final void setNombre(String nombre) {
			this.nombre = nombre;
		}

		/**
		 * @return the contenidoDocumento
		 */
		public final String getContenidoDocumento() {
			return contenidoDocumento;
		}

		/**
		 * @param contenidoDocumento the contenidoDocumento to set
		 */
		public final void setContenidoDocumento(String contenidoDocumento) {
			this.contenidoDocumento = contenidoDocumento;
		}
		
	}
	public Datos(String idSesion,Logger logger, Preferencias pref){
		this.idSesion= idSesion;
		this.logger = logger;
		this.preferencias= pref;
	}
	
	
	@SuppressWarnings("unchecked")
	public ResultadoConsulta consultaDocumento(String idDocumento) throws DatosException{

		LanzaPLService lanzaderaWS = new LanzaPLService();
		LanzaPL lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
		try{
			//Enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			//Cambiamos el endpoint
			bpr.getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,preferencias.getEndPointLanzador());
			Binding bi = bpr.getBinding();
			List<Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			{
			   handlerList = new ArrayList<Handler>();
			}
			handlerList.add(new ClientLogHandler(this.idSesion));
			bi.setHandlerChain(handlerList);
			cpl = new ConversorParametrosLanzador();
			cpl.setProcedimientoAlmacenado(preferencias.getpAConsulta());
			cpl.setParametro(idDocumento, ConversorParametrosLanzador.TIPOS.String); 
			cpl.setParametro("P", ConversorParametrosLanzador.TIPOS.String); 
		}catch (javax.xml.parsers.ParserConfigurationException e){
			logger.error("Fallo al preparar la consulta:"+e.getMessage());
			throw new DatosException ("Fallo al preparar la consulta:" + e.getMessage());
		}
		String resultadoEjecutarPL="";
		try{
			//Recogemos el resultado (->xml) y parseamos el nodo que nos interesa
			resultadoEjecutarPL = lanzaderaPort.executePL(preferencias.getEsquemaBaseDatos(), cpl.Codifica(),
				"", "", "", "");
			cpl.setResultado(resultadoEjecutarPL);
		}catch (Exception e){
			logger.error("Fallo al ejecutar la consulta:"+e.getMessage());
			throw new DatosException ("Fallo al ejecutar la consulta:" + e.getMessage());
		}
		String error = cpl.getNodoResultado(ERROR);
		if (error!=null && !"".equals(error))
		{
			throw new DatosException ("Error recibido en la consulta de documento:"+ error);
		}
		ResultadoConsulta resultado = new ResultadoConsulta();
		String codigo = cpl.getNodoResultado(STRING_CADE);
		if (CONSULTA_OK.equals (codigo))
		{
			resultado.setResultadoOperacion(ResultadoOperacionBD.CONSULTA_OK);
			resultado.setNombre(cpl.getNodoResultado(STRING1_CANU));
			resultado.setDescripcion(cpl.getNodoResultado(STRING2_CANU));
			boolean comprimido  = "S".equalsIgnoreCase(cpl.getNodoResultado(STRING3_CANU).toUpperCase());
			resultado.setComprimido(comprimido);
			resultado.setContenidoDocumento(cpl.getNodoResultado(PDF_DATA));
		}
		else if (CONSULTA_NO_DATOS.equals(codigo))
		{
			resultado.setResultadoOperacion(ResultadoOperacionBD.CONSULTA_NO_DATOS);
		}
		else if (CONSULTA_ERROR.equals(codigo))
		{
			resultado.setResultadoOperacion(ResultadoOperacionBD.CONSULTA_ERROR);
			resultado.setMensajeResultado(cpl.getNodoResultado(STRING1_CANU));
		}
		else
		{
			throw new DatosException ("Se ha recibido un código de resultado no contemplado en la consulta de documento:"+ codigo);
		}
		
		return resultado;

	}
	
	
	@SuppressWarnings("unchecked")
	public ResultadoAlta altaDocumento(String nombre, String descripcion, String contenido,
										String idElemento, String tipoElemento, String usuario, boolean comprimido) throws DatosException {
		LanzaPLService lanzaderaWS = new LanzaPLService();
		LanzaPL lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
		try{
			cpl = new ConversorParametrosLanzador();			
			cpl.setProcedimientoAlmacenado(preferencias.getPAAltaDocumento());
			cpl.setParametro(nombre, ConversorParametrosLanzador.TIPOS.String); 
			cpl.setParametro(usuario, ConversorParametrosLanzador.TIPOS.String);
			cpl.setParametro(descripcion, ConversorParametrosLanzador.TIPOS.String);
			cpl.setParametro(contenido, ConversorParametrosLanzador.TIPOS.Clob);
			cpl.setParametro(idElemento, ConversorParametrosLanzador.TIPOS.String);
			cpl.setParametro(tipoElemento, ConversorParametrosLanzador.TIPOS.String);
			cpl.setParametro(comprimido?"S":"N", ConversorParametrosLanzador.TIPOS.String);
			cpl.setParametro("P", ConversorParametrosLanzador.TIPOS.String); // con oracle = 'P'
	
			//Enlazador de protocolo para el servicio.
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort;
			//Cambiamos el endpoint
			bpr.getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,preferencias.getEndPointLanzador());
			Binding bi = bpr.getBinding();
			List<Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			{
			   handlerList = new ArrayList<Handler>();
			}
			handlerList.add(new ClientLogHandler(this.idSesion));
			bi.setHandlerChain(handlerList);
		}catch (javax.xml.parsers.ParserConfigurationException e){
			logger.error("Fallo al preparar el alta:"+e.getMessage());
			throw new DatosException ("Fallo al preparar el alta:" + e.getMessage());
		}
		String respuesta = new String();
		try{
			respuesta = lanzaderaPort.executePL(preferencias.getEsquemaBaseDatos(), cpl.Codifica(), "", "", "", "");
			cpl.setResultado(respuesta);
		}catch (Exception e){
			logger.error("Fallo al ejecutar el alta:"+e.getMessage());
			throw new DatosException ("Fallo al ejecutar el alta:" + e.getMessage());
		}
		String error = cpl.getNodoResultado(ERROR);
		if (error!=null && !"".equals(error))
		{
			throw new DatosException ("Error recibido en el alta de documento:"+ error);
		}
		ResultadoAlta resultado = new ResultadoAlta();
		String codigo = cpl.getNodoResultado(STRING1_CANU);
		if (ALTA_OK.equals(codigo))
		{
			resultado.setResultadoOperacion(ResultadoOperacionBD.ALTA_OK);
		}
		else if (ALTA_ERROR.equals(codigo))
		{
			resultado.setResultadoOperacion(ResultadoOperacionBD.ALTA_ERROR);
			resultado.setMensajeResultado(cpl.getNodoResultado(STRING2_CANU));
		}
		else
		{
			//No contemplado
			throw new DatosException ("Se ha recibido un código de resultado no contemplado en el alta de documento:"+ codigo);
		}
		return resultado;
	}

	
	


}
