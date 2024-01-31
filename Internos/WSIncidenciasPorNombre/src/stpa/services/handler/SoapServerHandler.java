package stpa.services.handler;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import stpa.utils.ConsultaIncidencias.preferencias.Preferencias;
import stpa.utils.ConsultaIncidencias.preferencias.PreferenciasException;
import stpa.utils.Log.SoapServerLogger;

public class SoapServerHandler implements SOAPHandler<SOAPMessageContext> {

	public static SOAPFaultException createSOAPFaultException(String pFaultString)  throws SOAPException{
		 SOAPFaultException sex=null;
		 try {
			 SOAPFault fault = SOAPFactory.newInstance().createFault();
			 fault.setFaultString(pFaultString);
			 fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "ConsultaIncidencias"));
			 sex= new SOAPFaultException(fault);
		 }catch (Exception ex){
			 throw new SOAPException("Consulta incidencias:===>Error grave al construir la excepción de SOAP."+ex.getMessage());
		 }
		 return sex;
	} 

	private synchronized void log(SOAPMessageContext context) throws SOAPException{
		//LogSoapserver log=null;
		SoapServerLogger log=null;
		try{
			Preferencias _pr=Preferencias.getPreferencias();
			String debugSoap=_pr.getDebugSoap();
			
			if ("S".equals(debugSoap)){
				String idSesion="Sesion no inicializada";
				Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	
				idSesion=(String)context.get("IdSesion");
				if (idSesion==null)
					idSesion="Sesion no inicializada";
	
				String direccion=(salida)?"Envío":"Recepción";
				//log=LogFactory.newLogSoapServer(idSesion);
				log=new stpa.utils.Log.SoapServerLogger();
				SOAPMessage msg = context.getMessage();
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		        msg.writeTo(byteArray);
		        String soapMessage = new String(byteArray.toByteArray());
		        if (log!=null)
		        	log.info(direccion+"::"+soapMessage);
			}
		}catch (javax.xml.soap.SOAPException ex){
			log.error("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			log.printErrorStack(ex.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de consulta incidencias.");
		}
		catch (java.io.IOException ex){
			log.error("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			log.printErrorStack(ex.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de consulta incidencias.");
		} catch (PreferenciasException e) {
			log.error("Error en la grabación de log de SOAP servidor:" + e.getMessage());
			log.printErrorStack(e.getStackTrace());
			throw createSOAPFaultException("Error mientras se procesaba la petición del servicio de envío de consulta incidencias.");
		}
	}

	@Override
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public void close(javax.xml.ws.handler.MessageContext context) {}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		try {
			log(context);
		}catch (SOAPException e) {
			e.printStackTrace();
		}
		return true;
	}
}