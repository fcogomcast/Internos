package es.tributasenasturias.servicios.documentos.impresion.soap.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.servicios.documentos.log.Logger;

public class ClientHandler extends SoapHandler {

	private  Logger.LOGTYPE logFile = Logger.LOGTYPE.CLIENTLOG;
	
	public ClientHandler() {
		super.setlogFile(logFile);
	}
	
	public synchronized boolean handleMessage(SOAPMessageContext context) {
		Logger.debug("Escribo en el log: " + logFile, Logger.LOGTYPE.APPLOG);
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!outboundProperty.booleanValue()){
			 Logger.debug("OUTBOUND SOAP MESSAGE: ", logFile);
		}else{
			 Logger.debug("INBOUND SOAP MESSAGE: ", logFile);
	    }
		try{
			SOAPMessage message = context.getMessage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			message.writeTo(baos);
			 Logger.debug(baos.toString(), logFile);	
		}catch(IOException ioe){
			 Logger.error("Error escribiendo soapmessage: ",ioe, logFile);
		}catch(SOAPException se){
			 Logger.error("Error escribiendo soapmessage: ",se, logFile);
		}catch(Throwable t){
			 Logger.error("Error escribiendo soapmessage: ",t, logFile);
		}
	    
	    return true;
	}

}
