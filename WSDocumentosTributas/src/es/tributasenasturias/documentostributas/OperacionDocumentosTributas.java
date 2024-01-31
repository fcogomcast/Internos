package es.tributasenasturias.documentostributas;

import javax.xml.ws.Holder;
import java.util.Base64;

import es.tributasenasturias.documentostributas.constantes.Constantes;
import es.tributasenasturias.documentostributas.datos.Datos;
import es.tributasenasturias.documentostributas.datos.DatosException;
import es.tributasenasturias.documentostributas.datos.Datos.ResultadoAlta;
import es.tributasenasturias.documentostributas.datos.Datos.ResultadoConsulta;
import es.tributasenasturias.documentostributas.datos.Datos.ResultadoOperacionBD;
import es.tributasenasturias.documentostributas.utils.Compresor;
import es.tributasenasturias.documentostributas.utils.Preferencias;
import es.tributasenasturias.documentostributas.utils.logging.Logger;

public class OperacionDocumentosTributas {
	private Datos data = null;
	private Logger logger = null;
	private Preferencias pref = null;
	public OperacionDocumentosTributas(String idSesion) throws OperacionException{
		logger = new Logger(idSesion);
		try {
			pref = Preferencias.getPreferencias();
		} catch (Exception e) {
			throw new OperacionException ("Imposible construir el objeto de operaci�n debido a un problema en las preferencias:"+ e.getMessage(),e);
		}
		data = new Datos(idSesion, logger, pref);
		
	}
	/**
	 * Valida los par�metros de la operaci�n de alta de documento
	 * @param documento Datos del documento a dar de alta
	 * @param usuario Usuario que da de alta el documento
	 * @param comprimir Indica si el contenido se ha de comprimir
	 * @param comprimido Indica si el contenido ya se recibe comprimido
	 * @param resultado Resultado de la operaci�n de alta. Lo rellenar� con el error de validaci�n si se produjera
	 * @return true si ha superado la validaci�n o false si no.
	 */
	private boolean validacionParametrosAlta (DocumentoType documento, ElementoRelacionadoType elementoRelacionado,String usuario, boolean comprimir, boolean comprimido, ResultadoType resultado)
	{
		String prfMensaje = "Error de validaci�n";
		boolean valido=true;
		if (comprimir && comprimido)
		{
			resultado.setCodigo(Constantes.getResultadoErrorValidacion());
			resultado.setMensaje(String.format("%s:%s",prfMensaje, "No se pueden indicar simult�neamente los par�metros de \"comprimir\" y \"comprimido\" "));
			valido= false;
		}
		if (valido & (documento.getNombre()==null || "".equals(documento.getNombre())))
		{
			resultado.setCodigo(Constantes.getResultadoErrorValidacion());
			resultado.setMensaje(String.format("%s:%s",prfMensaje, "Es obligatorio indicar el nombre de fichero"));
			valido= false;
		}
		if (valido & (documento.getDescripcion()==null || "".equals(documento.getDescripcion())))
		{
			resultado.setCodigo(Constantes.getResultadoErrorValidacion());
			resultado.setMensaje(String.format("%s:%s",prfMensaje, "Es obligatorio indicar una descripci�n para el fichero"));
			valido= false;
		}
		if (valido & (documento.getContenido()==null || documento.getContenido().length==0 ))
		{
			resultado.setCodigo(Constantes.getResultadoErrorValidacion());
			resultado.setMensaje(String.format("%s:%s",prfMensaje, "Es obligatorio que el fichero tenga alg�n contenido"));
			valido= false;
		}
		if (valido & (usuario==null || "".equals(usuario)))
		{
			resultado.setCodigo(Constantes.getResultadoErrorValidacion());
			resultado.setMensaje(String.format("%s:%s",prfMensaje, "Es obligatorio indicar el usuario de alta "));
			valido= false;
		}
		return valido;
	}
	/**
	 * Valida par�metros de la operaci�n de consulta
	 * @param idDocumento Identificador de documento
	 * @param resultado Resultado de la operaci�n de consulta. Lo rellenar� con el error de validaci�n en caso de no superarla
	 * @return true si ha superado la validaci�n o false si no lo ha hecho.
	 */
	private boolean validacionParametrosConsulta (String idDocumento, ResultadoType resultado)
	{
		String prfMensaje= "Error de validaci�n";
		if (idDocumento==null || "".equals(idDocumento))
		{
			resultado.setCodigo(Constantes.getResultadoErrorValidacion());
			resultado.setMensaje(String.format("%s:%s",prfMensaje, "No se pueden indicar simult�neamente los par�metros de \"comprimir\" y \"comprimido\" "));
			return false;
		}
		return true;
	}
	public ResultadoType altaDocumento (DocumentoType documento, ElementoRelacionadoType elementoRelacionado,String usuario, 
			 boolean comprimir, boolean comprimido) throws OperacionException
	{
		boolean esComprimido;
		logger.info(">>> Entrada en alta de documento.");
		ResultadoType resultado= new ResultadoType();
		if (validacionParametrosAlta(documento, elementoRelacionado, usuario, comprimir, comprimido, resultado))
		{
			logger.info ("1. Se supera la validaci�n de par�metros.");
			//Se comprueba si se ha de comprimir
			esComprimido=comprimido;
			String contenido;
			if (comprimir)
			{
				logger.info ("1.1. Se comprime el contenido");
				contenido=Compresor.comprimir(documento.getContenido());
				esComprimido=true;
				logger.info ("1.2. Contenido comprimido");
			}
			else
			{
				contenido=Base64.getEncoder().encodeToString(documento.getContenido());
			}
			ResultadoAlta resAlta;
			try {
				resAlta=data.altaDocumento(documento.getNombre(), documento.getDescripcion(), contenido,
						elementoRelacionado.getId(), elementoRelacionado.getTipo(),
						usuario, esComprimido);
			} catch (DatosException e) {
				logger.error("Error en alta de documento:"+e.getMessage());
				logger.trace(e.getStackTrace());
				throw new OperacionException ("Error en alta de documento:"+e.getMessage(),e);
			}
			if (resAlta.getResultadoOperacion()==ResultadoOperacionBD.ALTA_OK)
			{
				logger.info("2.Alta realizada correctamente.");
				resultado.setCodigo(Constantes.getResultadoOK());
				resultado.setMensaje(Constantes.getResultadoOKMsg());
			}
			else if (resAlta.getResultadoOperacion()==ResultadoOperacionBD.ALTA_ERROR)
			{
				logger.info ("2. La operaci�n de alta ha devuelto un c�digo de error, con mensaje:"+resAlta.getMensajeResultado() );
				resultado.setCodigo(Constantes.getResultadoAltaError());
				resultado.setMensaje(Constantes.getResultadoAltaErrorMsg());
			}
			else
			{
				//Practicamente imposible que ocurra, pero...
				logger.error ("2. C�digo de terminaci�n del alta de documento es desconocido.");
				throw new OperacionException ("Se ha recibido un estado de finalizaci�n de alta no esperado" + resAlta.getResultadoOperacion());
			}
		}
		else
		{
			logger.info("1.No se supera la validaci�n de par�metros:" + resultado.getMensaje());
		}
		logger.info (">>> Salida de alta de  documento");
		return resultado;
	}
	
	public ResultadoType consultaDocumento(String idDocumento,
			Holder<DocumentoType> documento) throws OperacionException
	{
		logger.info (">>> Entrada en consulta de documento");
		ResultadoType resultado= new ResultadoType();
		DocumentoType doc = new DocumentoType();
		if (validacionParametrosConsulta(idDocumento, resultado))
		{
			logger.info ("1. Se supera la validaci�n de par�metros.");
			ResultadoConsulta rCons;
			try {
				rCons = data.consultaDocumento(idDocumento);
			} catch (DatosException e) {
				logger.error("Error en consulta de datos");
				logger.trace(e.getStackTrace());
				throw new OperacionException("Error en la consulta de documento:"+ e.getMessage());
			}
			if (rCons.getResultadoOperacion()==ResultadoOperacionBD.CONSULTA_OK)
			{
				logger.info ("2. Consulta realizada correctamente");
				resultado.setCodigo(Constantes.getResultadoOK());
				resultado.setMensaje(Constantes.getResultadoOKMsg());
				doc.setNombre(rCons.getNombre());
				doc.setDescripcion(rCons.getDescripcion());
				//Se descomprimen datos
				if (rCons.isComprimido())
				{
					doc.setContenido(Compresor.descomprimir(rCons.getContenidoDocumento()));
				}
				else
				{
					doc.setContenido(Base64.getDecoder().decode(rCons.getContenidoDocumento()));
				}
				documento.value=doc;
			}
			else if (rCons.getResultadoOperacion()==ResultadoOperacionBD.CONSULTA_NO_DATOS)
			{
				logger.info ("2. No hay documento para esos datos");
				resultado.setCodigo(Constantes.getResultadoNoDocumento());
				resultado.setMensaje(Constantes.getResultadoNoDocumentoMsg());
			}
			else if (rCons.getResultadoOperacion()==ResultadoOperacionBD.CONSULTA_ERROR)
			{
				resultado.setCodigo(Constantes.getResultadoConsultaError());
				resultado.setMensaje(Constantes.getResultadoConsultaErrorMsg());
				logger.info ("2. La operaci�n de consulta ha devuelto un error:" + rCons.getMensajeResultado());
			}
			else
			{
				//Deber�a ser imposible
				throw new OperacionException ("Se ha recibido un estado de finalizaci�n de la consulta desconocido");
			}
		}
		else
		{
			logger.info("1.No se supera la validaci�n de par�metros:" + resultado.getMensaje());
		}
		logger.info (">>> Salida de consulta de documento");
		return resultado;
	}
}
