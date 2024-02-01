package es.tributasenasturias.utils.log;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

/**
 * Clase con utilidades para manejo de XML.
 * @author crubencvs
 *
 */
public class XMLUtil {

	/**
	 * Recupera un nodo XML como texto.
	 * @param nodo Nodo XML como {@link Node}
	 * @return Texto equivalente del nodo.
	 * @throws Exception
	 */
	protected String getXMLText(Node nodo) throws Exception
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no"); // No indentar salida, dejar tal como está.
	
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(nodo);
			transformer.transform(source, result);
	
			return result.getWriter().toString();
		}
		catch (TransformerConfigurationException ex)
		{
			throw new Exception (ex.getMessage(),ex);
		}
		catch (TransformerException ex)
		{
			throw new Exception (ex.getMessage(),ex);
		}
	}
	/**
	 * Recupera un objeto Apache XMLBeans xmlObject como texto
	 * Se utiliza para el mensaje de entrada, que es un XML Completo. En el resto de casos
	 * se utiliza la otra función, porque son fragmentos de XML y no se mostrarían adecuadamente.
	 * @param objeto Objeto a convertir a texto
	 * @return Representación en texto del objeto
	 * @throws Exception
	 */
	protected String getXMLText (XmlObject objeto) throws Exception
	{
		ByteArrayOutputStream baos = null;
		try
		{
			baos = new ByteArrayOutputStream();
			objeto.save(baos);
			return new String(baos.toByteArray());
		}
		finally
		{
			if (baos!=null)
			{
				try { baos.close();} catch (Exception e){}
			}
		}
	}
}
