
package es.tributasenasturias.documentostributas.utils.soap;


import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.documentostributas.constantes.Constantes;
import es.tributasenasturias.documentostributas.sesion.GeneradorIdSesion;
import es.tributasenasturias.documentostributas.utils.Preferencias;
import es.tributasenasturias.documentostributas.utils.logging.GenericAppLogger;
import es.tributasenasturias.documentostributas.utils.logging.Logger;


/**
 * @author noelianbb
 * Clase que hará log de los mensajes entrantes.
 */
public class LogHandler implements SOAPHandler<SOAPMessageContext>{

	private String logFile ="";
	private String logDir="";
	private String idSesion="Sin sesion";
	private boolean doLog;
	
	
	/**
	 * Realiza el log de la llamada entrante o saliente
	 * @param context
	 */
	private void log(SOAPMessageContext context)
	{
		if (!this.doLog)
		{
			return;
		}
		//obtenemos la ip origen
		String ip = "";
		try {
			HttpServletRequest request = (HttpServletRequest)context.get(MessageContext.SERVLET_REQUEST);        
			ip = request.getRemoteAddr();
		} catch (Exception e) {
			ip = "";
			//Excepción cuando es ejecutado por ClientLogHandler
		}

    	
        SOAPMessage msg = context.getMessage();
        FileWriter out = null;
        PrintWriter pw=null;
        String endl=System.getProperty("line.separator");
        synchronized (this)
        {
	     try 
	     {
	    	 Boolean salida = (Boolean) context.get(javax.xml.ws.handler.MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	    	 String direccion=(salida)?"Envío":"Recepción"; // La propiedad nos dice si el mensaje es de salida o no.
	    	 //obtenemos el array de bytes que se corresponde con el mensaje soap
	    	 ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	         msg.writeTo(byteArray);
	         
	         //componemos la linea del log
	         String soapMessage = new String(byteArray.toByteArray());
	         Date today = new Date();
	         String tipoLog = "";
	         
	         if (ServerLogHandler.class.getName().equalsIgnoreCase(this.getClass().getName()))
	         {
	        	 tipoLog = "SOAP_SERVER";
	        	 if (!salida)
	        	 {
	        	 //Inicializamos y pasamos el id de sesión
	     		 this.setIdSesion(GeneradorIdSesion.generaIdSesion());
	        	 context.put(Constantes.getIdSesion(), idSesion);
	     		 context.setScope(Constantes.getIdSesion(), MessageContext.Scope.APPLICATION);
	        	 }
	         }
	         else
	         {
	        	 tipoLog = "SOAP_CLIENT";
	         }
	         String log = "=========================>"+ Preferencias.getNombreAplicacion()+":: "+ tipoLog + "::Inicio ::+"+direccion+"::"+ ip + " :: "+today+"::"+idSesion+endl;
	         log += soapMessage + endl;
	         log +="=========================>"+ Preferencias.getNombreAplicacion()+ "::" + tipoLog + "::Fin ::"+direccion +"::" + today + "::"+idSesion+endl;
	         
	         out = new FileWriter(logDir+"/"+logFile,true);
	         if (out!=null)
	         {
	        	 pw = new PrintWriter(out);
	        	 pw.println(log);
	         }
	     } 
	     catch (SOAPException ex) 
	     {
	    	 GenericAppLogger log = new Logger(this.idSesion);
     		 log.error("SOAP Exception escribiendo mensaje a fichero: "+ex.getMessage());
	         System.out.println("SOAP Exception escribiendo mensaje a fichero: "+ex.getMessage());
	         ex.printStackTrace();
	     } 
	     catch (IOException ex) 
	     {
	    	 GenericAppLogger log = new Logger(this.idSesion);
     		 log.error("IO Exception escribiendo mensaje a fichero: "+ex.getMessage());
	    	 System.out.println("IO Exception escribiendo mensaje a fichero: "+ex.getMessage());
	    	 ex.printStackTrace();
	     }
	     finally
	     {
            if(out != null)
            {
            	try
            	{
            		pw.close();
            	}
            	catch (Exception e) // En principio no debería devolver, nunca, una excepción. Se controla 
            						// por si hay cambios en la implementación.
            	{
            		GenericAppLogger log = new Logger(this.idSesion);
            		log.error("Error cerrando flujo de impresión: " + e.getMessage());
            		System.out.println (Preferencias.getNombreAplicacion()+".Error cerrando flujo de impresión: " + e.getMessage());
            		e.printStackTrace();
            	}
                try
                {
                    out.close();
                }
                catch(Exception e)
                {
                	GenericAppLogger log = new Logger(this.idSesion);
                	log.error("Error cerrando fichero de log -> "+e.getMessage());
                    System.out.println(Preferencias.getNombreAplicacion()+".Error cerrando fichero de log -> "+e.getMessage());
                    e.printStackTrace();
                }
            }
	     }
        }
	}
	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		//Hemos de escribir igualmente.
		log (context);
		return true;
	}

	@Override
	public synchronized boolean handleMessage(SOAPMessageContext context) {
		 	
		log (context);	
		return true;
	}
	/**
	 * @return the lOG_FILE
	 */
	public String getLOG_FILE() {
		return logFile;
	}
	/**
	 * @param log_file the lOG_FILE to set
	 */
	public void setLOG_FILE(String log_file) {
		logFile = log_file;
	}
	/**
	 * @return the lOG_DIR
	 */
	public String getLOG_DIR() {
		return logDir;
	}
	/**
	 * @param log_dir the lOG_DIR to set
	 */
	public void setLOG_DIR(String log_dir) {
		logDir = log_dir;
	}
	/**
	 * @return the doLog
	 */
	public final boolean isDoLog() {
		return doLog;
	}
	/**
	 * @param doLog the doLog to set
	 */
	public final void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}
	/**
	 * @return the idSesion
	 */
	public final String getIdSesion() {
		return idSesion;
	}
	/**
	 * @param idSesion the idSesion to set
	 */
	public final void setIdSesion(String idSesion) {
		this.idSesion = idSesion;
	}


}
