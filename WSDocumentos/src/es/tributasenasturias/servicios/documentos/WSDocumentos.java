package es.tributasenasturias.servicios.documentos;

import javax.jws.*;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceException;

import es.tributasenasturias.servicios.documentos.custodia.types.FirmaType;
import es.tributasenasturias.servicios.documentos.gestion.MediadorAltaDocumento;
import es.tributasenasturias.servicios.documentos.gestion.GestorDocumentos;
import es.tributasenasturias.servicios.documentos.gestion.MediadorArchivoDigital;
import es.tributasenasturias.servicios.documentos.gestion.MediadorBaseDatos;
import es.tributasenasturias.servicios.documentos.gestion.MediadorArchivoDigital.AltaArchivoDigitalResponse;
import es.tributasenasturias.servicios.documentos.impresion.utils.Base64;
import es.tributasenasturias.servicios.documentos.impresion.utils.CompresionDocumento;
import es.tributasenasturias.servicios.documentos.log.Logger;
import es.tributasenasturias.servicios.documentos.operaciones.ImpresionGD;
import es.tributasenasturias.servicios.documentos.operaciones.Reimpresion;
/**
 * Interfaz e implementación de servicio de documentos. Permite realizar las operaciones: <br/>
 * - Obtención de un reimprimible. <br/>
 * - Creación de un nuevo documento de gestor documental. <br/>
 * - Obtención de un reimprimible y alta del mismo. <br/>
 * @author crubencvs
 *
 */
@WebService (serviceName="WSDocumentos")
public class WSDocumentos {

	@WebMethod(operationName="obtenerReimprimible")
	public String obtenerReimprimible(@WebParam(name = "elemento") String elemento, @WebParam(name = "tipo") String tipo, @WebParam (name="codigoVerificacion") String codigoVerificacion) {
		try {
			Reimpresion impr = Reimpresion.newReimpresion();
			return impr.getPDFReimprimido(elemento, tipo, codigoVerificacion, true); //true=consultar en archivo digital
		}
		catch (Exception e)
		{
			Logger.error("error".concat(e.getMessage()),Logger.LOGTYPE.APPLOG);			
			throw new WebServiceException ("Error en la ejecución de reimpresión:" + e.getMessage(),e);
		}
	}
	/**
	 * Realiza la impresión mediante gestor documental de un documento. Puede también dar de alta en
	 * base de datos el documento recién generado.
	 * @param origenDatos Petición XML de lanzador mediante la que se generará el documento.
	 * @param codigoVerificacion Código de verificación del documento(opcional). Se imprimirá en 
	 *                           el documento generado, en una posición fija e independiente del
	 *                           contenido del mismo.
	 * @param altaDocumento true si se desea dar de alta el documento en base de datos, false si no.
	 * @return
	 */
	@WebMethod(operationName="impresionGD")
	public String impresionGD(@WebParam(name = "origenDatos") String origenDatos, @WebParam (name="codigoVerificacion") String codigoVerificacion,
							  @WebParam(name="altaDocumento") boolean altaDocumento) {
		try {
			ImpresionGD impr = ImpresionGD.newImpresionGD();
			ImpresionGD.InfoDocImpresion info = impr.getPDFImpresion(origenDatos, codigoVerificacion, altaDocumento); 
			if (altaDocumento)
			{
				MediadorAltaDocumento mediador = new MediadorAltaDocumento();
				mediador.altaDocumento(info);
			}
			String pdf = info.getDocb64(); 
			return pdf;
		}
		catch (Exception e)
		{
			Logger.error("error ".concat(e.getMessage()),Logger.LOGTYPE.APPLOG);			
			throw new WebServiceException ("Error en la ejecución de impresión por gestor documental:" + e.getMessage(),e);
		}
	}
	
	@WebMethod(operationName="altaReimprimible")
	@WebResult(name="documento")
	public String altaReimprimible(@WebParam(name = "nombre") String nombre, 
								   @WebParam (name="tipo") String tipo,
								   @WebParam (name="codigo_verificacion") String codigoVerificacion,
								   @WebParam (name="nif_sujeto_pasivo") String nifSP,
								   @WebParam (name="nif_presentador") String nifPR,
								   @WebParam (name="tipo_documento") String tipoDocumento,
								   @WebParam (name="id_sesion") String idSesion,
								   @WebParam (name="comprimir_documento") boolean comprimirDocumento,
								   @WebParam (name="elemento") String elemento,
								   @WebParam (name="tipo_elemento") String tipoElemento,
								   @WebParam (name="comprimir_salida") boolean comprimirSalida
								   )  {
		try {
			String retorno;
			String pdf= Reimpresion.newReimpresion().getPDFReimprimido(elemento, tipoElemento, tipo+nombre+"-"+codigoVerificacion, true);// true=consultar en archivo digital
			if (pdf==null || "".equals(pdf))
			{
				Logger.error ("No se ha podido reimprimir el documento.",Logger.LOGTYPE.APPLOG);
				throw new WebServiceException ("No se ha podido reimprimir el documento.");
			}
			if (GestorDocumentos.altaDocumento(nombre, tipo, codigoVerificacion, nifSP, nifPR, tipoDocumento, idSesion,comprimirDocumento, pdf))
			{
				if (comprimirSalida)
				{
					retorno = CompresionDocumento.comprimir(pdf);
				}
				else
				{
					retorno = pdf;
				}
			}
			else
			{
				Logger.error("No se ha podido dar de alta el documento.",Logger.LOGTYPE.APPLOG);			
				throw new WebServiceException ("No se ha podido dar de alta el documento.");
			}
			return retorno;
		}
		catch (Exception e)
		{
			Logger.error("Error en la ejecución de alta de informe reimprimible:"+e.getMessage(),Logger.LOGTYPE.APPLOG);			
			throw new WebServiceException ("Error en la ejecución de alta de informe reimprimible.",e);
		}
	}
	
	/**
	 * Obtención de reimprimible por identificador de reimprimible.
	 * @param idGdre
	 * @param codigoVerificacion
	 * @return
	 */
	@WebMethod(operationName="obtenerReimprimibleGDRE")
	public String obtenerReimprimibleGDRE(@WebParam(name = "id_gdre") String idGdre,  @WebParam (name="codigoVerificacion") String codigoVerificacion) {
		try {
			Reimpresion impr = Reimpresion.newReimpresion();
			return impr.getPDFReimprimido(idGdre, codigoVerificacion, true); //true= recuperar de archivo digital, si hay
		}
		catch (Exception e)
		{
			Logger.error("error".concat(e.getMessage()),Logger.LOGTYPE.APPLOG);			
			throw new WebServiceException ("Error en la ejecución de reimpresión:" + e.getMessage(),e);
		}
	}
	
	/**
	 * Permite la reimpresión y custodia de un documento
	 * @param idGdre Identificador del reimprimible
	 * @param codigoUsuario Código del usuario que realizará la custodia
	 * @param idArchivoPadre Identificador de custodia anterior, de la que se hará una versión
	 * @param firma Objeto {@link FirmaType} con los parámetros de firma
	 * @param esError true si ha habido un error, false si no
	 * @param mensaje Mensaje de finalización
	 * @param csv  CSV del archivo custodiado
	 * @param hash Hash del archivo custodiado
	 * @param idadar Id_adar del archivo custodiado
	 */
	@WebMethod(operationName="custodiarReimprimible")
	@RequestWrapper(localName = "custodiarReimprimible", targetNamespace = "http://documentos.servicios.tributasenasturias.es/", className = "es.tributasenasturias.servicios.documentos.custodia.types.CustodiarReimprimible")
	@ResponseWrapper(localName = "custodiarReimprimibleResponse", targetNamespace = "http://documentos.servicios.tributasenasturias.es/", className = "es.tributasenasturias.servicios.documentos.custodia.types.CustodiarReimprimibleResponse")
	public void custodiarReimprimible(
			@WebParam(name = "idGdre")
			String idGdre,
			@WebParam(name="codigoUsuario")
			String codigoUsuario,
			@WebParam(name="idArchivoPadre")
			String idArchivoPadre,
			@WebParam(name="firma")
			FirmaType firma,
	        @WebParam(name = "esError", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<Boolean> esError,
	        @WebParam(name = "mensaje", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> mensaje,
	        @WebParam(name = "idadar", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> idadar,
	        @WebParam(name = "csv", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> csv,
	        @WebParam(name = "hash", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> hash			)
	{
		try {
			Logger.info("Entrada a la reimpresión y custodia del reimprimible con id: " + idGdre,Logger.LOGTYPE.APPLOG);
			if (idGdre==null || "".equals(idGdre)){
				esError.value=true;
				mensaje.value="Es obligatorio indicar el identificador de reimprimible";
				return;
			}
			if (codigoUsuario==null || "".equals(codigoUsuario)){
				esError.value=true;
				mensaje.value="Es obligatorio indicar el código de usuario";
				return;
			}
			Reimpresion impr = Reimpresion.newReimpresion();
			Logger.debug("Se va a generar el reimprimible con id: " + idGdre,Logger.LOGTYPE.APPLOG);
			String pdf= impr.getPDFReimprimido(idGdre, "", false); //false= nunca consultar el archivo de archivo digital
			if (pdf==null || "".equals(pdf)){
				Logger.error("No se ha podido realizar la reimpresión del id: " + idGdre,Logger.LOGTYPE.APPLOG);
				esError.value=true;
				mensaje.value="No se ha podido realizar la reimpresión del id:"+ idGdre;
			}
			Logger.debug("Reimpreso. Se va a archivo digital " + idGdre,Logger.LOGTYPE.APPLOG);
			MediadorArchivoDigital ad = new MediadorArchivoDigital();
			AltaArchivoDigitalResponse r;
			if (idArchivoPadre==null || "".equals(idArchivoPadre.trim())){
				Logger.debug("Nuevo archivo, no es una versión ",Logger.LOGTYPE.APPLOG);
				r = ad.altaArchivoDigital(idGdre, codigoUsuario, "GDRE", Base64.decode(pdf.toCharArray()), firma);
			} else{
				r = ad.custodiaVersion(idGdre, codigoUsuario, "GDRE", idArchivoPadre,Base64.decode(pdf.toCharArray()), firma);
			}
			if (!r.esError()){
				Logger.debug("Custodiado con id " + r.getIdAdar(),Logger.LOGTYPE.APPLOG);
				MediadorBaseDatos bd = new MediadorBaseDatos();
				bd.actualizaIdadarGdre(idGdre, r.getIdAdar(), r.getCsvAdar(), r.getHashAdar());
				//Termine bien o mal, no podemos saberlo
				esError.value=false;
				mensaje.value="Documento generado y custodiado";
				idadar.value= r.getIdAdar();
				csv.value= r.getCsvAdar();
				hash.value= r.getHashAdar();
				
			} else {
				Logger.debug("No se ha podido custodiar - " + r.getMensajeError(),Logger.LOGTYPE.APPLOG);
				esError.value=true;
				mensaje.value="Error en custodia en archivo digital:" + r.getMensajeError();
			}
		}
		catch (Exception e)
		{
			Logger.error("error".concat(e.getMessage()),Logger.LOGTYPE.APPLOG);			
			throw new WebServiceException ("Error en la ejecución de custodia de reimprimible:" + e.getMessage(),e);
		} finally {
			Logger.info("Salida de la reimpresión y custodia del reimprimible con id: " + idGdre,Logger.LOGTYPE.APPLOG);
		}
       return;		
	}
	
	/**
	 * Permite la impresión y custodia de un documento a partir de sus datos de gestor documental y plantilla
	 * @param gzippedData Plantilla y los Datos a partir de los que construir el PDF, en formato de gestor documental y comprimidos con Gzip 
	 * tal como lo hace la opción de "Gestor Documental/Herramientas/Comprimir__Descomprimir de Tributas 
	 * @param codigoUsuario Código del usuario que realizará la custodia
	 * @param idElementoTributas Identificador de elementos de Tributas que se guardará en la custodia
	 * @param tipoElemento Uno de los tipos de elemento de archivo digital, según la tabla ADTE_TIPOS_ELEMENTO
	 * @param firma Objeto {@link FirmaType} con los parámetros de firma
	 * @param esError true si ha habido un error, false si no
	 * @param mensaje Mensaje de finalización
	 * @param csv  CSV del archivo custodiado
	 * @param hash Hash del archivo custodiado
	 * @param idadar Id_adar del archivo custodiado
	 */
	@WebMethod(operationName="custodiarComposicion")
	@RequestWrapper(localName = "custodiarComposicion", targetNamespace = "http://documentos.servicios.tributasenasturias.es/", className = "es.tributasenasturias.servicios.documentos.custodia.types.CustodiarPDFConstruido")
	@ResponseWrapper(localName = "custodiarComposicionResponse", targetNamespace = "http://documentos.servicios.tributasenasturias.es/", className = "es.tributasenasturias.servicios.documentos.custodia.types.CustodiarPDFConstruidoResponse")
	public void custodiarComposicion(
			@WebParam(name = "gzippedData")
			String gzippedData,
			@WebParam(name="codigoUsuario")
			String codigoUsuario,
			@WebParam(name="tipoElemento")
			String tipoElemento,
			@WebParam(name="idElementoTributas")
			String idElementoTributas,
			@WebParam(name="idArchivoPadre")
			String idArchivoPadre,
			@WebParam(name="firma")
			FirmaType firma,
	        @WebParam(name = "esError", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<Boolean> esError,
	        @WebParam(name = "mensaje", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> mensaje,
	        @WebParam(name = "idadar", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> idadar,
	        @WebParam(name = "csv", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> csv,
	        @WebParam(name = "hash", targetNamespace = "", mode = WebParam.Mode.OUT)
	        Holder<String> hash			)
	{
		try {
			Logger.info("Entrada a la impresión y custodia de una composición de Gestor Documental con id Tributas: " + idElementoTributas,Logger.LOGTYPE.APPLOG);
			if (gzippedData==null || "".equals(gzippedData)){
				esError.value=true;
				mensaje.value="Es obligatorio indicar los datos comprimidos a partir de los cuales generar el PDF";
				return;
			}
			if (codigoUsuario==null || "".equals(codigoUsuario)){
				esError.value=true;
				mensaje.value="Es obligatorio indicar el código de usuario";
				return;
			}
			
			if (tipoElemento==null || "".equals(tipoElemento)){
				esError.value=true;
				mensaje.value="Es obligatorio indicar el tipo de elemento";
				return;
			}
			Reimpresion impr = Reimpresion.newReimpresion();
			Logger.debug("Se va a generar el PDF",Logger.LOGTYPE.APPLOG);
			String pdf= impr.construirPDFFromData(gzippedData); 
			if (pdf==null || "".equals(pdf)){
				Logger.error("No se ha podido realizar impresión. " ,Logger.LOGTYPE.APPLOG);
				esError.value=true;
				mensaje.value="No se ha podido realizar la impresión";
			}
			Logger.debug("Reimpreso. Se va a custodiar archivo digital ",Logger.LOGTYPE.APPLOG);
			MediadorArchivoDigital ad = new MediadorArchivoDigital();
			AltaArchivoDigitalResponse r;
			if (idArchivoPadre!=null && !"".equals(idArchivoPadre.trim())){
				r = ad.custodiaVersion(idElementoTributas, codigoUsuario, tipoElemento, idArchivoPadre,Base64.decode(pdf.toCharArray()), firma);
			} else {
				r = ad.altaArchivoDigital(idElementoTributas, codigoUsuario, tipoElemento, Base64.decode(pdf.toCharArray()), firma);
			}
			if (!r.esError()){
				Logger.debug("Custodiado con id " + r.getIdAdar(),Logger.LOGTYPE.APPLOG);
				esError.value=false;
				mensaje.value="Documento generado y custodiado";
				idadar.value= r.getIdAdar();
				csv.value= r.getCsvAdar();
				hash.value= r.getHashAdar();
				
			} else {
				Logger.debug("No se ha podido custodiar - " + r.getMensajeError(),Logger.LOGTYPE.APPLOG);
				esError.value=true;
				mensaje.value="Error en custodia en archivo digital:" + r.getMensajeError();
			}
			
		}
		catch (Exception e)
		{
			Logger.error("error".concat(e.getMessage()),Logger.LOGTYPE.APPLOG);			
			throw new WebServiceException ("Error en la ejecución de custodia de reimprimible:" + e.getMessage(),e);
		} finally {
			Logger.info("Salida de la impresión y custodia de una composición de Gestor Documental con id Tributas: " + idElementoTributas,Logger.LOGTYPE.APPLOG);
		}
       return;		
	}
	
	
	
}