package es.tributasenasturias.documentostributas.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * @author crubencvs
 * Paquete con utilidades varias de XML.
 */
public class XMLDOMUtils {
	
	/**
	 * Recupera el valor del atributo "nombreAtributo" del nodo "nodo".
	 * @param nodo
	 * @param nombreAtributo
	 * @return Valor del atributo o nulo si no se ha encontrado.
	 */
	public static String getAttributeValue (Node nodo, String nombreAtributo)
	{
		NamedNodeMap attrs=null;
		Node att=null;
		String valor=null;
		attrs = nodo.getAttributes();
		if (attrs!=null)
		{
			att = attrs.getNamedItem(nombreAtributo);
			if (att!=null)
			{
				valor = att.getNodeValue();
			}
		}
		return valor;
	}

	/**
	 * Recupera el nodo que contiene el valor (nodo Texto | CData)
	 * @param nodo Nodo del que se quiere recuperar el nodo valor.
	 * @return
	 */
	private static Node getNodoValor (Node nodo)
	{
		Node nodoValor=null;
		Node hijo=null;
		if (nodo!=null)
		{
			if (nodo.hasChildNodes())
			{
				for (hijo=nodo.getFirstChild();hijo!=null;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeType()==Node.TEXT_NODE||hijo.getNodeType()==Node.CDATA_SECTION_NODE)
					{
						nodoValor = hijo;// El texto del nodo está en el nodo texto hijo.
						break;
					}
				}
			}
		}
		return nodoValor;
	}
	/**
	 * Recupera el texto de un nodo.
	 * @param nodo
	 * @return El valor del nodo, o null si no tiene valor.
	 */
	public static String getNodeText(Node nodo)
	{
		String valor=null;
		Node hijo=null;
		if (nodo!=null)
		{
			if (nodo.hasChildNodes())
			{
				for (hijo=nodo.getFirstChild();hijo!=null;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeType()==Node.TEXT_NODE||hijo.getNodeType()==Node.CDATA_SECTION_NODE)
					{
						valor = hijo.getNodeValue();// El texto del nodo está en el nodo texto hijo.
						break;
					}
				}
			}
		}
		return valor;
	}
	/**
	 * Recupera el nesimo nodo con el tag indicado que sea hijo del nodo pasado por parámetro.
	 * @param padre Nodo padre en el que se buscarán sus hijos.
	 * @param tag Tag del nodo hijo a buscar.
	 * @param occur Número de ocurrencia del nodo (1,2,3, para primero, segundo, tercero, etc).
	 * @return Nodo hijo o null si no se ha encontrado.
	 */
	public static Node getNthChildNode (Node padre, String tag, int occur)
	{
		Node res=null;
		Node hijo=null;
		int i=1;
		if (padre!=null)
		{
			if (padre.hasChildNodes() && padre.getChildNodes().getLength()>=occur)
			{
				for (hijo=padre.getFirstChild();hijo!=null && i<=occur;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeName().equalsIgnoreCase(tag) && (i==occur))
					{
						res = hijo;// El texto del nodo está en el nodo texto hijo.
						break;
					}
					else if (hijo.getNodeName().equalsIgnoreCase(tag))
					{
						i++;
					}
				}
			}
		}
		return res;
	}
	/**
	 * Recupera el primer nodo hijo con ese tag.
	 * @param padre Nodo padre.
	 * @param tag Etiqueta del nodo hijo a buscar.
	 * @return Primer hijo con esa etiqueta o null si no se ha encontrado.
	 */
	public static Node getFirstChildNode(Node padre, String tag)
	{
		Node res=null;
		res = getNthChildNode (padre, tag,1);
		return res;
	}
	/**
	 * Recupera la lista de nodos con ese nombre en cualquier Namespace.
	 * @param doc Documento en el que buscar.
	 * @param tagNodo Etiqueta del nodo
	 * @return Lista de nodos DOM cuya etiqueta coincida.
	 */
	public static NodeList getAllNodes (Document doc, String tagNodo)
	{
		NodeList list=null;
		try
		{
			//Compilamos una expresión XPath que devuelva todos los nodos con el tag, en cualquier parte del documento.
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression exp = xpath.compile("//*[local-name()='"+tagNodo+"']");
			list = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			list=null;
		}
		return list;
	}
	/**
	 * Recupera el primer con ese nombre en cualquier Namespace.
	 * @param doc Documento en el que buscar.
	 * @param tagNodo Etiqueta del nodo
	 * @return Nodo DOM cuya etiqueta coincida.
	 */
	public static Node getFirstNode (Document doc, String tagNodo)
	{
		NodeList list=null;
		Node nodo=null;
		try
		{
			//Compilamos una expresión XPath que devuelva todos los nodos con el tag, en cualquier parte del documento.
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression exp = xpath.compile("//*[local-name()='"+tagNodo+"']");
			list = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
			if (list.getLength()>0)
			{
				nodo=list.item(0);
			}
		}
		catch (XPathExpressionException ex)
		{
			nodo=null;
		}
		return nodo;
	}
	/**
	 * Recupera la lista de nodos con esa expresión XPath en cualquier profundidad.
	 * @param doc Documento en el que buscar.
	 * @param expresion Expresión XPath
	 * @return Lista de nodos DOM que cumplen la expresión.
	 */
	public static NodeList getAllNodesXPath (Document doc, String expresion)
	{
		NodeList list=null;
		try
		{
			//Compilamos una expresión XPath que devuelva todos los nodos con el tag, en cualquier parte del documento.
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression exp = xpath.compile(expresion);
			list = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			list=null;
		}
		return list;
	}
	/**
	 * Inserta el texto en el nodo.
	 * @param doc Documento en el que se encuentra el texto a insertar.
	 * @param nodo Nodo en el que insertar texto (en un nodo hijo )
	 * @param texto  Texto.
	 */
	public static  void setNodeText (Document doc,Node nodo, String texto)
	{
		Node valorAnterior = getNodoValor (nodo);
		if (valorAnterior != null) //Existía un nodo previo con valor
		{
			valorAnterior.setNodeValue(texto);
		}
		else
		{
			Node textN = doc.createTextNode(texto);
			nodo.appendChild(textN);
		}
	}
	/**
	 * Parsea un xml que se incluye a través del InputStream, y lo asigna al objeto documento 
	 * @param doc Objeto de tipo documento que se creará.
	 * @param iXML InputStream que se haya creado sobre el XML.
	 * @return Documento xml.
	 * @throws XMLDOMDocumentException
	 */
	public static Document parseXML (Document doc,InputStream iXML) throws XMLDOMDocumentException
	{
		javax.xml.parsers.DocumentBuilderFactory fact;
		javax.xml.parsers.DocumentBuilder db;
		try
		{
			fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			db= fact.newDocumentBuilder();
			doc = db.parse(iXML);
			return doc;
		}
		catch (javax.xml.parsers.ParserConfigurationException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml.",ex);
		}
		catch (org.xml.sax.SAXException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml.",ex);
		}
		catch (java.io.IOException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml.",ex);
		}
	}
	/**
	 * Parsea un xml que se incluye a través de fichero, y lo asigna al objeto documento 
	 * @param doc Objeto de tipo documento que se creará.
	 * @param pFicheroXML. Fichero que contiene el XML que se parseará.
	 * @return Documento xml
	 * @throws XMLDOMDocumentException
	 */
	public static Document parseXML (Document doc, File pFicheroXML) throws XMLDOMDocumentException
	{
		try
		{
			if (pFicheroXML !=null)
			{
				FileInputStream fs = new FileInputStream(pFicheroXML);
				doc = parseXML(doc,fs);
				return doc;
			}
			else 
			{
				throw new XMLDOMDocumentException ("No se ha indicado fichero a tratar.");
			}
		}
		catch (FileNotFoundException ex)
		{
			throw new XMLDOMDocumentException ("No existe el fichero a tratar o no se puede leer.");
		}
	}
	
	/**
	 * Parsea un xml que se incluye a través de un string, y lo asigna al objeto documento 
	 * @param doc Objeto de tipo documento que se creará.
	 * @param pXml. String que contiene XML.
	 * @return Documento Xml
	 * @throws XMLDOMDocumentException
	 */
	public static Document parseXml(Document doc,String pXML) throws XMLDOMDocumentException
	{
		javax.xml.parsers.DocumentBuilderFactory fact;
		javax.xml.parsers.DocumentBuilder db;
		try
		{
			fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			db= fact.newDocumentBuilder();
			org.xml.sax.InputSource inStr = new org.xml.sax.InputSource ();
			inStr.setCharacterStream(new java.io.StringReader(pXML));
			doc = db.parse(inStr);
			return doc;
		}
		catch (javax.xml.parsers.ParserConfigurationException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml.",ex);
		}
		catch (org.xml.sax.SAXException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:" + ex.getMessage(),ex);
		}
		catch (java.io.IOException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Parsea un xml que se incluye a través de un string, y lo asigna al objeto documento 
	 * @param pXml. String que contiene XML.
	 * @return Documento Xml
	 * @throws XMLDOMDocumentException
	 */
	public static Document parseXml(String pXML) throws XMLDOMDocumentException
	{
		javax.xml.parsers.DocumentBuilderFactory fact;
		javax.xml.parsers.DocumentBuilder db;
		Document doc;
		try
		{
			fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			db= fact.newDocumentBuilder();
			org.xml.sax.InputSource inStr = new org.xml.sax.InputSource ();
			inStr.setCharacterStream(new java.io.StringReader(pXML));
			doc = db.parse(inStr);
			return doc;
		}
		catch (javax.xml.parsers.ParserConfigurationException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml.",ex);
		}
		catch (org.xml.sax.SAXException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:" + ex.getMessage(),ex);
		}
		catch (java.io.IOException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Devuelve la representación XML de objeto doc.
	 * @param doc Documento que se devolverá  como texto
	 * @return Cadena que representa el texto de XML.
	 * @throws XMLDOMDocumentException
	 */
	public static String getXMLText(Document doc) throws XMLDOMDocumentException
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no"); // No indentar salida, dejar tal como está.
	
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
	
			String xmlString = result.getWriter().toString();
			return xmlString;
		}
		catch (TransformerConfigurationException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
		catch (TransformerException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
	}
	/**
	 * Devuelve la representación XML de un elemento DOM.
	 * @param node Documento que se devolverá  como texto
	 * @return Cadena que representa el texto de XML.
	 * @throws XMLDOMDocumentException
	 */
	public static String Node2Text(Node node) throws XMLDOMDocumentException
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no"); // No indentar salida, dejar tal como está.
	
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(node);
			transformer.transform(source, result);
	
			String xmlString = result.getWriter().toString();
			return xmlString;
		}
		catch (TransformerConfigurationException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
		catch (TransformerException ex)
		{
			throw new XMLDOMDocumentException ("Imposible devolver la representación en texto de este XML:" + ex.getMessage(),ex);
		}
	}
}
