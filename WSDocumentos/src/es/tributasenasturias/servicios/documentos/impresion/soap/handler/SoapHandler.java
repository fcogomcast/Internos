package es.tributasenasturias.servicios.documentos.impresion.soap.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.servicios.documentos.log.Logger;

public class SoapHandler implements SOAPHandler<SOAPMessageContext>{
		
	private Logger.LOGTYPE logFile = Logger.LOGTYPE.SERVERLOG;	

	public synchronized void setlogFile(Logger.LOGTYPE logFile){
		this.logFile = logFile;
	}
	
	public void close(MessageContext context) {
	}

	public Set<QName> getHeaders() { 
	     return Collections.emptySet(); 
	} 

	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}
	
	public synchronized boolean handleMessage(SOAPMessageContext context) {
		Logger.debug("Escribo en el log: " + logFile, Logger.LOGTYPE.APPLOG);
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outboundProperty.booleanValue()){
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