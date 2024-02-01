package es.tributasenasturias.servicios.documentos.impresion;


import org.w3c.dom.Document;
import org.w3c.dom.Node;



/**
 * Parser para los xml de reimpresión de Gestor Documental
 * @author crubencvs
 *
 */
public class PDFParserHandlerGD {
	
	String dataCadena = "";
	String elemActual = "";
	DocumentoGD dc;
	Document doc;

	/**
	 * Constructor
	 * @param datos DocumentoGD
	 * @param documento xml de reimpresión a tratar.
	 */
	protected PDFParserHandlerGD(DocumentoGD datos, 
			Document documento) throws Exception{
		dc = datos;
		doc = documento;
		if (dc == null)
			throw new Exception ("Documento vacío pasado a " + PDFParserHandlerGD.class.getName());
	}

	/**
	 * Escribe a PDF el Documento informado.
	 */
	public void parse() {
		if (doc == null)
			return;
		Node nodeActual = doc;
		boolean devuelta = false;
		while (nodeActual != null) {
			String nombre = nodeActual.getNodeName();
			boolean noesTextoComment = nodeActual.getNodeType() != Node.TEXT_NODE
					&& nodeActual.getNodeType() != Node.COMMENT_NODE;
			// para cada nodo con informacion, llama los procedimientos de
			// inicio(pdf_open_page) y modificacion, pasa a
			// sus hijos y llama los procedimientos de cerrado(pdf_close_page)
			if (!devuelta) {
				if (noesTextoComment) {
					if (dc != null)
						startElementDSI(nodeActual, nombre);
				}
			}
			if (nodeActual.hasChildNodes() && !devuelta) {
				nodeActual = nodeActual.getFirstChild();
			} else if (nodeActual.getNextSibling() != null) {
				if (noesTextoComment) {
					if (dc != null)
						endElementDSI(nodeActual, nombre);
				}
				devuelta = false;
				nodeActual = nodeActual.getNextSibling();
			} else {
				if (noesTextoComment) {
					if (dc != null)
						endElementDSI(nodeActual, nombre);
				}
				nodeActual = nodeActual.getParentNode();
				devuelta = true;
			}
			if (nodeActual.equals(doc))
				break;
		}
	}

	/**
	 * Realiza las tareas relacionadas con la inicializacion del nodo
	 * 
	 * @param node
	 * @param nombre
	 */
	public void startElementDSI(Node node, String nombre) {

		if (nombre.equalsIgnoreCase("Documento")) {
			dc.pdf_gd_open_file(node);
			dc.pdf_gd_get_version_documento(node);
			dc.pdf_gd_cargar_imagenes(node);
		} else if (nombre.equalsIgnoreCase("Pagina")) {
			dc.pdf_gd_iniciar_variables();
			dc.pdf_gd_begin_page(node);
		} else if (nombre.equalsIgnoreCase("Bloque")) {
			dc.pdf_gd_dibuja_bloque(node);
		} else if (nombre.equalsIgnoreCase("Imagen")) {
			if ("BLOQUE".equalsIgnoreCase(node.getParentNode().getLocalName()))
			{
				dc.pdf_gd_place_image_file(node);
			}
		} else if (nombre.equalsIgnoreCase("Cabecera")) {
			dc.pdf_gd_open_cabecera(node);
		} else if (nombre.equalsIgnoreCase("Pie")) {
			dc.pdf_gd_open_pie(node);
		} else if (nombre.equalsIgnoreCase("Cuadro")) {
			if ("BLOQUE".equalsIgnoreCase(node.getParentNode().getLocalName()))
			{
				dc.pdf_gd_rect(node);
			}
		} else if (nombre.equalsIgnoreCase("Linea")) {
			if ("BLOQUE".equalsIgnoreCase(node.getParentNode().getLocalName()))
			{
				dc.pdf_gd_linea(node);
			}
		}  else if (nombre.equalsIgnoreCase("Campo")) {
			//Sólo se tratan aquí los de los bloques,
			//los de cabecera o pie se tratan aparte.
			if ("BLOQUE".equalsIgnoreCase(node.getParentNode().getLocalName()))
			{
				dc.pdf_gd_show_texto(node);
			}
		}  else if (nombre.equalsIgnoreCase("Salto")) {
			dc.pdf_gd_end_page();
			dc.pdf_gd_reset_despl();
			dc.pdf_gd_begin_page(node);
		} else if (nombre.equalsIgnoreCase ("Barras"))
		{
			if ("BLOQUE".equalsIgnoreCase(node.getParentNode().getLocalName()))
			{
				dc.pdf_gd_barras(node);
			}
		}
		
	}

	/**
	 * Realiza las tareas relacionadas con la finalizacion del nodo
	 * 
	 * @param node
	 * @param nombre
	 */
	public void endElementDSI(Node node, String nombre) {

		if (nombre.equalsIgnoreCase("Documento")) {
			dc.pdf_gd_close();
		} else if (nombre.equalsIgnoreCase("Pagina")) {
			dc.pdf_gd_end_page();
			dc.pdf_gd_save_numpag();
		} else if (nombre.equalsIgnoreCase("Cabecera")) {
			dc.pdf_gd_close_cabecera();
		}else if (nombre.equalsIgnoreCase("Pie")) {
			dc.pdf_gd_close_pie();
		}
	}



}
