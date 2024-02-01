package stpa.utils.ControlHorario.preferencias;

//Este paquete se comporta como singleton en acceso, solo admite una instancia de la clase.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

//Gestion de preferencias
public class Preferencias{

	//Se hace instancia privada y estática.
	private Preferences m_preferencias;
	private final static String FICHERO_PREFERENCIAS = "preferenciasControlHorario.xml";
	private final static String DIRECTORIO_PREFERENCIAS = "proyectos/WSControlHorario";

	private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();
	
	//Nombres de las preferencias
	private final static String KEY_PREF_PROVIDER_URL = "PROVIDER_URL";
	private final static String KEY_PREF_DATASOURCE = "DATASOURCE";
	private final static String KEY_PREF_DEBUG_SOAP = "DEBUG_SOAP";
	private final static String KEY_PREF_TIPO1 = "contadorId";
	private final static String KEY_PREF_TIPO2 = "contadorAcumulador";
	
	private Preferencias(){		
		try{
			CargarPreferencias();
		}catch (Exception e){
			//Para comprobar posteriormente si se ha creado bien, se comprobará que la 
			//variable privada no es estática.
		}
	}

	protected void CargarPreferencias() throws PreferenciasException{
		try{
			if(CompruebaFicheroPreferencias()){		       
		        FileInputStream inputStream = new FileInputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		        Preferences.importPreferences(inputStream);
		        inputStream.close();
		        m_preferencias = Preferences.systemNodeForPackage(this.getClass());
		        String[] keys = m_preferencias.keys();
		        for(int i=0;i<keys.length;i++){
		        	String value = m_preferencias.get(keys[i], "");
		        	tablaPreferencias.put(keys[i], value);
		        }
			}
		}catch (Exception ex){
			throw new PreferenciasException (ex.getMessage());
		}
	}

	private void InicializaTablaPreferencias(){
		tablaPreferencias.clear();
		tablaPreferencias.put(KEY_PREF_PROVIDER_URL,"t3://localhost:7101");
		tablaPreferencias.put(KEY_PREF_DATASOURCE,"specDS");
		tablaPreferencias.put(KEY_PREF_DEBUG_SOAP,"N");
		tablaPreferencias.put(KEY_PREF_TIPO1,"35");
		tablaPreferencias.put(KEY_PREF_TIPO2,"1");
	}
	
	private boolean CompruebaFicheroPreferencias(){
		boolean existeFichero = false;
		
		File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		existeFichero = f.exists();
		if (existeFichero == false){
			CrearFicheroPreferencias();
		}
		return existeFichero;
	}

	/***********************************************************
	  * 
	  * Creamos el fichero de preferencias con los valores por defecto
	  * 
	  ***************************************************************/
	private void CrearFicheroPreferencias(){
	     //preferencias por defecto
		m_preferencias = Preferences.systemNodeForPackage(this.getClass());
	     
	    InicializaTablaPreferencias();
	     
	    //recorremos la tabla cargada con las preferencias por defecto
	    Iterator<Map.Entry<String, String>> itr = tablaPreferencias.entrySet().iterator();
	    while(itr.hasNext()){
	     	Map.Entry<String, String> e = (Map.Entry<String,String>)itr.next();
	     	m_preferencias.put(e.getKey(),e.getValue());
	    }
	    FileOutputStream outputStream = null;
	    File fichero;
	    try{
	         fichero = new File(DIRECTORIO_PREFERENCIAS);
	         if(fichero.exists() == false)
	             if (fichero.mkdirs()==false)
	             	{
	             	 throw new java.io.IOException ("No se puede crear el directorio de las preferencias.");
	             	}
	         
	         outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
	         m_preferencias.exportNode(outputStream);
	    }catch (Exception e){
	    	 e.printStackTrace();
	    }finally{
	         try{
	             if(outputStream != null)
	                 outputStream.close();
	         }catch(Exception e){
	        	 e.printStackTrace();
	         }
	    }
	}
 
 	public void recargaPreferencias() throws PreferenciasException{
 		CargarPreferencias();
 	}
 
 	private String getValueFromTablaPreferencias(String key){
 		String toReturn="";
 	
 		if(tablaPreferencias.containsKey(key)){
 			toReturn = tablaPreferencias.get(key);
 		}
 		//Logger.debug("Se ha pedido la preferencia '"+key+"' a lo que el sistema devuelve '"+toReturn+"'");
 		return toReturn;
 	}
 
 	private void setValueIntoTablaPreferencias(String key, String value){
 		//Logger.debug("Se actualizara el valor de la preferencia '"+key+"' a '"+value+"'");
 		tablaPreferencias.put(key, value);
 	}
	
	// Este método devolverá la instancia de clase.
 	public static Preferencias getPreferencias () throws PreferenciasException{
 		return new Preferencias();
 	}

 	public String getPreferencia1(){
	 	return getValueFromTablaPreferencias(KEY_PREF_TIPO1);
 	}
 
 	public String getPreferencia2(){
 		return getValueFromTablaPreferencias(KEY_PREF_TIPO2);
 	}
 
	public void setPreferencia1(String preferencia1) {
		setValueIntoTablaPreferencias(KEY_PREF_TIPO1, preferencia1);
	} 	

	public void setPreferencia2(String preferencia2) {
		setValueIntoTablaPreferencias(KEY_PREF_TIPO2, preferencia2);
	} 	

	public String getProviderURL() {
		return getValueFromTablaPreferencias(KEY_PREF_PROVIDER_URL);
	}
	public void setProviderURL(String providerURL) {
		setValueIntoTablaPreferencias(KEY_PREF_PROVIDER_URL, providerURL);
	}
	public String getDataSource() {
		return getValueFromTablaPreferencias(KEY_PREF_DATASOURCE);
	}
	public void setDataSource(String datasource) {
		setValueIntoTablaPreferencias(KEY_PREF_DATASOURCE, datasource);
	}
	public String getDebugSoap() {
		return getValueFromTablaPreferencias(KEY_PREF_DEBUG_SOAP);
	}
	public void setDebugSoap(String debug) {
		setValueIntoTablaPreferencias(KEY_PREF_DEBUG_SOAP, debug);
	}
}
