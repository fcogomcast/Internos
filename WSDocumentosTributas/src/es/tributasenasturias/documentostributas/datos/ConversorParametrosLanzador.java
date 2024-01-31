package es.tributasenasturias.documentostributas.datos;

import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import es.tributasenasturias.documentostributas.utils.XMLDOMUtils;
import es.tributasenasturias.webservices.Certificados.tipos.Peti;
import es.tributasenasturias.webservices.Certificados.tipos.Peti.Proc;
import es.tributasenasturias.webservices.Certificados.tipos.Peti.Proc.Param;

//import es.tributasenasturias.validacion.XMLDOMUtils;



/**
 * Clase de utilidad para manejar parámetros a procedimientos de base de datos.
 */
public class ConversorParametrosLanzador{

	private HashMap<TIPOS,String> m_tipos = new HashMap<TIPOS, String>();
	private String m_procedimientoAlmacenado = new String();
	private String m_resultado = new String();
	private org.w3c.dom.Document m_resultadoXML = null;
	
	// Objeto para manejar árbol DOM.
	private javax.xml.parsers.DocumentBuilderFactory fact;
	private javax.xml.parsers.DocumentBuilder db;
	
	private Peti peticion;
	/**
	 * Enumeración de tipos de parámetro.
	 */
	public static enum TIPOS {String, Integer, Date, Clob}

	/**
	 * Constructor público.
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public ConversorParametrosLanzador() throws javax.xml.parsers.ParserConfigurationException{
		//Inicializamos la correspondencia de tipos
		m_tipos.put(TIPOS.String, 	"1");
		m_tipos.put(TIPOS.Integer, 	"2");
		m_tipos.put(TIPOS.Date, 	"3");
		m_tipos.put(TIPOS.Clob, 	"4");
		//Inicializamos  los objetos que nos servirán para parsear los resultados.
		fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		db= fact.newDocumentBuilder(); // Lanza javax.xml.parsers.ParserConfigurationException
		peticion = new Peti();
	}

	/**
	 * Método que devuelve el resultado de una llamda.
	 * @return Cadena de resultado de una llamada.
	 */
	public String getResultado(){
		return m_resultado;
	}
	
	
	/**
	 * Obtiene el primer valor asociado al nombre del nodo dado
	 * @param nombreNodo
	 * @return
	 */
	public String getNodoResultado(String nombreNodo){
		if (this.m_resultadoXML==null){
			return null;
		}
		String toReturn = "";
		//org.w3c.dom.NodeList nodos = this.m_resultadoXML.getElementsByTagName(nombreNodo);
		org.w3c.dom.NodeList nodos = XMLDOMUtils.getAllNodes(m_resultadoXML, nombreNodo);
		if (nodos.getLength()>0){
			toReturn = XMLDOMUtils.getNodeText(nodos.item(0));
		}		
		return toReturn;
	}

	/**
	 * Parsea el resultado como XML. En caso de no poder hacerlo, lo trata como null.
	 * @param nombreNodo
	 * @return
	 */
	private void interpretaResultadoXML(){
		if (this.m_resultado !=null && this.m_resultado !=""){
			org.xml.sax.InputSource inStr = new org.xml.sax.InputSource (); 
			inStr.setCharacterStream(new java.io.StringReader(m_resultado.toString()));// Ya es un string, pero para prevenir futuros cambios.
			try{
				this.m_resultadoXML = db.parse(inStr);
			}catch (java.io.IOException ex){
				this.m_resultadoXML =null;
			}catch (org.xml.sax.SAXException ex){
				this.m_resultadoXML=null;
			}
		}
	}

	/**
	 * Método para pasar un parámetro a la lista de un procedimiento  almacenado que se va a ejecutar.
	 * @param valor Valor del parámetro. Los parámetros son posicionales.
	 * @param tipo Tipo del parámetro.
	 */
	public void setParametro(String valor, TIPOS tipo){
		if (valor==null) // Si no se hace así, introduce en la base de datos "null", al incluirlo en el ArrayList.
			valor="";
		Param param = new Param();
		param.setValor(valor);
		param.setTipo(Short.valueOf(m_tipos.get(tipo)));
		param.setFormato("");
		peticion.getProc().getParam().add(param);
		
	}

	/**
	 * Método para pasar un parámetro con formato a la lista de un procedimiento almacenado que se va a ejecutar.
	 * @param valor Valor del parámetro. Los parámetros son posicionales.
	 * @param tipo Tipo del parámetro.
	 * @param formato Formato del parámetro.
	 */
	public void setParametro(String valor, TIPOS tipo, String formato){
		if (valor==null)
			valor="";
		Param param = new Param();
		param.setValor(valor);
		param.setTipo(Short.valueOf(tipo.toString()));
		param.setFormato(formato);
		peticion.getProc().getParam().add(param);
	}

	/**
	 * Método que indica el procedimiento almacenado que se va a ejecutar.
	 * @param nombre Nombre del procedimiento almacenado.
	 */
	public void setProcedimientoAlmacenado(String nombre){
		m_procedimientoAlmacenado = nombre;
		Proc proc = new Peti.Proc();
		proc.setNombre(nombre);
		peticion.setProc(proc);
	}

	public String getProcedimientoAlmacenado(){
		return m_procedimientoAlmacenado;
	}
	/**
	 * Petición a base de datos interpretada como una cadena
	 * @param peticion Objeto {@link Peti}
	 * @return Cadena con la representación en texto del objeto Petición
	 * @throws DatosException
	 */
	private String peticionToString(Peti peticion) throws DatosException
	{
		try {
			JAXBContext ctx= JAXBContext.newInstance(this.peticion.getClass().getPackage().getName());
			Marshaller mars = ctx.createMarshaller();
			StringWriter writer = new StringWriter();
			mars.marshal(peticion, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new DatosException  ("Excepción al crear la petición:" + e.getMessage(),e);
		}
	}
	/**
	 * Devuelve la representación del acceso a datos que necesita el objeto Lanzador
	 * @return
	 */
	public String Codifica() throws DatosException{
		return peticionToString(this.peticion);	
	}
	public void setResultado(String resultado) {
		this.m_resultado = resultado;
		interpretaResultadoXML();
	}
}