package stpa.services;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;

import stpa.utils.utilsDB;
import stpa.utils.exception.ControlHorarioException;

/**
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "ControlHorarioSOAP", serviceName = "ControlHorario", targetNamespace = "http://stpa/ControlHorarioWSDL/", wsdlLocation = "/wsdls/ControlHorarioWSDL.wsdl", endpointInterface = "stpa.services.ControlHorarioWSDL")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain (file="LogMessageHandler.xml")
public class ControlHorario_ControlHorarioSOAPImpl implements ControlHorarioWSDL {

	public ControlHorario_ControlHorarioSOAPImpl() {}

	/**
	 * 
	 * @param parameters
	 * @return returns stpa.services.ControlHorarioOperationResponse
	 */
	public ControlHorarioOperationResponse saldoAcumulado(ControlHorarioOperation parameters) {
		ControlHorarioOperationResponse response=null;
		try {
			response = new ControlHorarioOperationResponse();
			response.setOut(utilsDB.saldoAcumuladoQuery(parameters.getUsuario()));
		}catch (ControlHorarioException che) {
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			log.error(che.getMessage());
			log.printErrorStack(che.getStackTrace());
			throw new WebServiceException(che.getMessage(),che);
		}catch (Exception e) {
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			log.error("Se ha producido un error inesperado en la llamada al servicio: "+e.getMessage());
			log.printErrorStack(e.getStackTrace());
			throw new WebServiceException("Se ha producido un error inesperado en la llamada al servicio:"+e.getMessage(),e);
		}
		return response;
	}
}
