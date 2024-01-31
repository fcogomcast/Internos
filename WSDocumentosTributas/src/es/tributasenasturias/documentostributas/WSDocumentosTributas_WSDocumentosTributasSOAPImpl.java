package es.tributasenasturias.documentostributas;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

import es.tributasenasturias.documentostributas.constantes.Constantes;
import es.tributasenasturias.documentostributas.sesion.GeneradorIdSesion;

/**
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "WSDocumentosTributasSOAP", serviceName = "WSDocumentosTributas", targetNamespace = "http://documentostributas.tributasenasturias.es/WSDocumentosTributas/", wsdlLocation = "/wsdls/WSDocumentosTributas.wsdl", endpointInterface = "es.tributasenasturias.documentostributas.WSDocumentosTributas")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain(file="HandlerChain.xml")
public class WSDocumentosTributas_WSDocumentosTributasSOAPImpl implements
		WSDocumentosTributas {
	
	@Resource
	private WebServiceContext wcontexto;
	public WSDocumentosTributas_WSDocumentosTributasSOAPImpl() {
	}
	/**
	 * Recupera el id de sesi�n de contexto, o genera uno nuevo. Ya existir� si se activaron
	 * los manejadores SOAP.
	 * @return Id de sesi�n.
	 */
	private String recuperarIdSesion()
	{
		String idSesion = (String)wcontexto.getMessageContext().get(Constantes.getIdSesion());
		if (idSesion==null)
		{
			idSesion=GeneradorIdSesion.generaIdSesion();
		}
		return idSesion;
	}
	/**
	 * 
	 * @param elementoRelacionado
	 * @param usuario
	 * @param comprimir
	 * @param documento
	 * @param comprimido
	 * @return returns es.tributasenasturias.documentostributas.ResultadoType
	 * @throws AltaDocumentoFaultMsg
	 */
	public ResultadoType altaDocumento(DocumentoType documento,
			ElementoRelacionadoType elementoRelacionado, String usuario,
			boolean comprimir, boolean comprimido) throws AltaDocumentoFaultMsg {
		OperacionDocumentosTributas oper;
		try {
			//Asociamos el id de sesi�n, para los log
			oper = new OperacionDocumentosTributas(recuperarIdSesion());
			return oper.altaDocumento(documento, elementoRelacionado,usuario, comprimir, comprimido);
		} catch (OperacionException e) {
			
			AltaDocumentoFault innFault = new AltaDocumentoFault();
			innFault.setAltaDocumentoFault(e.getMessage());
			throw new  AltaDocumentoFaultMsg("Error inesperado en alta de documento", innFault,e);
		}
	}

	/**
	 * 
	 * @param documento
	 * @param resultado
	 * @param idDocumento
	 * @throws ConsultaDocumentoFaultMsg
	 */
	public void consultaDocumento(String idDocumento,
			Holder<ResultadoType> resultado, Holder<DocumentoType> documento)
			throws ConsultaDocumentoFaultMsg {
		OperacionDocumentosTributas oper;
		try {
			oper = new OperacionDocumentosTributas(recuperarIdSesion());
			resultado.value = oper.consultaDocumento(idDocumento, documento);
		} catch (OperacionException e) {
			ConsultaDocumentoFault innFault = new ConsultaDocumentoFault();
			innFault.setConsultaDocumentoFault(e.getMessage());
			throw new ConsultaDocumentoFaultMsg ("Error inesperado en consulta de documento",innFault,e);
		}
	}

}