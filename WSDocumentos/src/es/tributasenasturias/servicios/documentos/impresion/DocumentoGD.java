package es.tributasenasturias.servicios.documentos.impresion;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import es.tributasenasturias.servicios.documentos.exceptions.ImpresionGDException;
import es.tributasenasturias.servicios.documentos.impresion.utils.NumberUtil;
import es.tributasenasturias.servicios.documentos.impresion.utils.XMLUtils;
import es.tributasenasturias.servicios.documentos.log.Logger;
import es.tributasenasturias.servicios.documentos.preferencias.Preferencias;



/**
 * Implementación de las funciones relacionadas con PDF del gestor documental.
 * @author crubencvs
 *
 */
public class DocumentoGD {

	/** Campos
	 * 
	 */
	private Preferencias pr;
	private com.lowagie.text.Document oPDF;
	private PdfContentByte pcb;
	private Document fuente = null;
	private PdfWriter pdfwriter = null;
	private Font ff = null;
	//private String pathSalidaBase = null;
	private ByteArrayOutputStream pdfoutput = null;
	//Objetos de impresión
	private Object cabeceraPlantilla;
	private boolean cabeceraFirstPage;
	private double cabeceraAlto;
	private Object piePlantilla;
	private boolean pieFirstPage;
	private int pieAlto;
	private int desplazamiento;
	private int anchoPdf;
	private int altoPdf;
	private String numeracion_global;
	private int pagRelativa;
	private List<Integer> pagBloques;
	private int pagina; //Número d e página
	private Map<String, Image> imagenes;
	private List<Node> listAutonumericos; // Lista de campos autonuméricos, para procesar después de pintar el documento.
	private int margenSup;
	private int margenInf;
	//Para impresión de cabeceras
	private Node cabecera=null; //se guarda la cabecera para poder imprimirla en cada página que se genere.
	//Para impresión de pie
	private Node pie=null;
	//Para imprimir el código de verificación.
	private String codigoVerificacion;
	//Para comprobar la versión de documento, de cara a conocer la diferencia entre versiones, 
	//como si hay cabeceras estandar o no.
	private String versionDocumento;
	private boolean borrador=false;
	//Código de barras
	int[] codigos;
	String[] patrones = {"|| ||  ||  ", "||  || ||  ", "||  ||  || ", "|  |  ||   ", "|  |   ||  ", "|   |  ||  ", "|  ||  |   ", "|  ||   |  ", "|   ||  |  ", "||  |  |   ", "||  |   |  ", "||   |  |  ", "| ||  |||  ", "|  || |||  ", "|  ||  ||| ", "| |||  ||  ", "|  ||| ||  ", "|  |||  || ", "||  |||  | ", "||  | |||  ", "||  |  ||| ", "|| |||  |  ", "||  ||| |  ", "||| || ||| ", "||| |  ||  ", "|||  | ||  ", "|||  |  || ", "||| ||  |  ", "|||  || |  ", "|||  ||  | ", "|| || ||   ", "|| ||   || ", "||   || || ", "| |   ||   ", "|   | ||   ", "|   |   || ", "| ||   |   ", "|   || |   ", "|   ||   | ", "|| |   |   ", "||   | |   ", "||   |   | ", "| || |||   ", "| ||   ||| ", "|   || ||| ", "| ||| ||   ", "| |||   || ", "|   ||| || ", "||| ||| || ", "|| |   ||| ", "||   | ||| ", "|| ||| |   ", "|| |||   | ", "|| ||| ||| ", "||| | ||   ", "||| |   || ", "|||   | || ", "||| || |   ", "||| ||   | ", "|||   || | ", "||| |||| | ", "||  |    | ", "||||   | | ", "| |  ||    ", "| |    ||  ", "|  | ||    ", "|  |    || ", "|    | ||  ", "|    |  || ", "| ||  |    ", "| ||    |  ", "|  || |    ", "|  ||    | ", "|    || |  ", "|    ||  | ", "||    |  | ", "||  | |    ", "|||| ||| | ", "||    | |  ", "|   |||| | ", "| |  ||||  ", "|  | ||||  ", "|  |  |||| ", "| ||||  |  ", "|  |||| |  ", "|  ||||  | ", "|||| |  |  ", "||||  | |  ", "||||  |  | ", "|| || |||| ", "|| |||| || ", "|||| || || ", "| | ||||   ", "| |   |||| ", "|   | |||| ", "| |||| |   ", "| ||||   | ", "|||| | |   ", "|||| |   | ", "| ||| |||| ", "| |||| ||| ", "||| | |||| ", "|||| | ||| ", "|| |    |  ", "|| |  |    ", "|| |  |||  ", "||   ||| | ||"};
	/** 
	 * Permite indicar el código de verificación a imprimir en cada página. Si no se indica, o se 
6	 * indica vacío, no se imprimirá.
	 * @param codigoVerificacion Cadena con el código de verificación.
	 */
	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}
	/**
	 * Constructor privado.
	 */
	private DocumentoGD() {
		pr = new Preferencias();
		anchoPdf =0;
		altoPdf =0;
		numeracion_global=null;
		pagina = 0;
		pagBloques = new ArrayList<Integer>();
		imagenes = new HashMap<String,Image>();
		listAutonumericos= new ArrayList<Node>();
		margenSup = 0;
		margenInf= 0;
	}
	/**
	 * Constructor público
	 * @param xml XML con los datos de reimpresión.
	 * @param output {@link ByteArrayOutputStreamOutputStream} donde escribir el PDF resultante.
	 */
	public DocumentoGD(Document xml, Object output)
	{
		this();
		if (output instanceof OutputStream) {			
			pdfoutput = (ByteArrayOutputStream) output;
		} else {
			if (output != null)
			{
				es.tributasenasturias.servicios.documentos.log.Logger.debug("El objeto pasado a DocumentoGD no es un outputStream. ",es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			}
		}
		oPDF = pcbLoad();
		fuente = xml;
	}
	/**
	 * Genera el xml.
	 * @return
	 */
	public byte[] Generar() throws ImpresionGDException{

		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("autor", "autor");
			params.put("titulo", this.getClass().getName());
			PDFParserHandlerGD myparser = new PDFParserHandlerGD(this, fuente);
			myparser.parse();
			return pdfoutput.toByteArray();
		} catch (Exception eee) {
			Logger.trace(eee.getStackTrace(), es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			throw new ImpresionGDException("Excepción en la generación de documento:" + eee.getMessage(),eee);
		}
	}
	/**************************************************************
	 * Métodos de utilidad
	 *************************************************************/
	/**
	 * Devuelve el número presente en una cadena tipo char como un número. Nota: sólo para números en base decimal. Si la
	 * cadena contiene espacios los elimina.
	 * 
	 * @param input
	 * @return
	 */
	public static double Val(String input) {
		double valor = 0;
		try {
			//19/02/2021. La función "Val" utiliza funciones de NumberUtil que no se
			//pensaron para usar en estas circunstancias.
			//La sustituyo por el equivalente, que es convertir lo que viene, en céntimos
			//en un número entero
			//Double value = NumberUtil.getDoubleFromString(input);
			if (StringUtils.isNotBlank(input.trim())){
				Double value= Double.parseDouble(input.trim());
				if (value != null)
					valor = value.doubleValue();
			}
			
		} catch (NumberFormatException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error("Error:"+e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
		
		return valor; 
	}
	/**
	 * Devuelve la cadena de entrada al reves.
	 * @param cadena
	 * @param longitud
	 * @return
	 */
	public static String Cadreverse(String cadena, int longitud) {
		String cad1 = "";
		for (int i = longitud - 1; i >= 0; i--)
			cad1 = cad1 + cadena.charAt(i);
		return cad1;
	}
	/**
	 * Formatea la parte entera de cad1 según indica cad2. EL formato de cad2 es: #-> representa un dígito cualquiera.
	 * El resto de los caracteres se introducen a "pelo" en la cad2 Ejemplos para cad2:
	 * 
	 * "#######" "###.###" -> El punto se meterá "a pelo" en la posición indicada por cad2 en la cad1 "####A#" -> La A
	 * se meterá "a pelo" en la posición indicada por cad2 en la cad1
	 * 
	 * @param cad1 Cadena a modificar
	 * @param cad2 Formato
	 * @return
	 */
	public static String Format(String cad1, String cad2) {
		if (cad1 == null)
			cad1 = "";
		if (cad2 == null)
			cad2 = "";
		String cad3 = "";
		String cad4 = "";
		String cad5 = "";
		String cad6 = "";
		int long1, long2;
		int pos = cad1.indexOf(",", 0);

		if (pos != -1) {
			cad3 = cad1.substring(pos, cad1.length());
			cad1 = cad1.substring(0, pos);
		}
		long1 = cad1.length();
		long2 = cad2.length();

		cad4 = Cadreverse(cad1, long1);
		cad5 = Cadreverse(cad2, long2);

		int i = 0;
		int j = 0;

		while ((i < long1) && (j < long2)) {
			if (cad5.charAt(j) == '#') {
				cad6 = cad6 + cad4.charAt(i);
				i++;
			} else
				cad6 = cad6 + cad5.charAt(j);
			j++;
		}

		if (i < long1 - 1)
			cad6 = cad6 + cad4.substring(i, long1 - 1);

		cad6 = Cadreverse(cad6, cad6.length());
		return (cad6 + cad3);
	}
	/**
	 * Convierte una cantidad a Euros truncando los decimales. Si el número de decimales exigido es menor que los de
	 * entrada se añaden ceros hasta completar el número de decimales. Acepta cadenas del tipo ".1", "1.", "1","1.23234"
	 *
	 * @param cadena
	 * @param factor Si factor==100, convierte a centimos; si factor==166.386, convierte a Euros
	 * @param decimales
	 * @return
	 */
	public static String ConvEuro(String cadena, double factor, int decimales) {
		if (cadena == null)
			cadena = "";
		if ("".equals(cadena.trim())) {
			return "";
		}
		String cad = "";
		String cad1, cad2; // cadenas auxiliares
		int i1, i2;
		
		cadena = (Val(cadena) / factor) + "";

		// Cambia el punto por una coma
		int pos = cadena.indexOf(".", 0);

		if (pos != -1) {
			cad1 = cadena.substring(0, pos);
			cad2 = cadena.substring(pos + 1, cadena.length());
			cadena = cad1 + "," + cad2;
		}

		cadena = Format(cadena, "###.###.###");

		// Ahora vamos con los decimales

		pos = cadena.indexOf(",", 0);

		if (decimales == 0) {
			if (pos > 0)
				return (cadena.substring(0, pos));
			else
				return (cadena);
		} else {
			if (pos == -1) {
				cad = ",";
				for (i1 = 0; i1 < decimales; i1++) {
					cad = cad + "0";
				}
				return (cadena + cad);
			}

			if ((pos + 1 + decimales) <= (cadena.length())) {
				return (cadena.substring(0, pos + decimales + 1));
			} else { // añade ceros para completar decimales
				i1 = (pos + 1 + decimales) - (cadena.length());
				cad = "";
				for (i2 = 0; i2 < i1; i2++)
					cad = cad + "0";
				return (cadena + cad);
			}
		}
	}
	/**
	 * Llama ConvEuro(cadena,100,decimales).
	 * @param cadena
	 * @param decimales
	 * @return
	 */
	public static String toEuro(String cadena, int decimales) {
		if (cadena==null || cadena.trim().length()==0){
			return "";
		}
		return ConvEuro(cadena.trim(), 100, decimales);
	}
	/**
	 * Llama ConvEuro(cadena,100,2).
	 * @param cadena
	 * @return
	 */
	public static String toEuro(String cadena) {
		if (cadena==null || cadena.trim().length()==0){
			return "";
		}
		return ConvEuro(cadena.trim(), 100, 2);
	}
	
	/**
	 * Convierte valores Unicode a caracteres. 
	 * @param codePoints Codepoints Unicode a convertir a caracter. Pueden ser varios, separados por comas -  XX,YY,ZZ ...
	 * @return
	 */
	public static String fromCharCode(int... codePoints) {
	    return new String(codePoints, 0, codePoints.length);
	}
	
	/**
	 * Procesa elementos comunes a todas las páginas, como la cabecera, el pie,
	 * y si se ha solicitado, el código de verificación de documento.
	 * @param principal {@link com.lowagie.text.Document} Documento principal, sobre el que se modificarán datos.
	 * @param writer {@link PdfWriter} que se usará para escribir el resultado
	 * @param principalOutput Representación binaria del documento en salida.
	 * @return
	 */
	private ByteArrayOutputStream procesarComunes(com.lowagie.text.Document principal,PdfWriter writer, ByteArrayOutputStream principalOutput)
	{	//Esto perderá cualquie información de metadatos del viejo documento.
		//Como esto son reimpresiones y no estamos utilizando, no importará,
		//pero si fuese necesario que se conservara, se tiene que utilizar el PdfStamper.
		try {
			com.lowagie.text.Document nuevo = new com.lowagie.text.Document();
			ByteArrayOutputStream outNuevo = new ByteArrayOutputStream();
			PdfWriter writerNuevo = PdfWriter.getInstance(nuevo, outNuevo);
			PdfContentByte cbNuevo;
			//Para la parte de comunes (Cabecera y pie)
			com.lowagie.text.Document comunes= new com.lowagie.text.Document();
			ByteArrayOutputStream out= new ByteArrayOutputStream();
			PdfWriter writerComunes = PdfWriter.getInstance(comunes, out);
			//Abrimos el documento de destino
			nuevo.setPageSize(principal.getPageSize());
			nuevo.open();
			cbNuevo=writerNuevo.getDirectContent();
			//Abrimos el documento de comunes.
			comunes.setPageSize(principal.getPageSize());
			comunes.open();
			//Recuperamos el contenido del documento principal.
			//Esto nos dirá, entre otras cosas, el número de páginas que tenemos.
			PdfReader readerPrincipal = new PdfReader(principalOutput.toByteArray());
			int totalPaginas= readerPrincipal.getNumberOfPages();
			NodeList nodos;
			Node nodo;
			//Generaremos el mismo número de páginas que el original.
			//De esta forma podremos poner la numeración en las propias páginas,
			//y añadirlas, así como generar la cabecera y pie en cada una de ellas.
	
			for (int pagina=1;pagina<=totalPaginas;pagina++)
			{
				if (pagina>1)
				{
					comunes.newPage();
				}
				writerComunes.setPageEmpty(false); // Necesario para que genere páginas, aunque en alguna de ellas no se incluya nada, porque tenemos que tener el mismo número que el original.
				//Recorremos los nodos de la cabecera, procesando los que tenga.
				//Todo son nodos simples, así que lo hacemos directamente.
				//Inicializamos el desplazamiento relativo, para que no pinte en donde estaba antes.
				desplazamiento = 0;
				if (cabecera!=null && (cabeceraFirstPage || (pagina > 1)))
				{
					nodos = cabecera.getChildNodes();
					for (int i=0; i< nodos.getLength(); i++)
					{
						nodo = nodos.item(i);
						if (nodo.getNodeName().equals("Imagen"))
						{
							pdf_gd_place_image_file(nodo, comunes, writerComunes);
						}
						else if (nodo.getNodeName().equals("Linea"))
						{
							pdf_gd_linea(nodo,comunes,writerComunes);
						}
						else if (nodo.getNodeName().equals("Cuadro"))
						{
							pdf_gd_rect(nodo, comunes, writerComunes);
						}
						else if (nodo.getNodeName().equals("Campo"))
						{
							pdf_gd_show_texto(nodo, comunes, writerComunes);
						}
						else if (nodo.getNodeName().equals("Barras"))
						{
							pdf_gd_barras(nodo, comunes, writerComunes);
						}
					}
				}
				//Pie
				desplazamiento = 0;
				if (pie!=null && (pieFirstPage || (pagina> 1)))
				{
					nodos = pie.getChildNodes();
					for (int i=0; i< nodos.getLength() ; i++)
					{
						nodo = nodos.item(i);
						if (nodo.getNodeName().equals("Imagen"))
						{
							pdf_gd_place_image_file(nodo, comunes, writerComunes);
						}
						else if (nodo.getNodeName().equals("Linea"))
						{
							pdf_gd_linea(nodo,comunes,writerComunes);
						}
						else if (nodo.getNodeName().equals("Cuadro"))
						{
							pdf_gd_rect(nodo, comunes, writerComunes);
						}
						else if (nodo.getNodeName().equals("Campo"))
						{
							pdf_gd_show_texto(nodo, comunes, writerComunes);
						}
						else if (nodo.getNodeName().equals("Barras"))
						{
							pdf_gd_barras(nodo, comunes, writerComunes);
						}
					}
				}
				
				//Numeración de la página.
				//Debería existir un solo autonumérico en el informe, pero quien sabe...
				//Se procesa cada uno por página.
				for (int numAuto=0;numAuto<listAutonumericos.size();numAuto++) //Procesamos todos los autonuméricos, que normalmente serán uno solo.
				{
					Node auto=listAutonumericos.get(numAuto);
					procesaAutonumerico (auto,writerComunes,totalPaginas,pagina);
				}
				//Procesamos el código de verificación
				//TODO: versión inicial, no tiene en cuenta las cabeceras comunes.
				//Debería ir en base a la versión de documento, por el momento no lo hago.
				if (this.codigoVerificacion!=null && !"".equals(codigoVerificacion))
				{
					procesaCodigoVerificacion (writerComunes, codigoVerificacion);
				}
			}
			
			if (comunes.isOpen())
			{
				comunes.close();
			}
			writerComunes.close();
			//Leemos 
			PdfReader readerComunes =  new PdfReader (out.toByteArray());
			for (int j=1; j<= totalPaginas; j++)
			{
				PdfImportedPage pg = writerNuevo.getImportedPage(readerPrincipal, j);
				PdfImportedPage pgComun = writerNuevo.getImportedPage(readerComunes, j);//La misma página en los elementos comunes.
				nuevo.newPage();
				cbNuevo.addTemplate(pgComun, 0, 0);
				cbNuevo.addTemplate(pg, 0, 0);
				
			}
			if (nuevo.isOpen())
			{
				nuevo.close();
			}
			writerNuevo.close();
			//Procesamos el fondo de "borrador" si fuese necesario.
			//¿Cómo saberlo? Tiene que subir una variable diferente al servicio web.
			if (this.borrador) {
				ByteArrayOutputStream outTemp= outNuevo;
				outNuevo= new ByteArrayOutputStream();
				PdfStamper stamper= new PdfStamper(new PdfReader(outTemp.toByteArray()), outNuevo);
				for (int j=1;j<=totalPaginas;j++){
					PdfContentByte borrador = stamper.getUnderContent(j);
					borrador.saveState();
					borrador.setColorFill(Color.GRAY);
					PdfGState pgs= new PdfGState();
					pgs.setFillOpacity(0.70f);
					pgs.setStrokeOpacity(0.70f);
					borrador.setGState(pgs);
					Phrase texto = new Phrase("Borrador", new Font(BaseFont.createFont("Helvetica-Bold", BaseFont.WINANSI, BaseFont.NOT_EMBEDDED),150));
					ColumnText.showTextAligned(borrador, Element.ALIGN_CENTER, texto, this.anchoPdf/2, this.altoPdf/2, 45);
					borrador.restoreState();
				}
				stamper.close();
			}
			return outNuevo;
		} catch (IOException e) {
			Logger.error("Error al procesar la cabecera:"+ e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			Logger.trace(e.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		} catch (DocumentException e) {
			Logger.error("Error al procesar la cabecera:"+ e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			Logger.trace(e.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
		return null;
	}
	/**************************************************************
	 * Métodos para generar un PDF
	 *************************************************************/
	
	/**
	 * Inserta una nueva pagina en el documento con la orientacion y tipo de papel informados. Las 
	 * coordenadas empiezan en la esquina inferior izquierda
	 * @param node Nodo con atributos "orientacion"(v,h) y "papel"; por defecto, "v","A4"
	 * @return
	 */
	public String pdf_gd_begin_page(Node node) {

		String giro = "v";
		Node n1 = node;
		if (n1.getAttributes().getNamedItem("orientacion") != null)
			giro = n1.getAttributes().getNamedItem("orientacion").getNodeValue();

		if (n1.getAttributes().getNamedItem("papel") != null) {
			String papel = n1.getAttributes().getNamedItem("papel").getTextContent();
			papel = papel.toUpperCase();
			if (papel.equals("A0")) {
				this.anchoPdf=2380;
				this.altoPdf=3368;
			} else if (papel.equals("A1")) {
				this.anchoPdf=1684;
				this.altoPdf=2380;

			} else if (papel.equals("A2")) {
				this.anchoPdf=1190;
				this.altoPdf=1684;
			} else if (papel.equals("A3")) {
				this.anchoPdf=842;
				this.altoPdf=1190;
			} else if (papel.equals("A4")) {
				this.anchoPdf=595;
				this.altoPdf=842;
			} else if (papel.equals("A5")) {
				this.anchoPdf=421;
				this.altoPdf=595;
			} else if (papel.equals("A6")) {
				this.anchoPdf=297;
				this.altoPdf=421;
			} else if (papel.equals("B5")) {
				this.anchoPdf=501;
				this.altoPdf=709;
			} else if (papel.equals("LETTER")) {
				this.anchoPdf=612;
				this.altoPdf=792;
			} else if (papel.equals("LEGAL")) {
				this.anchoPdf=612;
				this.altoPdf=1008;
			} else if (papel.equals("LEDGER")) {
				this.anchoPdf=1224;
				this.altoPdf=792;
			} else if (papel.equals("11X17")) {
				this.anchoPdf=792;
				this.altoPdf=1224;
			} else {
				this.anchoPdf=595;
				this.altoPdf=842;
			}
		}
		else
		{
			this.anchoPdf=595;
			this.altoPdf=842;
		}
		if (!giro.equalsIgnoreCase("v"))
		{
			int aux= this.anchoPdf;
			this.anchoPdf = this.altoPdf;
			this.altoPdf = aux;
		}
		if (oPDF!=null)
		{
			oPDF.setPageSize(new Rectangle(this.anchoPdf, this.altoPdf));
		}
		//siempre se inicia una pagina antes de insertar contenido
		//open() ya crea la primera pagina
		boolean docNuevo = oPDF != null && !oPDF.isOpen();
		if (docNuevo) {
			oPDF.open();
			oPDF.setPageSize(new Rectangle(this.anchoPdf, this.altoPdf));
			pcb = pdfwriter.getDirectContent();
			pcb.setLineWidth(1);
		}
		if (!docNuevo)
		{
			oPDF.newPage();
		}
		return "ok";
	}
	
	/**
	 * Crea e inicializa el Documento y PDFWriter asociado al OutputStream informado.
	 * @return Documento
	 */
	public com.lowagie.text.Document pcbLoad() {
		com.lowagie.text.Document docu = new com.lowagie.text.Document();
		try {
			if (pdfoutput == null) {
				throw new IllegalArgumentException("Error. No se ha indicado flujo de salida para  el PDF.");
			} else
				pdfwriter = PdfWriter.getInstance(docu, pdfoutput);
			return docu;
		} catch (Throwable t) {
			Logger.trace(t.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			return null;
		}
	}
	/**
	 * Abre el Document donde escribir el PDF.
	 * @param x Nodo documento.
	 * @return
	 */
	public String pdf_gd_open_file(Node x) {
		if (oPDF == null)
			oPDF = pcbLoad();
		return "ok";
	}
	/**
	 * Recupera la versión de documento, si existe. Se utilizará 
	 * para saber si se incluye la cabecera y pie estandar, aunque 
	 * no está restringido y puede tener más interpretaciones en el futuro.
	 * @param x
	 */
	public void pdf_gd_get_version_documento (Node x)
	{
		//Recuperamos la versión de documento, si existe.
		//Será un atributo de nombre "ver".
		if (x!=null)
		{
			NamedNodeMap atributos =  x.getAttributes();
			if (atributos!=null && atributos.getLength()>0)
			{
				Node ver = atributos.getNamedItem("ver");
				if (ver!=null)
				{
					this.versionDocumento = ver.getNodeValue();
				}
			}
		}
	}
	/**
	 * Guarda el número de página. Se llama al final del proceso de cada página.
	 */
	public void pdf_gd_save_numpag()
	{
		this.pagBloques.add(this.pagRelativa - 1);
		return;
	}
	/**
	 * Cierra el documento pdf y termina el proceso.
	 * @return
	 */
	public String pdf_gd_close() {
		if (oPDF.isOpen())
			oPDF.close();
		pdfwriter.close();
		//Marcas para modificar la funcionalidad.
		//Procesamos la cabecera y pie, si existen.
		if (cabecera!=null || pie!=null || (codigoVerificacion!=null && !"".equals(codigoVerificacion)))
		{
			ByteArrayOutputStream datos=procesarComunes(oPDF, pdfwriter, pdfoutput);
		
			if (datos!=null)
			{
				pdfoutput= datos;
			}
		}
		return "ok";
	}
	
	public String getNombreFichero(String ficheroConPath)
	{
		int i = ficheroConPath.lastIndexOf("/");
		String fichero = ficheroConPath.substring(i + 1, ficheroConPath.length()).toLowerCase();
		return pr.getDirImagenes() + "/" + fichero;
	}
	/**
	 * Abre las imágenes y las incluye en el array de imágenes de página. 
	 * Si se produce un fallo continúa, simplemente no cargará las imágenes que no pueda.
	 * @param n1 Nodo imagen
	 * 
	 */
	public void pdf_gd_open_image_file(Node n1)
	{
		String fichero=getNombreFichero(n1.getTextContent()); //Si le pasamos el de imagen, sólo debería obtener el nombre.

		if (fichero.equals(""))
		{
			fichero = pr.getDirImagenes()+ "/" + "BLANCO.TIF";
		}
		Image lImage;
		try {
			lImage = Image.getInstance(fichero);
			imagenes.put(n1.getTextContent(),lImage);
		} catch (BadElementException e) {
			Logger.error("ERROR. Error al recuperar imágenes. Imagen:" + n1.getTextContent(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			Logger.trace(e.getStackTrace(), es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		} catch (MalformedURLException e) {
			Logger.error("ERROR. Error al recuperar imágenes. Imagen:" + n1.getTextContent(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			Logger.trace(e.getStackTrace(), es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		} catch (IOException e) {
			Logger.error("ERROR. Error al recuperar imágenes. Imagen:" + n1.getTextContent(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			Logger.trace(e.getStackTrace(), es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
	}
	/**
	 * Carga las imágenes del documento en un mapa de imágenes.
	 * @param node Nodo en el que se buscarán las imágenes.
	 */
	public void pdf_gd_cargar_imagenes(Node node)
	{
		Node n=node;
		NodeList campos =  XMLUtils.selectAllNodes(n, ".//Imagen");
		for (int j = 0; j < campos.getLength(); j++)
		{
			Node c = campos.item(j);
			if (!imagenes.containsKey(c.getTextContent()))
			{
				pdf_gd_open_image_file(c);
			}
		}
	}
	
	private boolean isNaN(char numero)
	{
		return !Character.isDigit(numero);
	}
	/*** CALCULA LAS POSICIONES DEL CODIGO DE BARRAS ***/
	private int  SubMkCod(String txt)
	{
		int codPos = 0, txtPos = 0;
		char tipo = ' ';

		codigos = new int[300];
			
		while(txtPos < txt.length() - 1)
		{
			//Si son dígitos
			if(!isNaN(txt.charAt(txtPos)) && !isNaN(txt.charAt(txtPos+1)))
			{
				if(tipo != 'C')
				{
					codigos[codPos] = 99;
					codigos[codPos+1] = (Character.codePointAt(txt,txtPos) - 48) * 10 + (Character.codePointAt(txt,txtPos + 1) - 48);
					txtPos += 2;
					codPos += 2;
					tipo = 'C';
				}
				else
				{
					codigos[codPos] = (Character.codePointAt(txt,txtPos) - 48) * 10 + (Character.codePointAt(txt,txtPos+1) - 48);
					txtPos += 2;
					codPos += 1;
					tipo = 'C';
				}
			}
			else
			{
				if (tipo != 'B')
				{
					codigos[codPos] = 100;
					codigos[codPos+1] = Character.codePointAt(txt,txtPos) - 32;
					if(txt.charAt(txtPos)=='ñ')
						codigos[codPos+1] = 62;
					else
						if(txt.charAt(txtPos)=='Ñ')
							codigos[codPos+1] = 3;
					txtPos += 1;
					codPos += 2;
					tipo = 'B';
				}
				else
				{
					codigos[codPos] = Character.codePointAt(txt,txtPos) - 32;
					if(txt.charAt(txtPos)=='ñ')
						codigos[codPos] = 62;
					else
						if(txt.charAt(txtPos)=='Ñ')
							codigos[codPos] = 3;
					txtPos += 1;
					codPos += 1;
					tipo = 'B';
				}
			}			
		}
		if(txtPos==(txt.length() -1))
		{
			if (tipo != 'B')
				{
					codigos[codPos] = 100;
					codigos[codPos+1] = Character.codePointAt(txt,txtPos) - 32;
					if(txt.charAt(txtPos)=='ñ')
						codigos[codPos+1] = 62;
					else
						if(txt.charAt(txtPos)=='Ñ')
							codigos[codPos+1] = 3;
					codPos += 2;
				}
				else
				{
					codigos[codPos] = Character.codePointAt(txt,txtPos) - 32;
					if(txt.charAt(txtPos)=='ñ')
						codigos[codPos] = 62;
					else
						if(txt.charAt(txtPos)=='Ñ')
							codigos[codPos] = 3;
					codPos += 1;
				}
		}
		codigos[codPos] = -1;
		return codPos;
	}

	/*** CALCULA EL CÓDIGO DE BARRAS - TIPO B Y C ***/
	public String MkCodigosBarras(String txtCodeC)
	{	
		String codigo = "";
		int i, lon = SubMkCod(txtCodeC);
		for(i = ++lon; i > 1; i--)
			codigos[i] = codigos[i-1];
		codigos[1] = 102;
		codigos[0] = (codigos[0]==99?105:104);
		
		int check = codigos[0] + codigos[1];
		for(int j = 2; j < lon; j++)
			check += codigos[j]*j;
		codigos[lon++] = check % 103;
		codigos[lon] = -1;
		
		for(int ch = 0; codigos[ch]!=-1; ch++)
			codigo += patrones[codigos[ch]];
		codigos = null;		
		codigo += patrones[106];
		
		return codigo;
	}
	public void pdf_gd_barras(Node nodo, com.lowagie.text.Document doc, PdfWriter writer)
	{
	    Node n1 = nodo;
	    String dato = n1.getTextContent();
	    Node padre=n1.getParentNode();

	    if (dato.length() == 41 || dato.length() == 61) {
	    	dato += "0";
	    }

	    int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
	    int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
	    int h = 30;
	    if(n1.getAttributes().getNamedItem("y1")!=null)
	    {
	    	h = Integer.parseInt(n1.getAttributes().getNamedItem("y1").getNodeValue());
	    }
	    float w = 0.72F;
	    // No sumo la h porque quiero que situarme en la posición superior del codigo de barras
	    int ypadre = y + (int) Math.max(margenSup, this.cabeceraAlto) + desplazamiento;
	    int xpadre = 0;
	    
	    while ("Bloque".equalsIgnoreCase(padre.getLocalName()))
	    {
	    	xpadre += Integer.parseInt(padre.getAttributes().getNamedItem("x").getNodeValue());
	    	ypadre += Integer.parseInt(padre.getAttributes().getNamedItem("y").getNodeValue());
	    	padre = padre.getParentNode();
	    }
	    padre = n1.getParentNode();
	    
	    //Los de la cabecera no respetan los margenes ni el alto de la cabecera
		if("Cabecera".equalsIgnoreCase(padre.getLocalName())){
			ypadre -= Math.max(margenSup, this.cabeceraAlto);
		}
	    
	   	float yrelativa = this.altoPdf - (ypadre % this.altoPdf);
			
		boolean salto = false;
		int pagina_relativa = (int)Math.floor(ypadre/this.altoPdf);
		
		if(ypadre%this.altoPdf!=0)
		{
			pagina_relativa++;
		}

		if(pagina_relativa > this.pagRelativa)
		{
			salto = true;
		}

		if("Bloque".equalsIgnoreCase(padre.getLocalName()) && ( yrelativa <= Math.max(margenInf, this.pieAlto) || salto ))
		{
			//oPDF.suspend_page("");
			//oPDF.begin_page_ext(this.ancho_pdf, this.alto_pdf, "");
			//Nueva página
			doc.newPage();
			//TODO: Cabeceras y pies de plantilla.
			if(this.cabeceraPlantilla!=null)
			{
				//oPDF.fit_image(this.cabecera_plantilla, 0.0, 0.0, "");
			}
			if(this.piePlantilla!=null)
			{
				//oPDF.fit_image(this.pie_plantilla, 0.0, 0.0, "");
			}
			//TODO:Por ahora no se soporta el borrador 
			/*if (this.marcaBorrador!=null)
			{
				//oPDF.fit_image(this.marca_borrador, 0.0, 0.0, "");
			}*/
			this.pagina = this.pagina + 1;
			this.pagRelativa = this.pagRelativa + 1;

			// No sumo la h porque quiero la posición superior
			int yrelativa_new = this.altoPdf - (int)Math.max(margenSup, this.cabeceraAlto);
			if(salto)
			{
				desplazamiento = desplazamiento - (int)(yrelativa_new - yrelativa) ;
			}
			else
			{
				desplazamiento = desplazamiento + (int)yrelativa + (int)Math.max(margenSup, this.cabeceraAlto);
			}
			yrelativa = yrelativa_new;
		}
		
	    float ycodbarras = yrelativa;
	    float xcodbarras = xpadre + x;
	    String codigo = MkCodigosBarras(dato);
	    //Para pintar líneas
	    PdfContentByte canvas = writer.getDirectContent();
		canvas.saveState();
		canvas.setLineWidth(w);
	    //oPDF.setlinewidth(w);
	    for (int i = 0; i < codigo.length(); i++)
	    {
	        char c = codigo.charAt(i);
	        if (c == '|')
	        {		
	        	//oPDF.moveto(xcodbarras, ycodbarras)
	        	// Pinto hacia abajo
		    	//oPDF.lineto(xcodbarras, ycodbarras - h);
	    		//oPDF.stroke();
	        	canvas.moveTo(xcodbarras, ycodbarras);
	        	canvas.lineTo(xcodbarras, ycodbarras-h);
	        	canvas.stroke();
	        }
	        xcodbarras += w;
	    }
	    //oPDF.setlinewidth(1);
	    canvas.restoreState();
	    return;
	}
	
	public void pdf_gd_barras (Node nodo)
	{
		pdf_gd_barras (nodo, oPDF, pdfwriter);
	}
	/**
	 * Abrir la cabecera.
	 * @param node
	 */
	public void pdf_gd_open_cabecera(Node node)
	{
		//this.cabeceraPlantilla = oPDF.begin_template(this.ancho_pdf, this.alto_pdf);
		//TODO: Crear una plantilla para repetir lo mismo que pinta al aplicar los hijos de 
		//cabecera en todas las páginas. Debería hacerse una función que pinte lo mismo 
		//donde ahora inserta la cabecera.
		//Atributos de la cabecera
		Node n = node;
		if (cabecera==null)
		{
			this.cabecera = n;//Se guarda la cabecera para  mejor ocasión.
			this.cabeceraFirstPage = Boolean.parseBoolean(n.getAttributes().getNamedItem("primera_pagina").getNodeValue());
			this.cabeceraAlto = Integer.parseInt(n.getAttributes().getNamedItem("h").getNodeValue());
		}
		return;
	}
	
	public void pdf_gd_close_cabecera()
	{
		if(this.cabeceraFirstPage)
		{
			//oPDF.fit_image(this.cabecera_plantilla, 0.0, 0.0, "");
			//TODO:Pintamos la cabecera que haya en cada página, cada vez que encuentre una nueva la 
			//irá guardando, y se pinta al salir.
		}
		return;
	}
	/**
	 * Carga de variables por página.
	 */
	public void pdf_gd_iniciar_variables()
	{
		pagRelativa = 1;
		cabeceraPlantilla=null;
		cabeceraFirstPage=false;
		cabeceraAlto=0;
		piePlantilla=null;
		pieFirstPage=false;
		pieAlto = 0;
		desplazamiento = 0;
	}
	
	/**
	 * Finaliza la página.
	 */
	public void pdf_gd_end_page()
	{
		this.pagRelativa++;
	}
	/**
	 * Inserta una imagen en el documento principal.
	 * @param node Nodo de Imagen en el XML.
	 */
	public void pdf_gd_place_image_file (Node node)
	{
		pdf_gd_place_image_file (node, oPDF, pdfwriter);
		return;
	}
	/**
	 * Inserta una imagen en el documento que se indica, y utiliza el PdfWriter 
	 * @param node Nodo "Imagen"
	 * @param doc Documento donde se va a incluir.
	 * @param writer se utilizará para escribir en el documento.
	 */
	public void pdf_gd_place_image_file (Node node, com.lowagie.text.Document doc, PdfWriter writer)
	{
		Node n1 = node;
		Node padre = n1.getParentNode();
		String identificador = n1.getTextContent();
		if (identificador.equals(""))
		{
			identificador = "/SINLOGO.JPG"; //TODO:No tenemos este JPG, debería ser BLANCO.TIF.
		}
		int r;
		int x1=86;
		int y1=86;
		int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
		if ( n1.getAttributes().getNamedItem("h") !=null && n1.getAttributes().getNamedItem("w") != null)
		{
			x1 = Integer.parseInt(n1.getAttributes().getNamedItem("w").getNodeValue());
			y1 = Integer.parseInt(n1.getAttributes().getNamedItem("h").getNodeValue());
		}
		else
		{
			r = identificador.toUpperCase().indexOf("LOGOS/LOG");
			if (r==-1) //No es un logo
			{
				//Buscamos firma
				r=identificador.toUpperCase().indexOf("FIRMA");
				if (r==-1) //Tampoco es firma
				{
					//Buscamos "BLANCO"
					r=identificador.toUpperCase().indexOf("BLANCO");
					if (r==-1) //Tampoco
					{
						x1=70; // Porque él lo vale, o sea, número mágico
						y1=76;
					}
					else
					{
						x1=25;
						y1=25;
					}
				}
				else
				{
					x1=70;
					y1=76;
				}
			}
		}
		int yPadre = (int) (y + y1 + Math.max(margenSup, this.cabeceraAlto) + desplazamiento);
		int xPadre = x;
		while (padre.getLocalName().equals("Bloque"))
		{		
			xPadre += Integer.parseInt(padre.getAttributes().getNamedItem("x").getNodeValue());
			yPadre += Integer.parseInt(padre.getAttributes().getNamedItem("y").getNodeValue());
			padre = padre.getParentNode();
		}
		padre = n1.getParentNode();
		if("Cabecera".equals(padre.getLocalName()))
		{
			yPadre -= (int)Math.max(margenSup, this.cabeceraAlto);
		}
		int yRelativa = this.altoPdf - (yPadre%this.altoPdf);
		boolean salto = false;
		long paginaRelativa = (int)Math.floor(yPadre/this.altoPdf); //FIXME: Se podría sustituir el altoPdf y demás por el alto del documento que se pase, pero como todos van a tener el mismo, no importa.
		if(yPadre%this.altoPdf!=0)
		{
			paginaRelativa++;
		}
		if(paginaRelativa > this.pagRelativa)
		{
			salto = true;
		}
		//Cambio de página.
		if(padre.getLocalName().equals("Bloque") && ( yRelativa <= (int)Math.max(margenInf, this.pieAlto) || salto ))
		{	
			//oPDF.begin_page_ext(this.anchoPdf, this.altoPdf, "");
			doc.newPage();
			/* Funciones equivalentes a estas?. 
			 * Deberían insertarse la cabecera 
			if(this.cabeceraPlantilla!=null)
			{
				oPDF.fit_image(this.cabeceraPlantilla, 0.0, 0.0, "");
			}
			if(this.piePlantilla!=null)
			{
				oPDF.fit_image(this.piePlantilla, 0.0, 0.0, "");
			}
			*/
			this.pagina++;
			this.pagRelativa++;
		
			int yRelativaNew = this.altoPdf - ((int)Math.max(margenSup, this.cabeceraAlto)) - y1;
			if(salto)
			{
			      desplazamiento = desplazamiento - (yRelativaNew - yRelativa);
			}
			else
			{
				desplazamiento = desplazamiento + (int)Math.max(margenSup, this.cabeceraAlto) + yRelativa - y1;
			}
			yRelativa = yRelativaNew;
		}
		int ximagen = xPadre;
		int yimagen = yRelativa;
		
		if (imagenes.containsKey(identificador))
		{
			Image img=imagenes.get(identificador);
			img.setAbsolutePosition(ximagen, yimagen);
			img.scaleToFit(x1, y1);
			//oPDF.fit_image(imagenes[identificador], ximagen, yimagen , "boxsize {" + x1 + " " + y1 + "} fitmethod=meet");
			PdfContentByte pcb2 = writer.getDirectContentUnder();
			try {
				pcb2.addImage(img);
			} catch (DocumentException e) {
				es.tributasenasturias.servicios.documentos.log.Logger.error("No se ha podido insertar la imagen:" + identificador + " en el pdf.",es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			}
			pcb2.stroke();
		}
	}
	/**
	 * Obtiene el tamaño de un bloque.
	 * @param nodo El nodo de bloque en un objeto  {@link Node}
	 * @return El tamaño del bloque, en el que tiene en cuenta el de los subbloques
	 */
	private int obtener_tam_bloque(Node nodo)
	{
		int tam = 0;
		
		NodeList hijos = nodo.getChildNodes();
		int y = 0;
		int h = 0;
		int y1 = 0;
		if (hijos!=null)
		{
			for(int i = 0; i < hijos.getLength(); i++)
			{
				y = h = 0;
				Node obj = hijos.item(i);
				if (obj!=null && obj.getNodeType()==Node.ELEMENT_NODE) //Sólo los elementos, no atributos, texto, ...
				{
					String nombreObj =obj.getLocalName(); 
					if ("Bloque".equals(nombreObj))
					{
						y = Integer.parseInt(obj.getAttributes().getNamedItem("y").getNodeValue());
						h = obtener_tam_bloque(obj);
					}
					else if ("Barras".equals(nombreObj))
					{
						y = Integer.parseInt(obj.getAttributes().getNamedItem("y").getNodeValue());
						if(obj.getAttributes().getNamedItem("y1")!=null)
						{
							h = Integer.parseInt(obj.getAttributes().getNamedItem("y1").getNodeValue());
						}
						else
						{
							h = 30;
						}
					}
					else if ("Imagen".equals(nombreObj))
					{
						y = Integer.parseInt(obj.getAttributes().getNamedItem("y").getNodeValue());
						if(obj.getAttributes().getNamedItem("h")!=null)
							h = Integer.parseInt(obj.getAttributes().getNamedItem("h").getNodeValue());
						else
						{
							h = 86;
							String nombre = XMLUtils.getNodeText(obj);
							if(nombre.toUpperCase().indexOf("LOGOS/LOG")==-1)
							{
								if(nombre.toUpperCase().indexOf("FIRMA")==-1)
								{
									if(nombre.toUpperCase().indexOf("BLANCO")==-1)
										h = 76;
									else
										h = 25;
								}
								else
									h = 76;
							}
						}
					}
					else if ("Bloque".equals(nombreObj))
					{
					
						y = Integer.parseInt(obj.getAttributes().getNamedItem("y").getNodeValue());
						y1 = Integer.parseInt(obj.getAttributes().getNamedItem("y1").getNodeValue());
						h = Math.abs(y - y1);
						y = Math.min(y, y1);
					}
					else
					{
						try {
							y = Integer.parseInt(obj.getAttributes().getNamedItem("y").getNodeValue());
							h = Integer.parseInt(obj.getAttributes().getNamedItem("y1").getNodeValue());
						} catch(Exception ex) {} //Pueden fallar si el elemento no tiene esas propiedades, no nos importa.
					}
				}
							
				if((h+y) > tam)
				{
					tam = y + h;
				}
	
				obj = null;
			}
		}
		return tam;
	}
	/**
	 * Proceso del bloque
	 * @param node Nodo "Bloque"
	 * @param doc Documento en el que se insertará el bloque (utilizado para crear nuevas páginas)
	 * @param writer No se utiliza en este momento.
	 */
	public void pdf_gd_dibuja_bloque(Node node,com.lowagie.text.Document doc, PdfWriter writer)
	{
		Node n1 = node;
		Node padre=n1.getParentNode();
		int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
		int ypadre = y + (int)Math.max(margenSup,this.cabeceraAlto) + desplazamiento;
		int xpadre = x;
		
		while(padre.getLocalName().equals("Bloque"))
		{
			xpadre += Integer.parseInt(padre.getAttributes().getNamedItem("x").getNodeValue());
			ypadre += Integer.parseInt(padre.getAttributes().getNamedItem("y").getNodeValue());
			padre = padre.getParentNode();
		}
		padre = n1.getParentNode();
		
		int yrelativa = this.altoPdf - (ypadre % this.altoPdf);
		
	    boolean indivisible = false;
	    boolean salto = false;
		if(n1.getAttributes().getNamedItem("indivisible")!=null)
		{
			indivisible = Boolean.parseBoolean(n1.getAttributes().getNamedItem("indivisible").getNodeValue());
			if(indivisible)
			{
				int tambloque = obtener_tam_bloque(n1);
				int izq = yrelativa - tambloque - Math.max(margenInf, this.pieAlto);

				if(izq < 0)
					salto = true;
			}
		}

		// No podemos controlar que el tag padre sea un Bloque porque los Bloques pueden colgar de Pagina
		if( yrelativa <= Math.max(margenInf, this.pieAlto) || salto )
		{	
			//oPDF.suspend_page("");
			//oPDF.begin_page_ext(this.ancho_pdf, this.alto_pdf, "");
			//Nueva página
			doc.newPage();
					
			this.pagina++;
			this.pagRelativa++;;

			desplazamiento = desplazamiento + yrelativa + (int)Math.max(margenSup, this.cabeceraAlto);
			
			//yrelativa = this.altoPdf - (int)Math.max(margenSup, this.cabeceraAlto);
		}
	}
	/**
	 * Proceso del bloque, con desplazamientos. Incluye los bloque indivisibles. Se utiliza el documento principal para los cálculos de tamaño.
	 * @param node Nodo bloque, {@link Node}
	 */
	public void pdf_gd_dibuja_bloque(Node node)
	{
		pdf_gd_dibuja_bloque (node, oPDF, pdfwriter);
		return;
	}
	/**
	 * Abre el pie de página
	 * @param node Nodo de pie de página
	 */
	public void pdf_gd_open_pie(Node node)
	{
		//this.pie_plantilla = oPDF.begin_template(this.ancho_pdf, this.alto_pdf);
		if (pie==null)
		{
			Node n = node;
			this.pie = n;
			this.pieFirstPage = Boolean.parseBoolean(n.getAttributes().getNamedItem("primera_pagina").getNodeValue());
			this.pieAlto = Integer.parseInt(n.getAttributes().getNamedItem("h").getNodeValue());
		}

		return;
	}
	/**
	 * Cierra el pie de página.
	 */
	public void pdf_gd_close_pie()
	{
		//oPDF.end_template();
		//TODO: Tratar esto como la cabecera, tendría que pintarse en la primera página según indique la
		//propiedad "primera_pagina"
		if(this.pieFirstPage)
		{
			//oPDF.fit_image(this.pie_plantilla, 0.0, 0.0, "");
		}
		return;
	}
	/**
	 * Establece el color de un elemento utilizando el PdfWriter indicado.
	 * @param n1 Nodo "Campo"
	 * @param defecto Color por defecto
	 * @param writer PdfWriter que se utilizará para establecer el color.
	 */
	public void pdf_gd_setcolor(Node n1, String defecto, PdfWriter writer)
	{
   String color = defecto;
		
		if(n1!=null)
		{
			if(n1.getAttributes().getNamedItem("color")!=null)
			{
				color = n1.getAttributes().getNamedItem("color").getNodeValue();
			}
			if(n1.getAttributes().getNamedItem("fondo")!=null)
			{
				color = n1.getAttributes().getNamedItem("fondo").getNodeValue();
			}
		}
			
		if(color==null)
			color = defecto;
		
		int r = 0, g = 0, b = 0;
		if ("blanco".equals(color))
		{
			r = 255; g = 255; b = 255;
		}
		else if ("gris1".equals(color))
		{
			r = 226; g = 226; b = 226;
		}
		
		else if ("gris2".equals(color))
		{
			r = 170; g = 170; b = 170;
		}
		else if ("negro".equals(color))
		{
			r = 0; g = 0; b = 0;
		}
		else if ("azul".equals(color))
		{
			r = 0; g = 0; b = 255;
		}
		else if ("cyan".equals(color))
		{
			r = 0; g = 255; b = 255;
		}
		else if ("navy".equals(color))
		{
			r = 0; g = 0; b = 128;
		}
		else if ("beige".equals(color))
		{
			r = 245; g = 245; b = 220;
		}
		else if ("marron".equals(color))
		{
			r = 165; g = 42; b = 42;
		}
		else if ("gris".equals(color))
		{
			r = 190; g = 190; b = 190;
		}
		else if ("verde".equals(color))
		{
			r = 0; g = 255; b = 255;
		}
		else if ("naranja".equals(color))
		{
			r = 255; g = 165; b = 0;
		}
		else if ("sienna".equals(color))
		{
			r = 160; g = 82; b = 45;
		}
		else if ("rosa".equals(color))
		{
			r = 255; g = 192; b = 203;
		}
		else if ("rojo".equals(color))
		{
			r = 255; g = 0; b = 0;
		}
		else if ("magenta".equals(color))
		{
			r = 255; g = 0; b = 255;
		}
		else if ("violeta".equals(color))
		{
			r = 238; g = 130; b = 238;
		}
		else if ("amarillo".equals(color))
		{
			r = 0; g = 255; b = 255; 
		}
		else //blanco
		{
			r = 255; g = 255; b = 255;
		}
		
		//oPDF.setcolor("fill", "rgb", r/255, g/255, b/255, 0);
		//Ponemos el color de relleno.
		pdf_set_rgb_fill(r,g,b, writer);
	}
	/**
	 * Establece el color del elemento. Utiliza el PdfWriter del documento principal.
	 * @param n1 Nodo "Campo"
	 * @param defecto Color por defecto.
	 */
	public void pdf_gd_setcolor(Node n1, String defecto)
	{
		pdf_gd_setcolor (n1, defecto, pdfwriter);
		return;
	}
	
	
	/**
	 * Establecer el color de elemento.
	 * @param n1 Nodo "Campo"
	 * @param defecto Color por defecto.
	 * @param flag Indicador. No se utiliza.
	 */
	public void pdf_gd_setcolor(Node n1, String defecto, int flag)
	{
		pdf_gd_setcolor (n1, defecto);
	}
	/**
	 * Modifica el color utilizado para relleno de dibujo en formato RGB. Utiliza el PdfWriter que se indica
	 * @param r Rojo
	 * @param g Verde
	 * @param b Azul
	 * @param writer PdfWriter
	 */
	public void pdf_set_rgb_fill (int r, int g, int b, PdfWriter writer)
	{
		PdfContentByte canvas = writer.getDirectContent();
		canvas.setRGBColorFill(r, g, b);
		canvas.fillStroke();
	}
	/**
	 * Dibuja un rectángulo con las coordenadas de origen y final indicadas, en el writer indicado
	 * Se indica si irá relleno o no.
	 * @param x Coordenada x de origen
	 * @param y Coordenada y de origen
	 * @param x1 Coordenada x de destino
	 * @param y1 Coordenada y de destino.
	 * @param writer PdfWriter a utilizar
	 */
	public void pdf_dibujar_rect (int x,int y, int x1, int y1, boolean relleno, PdfWriter writer)
	{
		PdfContentByte canvas = writer.getDirectContent();
		canvas.setColorStroke(Color.black); //Lo pintamos en negro, porque no se ha dicho otra cosa.
		canvas.rectangle(x, y, x1, y1);
		if (!relleno)
		{
			canvas.stroke();
		}
		else
		{
			canvas.fillStroke();
		}
	}
	/**
	 * Dibuja un rectángulo con las coordenadas de origen y final indicadas, en PdfWriter del documento principal.
	 * Se indica si irá relleno o no.
	 * @param x Coordenada x de origen
	 * @param y Coordenada y de origen
	 * @param x1 Coordenada x de destino
	 * @param y1 Coordenada y de destino.
	 * */
	public void pdf_dibujar_rect (int x,int y, int x1, int y1, boolean relleno)
	{
		pdf_dibujar_rect (x,y,x1,y1,relleno, pdfwriter);
	}
	/**
	 * Procesa un rectángulo del Xml a pintar, dibujándolo en el PDF que se indica, utilizando el writer asociado.
	 * @param node Nodo Cuadro.
	 * @param doc PdfDocument en el que se va a incluir el rectángulo.
	 * @param writer PdfWriter a utilizar para  escribir en el documento.
	 */
	public void pdf_gd_rect(Node node, com.lowagie.text.Document doc,PdfWriter writer)
	{
		Node n1 = node;
		Node padre=n1.getParentNode();
	   	int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
		int x1 = Integer.parseInt(n1.getAttributes().getNamedItem("x1").getNodeValue());
		int y1 = Integer.parseInt(n1.getAttributes().getNamedItem("y1").getNodeValue());
		
		
		int ypadre = y + y1 + (int)Math.max(margenSup, this.cabeceraAlto) + desplazamiento;
		int xpadre = x;
		while ("Bloque".equals(padre.getNodeName()))
		{
			xpadre += Integer.parseInt(padre.getAttributes().getNamedItem("x").getNodeValue());
			ypadre += Integer.parseInt(padre.getAttributes().getNamedItem("y").getNodeValue());
			padre = padre.getParentNode();
		}
		padre = n1.getParentNode();

		//No quitamos el desplazamiento porque la cabecera esta al principio y no hay desplazamiento
		if("Cabecera".equals(padre.getNodeName()))
		{
			ypadre -= Math.max(margenSup, this.cabeceraAlto);
		}

		int yrelativa = this.altoPdf - (ypadre%this.altoPdf);
		

		boolean salto = false;
		int pagina_relativa = (int)Math.floor(ypadre/this.altoPdf);
		if(ypadre%this.altoPdf!=0)
		{
			pagina_relativa++;
		}

		if(pagina_relativa > this.pagRelativa)
		{
			salto = true;
		}
		
		if("Bloque".equals(padre.getNodeName()) && ( yrelativa <= Math.max(margenInf, this.pieAlto) || salto ))
		{	
			//oPDF.suspend_page("");
			//oPDF.begin_page_ext(this.ancho_pdf, this.alto_pdf, "");
			doc.newPage();
			if(this.cabeceraPlantilla!=null)
			{
			//	oPDF.fit_image(this.cabecera_plantilla, 0.0, 0.0, "");
			}
			if(this.piePlantilla!=null)
			{
			//	oPDF.fit_image(this.pie_plantilla, 0.0, 0.0, "");
			}
			this.pagina++;
			this.pagRelativa = this.pagRelativa + 1;

			int yrelativa_new = this.altoPdf - (int)Math.max(margenSup, this.cabeceraAlto) - y1;
			if(salto)
			{
				desplazamiento = desplazamiento - (yrelativa_new - yrelativa);
			}
			else
			{
				desplazamiento = desplazamiento + yrelativa + (int)Math.max(margenSup, this.cabeceraAlto);
			}
			yrelativa = yrelativa_new;
		}
		
		int xcuadro = xpadre;
		int ycuadro = yrelativa;
		if(n1.getAttributes().getNamedItem("fondo")!=null)
		{
			pdf_gd_setcolor(n1, "blanco", writer);
			pdf_dibujar_rect (xcuadro, ycuadro, x1, y1,true, writer);
			//Dibujar un rectángulo.
			//oPDF.rect(xcuadro,ycuadro,x1,y1);
			//oPDF.fill_stroke();
		}
		else
		{
			//Ponemos el color del borde a negro
			pdf_gd_setcolor(null, "negro", writer);
			pdf_dibujar_rect (xcuadro, ycuadro, x1, y1, false, writer);
			//oPDF.rect(xcuadro,ycuadro,x1,y1);
			//oPDF.stroke();
		}
		//Poner el color al valor por defecto 
		//oPDF.setcolor("fill", "rgb", 0, 0, 0, 0);
		pdf_set_rgb_fill (0,0,0, writer);
		return;
	}
	/**
	 * Procesa un rectángulo del Xml a pintar, dibujándolo en el PDF. Utiliza el PdfWriter que se indica
	 * @param node Nodo Cuadro.
	 */
	public void pdf_gd_rect(Node node)
	{
		pdf_gd_rect (node, oPDF, pdfwriter);
	}
	/**
	 * Dibuja una línea en el documento indicado, utilizando el writer que se indica.
	 * @param nodo Nodo "Linea"
	 * @param doc Documento en el que se dibujará.
	 * @param writer 
	 */
	public void pdf_gd_linea(Node nodo, com.lowagie.text.Document doc,PdfWriter writer)
	{
		Node n1 = nodo;
	    Node padre = n1.getParentNode();
	    int grosor = 0;
	   	
	   	int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue()); //X_Inicial
		int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue()); //Y_Final
		int x1 = Integer.parseInt(n1.getAttributes().getNamedItem("x1").getNodeValue()); //X_Final
		int y1 = Integer.parseInt(n1.getAttributes().getNamedItem("y1").getNodeValue()); //Y_Final
		
		int h = Math.abs(y-y1);
		
		int ypadre = (int)Math.max(y, y1) + (int)Math.max(margenSup, this.cabeceraAlto) + desplazamiento;
		int xpadre = 0;
		
		while ("Bloque".equals(padre.getNodeName()))
		{		
			xpadre += Integer.parseInt(padre.getAttributes().getNamedItem("x").getNodeValue());
			ypadre += Integer.parseInt(padre.getAttributes().getNamedItem("y").getNodeValue());
			padre = padre.getParentNode();
		}
		padre = n1.getParentNode();
		
		if("Cabecera".equals(padre.getNodeName()))
		{
			ypadre -= Math.max(margenSup, this.cabeceraAlto);
		}
		int yrelativa = this.altoPdf - (ypadre%this.altoPdf);

		boolean salto = false;
		int pagina_relativa = (int)Math.floor(ypadre/this.altoPdf);
		if(ypadre%this.altoPdf!=0)
		{
			pagina_relativa++;
		}
		if(pagina_relativa > this.pagRelativa)
		{
			salto = true;
		}
		if("Bloque".equals(padre.getNodeName()) && ( yrelativa <= Math.max(margenInf, this.pieAlto) || salto ))
		{	
			doc.newPage();
			
			this.pagina++;
			this.pagRelativa = this.pagRelativa + 1;
			
			int yrelativa_new = this.altoPdf - (int)Math.max(margenSup, this.cabeceraAlto) - h;
			if(salto)
			{
				desplazamiento = desplazamiento - (yrelativa_new - yrelativa);
			}
			else
			{
				desplazamiento = desplazamiento + yrelativa + (int)Math.max(margenSup, this.cabeceraAlto) + h;
			}
			yrelativa = yrelativa_new;
		}

		int yInicial = yrelativa;
		int xInicial = xpadre + x;
		int yFinal = yrelativa + h;
		int xFinal = xpadre + x1;
		//Para pintar la línea
		PdfContentByte canvas = writer.getDirectContent();
		canvas.saveState();
		if(n1.getAttributes().getNamedItem("ancho")!=null)
		{
			grosor = Integer.parseInt(n1.getAttributes().getNamedItem("ancho").getNodeValue());
			//oPDF.setlinewidth(grosor); 
			canvas.setLineWidth(grosor);
		}   
		
		if (n1.getAttributes().getNamedItem("tipo")!=null)
		{
			String discontinuo = n1.getAttributes().getNamedItem("tipo").getNodeValue().toLowerCase();
			if(discontinuo=="discontinuo")
			{
				//oPDF.setdash(2,2);
				canvas.setLineDash(2, 2);
			}
		}   
		canvas.moveTo(xInicial,yInicial);
		canvas.lineTo(xFinal, yFinal);
		canvas.stroke();
		//oPDF.moveto(xInicial, yInicial)
		//oPDF.lineto(xFinal, yFinal);
		//oPDF.stroke();
		
		//Reseteamos a valores por defecto
		//Si ponemos las dos líneas siguientes, el programa falla. 
		//Probablemente sea por argumentos inválidos, pero sin ellas funciona correctamente.
		//canvas.setLineWidth(1);
		//canvas.setLineDash(0,0);
		canvas.restoreState();
	  	//oPDF.setlinewidth(1);
	  	//oPDF.setdash(0,0);
		return;
	}
	/**
	 * Procesa un nodo Linea en el documento. Utiliza el PdfWriter del documento principal.
	 * @param nodo Nodo de tipo Linea.
	 */
	public void pdf_gd_linea(Node nodo)
	{
		pdf_gd_linea (nodo, oPDF,pdfwriter);
	}
	
	/**
	 * Establece la fuente a la indicada. Utiliza el PdfWriter que se indica.
	 * @param fuente Nombre de la fuente
	 * @param tamanio Tamaño
	 * @param encoding Codificación
	 * @param writer PdfWriter en el que se establecerá la fuente.
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void setFont (String fuente,float tamanio, String encoding, PdfWriter writer) throws DocumentException, IOException
	{
		PdfContentByte canvas = writer.getDirectContent();
		BaseFont bf = BaseFont.createFont(fuente, encoding, BaseFont.NOT_EMBEDDED);
		canvas.setFontAndSize(bf, tamanio);
		ff = new Font(bf);
		ff.setSize(tamanio);
		ff.setFamily(encoding);
	}
	/**
	 * Establece la fuente a la indicada. Utiliza el pdfwriter por defecto.
	 * @param fuente Nombre de la fuente
	 * @param tamanio Tamaño
	 * @param encoding Codificación
	 * @throws DocumentException 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void setFont (String fuente,float tamanio, String encoding) throws DocumentException, IOException
	{
		setFont (fuente, tamanio, encoding, pdfwriter);
	}
	/**
	 * Establece la fuente de documento de el punto en que es llamada la función en adelante.
	 * Utiliza el pdfwriter del documento principal.
	 * @param n1 Nodo "Campo"
	 */
	private void pdf_gd_setfont(Node n1) 
	{
		pdf_gd_setfont (n1, pdfwriter);
	}
	/**
	 * Establece la fuente de documento de el punto en que es llamada la función en adelante.
	 * Puede utilizarse para establecer la fuente en el pdf de elementos comunes.
	 * @param n1 Nodo "Campo"
	 * @param writer El PdfWriter utilizado para establecer la fuente.
	 */
	private void pdf_gd_setfont(Node n1, PdfWriter writer) 
	{	
		String fuente = n1.getAttributes().getNamedItem("fuente").getNodeValue();
		if (fuente.equals("F1"))
		{		
			fuente = "Helvetica";
		}
		else if (fuente.equals("F2"))
		{
			fuente = "Helvetica-Bold";
		}
		else if (fuente.equals("F3")) 
		{
			fuente = "Helvetica-Oblique";
		}
		else if (fuente.equals("F4"))
		{
			fuente = "Helvetica-BoldOblique";
		}
		else if (fuente.equals("F5"))
		{
			fuente = "Courier";
		}
		else if (fuente.equals("F6"))
		{
			fuente = "Courier-Bold";
		}
		else if (fuente.equals("F7"))
		{
			fuente = "Courier-Oblique";
		}
		else if (fuente.equals("F8"))
		{
			fuente = "Courier-BoldOblique";
		}
		else if (fuente.equals("F9"))
		{
			fuente = "Times-Bold";
		}
		else if (fuente.equals("F10"))
		{
			fuente = "Arial";
		}
		else if (fuente.equals("F11"))
		{
			fuente = "Arial-Bold";
		}
		else if (fuente.equals("F12"))
		{
			fuente = "Arial-Italic";
		}
		else if (fuente.equals("F13"))
		{
			fuente = "Arial-Bold-Italic";
		}
		else
		{
			fuente = "Helvetica";
		}
		
		
		float tam = Float.parseFloat(n1.getAttributes().getNamedItem("tam").getNodeValue());
		String encoding = BaseFont.CP1252;
		//String font = oPDF.load_font(fuente, encoding, "");
		//oPDF.setfont(font, s);
		try {
			setFont (fuente,tam,encoding, writer);
		} catch (Exception e) {
			Logger.error ("Error al establecer la fuente a utilizar:" + fuente,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			Logger.trace(e.getStackTrace(), es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}

		return;
	}
	
	private void pdf_gd_setfont(Node n1, String flag)
	{
		pdf_gd_setfont (n1);
	}
	/**
	 * Retorna la alineación que entiende la librería PDF en función de la alineación que se declara
	 * en el elemento a pintar
	 * @param alineacion Alineación declarada en el elemento a pintar.
	 * @return
	 */
	public int getAlineacion (String alineacion)
	{
		int align=Element.ALIGN_LEFT;
		if (alineacion.equals("left"))
		{
			align=Element.ALIGN_LEFT;
		}
		else if (alineacion.equals("right"))
		{
			align=Element.ALIGN_RIGHT;
		} else if (alineacion.equals("justify"))
		{
			align=Element.ALIGN_JUSTIFIED;
		}
		else if (alineacion.equals("center"))
		{
			align = Element.ALIGN_CENTER;
		}
		return align;
	}
	
	public float obtenerPosX( float posx, int numPag)
	{
	    if (numPag > 0)
	        numPag = this.pagina;
	    //Por ahora ignoro lo de "iniciado template, ya que no lo soportamos        
	    /*if (numPag % 2 == 0 && !this.iniciado_template)
	        return (posx - this.factor_correccion);
	    else
	        return posx;
	    */
	    return posx;
	}
	/**
	 * Muestra un texto enmarcado en un tamaño concreto.
	 * @param texto Texto a mostrar
	 * @param x Coordenada X inicial del área que contendrá el texto
	 * @param y Coordenada Y inicial del área que contendrá el texto
	 * @param width Ancho del área que contendrá el texto.
	 * @param height Alto del área que contendrá el texto.
	 * @param alineacion Alineación
	 */
	public void pdf_show_boxed(String texto,int x, int y, int width, int height, String alineacion )
	{
		pdf_show_boxed (pdfwriter, texto, x,y, width, height, alineacion);
		
	}
	/**
	 * Muestra un texto enmarcado en un tamaño concreto.
	 * Usado internamente, permite establecer que se escribirá a un contenido PDF distinto al contenido 
	 * por defecto. 
	 * @param writer PdfWriter en el que se escribirá el texto.
	 * @param texto Texto a mostrar
	 * @param x Coordenada X inicial del área que contendrá el texto
	 * @param y Coordenada Y inicial del área que contendrá el texto
	 * @param width Ancho del área que contendrá el texto.
	 * @param height Alto del área que contendrá el texto.
	 * @param alineacion Alineación
	 */
	private void pdf_show_boxed (PdfWriter writer, String texto,int x, int y, int width, int height, String alineacion)
	{
		PdfContentByte canvas;
		canvas = writer.getDirectContent();
		
		ColumnText ct= new ColumnText (canvas);
		//float [] coord = {x, y, x+width, y -height};
		//El Leading tenemos que ponerlo al alto de la fuente, porque de otra manera
		//líneas consecutivas se escribirán en la misma línea si dejamos el valor
		// por defecto del canvas, que es 0.
		float leading = ff.getSize();		
		ct.setSimpleColumn(new Phrase(texto,ff), x, y, x+width,y+height, leading, getAlineacion(alineacion));
		try {
			ct.go(); // Ignoramos el retorno. Debería caber en el espacio asignado.
		} catch (DocumentException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error("Imposible mostrar el texto:" + texto,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			es.tributasenasturias.servicios.documentos.log.Logger.trace(e.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		} 
	}
	
	/**
	 * Muestra el texto con rotación. Sólo admite una línea de cada vez
	 * @param writer PdfWriter en el que se escribirá el texto.
	 * @param texto Texto a mostrar
	 * @param x Coordenada X inicial del área que contendrá el texto
	 * @param y Coordenada Y inicial del área que contendrá el texto
	 * @param alineacion Alineación
	 * @param rotacion Rotación, en grados
	 */
	public void pdf_show_boxed_rotation(PdfWriter writer,String texto,int x, int y, int width, int height, String alineacion, float rotacion )
	{
		PdfContentByte canvas;
		canvas = writer.getDirectContent();

		ColumnText ct= new ColumnText (canvas);
		float yFinal= y+(width/2); //Aproximación, la verdad es que no sé qué hace el PDFLib,
		                           //pero parece que la caja del ancho y alto la tiene en cuenta 
		                           //para posicionar ==después== de la rotación, por lo que 
		                           //en este caso tenemos que calcular un desplazamiento de la y.
		                           //Por eso sumo el ancho a la y, y no el alto...
		                           //Lo que funciona bien para rotaciones en 90 grados, pero no en 180.
		                           //Pero este desplazamiento también se verá afectado por la alineación...
		if (y+width>altoPdf){
			yFinal=y; //No sé como saber dónde acaba el texto, sólo tengo en cuenta la y más el ancho
		}
		ColumnText.showTextAligned(canvas, getAlineacion(alineacion), new Phrase(texto,ff), x, yFinal,rotacion);
		try {
			ct.go(); // Ignoramos el retorno. Debería caber en el espacio asignado.
		} catch (DocumentException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error("Imposible mostrar el texto:" + texto,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			es.tributasenasturias.servicios.documentos.log.Logger.trace(e.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		} 
		
	}
	/**
	 * Procesa los elemento "Campo" en un documento y usando el PdfWriter que se indica. 
	 * @param doc Documento que se utilizará.
	 * @param writer PdfWriter que se usa para escribir el contenido.
	 * @param node Nodo "Campo".
	 */
	public void pdf_gd_show_texto (Node node,com.lowagie.text.Document doc, PdfWriter writer)
	{
		try
		{
			Node n1 = node;
			Node padre = n1.getParentNode();	
			String texto = XMLUtils.getNodeText(n1);
			int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
			int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
			int w = Integer.parseInt(n1.getAttributes().getNamedItem("x1").getNodeValue());
			int h = Integer.parseInt(n1.getAttributes().getNamedItem("y1").getNodeValue());
	
			//40462. Se ha de soportar la rotación para el modelo 008
			float rotacion=0.0f;
			try {
				Node rotacionNode= n1.getAttributes().getNamedItem("rotacion");
				if (rotacionNode!=null){
					rotacion= Float.parseFloat(rotacionNode.getNodeValue());
				}
			}catch (Exception e){
				
			}
			// Fin 40462
			/*if (n1.getAttributes().getNamedItem("id").getNodeValue().equals("texto5"))
			{
				//String d="";
				System.out.println ("");
			}*/
			int xpadre = 0;
			int ypadre = h + y + (int)Math.max(margenSup, this.cabeceraAlto) + desplazamiento;
			
			//Los de la cabecera no respetan los margenes ni el alto de la cabecera
			if("Cabecera".equals(padre.getNodeName()))
			{
				ypadre -= Math.max(margenSup, this.cabeceraAlto);
			}
				
			while("Bloque".equals(padre.getNodeName()))
			{
				xpadre += Integer.parseInt(padre.getAttributes().getNamedItem("x").getNodeValue());
				ypadre += Integer.parseInt(padre.getAttributes().getNamedItem("y").getNodeValue());
				padre = padre.getParentNode();
			}
			padre = n1.getParentNode();
			
			int yrelativa = this.altoPdf - (ypadre % this.altoPdf);
				
			boolean salto = false;
			int pagina_relativa = (int)Math.floor(ypadre/this.altoPdf);
			if(ypadre%this.altoPdf!=0)
			{
				pagina_relativa++;
			}
			if(pagina_relativa > this.pagRelativa)
			{
				salto = true;
			}
			if("Bloque".equals(padre.getNodeName()) && ( yrelativa <= Math.max(margenInf, this.pieAlto) || salto ))
			{
				
				//oPDF.suspend_page("");
				//oPDF.begin_page_ext(this.ancho_pdf, this.alto_pdf, "");
				doc.newPage();
				if(this.cabeceraPlantilla!=null)
				{
					//oPDF.fit_image(this.cabeceraPlantilla, 0.0, 0.0, "");
					//Pinta la cabecera de la nueva página.
				}
				if(this.piePlantilla!=null)
				{
					//oPDF.fit_image(this.piePlantilla, 0.0, 0.0, "");
				}
				
				this.pagina++;
				this.pagRelativa = this.pagRelativa + 1;
	
				int yrelativa_new = this.altoPdf - (int)Math.max(margenSup, this.cabeceraAlto) - h;
				if(salto)
				{
					desplazamiento = desplazamiento - (yrelativa_new - yrelativa);
				}
				else
				{
					desplazamiento = desplazamiento + yrelativa + (int)Math.max(margenSup, this.cabeceraAlto) + h;
				}
	
				yrelativa = yrelativa_new;
			}
			int ytexto = yrelativa;
			int xtexto = xpadre + x;		
	
			String modo = null;
			if (n1.getAttributes().getNamedItem("alin")!=null)
			{
				modo=n1.getAttributes().getNamedItem("alin").getNodeValue();
			}
			if (modo==null)
			{
				modo = "left";
			}
			if(n1.getAttributes().getNamedItem("blancos")!=null)
			{
				if(n1.getAttributes().getNamedItem("blancos").getNodeValue().equals("si"))
				{
					String textDummy= XMLUtils.getNodeText(n1);
					texto = textDummy.substring(1,textDummy.length()-1);
				}
			}
			
			pdf_gd_setfont(n1, writer);
			pdf_gd_setcolor(n1, "negro", writer);
			
			String tipo="";
			if (n1.getAttributes().getNamedItem("tipo")!=null)
			{
				tipo = n1.getAttributes().getNamedItem("tipo").getNodeValue(); 
			}
			//FIXME: Tratar el símbolo de euro en los textos variables.
			if (tipo.equals("simbeuro"))
			{
				PdfContentByte canvas = writer.getDirectContent();
				canvas.beginText(); //TODO: Es posible que no se pueda hacer aunque hayamos puesto antes una fuente concreta. Deberíamos establecer el begintext al principio de esta función.
				canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, fromCharCode(8364), xtexto, ytexto, 0);
				//oPDF.show_xy(fromCharCode(8364),xtexto,ytexto);
				canvas.endText();
			}
			else if (tipo.equals("moneda"))
			{
				pdf_show_boxed (writer, toEuro(texto),xtexto, ytexto, w, h, modo);
				//oPDF.show_boxed(toEuro(texto),xtexto,ytexto,w,h,modo,"");
			}
			else if (tipo.equals("autonumeracion"))
			{
				// La numeración de páginas vamos a hacerla al procesar el pie de informe posteriormente,
				//no durante el proceso de pintado del informe, sino en una segunda pasada,
				//cuando ya sepamos el número de páginas.
				this.listAutonumericos.add(n1);
				/*var atrib = null, pie_num_total = "#undefined#", pie_num_numero = "#undefined#";
				atrib = n1.attributes.getNamedItem("total");
				if(atrib!=null)
					pie_num_total = atrib.value;
				var formatTotal = ((pie_num_total.length - pie_num_total.search("F") - 1)>=pie_num_total.length?1:(pie_num_total.length - pie_num_total.search("F") - 1));
				atrib = null;
				atrib = n1.attributes.getNamedItem("numero");
				if(atrib!=null)
					pie_num_numero = atrib.value;
				atrib = n1.attributes.getNamedItem("global");
				if(atrib!=null)
					this.numeracion_global = eval(atrib.value);
				var formatNumero = ((pie_num_numero.length - pie_num_numero.search("F") - 1)>=pie_num_numero.length?1:(pie_num_numero.length - pie_num_numero.search("F") - 1));				
				atrib = null;
				this.numeracion = pdf_gd_setfont(n1, 2);
				this.numeracion += pdf_gd_setcolor(n1, "negro", 2);
				this.numeracion += "var txtTotal = paginaTotal + \"\";";		
				this.numeracion += "for(var z = txtTotal.length; z < " + formatTotal + "; z++)";
				this.numeracion += "	txtTotal = \"0\" + txtTotal;";
				this.numeracion += "var txtPagina = paginaCont + \"\";";
				this.numeracion += "for(var z = txtPagina.length; z < " + formatNumero + "; z++)";
				this.numeracion += "	txtPagina = \"0\" + txtPagina;";
				this.numeracion += "var texto = \"" + texto + "\";";
				this.numeracion += "texto = texto.replace(\""+ pie_num_total + "\",txtTotal);";
				this.numeracion += "texto = texto.replace(\"" + pie_num_numero + "\",txtPagina);";
				this.numeracion += "oPDF.show_boxed(texto,"+ xtexto + ","+ ytexto+ ","+ w + ","+ h + ",\""+ modo +"\",\"\");";
				*/
			}
			else if (tipo.equals("textovariable"))
			{
				//Establecemos fuente
				//pdf_gd_setfont (n1,"1");
				pdf_gd_setfont (n1,writer);
				//y color
				//pdf_gd_setcolor (n1,"negro",1);
				pdf_gd_setcolor (n1,"negro", writer);
				//TODO: A ver cómo ponemos el relleno del texto variable. Probablemente en las características
				//de las columnas.
				
				int status=0;
				int i=0; //Sólo para evitar bucles infinitos.
				int k=0;
				PdfContentByte canvas = writer.getDirectContent();
				ColumnText ct = new ColumnText(canvas);
				ct.setAlignment(getAlineacion(modo));
				ct.addText(new Phrase(texto,ff));
				ct.setLeading(ff.getSize()); //Importante, para que las líneas extra salgan con el espacio adecuado.
				while (ColumnText.hasMoreText(status) && k<1000)
				{
					k++; // Para que no haya bucles infinitos.
					if (i==0) //Primer bloque de texto variable. No vamos a considerar que haya mas de dos, porque
						      //eso sería que un texto variable ocupase una página y parte de otra.
					{
						//ct.setColumns(coordIzq, coordDer);
						ct.setSimpleColumn(xtexto, (ytexto+h), xtexto+w, (int)Math.max(margenInf, this.pieAlto));
						i++;
					}
					else
					{
						doc.newPage();
						this.pagina++;
						this.pagRelativa++;
						desplazamiento += Math.max(this.margenSup, this.cabeceraAlto) + ytexto;
						ytexto = this.altoPdf - (int)Math.max (margenSup, this.cabeceraAlto);
						ct.setSimpleColumn(xtexto, ytexto, xtexto+w, Math.max(margenInf,this.pieAlto));
					}
					status = ct.go();
				}
				float posy = ct.getYLine();
				desplazamiento += (ytexto+h) - posy;//(posy + ff.getSize()); //Importante!: El leading también entra en juego.
				/*
				var formato = pdf_gd_setfont(n1, 1) + " " + pdf_gd_setcolor(n1, "negro", 1);
				var textflow = oPDF.create_textflow(texto, formato + " alignment=" + modo);
				var result = oPDF.fit_textflow(textflow, xtexto, (ytexto+h), xtexto+w, Math.max(margen_inf, this.pie_alto), "");
				while(result=="_boxfull")
				{
					
					oPDF.suspend_page("");
					oPDF.begin_page_ext(this.ancho_pdf, this.alto_pdf, "");
					if(this.cabecera_plantilla!=null)
						oPDF.fit_image(this.cabecera_plantilla, 0.0, 0.0, "");
					if(this.pie_plantilla!=null)
						oPDF.fit_image(this.pie_plantilla, 0.0, 0.0, "");
					this.pagina = this.pagina + 1;
					this.pag_relativa = this.pag_relativa + 1;
					
					desplazamiento = desplazamiento + Math.max(margen_sup, this.cabecera_alto) + ytexto;
					
					ytexto = this.alto_pdf - Math.max(margen_sup, this.cabecera_alto);
					result = oPDF.fit_textflow(textflow, xtexto,ytexto, xtexto + w, Math.max(margen_inf, this.pie_alto), "");
					}
				var posy = oPDF.info_textflow(textflow, "textendy");
				//Response.Write ("<br>Tamaño del texto impreso --> posy: "+posy+" Desplazamiento: "+desplazamiento+" ytexto: "+ytexto+" h: "+h);
				oPDF.delete_textflow(textflow);
				desplazamiento += (ytexto+h) - posy;
				//Response.Write("<br>Desplazamiento sin salto --> desplazamiento: "+desplazamiento);
				 * 
				 */
			}
			else if (tipo.equals("textovariabledos"))
			{
				
				//Por el momento no se utiliza en los informes, así que no lo reproduzco.
				//Sería como el texto variable, pero se tendría que poner un borde, quizá
				//con un rectángulo.
				/*
				var formato = pdf_gd_setfont(n1, 1);
				var textflow = oPDF.create_textflow(texto, formato + " alignment=" + modo);
				//var result = oPDF.fit_textflow(textflow, xtexto, (ytexto+h), xtexto + w, Math.max(margen_inf, this.pie_alto), " showborder=true firstlinedist=12 ");
				var result = oPDF.fit_textflow(textflow, xtexto, (ytexto+h), xtexto + w, Math.max(margen_inf, this.pie_alto), "");
				while(result=="_boxfull")
				{
					
					oPDF.suspend_page("");
					oPDF.begin_page_ext(this.ancho_pdf, this.alto_pdf, "");
					if(this.cabecera_plantilla!=null)
						oPDF.fit_image(this.cabecera_plantilla, 0.0, 0.0, "");
					if(this.pie_plantilla!=null)
						oPDF.fit_image(this.pie_plantilla, 0.0, 0.0, "");
					this.pagina = this.pagina + 1;
					this.pag_relativa = this.pag_relativa + 1;
					
					desplazamiento = desplazamiento + Math.max(margen_sup, this.cabecera_alto) + ytexto;
					
					ytexto = this.alto_pdf - Math.max(margen_sup, this.cabecera_alto);
					result = oPDF.fit_textflow(textflow, xtexto,ytexto, xtexto + w, Math.max(margen_inf, this.pie_alto), "");
					}
				var posy = oPDF.info_textflow(textflow, "textendy");
				//Response.Write ("<br>Tamaño del texto impreso --> posy: "+posy+" Desplazamiento: "+desplazamiento+" ytexto: "+ytexto+" h: "+h);
				oPDF.delete_textflow(textflow);
				//desplazamiento += (ytexto+h) - posy;
				//Response.Write("<br>Desplazamiento sin salto --> desplazamiento: "+desplazamiento);
				 * 
				 */
			}
		    /*case "firma":
            var formatod = pdf_gd_setfont(n1, 1) + " " + pdf_gd_setcolor(n1, "blanco", 1);
            //texto = n1.GetAttribute("id", String.Empty);
			texto = n1.attributes.getNamedItem("id").value;

            textflow = oPDF.create_textflow(texto, formatod + " alignment=" + modo);
            oPDF.fit_textflow(textflow, this.obtenerPosX(xtexto), (ytexto + h), this.obtenerPosX(xtexto) + w, ytexto, " rewind=-1");
            oPDF.delete_textflow(textflow);
            
			if (!this.iniciado_template) {
                min_y_pag = Math.min(ytexto + h, min_y_pag);
			}
            break; */
			else if (tipo.equals("firma")){ // CRUBENCVS 41512
				//El texto será el id del campo
				//No controlamos errores, si no tiene ID, no imprime. Mejor así,
				//porque si no imprime correctamente el nodo firma, no podrá firmar 
				//posteriormente.
				texto= n1.getAttributes().getNamedItem("id").getNodeValue();
				//Establecemos fuente
				pdf_gd_setfont (n1,writer);
				//y color
				pdf_gd_setcolor (n1,"blanco", writer);
				//No hay manera con ColumnText de que salga. Voy a probar con showboxed
				//PdfContentByte canvas = writer.getDirectContent();
				//ColumnText ct = new ColumnText(canvas);
				//ct.setAlignment(getAlineacion(modo));
				//ct.addText(new Phrase(texto,ff));
				//ct.setSimpleColumn(obtenerPosX(xtexto,0), (ytexto+h), obtenerPosX(xtexto,0)+w, ytexto);
				//ct.go();
				pdf_show_boxed (writer, texto, (int)obtenerPosX(xtexto,0), ytexto, w, h, modo);
				//La parte de min_y_pag no la hago, ya que en esta versión esa variable no existe.
			}
			else
			{
				
				//oPDF.show_boxed(texto.replace(/€/g," euros"),xtexto,ytexto,w,h,modo,"");
				//40462. Se ha de soportar la rotación
				
				
				if (texto!=null)
				{
					if (rotacion!=0){ 
						pdf_show_boxed_rotation(writer, texto.replaceAll("/€/g"," euros"), xtexto, ytexto, w , h ,modo, rotacion);
					} else {
						pdf_show_boxed (writer, texto.replaceAll("/€/g"," euros"),xtexto, ytexto, w, h, modo);
					}
				} else {
					pdf_show_boxed (writer, "",xtexto, ytexto, w, h, modo);
				}
			}
			
			

		}
		catch (Exception ex)
		{
			es.tributasenasturias.servicios.documentos.log.Logger.trace(ex.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		}
	}
/**
 * Procesa los elementos "Campo" de tipo texto. Escribe en el documento principal.
 * @param node
 */
	public void pdf_gd_show_texto(Node node) 
	{
		pdf_gd_show_texto (node, oPDF,pdfwriter);
		return;
	}
	/**
	 * Procesa el desplazamiento al encontrarse con un salto de página.
	 */
	public void pdf_gd_reset_despl()
	{
		desplazamiento = 0;
		
		return;
	}
	/**
	 * Procesa los elementos autonuméricos, para que pueda mostrar los números de página.
	 * @param n Nodo de campo autonumérico
	 * @param writer PdfWriter utilizado para imprimir el contenido del autonumérico
	 * @param paginaTotal Número de páginas totales
	 * @param paginaCont Número de página actual
	 */
	public void procesaAutonumerico(Node n, PdfWriter writer, int paginaTotal, int paginaCont)
	{
		Node n1= n;
		Node padre = n1.getParentNode();
		Node atrib = null;
		String pie_num_total = "#undefined#";
		String pie_num_numero = "#undefined#";
		int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
		int w = Integer.parseInt(n1.getAttributes().getNamedItem("x1").getNodeValue());
		int h = Integer.parseInt(n1.getAttributes().getNamedItem("y1").getNodeValue());
		atrib = n1.getAttributes().getNamedItem("total");
		int xpadre = 0;
		int ypadre = h + y + (int)Math.max(margenSup, this.cabeceraAlto); // + desplazamiento;
		
		//Los de la cabecera no respetan los margenes ni el alto de la cabecera
		if("Cabecera".equals(padre.getNodeName()))
		{
			ypadre -= Math.max(margenSup, this.cabeceraAlto);
		}
		padre = n1.getParentNode();
		
		int ytexto = this.altoPdf - (ypadre % this.altoPdf);
		int xtexto = xpadre + x;		
		String modo = null;
		if (n1.getAttributes().getNamedItem("alin")!=null)
		{
			modo=n1.getAttributes().getNamedItem("alin").getNodeValue();
		}
		if (modo==null)
		{
			modo = "left";
		}
		if(atrib!=null)
		{
			pie_num_total = atrib.getNodeValue();
		}
		int formatTotal = ((pie_num_total.length() - pie_num_total.indexOf("F") - 1)>=pie_num_total.length()?1:(pie_num_total.length() - pie_num_total.indexOf("F") - 1));
		atrib = null;
		atrib = n1.getAttributes().getNamedItem("numero");
		if(atrib!=null)
		{
			pie_num_numero = atrib.getNodeValue();
		}
		
		int formatNumero = ((pie_num_numero.length() - pie_num_numero.indexOf("F") - 1)>=pie_num_numero.length()?1:(pie_num_numero.length() - pie_num_numero.indexOf("F") - 1));				
		atrib = null;
		pdf_gd_setfont(n1, writer);
		pdf_gd_setcolor(n1, "negro", writer);
		String txtTotal = String.valueOf(paginaTotal);
		for (int z=txtTotal.length();z< formatTotal;z++)
		{
			txtTotal = "0" + txtTotal; //¿?
		}
		String txtPagina = String.valueOf(paginaCont);
		for (int z= txtPagina.length(); z< formatNumero;z++)
		{
			txtPagina = "0"+ txtPagina; //¿?
		}
		String texto = XMLUtils.getNodeText(n1).replace(pie_num_total, txtTotal).replace(pie_num_numero, txtPagina);
		pdf_show_boxed(writer,texto, xtexto, ytexto, w, h, modo);
	}
	
	public void procesaCodigoVerificacion(PdfWriter writer, String codigoVerificacion) throws DocumentException, IOException
	{
		//Fuente fija
		setFont("Courier-Bold", 10, BaseFont.CP1252, writer);
		//Color negro.
		pdf_show_boxed(writer,"Código de Verificación:" + codigoVerificacion, 0, this.altoPdf-25, this.anchoPdf, 14, "center");
	}
	public void procesaAutonumerico(Node n, PdfStamper stamp,  int paginaTotal, int paginaCont)
	{
		Node n1= n;
		Node padre = n1.getParentNode();
		Node atrib = null;
		String pie_num_total = "#undefined#";
		String pie_num_numero = "#undefined#";
		//int x = Integer.parseInt(n1.getAttributes().getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(n1.getAttributes().getNamedItem("y").getNodeValue());
		//int w = Integer.parseInt(n1.getAttributes().getNamedItem("x1").getNodeValue());
		int h = Integer.parseInt(n1.getAttributes().getNamedItem("y1").getNodeValue());
		atrib = n1.getAttributes().getNamedItem("total");
		//int xpadre = 0;
		int ypadre = h + y + (int)Math.max(margenSup, this.cabeceraAlto); // + desplazamiento;
		
		//Los de la cabecera no respetan los margenes ni el alto de la cabecera
		if("Cabecera".equals(padre.getNodeName()))
		{
			ypadre -= Math.max(margenSup, this.cabeceraAlto);
		}
		//Nuevos cálculos para saber donde poner el pie... a ver si están bien.
		padre = n1.getParentNode();
		int yrelativa = this.altoPdf - (ypadre % this.altoPdf);
		boolean salto = false;
		int pagina_relativa = (int)Math.floor(ypadre/this.altoPdf);
		if(ypadre%this.altoPdf!=0)
		{
			pagina_relativa++;
		}
		if(pagina_relativa > this.pagRelativa)
		{
			salto = true;
		}
		if(padre.getNodeName().equals("Bloque") && ( yrelativa <= Math.max(margenInf, this.pieAlto) || salto ))
		{
			oPDF.newPage();
			this.pagina++;
			this.pagRelativa++;

			int yrelativa_new = this.altoPdf - (int) Math.max(margenSup, this.cabeceraAlto) - h;
			if(salto)
			{
				desplazamiento = desplazamiento - (yrelativa_new - yrelativa);
			}
			else
			{
				desplazamiento = desplazamiento + yrelativa + (int)Math.max(margenSup, this.cabeceraAlto) + h;
			}


			yrelativa = yrelativa_new;
			//Response.write("<b>pintando en </b>" + yrelativa);
		}
		
		
		//int ytexto = yrelativa;
		//int xtexto = xpadre + x;		

		
		String modo = null;
		if (n1.getAttributes().getNamedItem("alin")!=null)
		{
			modo=n1.getAttributes().getNamedItem("alin").getNodeValue();
		}
		if (modo==null)
		{
			modo = "left";
		}
		if(atrib!=null)
		{
			pie_num_total = atrib.getNodeValue();
		}
		int formatTotal = ((pie_num_total.length() - pie_num_total.indexOf("F") - 1)>=pie_num_total.length()?1:(pie_num_total.length() - pie_num_total.indexOf("F") - 1));
		atrib = null;
		atrib = n1.getAttributes().getNamedItem("numero");
		if(atrib!=null)
		{
			pie_num_numero = atrib.getNodeValue();
		}
		
		int formatNumero = ((pie_num_numero.length() - pie_num_numero.indexOf("F") - 1)>=pie_num_numero.length()?1:(pie_num_numero.length() - pie_num_numero.indexOf("F") - 1));				
		atrib = null;
		// Y ahora, a pintar...
		//Con el PdfStamper
		pdf_gd_setfont(n1, "2");
		pdf_gd_setcolor(n1, "negro", 2);
		String txtTotal = String.valueOf(paginaTotal);
		for (int z=txtTotal.length();z< formatTotal;z++)
		{
			txtTotal = "0" + txtTotal; //¿?
		}
		String txtPagina = String.valueOf(paginaCont);
		for (int z= txtPagina.length(); z< formatNumero;z++)
		{
			txtPagina = "0"+ txtPagina; //¿?
		}
		//String texto = XMLUtils.getNodeText(n1).replace(pie_num_total, txtTotal).replace(pie_num_numero, txtPagina);
		//PdfContentByte pagina = stamp.getOverContent(paginaCont); //TODO:No tengo claro que esto funcione a menos que sea numeración global...
		//pdf_show_boxed(pagina,texto, xtexto, ytexto, w, h, modo);
	}
	public void procesarAutonumericos()
	{
		byte[] output= pdfoutput.toByteArray();
		PdfReader reader;
		PdfStamper stamp=null;
		try {
			reader = new PdfReader(output);
			//Reseteamos la salida
			pdfoutput = new ByteArrayOutputStream();
			oPDF = pcbLoad(); 
			oPDF.open();
			//Stamper para añadir información.
			stamp = new PdfStamper(reader, pdfoutput);
			stamp.getWriter().setCloseStream(false);
			for (int numAuto=0;numAuto<listAutonumericos.size();numAuto++) //Procesamos todos los autonuméricos, que normalmente serán uno solo.
			{
				Node auto=listAutonumericos.get(numAuto);
				Node atrib = auto.getAttributes().getNamedItem("global");
				if(atrib!=null)
				{
					this.numeracion_global = atrib.getNodeValue();
				}
				if(Boolean.valueOf(this.numeracion_global))
				{
					int paginaTotal = this.pagina;
					for(int paginaCont = paginaTotal; paginaCont > 0; paginaCont--)
					{
						procesaAutonumerico(auto,stamp,paginaTotal,paginaCont);
					}
				}
				else
				{
					int numBloques = this.pagBloques.size();
					int numPag = this.pagina;
					for(int nblock = numBloques-1; nblock >= 0; nblock--)
					{
						int paginaTotal = this.pagBloques.get(nblock);
	
						for(int paginaCont = paginaTotal; paginaCont > 0; paginaCont--)
						{
							procesaAutonumerico(auto,stamp,paginaTotal,paginaCont);
							numPag--;
						}
					}
				}
			}
		} catch (IOException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error("Error al procesar autonuméricos:"+e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			es.tributasenasturias.servicios.documentos.log.Logger.trace(e.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
		} catch (DocumentException e) {
			es.tributasenasturias.servicios.documentos.log.Logger.error("Error al procesar autonuméricos:"+e.getMessage(),e,es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			es.tributasenasturias.servicios.documentos.log.Logger.trace(e.getStackTrace(),es.tributasenasturias.servicios.documentos.log.Logger.LOGTYPE.APPLOG);
			
		}
		
		pdfwriter.close();
		if (stamp!=null)
		{
			try {stamp.close();} catch (Exception ex){}
		}
		if (oPDF.isOpen())
		{
			oPDF.close();
		}
		
		
	}

}
