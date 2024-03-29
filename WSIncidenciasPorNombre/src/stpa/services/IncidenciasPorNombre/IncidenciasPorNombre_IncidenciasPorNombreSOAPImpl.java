package stpa.services.IncidenciasPorNombre;

import java.util.ArrayList;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import stpa.utils.utilsDB;
import stpa.utils.exception.IncidenciasNombreException;

/**
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "IncidenciasPorNombreSOAP", serviceName = "IncidenciasPorNombre", targetNamespace = "http://stpa/IncidenciasPorNombreWSDL/", wsdlLocation = "/wsdls/IncidenciasPorNombreWSDL.wsdl", endpointInterface = "stpa.services.IncidenciasPorNombre.IncidenciasPorNombreWSDL")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain (file="LogMessageHandler.xml")
public class IncidenciasPorNombre_IncidenciasPorNombreSOAPImpl implements
		IncidenciasPorNombreWSDL {

	public IncidenciasPorNombre_IncidenciasPorNombreSOAPImpl() {
	}

	/**
	 * Devuelve una lista de incidencias asociadas a un nombre de usuario y opcionalmente un estado.
	 * @param parameters
	 * @return returns
	 *         stpa.services.IncidenciasPorNombre.IncidenciasPorNombreOperationResponseType
	 */
	public IncidenciasPorNombreOperationResponseType incidenciasPorNombre(
			IncidenciasPorNombreOperation parameters) {
		IncidenciasPorNombreOperationResponseType response=null;
		try {
			ObjectFactory fact=new ObjectFactory();
			response = fact.createIncidenciasPorNombreOperationResponseType();
			ArrayList<IncidenciaInfoType> resultado=utilsDB.incidenciasPorNombreQuery(parameters.getNombre(),parameters.getIdEstado());
			response.getIncidenciaInfo().addAll(resultado);
		}catch (IncidenciasNombreException ine) {
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			log.error("Se ha producido un error en la llamada a la operación de incidencias por nombre:" + ine.getMessage());
			log.printErrorStack(ine.getStackTrace());
			throw new WebServiceException(ine.getMessage(),ine);
		}catch (Exception e) {
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			log.error("Se ha producido un error inesperado en la llamada a la operación de incidencias por nombre: "+e.getMessage());
			log.printErrorStack(e.getStackTrace());
			throw new WebServiceException("Se ha producido un error inesperado en la llamada a la operación de incidencias por nombre:"+e.getMessage(),e);
		}
		return response;
	}

	/**
	 * Devuelve la lista de posibles estados de una incidencia.
	 * @param parameters
	 * @return returns stpa.services.IncidenciasPorNombre.ListaEstadosResponse
	 */
	public ListaEstadosResponse listaEstados(ListaEstadosRequest parameters) {
		ListaEstadosResponse response=null;
		try {
			ObjectFactory fact = new ObjectFactory();
			response=fact.createListaEstadosResponse();
			ArrayList<EstadoInfo> resultado = utilsDB.recuperaListaEstados();
			response.getEstadoInfo().addAll(resultado);
		} catch (IncidenciasNombreException ine) {
			stpa.utils.Log.TributasLogger log = new stpa.utils.Log.TributasLogger();
			log.error("Se ha producido un error en la llamada a la operación de lista de estados:" + ine.getMessage());
			log.printErrorStack(ine.getStackTrace());
		}
		catch (Exception ex)
		{
			stpa.utils.Log.TributasLogger log = new stpa.utils.Log.TributasLogger();
			log.error ("Se ha producido un error inesperado en la llamada a la operación de lista de estados:"+ ex.getMessage());
			log.printErrorStack(ex.getStackTrace());
			throw new WebServiceException ("Se ha producido un error inesperado en la llamada a la operación de lista de estados");
		}
		return response;
	}

	/**
	 * Devuelve los datos de una incidencia
	 * @param parameters Id de la incidencia
	 * @return returns stpa.services.IncidenciasPorNombre.IncidenciaInfoType
	 */
	public DetalleIncidenciaResponse detalleIncidencia(
			DetalleIncidenciaRequest parameters) {
		DetalleIncidenciaResponse resultado=new DetalleIncidenciaResponse();
		try {
			resultado.setIncidenciaInfo(utilsDB.detalleIncidencia(parameters.getIdIncidencia()));
		}catch (IncidenciasNombreException ine) {
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			log.error("Se ha producido un error en la llamada a la operación de incidencias por nombre:" + ine.getMessage());
			log.printErrorStack(ine.getStackTrace());
			throw new WebServiceException(ine.getMessage(),ine);
		}catch (Exception e) {
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			log.error("Se ha producido un error inesperado en la llamada a la operación de incidencias por nombre: "+e.getMessage());
			log.printErrorStack(e.getStackTrace());
			throw new WebServiceException("Se ha producido un error inesperado en la llamada a la operación de incidencias por nombre:"+e.getMessage(),e);
		}
		return resultado;
	}

}
