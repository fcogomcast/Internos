package es.tributasenasturias.servicios.documentos.impresion.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import es.tributasenasturias.servicios.documentos.exceptions.DocumentoException;

public class CompresionDocumento {
	public final static String comprimir(String documento) throws DocumentoException
	{
		byte[] docDecodificado = Base64.decode(documento.toCharArray());
		ByteArrayOutputStream resulByteArray = new ByteArrayOutputStream();
		try {
			resulByteArray.write(docDecodificado);
			return PdfComprimidoUtils
			.comprimirPDF(resulByteArray);
		} catch (IOException e) {
			throw new DocumentoException("Error al comprimir el documento:"+ e.getMessage());
		} catch (Exception e) {
			throw new DocumentoException("Error al comprimir el documento:"+ e.getMessage());
		}
		
	}
}
