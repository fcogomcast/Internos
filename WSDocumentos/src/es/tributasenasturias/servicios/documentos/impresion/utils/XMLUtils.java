package es.tributasenasturias.servicios.documentos.impresion.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;

import es.tributasenasturias.servicios.documentos.exceptions.XMLDOMDocumentException;
import es.tributasenasturias.servicios.documentos.log.Logger;

public class XMLUtils {
	
	Document doc = null;
	Node currentParent = null;
	Node currentNode = null;
	Element baseNode = null;
	String baseNodeName = "<peti>";

	/**
	 * Crea un nuevo Documento XML
	 * 
	 * @return Documento XML vacio
	 */
	public static Document crearDocument() {
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docbld = factory.newDocumentBuilder();
			doc = docbld.newDocument();
		} catch (ParserConfigurationException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			doc = null;
		} finally {
			doc = null;
		}
		return doc;
	}

	/**
	 * Crea un nuevo Documento XML para query a WebService
	 */
	public void crearXMLDoc() throws RemoteException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docbld = factory.newDocumentBuilder();
			doc = docbld.newDocument();
			currentParent = (Node) doc;
		} catch (ParserConfigurationException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			doc = null;
			throw new RemoteException("NO EXISTE XML", e);
			
		}
	}

	/**
	 * Asigna el nodo al que unir los nuevos nodos
	 * 
	 * @param nodeLoc si <0, escoge el padre del nodo actual; si >0, escoge el primer hijo del nodo actual
	 */
	public void reParentar(int nodeLoc) {
		if (nodeLoc < 0 && currentParent != baseNode) {
			currentNode = currentParent;
			currentParent = (Node) currentParent.getParentNode();
		}
		if (nodeLoc > 0) {
			currentParent = currentNode;
			if (currentParent.getChildNodes() != null && currentParent.getChildNodes().getLength() != 0)
				currentNode = (Node) currentParent.getFirstChild();
		}
	}

	/**
	 * Crea y asigna al nodo base un nuevo nodo sin atributos o propiedades.
	 * 
	 * @param nombre
	 * @param valor
	 */
	public void crearNode(String nombre, String valor) {
		crearNode(nombre, valor, null, null);
	}

	/**
	 * Crea y asigna al nodo base un nuevo nodo.
	 * 
	 * @param nombre
	 * @param valor
	 * @param atributos
	 * @param atribValores
	 */
	public void crearNode(String nombre, String valor, String[] atributos, String[] atribValores) {
		Element subNode = doc.createElement(nombre);
		if (atributos != null && atribValores != null && atributos.length == atribValores.length) {
			for (int i = 0; i < atributos.length; i++) {
				subNode.setAttribute(atributos[i], atribValores[i]);
			}
		}
		if (valor != null && !valor.equals(""))
			subNode.setTextContent(valor);
		currentParent.appendChild(subNode);
		currentNode = subNode;
	}

	/**
	 * Crea el nodo para un parametro de busqueda de query de Webservice y asigna valor,tipo y formato
	 * 
	 * @param atribValor Indice del parametro, valor numerico
	 * @param txtValor
	 * @param txtTipo Valor numerico (1,2,3)
	 * @param txtFormato Formato de fecha o vacio
	 */
	public void crearParamNode(String atribValor, String txtValor, String txtTipo, String txtFormato) {
		crearNode("param", "", new String[] { "id" }, new String[] { atribValor });
		reParentar(1);
		crearNode("valor", txtValor);
		crearNode("tipo", txtTipo);
		crearNode("formato", txtFormato);
		reParentar(-1);
	}

	/**
	 * Crea el nodo raiz del XML de busqueda de WebService. Nota: el nodo < proc > necesita el parametro "nombre"
	 * 
	 * @param valor1 Parametros del nodo < peti >
	 * @param atributos1
	 * @param atribValores1
	 * @param valor2 Parametros del nodo < proc >
	 * @param atributos2
	 * @param atribValores2
	 * 
	 */
	public void abrirPetiProcNode(String valor1, String valor2, String[] atributos1, String[] atribValores1,
			String[] atributos2, String[] atribValores2) {
		crearNode("peti", valor1, atributos1, atribValores1);
		reParentar(1);
		crearNode("proc", valor2, atributos2, atribValores2);
		reParentar(1);
	}

	/**
	 * Cierra el nodo raiz del XML de busqueda de WebService; reparenta los nodos base.
	 */
	public void cerrarPetiProcNode() {
		reParentar(-1);
		reParentar(-1);
	}

	/**
	 * Devuelve el texto XML del documento de busqueda de WebService creado
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String informarXMLDoc() throws RemoteException {
		try {
			TransformerFactory tfactory = TransformerFactory.newInstance();
			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(bos);

			Transformer transformer = tfactory.newTransformer();// intro param para xsl!
			transformer.transform(source, result);
			String resultdata = bos.toString("UTF-8");
			int posicionFin = resultdata.indexOf("/>");
			while (posicionFin != -1) {
				int posicion = resultdata.lastIndexOf("<", posicionFin);
				if (posicion == -1 || posicion == posicionFin - 1)
					break;
				String nombreNode = resultdata.substring(posicion + 1, posicionFin);
				resultdata = resultdata
						.replaceAll("<" + nombreNode + "/>", "<" + nombreNode + "></" + nombreNode + ">");
				posicionFin = resultdata.indexOf("/>");
			}

			resultdata = resultdata.replaceAll("<formato/>", "<formato></formato>");
			if (resultdata.indexOf("<", 1) != -1)
				resultdata = resultdata.substring(resultdata.indexOf("<", 1));
			return resultdata;
		} catch (TransformerException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new RemoteException("NO RETORNA XML", e);
		} catch (UnsupportedEncodingException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new RemoteException("UNSUPPORTED ENCODING EXCEPTION", e);
		}
	}

	/**
	 * Devuelve un mapa con los resultados del XML de salida del WSLanzador. Cada clave del mapa es una estructura de
	 * Oracle, cada valor son los objectos/filas devueltos para esa estructura. Cada fila contiene Strings con el nombre
	 * de la columna y su valor (solo columnas no vacias).
	 * 
	 * @param xmlTexto
	 * @return
	 * @throws RemoteException
	 */
	public static HashMap<String, Object> compilaXMLDoc(String xmlTexto) throws RemoteException {
		return compilaXMLDoc(xmlTexto, null, null, true);
	}

	/**
	 * Determina si un XML tiene una estructura valida
	 * @param xml
	 * @return
	 */
	public static boolean esXMLValido(String xml) {
		if (xml == null || xml.length() == 0)
			return false;
		try {
			XMLUtils.compilaXMLDoc(xml);
			return true;
		} catch (Throwable t) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(t.getMessage(),t,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			return false;
		}
	}

	/**
	 * Recupera el encoding de un documento XML, buscando el valor de <?xml encoding="..."> del documento
	 * 
	 * @param xml
	 * @return
	 */
	public static String getEncoding(String xml) {
		return getEncoding(xml, "UTF-8");
	}

	/**
	 * Recupera el encoding de un documento XML, buscando el valor de <?xml encoding="..."> del documento
	 * 
	 * @param xml
	 * @param baseEncoding
	 *            Encoding a usar; por defecto, UTF-8. Es necesario informar este campo para encodings no caompatibles
	 *            con UTF-8 (UTF-16, alfabetos no latinos)
	 * @return
	 */
	public static String getEncoding(String xml, String baseEncoding) {
		String encoding = baseEncoding;
		if (xml.startsWith("<?xml")||xml.startsWith("<?XML")) {
			String xmlCabecera = xml.substring(0, xml.indexOf(">") + 1);
			if (xmlCabecera != null && xmlCabecera.indexOf("encoding") != -1) {
				xmlCabecera = xmlCabecera.substring(xmlCabecera.indexOf("encoding"), xmlCabecera.indexOf(">"));
				int startpos = xmlCabecera.indexOf("\"");
				if (startpos == -1)
					startpos = xmlCabecera.indexOf("\'");
				int endpos = xmlCabecera.indexOf("\"", startpos + 1);
				if (endpos == -1)
					endpos = xmlCabecera.indexOf("\'", startpos + 1);
				encoding = xmlCabecera.substring(startpos + 1, endpos);
			}
		}
		return encoding;
	}


	/**
	 * Elimina el valor de <!doctype "..."> del documento, permitiendo la carga de un XML sin su plantilla. La
	 * alternativa es cargar la plantilla/ documento DTD localmente y asignarla por defecto al DocumentBuilder
	 * 
	 * @param xml
	 * @return
	 */
	public static String borrarDoctype(String xml) {
		int startpos=xml.indexOf("<!DOCTYPE",0);
		if(startpos==-1){
			startpos=xml.indexOf("<!doctype");}
		int endpos=xml.indexOf(">",startpos);
		if(startpos!=-1&&endpos!=-1){
			xml=xml.substring(0,startpos)+xml.substring(endpos+1,xml.length()-1);
		}
		return xml;
	}

	/**
	 * Devuelve un objeto con la informacion del XML de entrada. Si saxHandler==null, devuelve un Document con la
	 * estructura. De lo contrario devueve el Handler procesado - incluyendo el objeto a informar en la clase permite
	 * convertir de XML a Bean.
	 * 
	 * @param xmlTexto
	 * @param saxhandler
	 * @return
	 * @throws RemoteException
	 */
	public static Object compilaXMLObject(String xmlTexto, DefaultHandler saxhandler) throws RemoteException {
		// descodifica el documento:
		List<String> lstErrores = new ArrayList<String>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docbld = factory.newDocumentBuilder();
			Document doc = null;
			// String TEXTNULL = "#text";
			String encoding = getEncoding(xmlTexto);
			xmlTexto=borrarDoctype(xmlTexto);
			InputStream is = new ByteArrayInputStream(xmlTexto.getBytes(encoding));
			if (saxhandler != null) {
				SAXParserFactory parfact = SAXParserFactory.newInstance();
				SAXParser saxer = parfact.newSAXParser();
				saxer.parse(is, saxhandler);
				return saxhandler;
			} else {
				if (xmlTexto != null && xmlTexto.length() > 0)
					doc = docbld.parse(is);
				return doc;
			}
		} catch (ParserConfigurationException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new RemoteException("Error en Parser", e);
		} catch (SAXParseException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			lstErrores.add("Error en Parser SAX");
			return null;
		} catch (Throwable e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new RemoteException("NO RETORNA XML", e);
		}
	}

	/**
	 * Devuelve un mapa con los resultados del XML de salida del WSLanzador, para queries tipo < estruct>< estructura><
	 * fila>< prop>__<...>. Cada clave del mapa es una estructura de Oracle, cada valor son los objectos/filas devueltos
	 * para esa estructura. Cada fila contiene Strings con el nombre de la columna y su valor (solo columnas no vacias).
	 * SOLO los parametros informados se recuperan (en el orden informado) de las estructuras correspondientes (si
	 * informadas) si recuperaParam==false, el resultado es HashMap<nombre,Object[]{String[],...}>
	 * 
	 * @param xmlTexto
	 * @param estructuras
	 * @param parametros
	 * @return
	 * @throws RemoteException
	 */
	public static HashMap<String, Object> compilaXMLDoc(String xmlTexto, String[] estructuras, String[] parametros,
			boolean recuperarParam) throws RemoteException {
		// descodifica el documento:
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<String> lstErrores = new ArrayList<String>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docbld = factory.newDocumentBuilder();
			Document doc = null;
			String encoding = getEncoding(xmlTexto);
			InputStream is = new ByteArrayInputStream(xmlTexto.getBytes(encoding));
			doc = docbld.parse(is);
			Element baseElement = doc.getDocumentElement();
			NodeList lstEstructura = baseElement.getChildNodes();
			HashMap<String, Integer> mapParams = null;
			HashMap<String, String> mapEstruct = null;
			if (parametros != null && parametros.length != 0) {
				mapParams = new HashMap<String, Integer>();
				for (int i = 0; i < parametros.length; i++)
					mapParams.put(parametros[i], Integer.valueOf(i));
				if (estructuras != null && estructuras.length != 0) {
					mapEstruct = new HashMap<String, String>();
					for (int i = 0; i < parametros.length; i++)
						mapEstruct.put("" + i, estructuras[i]);
				}
			}
			for (int i = 0; i < lstEstructura.getLength(); i++) {
				Node nodeActual = lstEstructura.item(i);
				if (nodeActual.getNodeName().equals("estruc")) {
					String nombreEstruct = ((Element) nodeActual).getAttribute("nombre");
					if (nombreEstruct == null || nombreEstruct.equals(""))
						nombreEstruct = "estruc" + i;
					NodeList lstFilas = nodeActual.getChildNodes();
					nodeActual.getAttributes();
					if (lstFilas != null && lstFilas.getLength() != 0) {
						ArrayList<String[]> lstTotalFilas = new ArrayList<String[]>();
						ArrayList<String> lstCadenas;
						for (int j = 0; j < lstFilas.getLength(); j++) {
							Node nodeColumna = lstFilas.item(j);
							NodeList lstColumnas = nodeColumna.getChildNodes();
							if (lstColumnas != null && lstColumnas.getLength() > 0) {
								lstCadenas = new ArrayList<String>();
								if (mapParams != null && !mapParams.isEmpty()) {
									int limit = (lstColumnas.getLength() > mapParams.size()) ? lstColumnas.getLength()
											: mapParams.size();
									for (int k = 0; k < limit; k++) {
										lstCadenas.add("");
										if (recuperarParam)
											lstCadenas.add("");
									}
								}
								for (int k = 0; k < lstColumnas.getLength(); k++) {
									String key = lstColumnas.item(k).getNodeName();
									String valor = lstColumnas.item(k).getTextContent();
									int tipo = lstColumnas.item(k).getNodeType();
									if (valor == null)
										valor = "";
									if (tipo != Node.TEXT_NODE && tipo != Node.COMMENT_NODE) {
										if (mapParams == null && !valor.equals("")) {
											if (recuperarParam)
												lstCadenas.add(lstColumnas.item(k).getNodeName());
											lstCadenas.add(valor);
										} else if (mapParams != null && mapParams.containsKey(key)) {
											int pos = ((Integer) mapParams.get(key)).intValue();
											String estruct = nombreEstruct;
											if (mapEstruct != null) {
												estruct = (String) mapEstruct.get("" + pos);
												if (estruct == null || estruct.equals(""))
													estruct = nombreEstruct;
											}
											if (recuperarParam) {
												lstCadenas.set(2 * pos, lstColumnas.item(k).getNodeName());
												lstCadenas.set(2 * pos + 1, valor);
											} else if (estruct.equals(nombreEstruct))
												lstCadenas.set(pos, valor);
										}
									}

								}
								// limpiar caden de NULLS
								if (mapParams != null && !mapParams.isEmpty()) {
									int size = (recuperarParam) ? 2 * mapParams.size() : mapParams.size();
									int step = (recuperarParam) ? 2 : 1;
									int msize = (mapEstruct != null) ? step * mapEstruct.size() - 1 : size - 1;
									for (int m = msize; m >= 0; m -= step) {
										//String lstcadstr = "";
										//for (int y = 0; y < lstCadenas.size(); y++)
										//	lstcadstr += (String) lstCadenas.get(y);
										String estruc_actual = nombreEstruct;
										if (mapEstruct != null) {
											estruc_actual = (String) mapEstruct.get("" + m);
											if (estruc_actual == null || estruc_actual.equals(""))
												estruc_actual = nombreEstruct;
										}
										if (!estruc_actual.equals("") && !estruc_actual.equals(nombreEstruct)) {
											lstCadenas.remove(m);
											if (recuperarParam)
												lstCadenas.remove(m - 1);
											size -= step;
										}
									}
									while (lstCadenas.size() > size)
										lstCadenas.remove(lstCadenas.size() - 1);
								}
								String[] strCadenas = new String[lstCadenas.size()];
								for (int k = 0; k < lstCadenas.size(); k++) {
									strCadenas[k] = (String) lstCadenas.get(k);
								}
								lstTotalFilas.add(strCadenas);
							}
						}
						Object[] totalFilas = lstTotalFilas.toArray();
						map.put(nombreEstruct, totalFilas);
					} else {
						map.put(nombreEstruct, new Object[0]);
					}

				} else if (nodeActual.getNodeName().startsWith("error")) {
					lstErrores.add(nodeActual.getTextContent());
				}
			}
			if (!lstErrores.isEmpty())
				map.put("ERROR", lstErrores.toArray());
			return map;
		} catch (ParserConfigurationException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new RemoteException("NO RETORNA XML", e);
		} catch (SAXException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			lstErrores.add("Error: documento no valido");
			map.put("ERROR", lstErrores.toArray());
			return map;
		} catch (DOMException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			lstErrores.add("Error: documento no valido");
			map.put("ERROR", lstErrores.toArray());
			return map;
		} catch (Throwable e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new RemoteException("NO RETORNA XML", e);
		}
	}

	/**
	 * Convierte un String[] de retorno de WebService completo(XMLUtils.compilaXML()) en un mapa con lineas de parametros,
	 * donde Map.get(new Integer(n-1)) devuelve los parametros de la n-esima linea.
	 * @param arg0
	 * @return
	 */
	public static HashMap<Integer, Map<String, String>> convertirObjectStringArrayHashMap(Object[] arg0) {
		HashMap<Integer, Map<String, String>> result = new HashMap<Integer, Map<String, String>>();
		int count = 0;
		for (int i = 0; i < arg0.length; i++) {
			String[] cadenapares = (String[]) arg0[i];
			if (cadenapares == null)
				continue;
			Integer indice = Integer.valueOf(count);
			count++;
			HashMap<String, String> lineaResult = new HashMap<String, String>();
			if (cadenapares != null && cadenapares.length != 0) {
				for (int j = 0; j < cadenapares.length; j += 2)
					if (cadenapares[j + 1] != null && !cadenapares[j + 1].equals(""))
						lineaResult.put(cadenapares[j], cadenapares[j + 1]);
			}
			result.put(indice, lineaResult);
		}
		return result;
	}
	
	/**
	 * Devuelve el parametro del mapa completo del resultado de la query a WebService
	 * @param map Mapa del resultado de la query, incluyendo estructuras, filas i nodos
	 * @param estruct ID del nodo < estruct >
	 * @param index numero del nodo < fila > a recuperar
	 * @param param nombre del parametro
	 * @return
	 */
	public static String getParamIndexedHashMap(HashMap<String, Object> map, String estruct, Integer index, String param) {
		if (map != null)
			if (map.get(estruct) != null && map.get(estruct) instanceof HashMap)
				return ((HashMap<Integer, Map<String, String>>) map.get(estruct)).get(index).get(param);
		return "";
	}

	/**
	 * Devuelve todos los parametros de la primera fila de resultados del XML. Ver compilaXMLDoc()
	 * @param xmlTexto
	 * @param parametros
	 * @param recuperarParam
	 * @return
	 * @throws RemoteException si el XML no es valido o si contiene una seccion con errores del servidor
	 */
	public static String[] recuperaFilaXMLDoc(String xmlTexto, String[] parametros, boolean recuperarParam)
			throws RemoteException {
		return recuperaFilaXMLDoc(xmlTexto, null, parametros, recuperarParam);
	}

	/**
	 * Devuelve parametros de la primera fila de resultados del XML. Ver compilaXMLDoc()
	 * @param xmlTexto XML de entrada
	 * @param estructuras 
	 * @param parametros
	 * @param recuperarParam
	 * @return
	 * @throws RemoteException si el XML no es valido o si contiene una seccion con errores del servidor
	 */
	public static String[] recuperaFilaXMLDoc(String xmlTexto, String[] estructuras, String[] parametros,
			boolean recuperarParam) throws RemoteException {
		HashMap<String, Object> map = compilaXMLDoc(xmlTexto, estructuras, parametros, recuperarParam);
		if (map != null && map.containsKey("ERROR")) {
			Object[] lstErrores = (Object[]) map.get("ERROR");
			String listaErrores = "";
			for (int i = 0; i < lstErrores.length; i++)
				listaErrores += lstErrores[i].toString() + " \n";
			throw new WebServiceException(listaErrores);
		} else if (map != null) {
			Iterator<String> it = map.keySet().iterator();
			if (it.hasNext()) {
				String key = (String) it.next();
				Object[] lstFilas = (Object[]) map.get(key);
				for (int i = 0; i < lstFilas.length; i++) {
					String[] lstProps = (String[]) lstFilas[i];
					if (lstProps == null || lstProps.length == 0)
						continue;
					String[] lstStrings = new String[lstProps.length];
					for (int j = 0; j < lstProps.length; j++) {
						lstStrings[j] = lstProps[j];
					}
					return lstStrings;
				}
			}
		}
		return null;
	}

	/**
	 * @see XMLUtils.getNodes(XMLIn, nodeNames, p_nombrecompleto)
	 */
	public static Object[] getNodes(String XMLIn) throws RemoteException {
		return getNodes(XMLIn, null, false);
	}

	/**
	 * @see XMLUtils.getNodes(XMLIn, nodeNames, p_nombrecompleto)
	 */
	public static Object[] getNodes(String XMLIn, boolean p_nombrecompleto) throws RemoteException {
		return getNodes(XMLIn, null, p_nombrecompleto);
	}

	/**
	 * @see XMLUtils.getNodes(XMLIn, nodeNames, p_nombrecompleto)
	 */
	public static Object[] getNodes(String XMLIn, String[] nodeNames) throws RemoteException {
		return getNodes(XMLIn, nodeNames, false);
	}

	/**
	 * Devuelve dos mapas, uno de atributos y otro de valores, para la lista de nodos deseada
	 * @param XMLIn
	 * @param nodeNames Lista de nodos
	 * @param p_nombrecompleto Determina si incluir o no el nombre del nodo en la respuesta
	 * @return
	 * @throws RemoteException
	 */
	@SuppressWarnings("unchecked")
	public static Object[] getNodes(String XMLIn, String[] nodeNames, boolean p_nombrecompleto) throws RemoteException {
		Document doc = (Document) XMLUtils.compilaXMLObject(XMLIn, null);
		HashMap<String, Object> mapAttributes = new HashMap<String, Object>();
		HashMap<String, Object> mapTextValue = new HashMap<String, Object>();
		HashMap<String, String> mapNames = new HashMap<String, String>();
		if (nodeNames != null) {
			for (int i = 0; i < nodeNames.length; i++) {
				if (nodeNames[i].lastIndexOf("/") != -1)
					mapNames.put(nodeNames[i].substring(nodeNames[i].lastIndexOf("/") + 1), "");
				else
					mapNames.put(nodeNames[i], "");
			}
		}
		Object[] lstResults = new Object[2];
		Node nodeActual = doc;
		boolean devuelta = false;
		String nameRoot = "";
		while (nodeActual != null) {
			String name = nodeActual.getNodeName();
			String value = nodeActual.getTextContent();
			boolean chkNodeHijo = false;// comprueba que no existe un nodo inferior con su nombre
			boolean notNodeTexto = nodeActual.getNodeType() != Node.TEXT_NODE
					&& nodeActual.getNodeType() != Node.COMMENT_NODE;
			if (notNodeTexto && nodeActual.hasChildNodes() && value != null && value.length() != 0) {
				for (int i = 0; i < nodeActual.getChildNodes().getLength(); i++) {
					// Todos los nodos tienen un TEXT_NODE
					if (nodeActual.getChildNodes().item(i).getNodeType() != Node.TEXT_NODE
							&& nodeActual.getChildNodes().item(i).getNodeType() != Node.COMMENT_NODE) {
						if (nodeActual.getChildNodes().item(i).getNodeName().equals(name))
							chkNodeHijo = true;
						int pos=value.indexOf(nodeActual.getChildNodes().item(i).getTextContent());
						int length=nodeActual.getChildNodes().item(i).getTextContent().length();
						if(pos!=-1 && length!=0)
							value=value.substring(0,pos)+value.substring(pos+length, value.length());
					}
				}
			}
			if (notNodeTexto && !p_nombrecompleto && (!chkNodeHijo) && !devuelta
					&& (mapNames.containsKey(name) || mapNames.isEmpty())) {
				List<String> textoActual = (List<String>) mapTextValue.get(name);
				if (textoActual == null)
					textoActual = new ArrayList<String>();
				textoActual.add(value);
				List<Object> attribActual = (List<Object>) mapAttributes.get(name);
				if (attribActual == null)
					attribActual = new ArrayList<Object>();
				attribActual.add(nodeActual.getAttributes());
				mapTextValue.put(name, textoActual);
				mapAttributes.put(name, attribActual);
			} else if ((notNodeTexto && p_nombrecompleto && (!chkNodeHijo) && !devuelta)
					&& (mapNames.containsKey(nameRoot + name) || mapNames.isEmpty())) {
				List<String> textoActual = (List<String>) mapTextValue.get(nameRoot + name);
				if (textoActual == null)
					textoActual = new ArrayList<String>();
				textoActual.add(value);
				List<Object> attribActual = (List<Object>) mapAttributes.get(nameRoot + name);
				if (attribActual == null)
					attribActual = new ArrayList<Object>();
				mapTextValue.put(nameRoot + name, textoActual);
				mapAttributes.put(nameRoot + name, attribActual);
			}
			if (nodeActual.hasChildNodes() && !devuelta) {
				if (nodeActual.getNodeType() != Node.DOCUMENT_NODE)
					nameRoot += nodeActual.getNodeName() + "/";
				nodeActual = nodeActual.getFirstChild();
			} else if (nodeActual.getNextSibling() != null) {
				devuelta = false;
				nodeActual = nodeActual.getNextSibling();
			} else {
				nodeActual = nodeActual.getParentNode();
				if (nameRoot.lastIndexOf("/") == -1 || nameRoot.indexOf("/") == nameRoot.length() - 1)
					nameRoot = "";
				else
					nameRoot = nameRoot.substring(0, 1 + nameRoot.lastIndexOf("/", nameRoot.length() - 2));
				devuelta = true;
			}
			if (nodeActual.equals(doc))
				break;
		}
		lstResults[0] = mapAttributes;
		lstResults[1] = mapTextValue;
		return lstResults;

	}

	/**
	 * Selecciona los elementos hijos del nodo que cumplan las condiciones de la expresion de XMpath
	 * @param doc
	 * @param nodename expresion XPath, o nombre del elemento a retornar
	 * @return
	 */
	public static Element[] selectNodes(Node doc, String nodename) {
		XPathFactory xpfact = XPathFactory.newInstance();
		XPath xp = xpfact.newXPath();
			try {
				if (doc == null)
					return new Element[0];
				DTMNodeList o = (DTMNodeList) xp.evaluate(nodename, doc, XPathConstants.NODESET);
				Element[] result = new Element[o.getLength()];
				for (int i = 0; i < result.length; i++)
					result[i] = (Element) o.item(i);
				return result;
			} catch (XPathExpressionException e) {
				es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
				return new Element[0];
			}
	}
	

	/**
	 * Selecciona el primer elementos hijo del nodo que cumplan las condiciones de la expresion de XMpath
	 * @param doc
	 * @param nodename expresion XPath, o nombre del elemento a retornar
	 * @return
	 */
	public static Element selectSingleNode(Node doc, String nodename) {
		if (doc != null) {
			Element[] result = selectNodes(doc, nodename);
			if (result.length > 0)
				return result[0];
		}
		return null;
	}

	/**
	 * Asigna a los nodos del XML los valores deseades
	 * @param XMLIn
	 * @param nodeNames
	 * @param nodeValues
	 * @return
	 * @throws RemoteException Si el XML de entrada o salida no son validos
	 */
	public static String setNodes(String XMLIn, String[] nodeNames, String[] nodeValues) throws RemoteException {
		HashMap<String, String> mapNames = new HashMap<String, String>();
		if (nodeNames == null || nodeNames.length == 0 || nodeValues == null || nodeValues.length == 0)
			return XMLIn;
		for (int i = 0; i < nodeNames.length; i++) {
			mapNames.put(nodeNames[i], nodeValues[i]);
		}
		Document doc = (Document) XMLUtils.compilaXMLObject(XMLIn, null);
		Node nodeActual = doc;
		boolean devuelta = false;
		while (nodeActual != null) {
			String name = nodeActual.getNodeName();
			if (name.contains("/")) {
				// se puede asignar un subnodo especifico
				String[] nameParents = name.split("/");
				name = nameParents[nameParents.length - 1];
				if (mapNames.containsKey(name)) {
					Node nodeParent = nodeActual;
					for (int i = nameParents.length - 2; i >= 0; i--) {
						nodeParent = nodeParent.getParentNode();
						if (!nodeParent.getNodeName().equals(nameParents[i])) {
							name = nodeActual.getNodeName();
							break;
						}
					}
				}
			}
			if (mapNames.containsKey(name)) {
				nodeActual.setTextContent(mapNames.get(name));
				mapNames.remove(name);
			}
			if (nodeActual.hasChildNodes() && !devuelta) {
				nodeActual = nodeActual.getFirstChild();
			} else if (nodeActual.getNextSibling() != null) {
				devuelta = false;
				nodeActual = nodeActual.getNextSibling();
			} else {
				nodeActual = nodeActual.getParentNode();
				devuelta = true;
			}
			if (nodeActual.equals(doc))
				break;
		}
		try {
			TransformerFactory tfactory = TransformerFactory.newInstance();
			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(bos);

			Transformer transformer = tfactory.newTransformer();
			transformer.transform(source, result);
			String resultdata = bos.toString();
			return resultdata;
		} catch (TransformerException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error("Error en XML Transformer: " + e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new RemoteException("Error en XML Transformer", e);
		}
	}

	/**
	 * Elimina todos los espacios, tabulaciones y cambios de linea de una cadena
	 * @param input
	 * @return
	 */
	public static String borraEspacios(String input) {

		if (input != null) {
			input = input.replaceAll(" ", "");
			input = input.replaceAll("\t", "");
			input = input.replaceAll("\n", "");
		}
		return input;
	}

	/**
	 * Elimina todos los espacios, tabulaciones y cambios de linea al principio y final de una cadena
	 * @param input
	 * @return
	 */
	public static String borraEspaciosInicioFin(String input) {

		String input2 = input;
		if (input2 != null) {
			input2 = input2.replaceAll(" ", "");
			input2 = input2.replaceAll("\t", "");
			input2 = input2.replaceAll("\n", "");
			if (input2.length() == 0)
				return "";
			int posIni = input.indexOf(input2.substring(0, 1));
			int posFin = input.lastIndexOf(input2.substring(input2.length() - 1, input2.length()));
			if (posFin == -1)
				posFin = input.length();
			return input.substring(posIni, posFin + 1);
		}
		return input2;
	}

	/**
	 * Funcion de pruebas. Rellena una plantilla XML con datos de prueba.
	 * @param XMLIn
	 * @return
	 */
	public static String fillRandomXML(String XMLIn) {
		String XMLOut = XMLIn;
		Document doc = null;
		String[] randset = { "A", "B", "C", "D", "E", "F", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		try {
			doc = (Document) compilaXMLObject(XMLIn, null);
		} catch (RemoteException e) {
			Logger.trace(e.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			return XMLIn;
		}
		Node nodeActual = doc;
		boolean devuelta = false;
		while (nodeActual != null) {
			String value = nodeActual.getTextContent();
			boolean chkNodeHijo = false;// comprueba que no existe un nodo inferior con su nombre
			if (nodeActual.hasChildNodes() && value != null && value.length() != 0) {
				for (int i = 0; i < nodeActual.getChildNodes().getLength(); i++) {
					// Todos los nodos tienen un TEXT_NODE
					if (nodeActual.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
						chkNodeHijo = true;
					}
				}
			}
			if (!chkNodeHijo && !nodeActual.hasChildNodes() && nodeActual.getNodeType() != Node.TEXT_NODE) {
				int randsize = (int) (Math.random() * 5.0) + 1;
				String randstr = "";
				for (int i = 0; i < randsize; i++) {
					int randpos = (int) (Math.random() * (randset.length));
					randstr = randstr.concat(randset[randpos]);
				}
				nodeActual.appendChild(doc.createTextNode(randstr));
			}
			if (nodeActual.hasChildNodes() && !devuelta) {
				nodeActual = nodeActual.getFirstChild();
			} else if (nodeActual.getNextSibling() != null) {
				devuelta = false;
				nodeActual = nodeActual.getNextSibling();
			} else {
				nodeActual = nodeActual.getParentNode();
				devuelta = true;
			}
			if (nodeActual.equals(doc))
				break;
		}
		try {
			TransformerFactory tfactory = TransformerFactory.newInstance();
			DOMSource source = new DOMSource(doc);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(bos);

			Transformer transformer = tfactory.newTransformer();
			transformer.transform(source, result);
			XMLOut = bos.toString();
		} catch (TransformerException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			return XMLIn;
		}

		return XMLOut;
	}

	/**
	 * Elimina un nodo del Documento
	 * @param objXMLDoc
	 * @param nodo
	 * @return
	 */
	public static Node borrarNodo(Node objXMLDoc, String nodo) {
		Element[] nodelist = selectNodes(objXMLDoc, nodo);
		for (int i = 0; i < nodelist.length; i++) {
			Node parent = nodelist[i].getParentNode();
			parent.removeChild(nodelist[i]);
		}

		return objXMLDoc;
	}

	/**
	 * Retorna el primer Node con el nombre completo y la ID asignada
	 * @param objXMLDoc
	 * @param nodo
	 * @param id
	 * @return
	 */
	public static Node obtenerNodo(Node objXMLDoc, String nodo, String id) {
		Element[] elementList = selectNodes(objXMLDoc, nodo + "[@id=" + id + "]");
		for (int i = 0; i < elementList.length; i++) {
			Element elem = elementList[i];
			if (nodo.endsWith(elem.getNodeName())) {
				String idvalue = elem.getAttribute("id");
				if (idvalue != null && idvalue.equals(id))
					return elem;
			}
		}
		return null;
	}

	/**
	 * obtenerXML - carga el objeto con el xml en sesion
	 */
	public static Document obtenerXML(HashMap<String, Object> session) {
		String xml = (String) session.get("xml");
		if (esXMLValido(xml)) {
			try {
				return (Document) compilaXMLObject(xml, null);
			} catch (RemoteException e) {
				es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			}
		}
		return null;
	}

	/**
	 * Cambia el valor de un nodo del Documento
	 * @param obj
	 * @param nodo
	 * @param valor
	 */
	public static void CambiaNodo(Node obj, String nodo, String valor) {
		if (valor != null) {
			NodeList e = ((Element) obj).getElementsByTagName(nodo);
			if (e.getLength() != 0)
				e.item(0).setTextContent(valor);
		}
	}

	/**
	 * Cambia el valor de un nodo
	 * @param obj
	 * @param valor
	 */
	public static void CambiaNodo(Node obj, String valor) {
		if (obj != null) {
			obj.setTextContent(valor);
		}
	}

	/**
	 * Devuelve el valor del nodo rootNode: objeto xml
	 * @param objXMLDoc
	 * @param nodo
	 * @return
	 */
	public static String leerNodo(Node objXMLDoc, String nodo) {
		return valorNodo(objXMLDoc, "datos/modelo/" + nodo);
	}
	
	/**
	 * Devuelve el valor de un nodo dentro del nodo padre
	 * @param objXMLDoc Nodo padre
	 * @param nodo
	 * @return
	 */
	public static String valorNodo(Node objXMLDoc, String nodo) {
		Element[] elementlist = selectNodes(objXMLDoc, nodo);
		for (int i = 0; i < elementlist.length; i++) {
			Element elem = elementlist[i];
			return elem.getTextContent();
		}
		return "";
	}

	/**
	 * Convierte el texto dentro de los nodos de un XML a mayusculas
	 * @param objXMLDoc
	 * @return
	 */
	public static void XMLaMayusculas(Node objXMLDoc) {
		Element[] elementlist = selectNodes(objXMLDoc, "datos/modelo");
		for (int i = 0; i < elementlist.length; i++) {
			Node node = elementlist[i];
			NodeList nodelist = node.getChildNodes();
			for (int j = 0; j < nodelist.getLength(); j++) {
				Node nodo2 = nodelist.item(j);
				nodo2.setTextContent(nodo2.getTextContent().toUpperCase());
			}
		}
	}


	/**
	 * NOTA: este metodo incluye el node al actual, no al documento base. Para insertar en la raiz, usar
	 * Document.getDocumentElement().
	 * @param objXMLDoc
	 * @param nodo
	 * @throws NullPointerException
	 *             Cuando objXMLDoc es un Document o no pertenece a un Document
	 * @return
	 */
	public static Node addNode(Node objXMLDoc, String nodo) {
		Node objNewNode = null;
		if (objXMLDoc.getOwnerDocument() != null)
			objNewNode = objXMLDoc.getOwnerDocument().createElement(nodo);
		return objXMLDoc.appendChild(objNewNode);
	}

	/**
	 * Comprime un documento en formato ISO-8859-1
	 * @param str Documento en formato cadena
	 * @return
	 */
	public static byte[] compressGZIP(String str) {
		String strout = "";
		byte[] bitout = null;
		try {
			GZIPOutputStream gzipOutputStream = null;
			String encoding="ISO-8859-1";//"UTF-8";
			ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes(encoding));
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			gzipOutputStream = new GZIPOutputStream(output);
			byte[] buf = new byte[512];
			int len;
			while ((len = input.read(buf)) > 0) {
				gzipOutputStream.write(buf, 0, len);
			}
			gzipOutputStream.finish();
			strout = output.toString(encoding);
			strout = new String(output.toByteArray(), encoding);
			bitout = output.toByteArray();
			input.close();
			gzipOutputStream.close();
		} catch (IOException ex) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(ex.getMessage(),ex,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
		return bitout;
	}

	/**
	 * Decomprime un array de bytes en un String en formato ISO-8859-1 (estandar HTML)
	 * @param str byte[]
	 * @return
	 */
	public static String decompressGZIP(byte[] str) {
		return decompressGZIP(str, "ISO-8859-1");
	}
	
	/**
	 * Descomprime un array de bytes en un String en formato ISO-8859-1 (estandar HTML)
	 * @param str byte[]
	 * @param encoding
	 * @return
	 */
	public static String decompressGZIP(byte[] str, String encoding) {
		String strout = "";
		try {
			GZIPInputStream gzipInputStream = null;
			ByteArrayInputStream input = new ByteArrayInputStream(str);// .getBytes("UTF-8"));
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			gzipInputStream = new GZIPInputStream(input);
			byte[] buf = new byte[512];
			int len;
			while ((len = gzipInputStream.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			output.close();
			if (encoding == null||encoding.equals(""))
				encoding="ISO-8859-1";
			
			strout = output.toString(encoding);
			gzipInputStream.close();
		} catch (IOException ex) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(ex.getMessage(),ex,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
		return strout;
	}
	
	/**
	 * Descomprime un stream de bytes en un String en formato ISO-8859-1 (estandar HTML)
	 * @param input
	 * @param output
	 * @return
	 */
	public static void decompressGZIP(InputStream input, OutputStream output) {
		try {
			GZIPInputStream gzipInputStream = null;
			gzipInputStream = new GZIPInputStream(input);
			byte[] buf = new byte[512];
			int len;
			while ((len = gzipInputStream.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			output.flush();
		} catch (IOException ex) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(ex.getMessage(),ex,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
	}
	
	public static void decompressGZIP(String filename, String urlEntrada, OutputStream output) {
		GZIPInputStream gzipInputStream = null;
		try {
			ZipFile z = new ZipFile(new File(urlEntrada));
			gzipInputStream = new GZIPInputStream(z.getInputStream(z.getEntry(filename)));
			byte[] buf = new byte[512];
			int len;
			while ((len = gzipInputStream.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			output.flush();
		} catch (IOException ex) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(ex.getMessage(),ex,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
		finally
		{
				try{if (gzipInputStream!=null) {gzipInputStream.close();}} catch(Exception ex){}
		}
	}

	public static void compressGZIP(InputStream input, OutputStream output) {
		try {
			GZIPOutputStream gzipOutputStream = null;
			gzipOutputStream = new GZIPOutputStream(output);
			byte[] buf = new byte[512];
			int len;
			while ((len = input.read(buf)) > 0) {
				gzipOutputStream.write(buf, 0, len);
			}
			gzipOutputStream.flush();
		} catch (IOException ex) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(ex.getMessage(),ex,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
	}

	public static ArrayList<String> getCompFile(String urlEntrada, String outputFile) throws Exception {
		ZipFile z = new ZipFile(new File(urlEntrada));
		ArrayList<String> lstNombres = new ArrayList<String>();
		if (z != null) {
			Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) z.entries();
			while (enu.hasMoreElements()) {
				ZipEntry ze = enu.nextElement();
				lstNombres.add(ze.getName());
			}
		}
		return lstNombres;
	}
	
	/**
	 * Convierte un XML en un Document
	 * @param stringXML
	 * @return
	 * @throws Exception
	 */
	public static Document getStringXmlAsDocument(String stringXML) throws Exception {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			String encoding = getEncoding(stringXML);
			InputStream is = new ByteArrayInputStream(stringXML.getBytes(encoding));
			document = docBuilder.parse(is);
		} catch (Exception e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new Exception("No se pudo convertir el stringXML a un Document valido");
		}
		return document;
	}

	/**
	 * Devuelve el valor de un nodo en un XML
	 * @param stringXML
	 * @param tag
	 * @return
	 */
	public static String leerValor (String stringXML, String tag) {
		String valor = null;
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			String encoding = getEncoding(stringXML);
			InputStream is = new ByteArrayInputStream(stringXML.getBytes(encoding));
			document = docBuilder.parse(is);
			
			Node nodo = document;
			valor = valorNodo(nodo, tag);
			
		} catch (Exception e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error(e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);			
		}
		return valor ;
	}
	
	@SuppressWarnings("unused")
	public static boolean compareStruct(String xml1, String xml2){
		boolean result = false;
		try{
			Object[] obj1 = getNodes(xml1);
			Object[] obj2 = getNodes(xml2);
			
			ArrayList<String> nodos1 = nodosNames(obj1);
			ArrayList<String> nodos2 = nodosNames(obj2);
			
			boolean r1 = nodos1.containsAll(nodos2);
			boolean r2 = nodos2.containsAll(nodos1);
			
			return nodos1.containsAll(nodos2) && nodos2.containsAll(nodos1);
			
		}catch(RemoteException re){
			es.tributasenasturias.servicios.documentos.log.Logger.error(re.getMessage(),re,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			result = false;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static ArrayList<String> nodosNames(Object[] obj){
		ArrayList<String> nodos = new ArrayList<String>();
		for(int i=0;i<obj.length;i++){
			HashMap<String,String> map = (HashMap<String,String>)obj[i];
			Iterator it = map.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				nodos.add((String)entry.getKey());
			}
		}
		return nodos;
	}
	
	/**
	 * Recupera un documento XML a partir de una cadena que contiene un XML válido.
	 * @param xml Cadena con xml válido.
	 * @throws RemoteException 
	 */
	public static Document getXMLDoc(String xml) throws XMLDOMDocumentException {
		javax.xml.parsers.DocumentBuilderFactory fact;
		javax.xml.parsers.DocumentBuilder db;
		Document doc=null;
		try
		{
			fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			db= fact.newDocumentBuilder();
			org.xml.sax.InputSource inStr = new org.xml.sax.InputSource ();
			inStr.setCharacterStream(new java.io.StringReader(xml));
			doc = db.parse(inStr);
			return doc;
		}
		catch (javax.xml.parsers.ParserConfigurationException ex)
		{
			throw new XMLDOMDocumentException("Error al interpretar el código xml:" + ex.getMessage(),ex);
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
	 * Devuelve la representación XML de objeto doc, formateada para legibilidad.
	 * @param doc Documento que se devolverá  como texto
	 * @return Cadena que representa el texto de XML.
	 * @throws XMLDOMDocumentException
	 */
	public static String getPrettyXMLText(Document doc) throws XMLDOMDocumentException
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
	
			return result.getWriter().toString();
		}
		catch (javax.xml.transform.TransformerConfigurationException ex)
		{
			throw new XMLDOMDocumentException ("Error al recuperar texto de xml:" + ex.getMessage(),ex);
		}
		catch (TransformerException ex)
		{
			throw new XMLDOMDocumentException ("Error al recuperar texto de xml:"+ ex.getMessage(),ex);
		}
	}
	/**
	 * Recupera el texto de un nodo. Si no se encuentra texto, devolverá cadena vacía.
	 * @param nodo
	 * @return El valor del nodo, o null si no tiene valor.
	 * Histórico: 06/09/2022. Se sustituye en el retorno el null por cadena vacía.
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
		return valor==null?"":valor;
	}
	/**
	 * Recupera la lista de nodos que cumplen una condición XPATH.
	 * @param doc Documento en el que buscar.
	 * @param condicion Condición a tener en cuenta.
	 * @return Lista de nodos DOM que cumplan esa condicion coincida.
	 */
	public static NodeList selectAllNodes (Node doc, String condicion)
	{
		NodeList list=null;
		try
		{
			//Compilamos una expresión XPath que devuelva todos los nodos con la condición
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression exp = xpath.compile(condicion);
			list = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			list=null;
		}
		return list;
	}
	/**
	 * Convierte las secuencias &lt; y &gt; a "<" y ">"
	 * @param xml Cadena de texto conteniendo un xml que puede tener &lt; y &gt;
	 * @return
	 */
	public static String unescapeXmlString(String xml){
		return xml.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
	}
}
