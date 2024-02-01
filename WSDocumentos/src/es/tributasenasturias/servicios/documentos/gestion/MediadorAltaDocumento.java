package es.tributasenasturias.servicios.documentos.gestion;

import org.w3c.dom.Node;

import es.tributasenasturias.servicios.documentos.exceptions.DocumentoException;
import es.tributasenasturias.servicios.documentos.impresion.utils.XMLUtils;
import es.tributasenasturias.servicios.documentos.operaciones.ImpresionGD;

/**
 * Gestiona el alta de un documento generado mediante impresión de gestor documental, en la base de datos. 
 * @author crubencvs
 *
 */
public class MediadorAltaDocumento {
	/**
	 * Invoca al alta de documento sobre el documento guardado en la estructura ImpresionGD.InfoDocImpresion.
	 * @param infoDoc Estructura que contiene el documento y los datos de alta del mismo.
	 * @throws DocumentoException En caso de error, o de que la estructura de datos de alta no exista.
	 */
	public void altaDocumento (ImpresionGD.InfoDocImpresion infoDoc) throws DocumentoException
	{
		Node datosAlta= infoDoc.getXmlAltaDocumento();
		if (datosAlta==null)
		{
			throw new DocumentoException ("Se ha intentado dar de alta el documento, pero no se tienen datos acerca del alta. Revise el origen de datos del documento.");
		}
		String nombre = XMLUtils.getNodeText(XMLUtils.selectSingleNode(datosAlta, "NOMBRE"));
		String tipo = XMLUtils.getNodeText(XMLUtils.selectSingleNode(datosAlta, "TIPO"));
		String codVerif = XMLUtils.getNodeText(XMLUtils.selectSingleNode(datosAlta, "COD_VERIF"));
		String nifSP = XMLUtils.getNodeText(XMLUtils.selectSingleNode(datosAlta, "SP"));
		String nifPR= XMLUtils.getNodeText(XMLUtils.selectSingleNode(datosAlta, "PRESENTADOR"));
		if (nifPR==null)
		{
			nifPR=nifSP;
		}
		String tipoDocumento="PDF";
		String idSesion="";
		boolean comprimirDocumento=true;
		GestorDocumentos.altaDocumento(nombre, tipo, codVerif, nifSP, nifPR, tipoDocumento, idSesion,comprimirDocumento, infoDoc.getDocb64());
	}
}
