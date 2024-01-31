package es.tributasenasturias.documentostributas.sesion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionará los id de llamada. Asociará un id de llamada al identificador de hilo actual.
 * Dado que cada petición en el servidor está atendida por un hilo, permitirá que cada llamada
 * guarde constancia del identificador que necesita para insertar en los log.
 * En principio no es necesario controlar el acceso concurrente, ya que se accederá siempre con
 * la clave del identificador de hilo, y por tanto ningún hilo accederá a las mismas partes del
 * mapa que otro.
 *  
 * @author crubencvs
 *
 */
@Deprecated
public final class GestorIdLlamada {
	
	private GestorIdLlamada(){};
	//FIXME: Comprobar si esta implementación es la más eficiente.
	//private static Map<String,String> id = new HashMap<String,String>();
	//No se espera que de forma concurrente haya demasiados accesos, así que 
	//con esta inicialización estaría bien incluso en un sistema con ocho
	//núcleos y todos accediendo en el mismo momento al mapa (que es improbable)
	//
	//Además, utiliza menos memoria que la implementación por defecto.
	private static Map<String,String> id = new ConcurrentHashMap<String,String>(10,0.9f,8);
	/**
	 * Añade un Id de llamada
	 * @param clave
	 * @param idLlamada
	 */
	private static void addIdLlamada(String clave, String idLlamada)
	{
		id.put(clave, idLlamada);
	}
	/**
	 * Elimina un id de llamada
	 * @param clave
	 */
	private static void removeIdLlamada (String clave)
	{
		id.remove(clave);
	}
	/**
	 * Asocia un id de llamada con el identificador de hilo actual.
	 * @param idLlamada Identificador de llamada
	 */
	public static void asociarIdLlamada (String idLlamada)
	{
		addIdLlamada(String.valueOf(Thread.currentThread().getId()),idLlamada);
	}
	/**
	 * Desasocia cualquier id de llamada con el identificador de hilo actual.
	 */
	public static void desasociarIdLlamada()
	{
		removeIdLlamada(String.valueOf(Thread.currentThread().getId()));
	}
	/**
	 * Recupera el identificador de llamada asociado al hilo actual.
	 * @return
	 */
	public static String getIdLlamada()
	{
		String k=String.valueOf(Thread.currentThread().getId());
		if (id.containsKey(k))
		{
			return id.get(k);
		}
		return null;
	}
	
}

