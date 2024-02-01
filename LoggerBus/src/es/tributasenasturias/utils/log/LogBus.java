package es.tributasenasturias.utils.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.xmlbeans.XmlObject;

import es.tributasenasturias.utils.log.exceptions.LogBusException;

/**
 * Contiene los métodos para realizar log del mensaje recibido en bus.
 * @author crubencvs
 *
 */
public class LogBus {
	
	
	private static final String PATH_SERVICIO = "//*[local-name()='transport']/*[local-name()='uri']/text()";

	public static String getRutaFicheroConfiguracion (XmlObject inbound)
	{
		try
		{
			String proyecto=getNombreProyecto(inbound);
			String filename = getNombreFichero(inbound);
			return proyecto+"/Resources/"+filename+".logconf";
		}
		catch (Exception e) //No podemos permitirnos que falle.
		{
			e.printStackTrace();
			return "";
		}
		
	}
	private static String getNombreProyecto(XmlObject inbound)
	{
		XmlObject[] match = inbound.selectPath(PATH_SERVICIO);
		String uri="";
		String proyecto="";
		if (match.length>0)
		{
			//El primero será el único.
			uri = match[0].getDomNode().getFirstChild().getNodeValue();
			if (uri!=null && uri!="")
			{
				//Extraemos el nombre de proyecto.
				if (uri.indexOf("/")>-1)
				{
					//Hay Path
					proyecto= uri.substring(1,uri.indexOf("/",1)); //El formato será /NombreProyecto/XXX...
				}
				else //Se toma todo como nombre de proyecto.
				{
					proyecto = uri;
				}
			}
		}
		return proyecto;
	}
	private static String getNombreFichero(XmlObject inbound)
	{
		XmlObject[] match = inbound.selectPath(PATH_SERVICIO);
		String uri="";
		String filename="";
		if (match.length>0)
		{
			//El primero será el único.
			uri = match[0].getDomNode().getFirstChild().getNodeValue();
			if (uri!=null && uri!="")
			{
				//Extraemos el nombre de servicio.
				if (uri.indexOf("/")>-1)
				{
					//Hay Path
					filename= uri.substring(uri.lastIndexOf("/")+1);
				}
				else //Se toma todo como nombre de fichero.
				{
					filename = uri;
				}
			}
		}
		return filename;
	}
	private static void log (String messageId, String fechaInicio,XmlObject inbound, XmlObject mensajeEntrada,XmlObject headerSalida, XmlObject bodySalida, String nivel)
	{
			String filename= getNombreFichero(inbound);
			if ("".equals(filename))
			{
				System.err.println ("No se puede averiguar el nombre de Proxy para hacer log.");
				return;
			}
			//Grabamos el contenido del body a ese nombre de fichero.
			try {
				//Convertimos la fecha de entrada, que vendrá como fecha con zona horaria
				SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date fechaEntrada = frmt.parse(fechaInicio);
				
				XMLUtil xmlutil = new XMLUtil();
				FileOutputHandler fo = new FileOutputHandler(messageId,filename);
				fo.setNivel("");
				fo.receiveData("", "*************BEGIN***************");
				if ("error".equalsIgnoreCase(nivel))
				{
					fo.setNivel("INFO");
					fo.receiveData("Mensaje Entrada", xmlutil.getXMLText(mensajeEntrada),fechaEntrada);
					fo.setNivel("ERROR");
					fo.receiveData("Header Salida", xmlutil.getXMLText(headerSalida.getDomNode()));
					fo.receiveData("Body Salida", xmlutil.getXMLText(bodySalida.getDomNode()));
				}
				else
				{
					fo.setNivel("INFO");
					fo.receiveData("Mensaje Entrada", xmlutil.getXMLText(mensajeEntrada), fechaEntrada);
					fo.receiveData("Header Salida", xmlutil.getXMLText(headerSalida.getDomNode()));
					fo.receiveData("Body Salida", xmlutil.getXMLText(bodySalida.getDomNode()));
				}
				fo.setNivel("");
				fo.receiveData("", "*************END*****************");
				fo.writeData();
			} catch (LogBusException e) {
				System.err.println (new Date().toString()+ ":: Error al hacer log de mensajes en bus:"+ e.getMessage());
				e.printStackTrace();
			} catch (Exception e)
			{
				System.err.println (new Date().toString()+ ":: Error al hacer log de mensajes en bus:"+ e.getMessage());
				e.printStackTrace();
			}
	}
	
	public static void error (String messageId,String fechaInicio, XmlObject inbound, XmlObject mensajeEntrada,XmlObject headerSalida, XmlObject bodySalida)
	{
		log (messageId, fechaInicio,inbound, mensajeEntrada, headerSalida, bodySalida, "error");
	}

	public static void info (String messageId,String fechaInicio,XmlObject inbound, XmlObject mensajeEntrada,XmlObject headerSalida, XmlObject bodySalida)
	{
		log (messageId, fechaInicio,inbound, mensajeEntrada, headerSalida, bodySalida, "info");
	}

}
