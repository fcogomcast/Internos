package es.tributasenasturias.utils.log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import es.tributasenasturias.utils.log.exceptions.LogBusException;

/**
 * Implementa el envío de datos a fichero.
 * @author crubencvs
 *
 */
public class FileOutputHandler {

	private static final String PROYECTOS_LOGS_BUS = "proyectos/LoggerBus/";
	private String fichero;
	private ByteArrayOutputStream bao; //Los datos se guardarán aquí antes de enviarlos a disco, para miniminzar la entrada/salida
	private String idUnico;
	private String nivel;
	

	public String getFichero() {
		return fichero;
	}
	
	public void setFichero(String fichero) {
		this.fichero = fichero;
	}
	
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	
	/**
	 * Constructor
	 * @param nombreFichero Nombre de fichero en que se guardarán los datos.
	 */
	protected FileOutputHandler(String messageId,String nombreFichero)
	{
		fichero=nombreFichero;
		bao = new ByteArrayOutputStream();
		idUnico = messageId;
		
	}
	protected void receiveData(String descData, String data, Date fecha) throws LogBusException
	{
		try {
			String lvl = "";
			if (!"".equals(nivel))
			{
				lvl=nivel+ "::";
			}
			String linea = idUnico + "::" + lvl+descData+"::" + fecha + "::"+data+System.getProperty("line.separator");
			bao.write(linea.getBytes());
		} catch (IOException e) {
			throw new LogBusException ("Error al enviar datos a fichero " + this.fichero + ":"+e.getMessage(),e);
		} catch (Exception e)
		{
			throw new LogBusException ("Error al enviar datos a fichero " + this.fichero + ":"+e.getMessage(),e);
		}
	}
	/**
	 * Prepara datos para enviar a log.
	 * @param descData Descripción de los datos (se incorporará a la línea de log)
	 * @param data Datos para el log.
	 * @throws LogBusException
	 */
	protected void receiveData(String descData, String data) throws LogBusException
	{
		receiveData (descData, data, new Date());
	}
	/**
	 * Escribe en log los datos almacenados en este objeto. 
	 * @throws LogBusException
	 */
	protected void writeData() throws LogBusException
	{
		BufferedOutputStream bo =  null;
		try {
			File f = new File(PROYECTOS_LOGS_BUS);
			if (!f.exists())
			{
				f.mkdir();
			}
			bo = new BufferedOutputStream(new FileOutputStream (PROYECTOS_LOGS_BUS+this.fichero+".txt",true));
			bo.write(bao.toByteArray());
		} catch (IOException e) {
			throw new LogBusException ("Error al enviar datos a fichero " + this.fichero + ":"+e.getMessage(),e);
		} catch (Exception e)
		{
			throw new LogBusException ("Error al enviar datos a fichero " + this.fichero + ":"+e.getMessage(),e);
		}
		finally
		{
			if (bo!= null)
			{
				try {bo.close();} catch (Exception e){}
			}
			if (bao!=null)
			{
				try{bao.close();} catch (Exception e){}
			}
		}
	}
}
