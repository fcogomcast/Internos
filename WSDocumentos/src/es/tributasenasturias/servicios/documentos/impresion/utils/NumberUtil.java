package es.tributasenasturias.servicios.documentos.impresion.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import org.apache.commons.lang.StringUtils;

public class NumberUtil {

	// SOBRE RECUPERAR UN STRING EN BASE A UN DOUBLE, USANDO EL LOCALE
	// INTERNO

	/**
	 * Devolvera el double como un String FORMATEADO usando el sistema numerico
	 * regional obtenido del locale interno.
	 */
	public static String getDoubleAsFormatString(double numeroAFormatear) {
		DecimalFormat formateador = new DecimalFormat();
		return formateador.format(numeroAFormatear);
	}

	/**
	 * Devolvera el double como un String FORMATEADO usando el sistema numerico
	 * regional, pero ademas se le indica el redondeo(dado por el parametro:
	 * precisionDecimal)
	 */
	public static String getDoubleAsFormatString(double numeroAFormatear, int precisionDecimal, boolean isCompletarCerosParteDecimal) {
		String formateado = "";
		double numeroRedondeado = Math.round(numeroAFormatear * Math.pow(10, precisionDecimal)) / Math.pow(10, precisionDecimal);
		DecimalFormat formateador = new DecimalFormat();
		if (!isCompletarCerosParteDecimal) {
			formateado = formateador.format(numeroRedondeado);
		} else {
			String patron = "##,##0";
			if (precisionDecimal > 0) {
				patron = patron + "." + StringUtils.rightPad("", precisionDecimal, '0');
			}
			formateador = new DecimalFormat(patron);
			formateado = formateador.format(numeroRedondeado);
		}
		return formateado;
	}

	public static String getDoubleAsFormatString(String numeroAFormatearAsString, int precisionDecimal, boolean isCompletarCerosParteDecimal) {
		String formateado = "";
		if (StringUtils.isNotBlank(numeroAFormatearAsString) && isNumberValid(numeroAFormatearAsString)) {
			Double numeroAsDouble = getDoubleFromString(numeroAFormatearAsString);
			formateado = getDoubleAsFormatString(numeroAsDouble, precisionDecimal, isCompletarCerosParteDecimal);
		}
		return formateado;
	}

	/**
	 * Devolvera el double como un String sin formatear, es decir solo contendra
	 * el simbolo indicador de decimales si fuera el caso, se esta usando el
	 * sistema numerico regional obtenido del locale interno
	 */
	public static String getDoubleAsString(double numeroAFormatear) {
		DecimalFormat formateador = new DecimalFormat();
		formateador.setGroupingSize(0);
		return formateador.format(numeroAFormatear);
	}
	
	

	/**
	 * Devolvera el int como un String formateando sus miles ,usando el sistema
	 * numerico regional
	 */
	public static String getIntAsFormatString(int numeroAFormatear) {
		DecimalFormat formateador = new DecimalFormat("###,###");
		return formateador.format(numeroAFormatear);
	}

	// SOBRE RECUPERAR UN NUMERO EN BASE A UN STRING, USANDO EL LOCALE
	// INTERNO

	public static boolean isNumberValid(String numeroAEvaluar) {
		boolean isValid = true;
		NumberFormat parseador = NumberFormat.getInstance();
		if (StringUtils.isBlank(numeroAEvaluar)) {
			isValid = false;
		} else {
			if (!validarGruposMiles(numeroAEvaluar)) {
				isValid = false;
			} else {
				ParsePosition posicionador = new ParsePosition(0);
				parseador.parse(numeroAEvaluar, posicionador);
				if ((posicionador.getErrorIndex() > -1) || (posicionador.getIndex() < numeroAEvaluar.length())) {
					isValid= false;
				}
			}
		}
		return isValid;
	}

	/**
	 * Este metodo es necesario ya que NumberFormat de java tiene un bug, ya que
	 * NO ES ESTRICTO a la hora de parsear.Por eso se tiene que verificar si los
	 * miles estan agrupados de tres en tres o simplemente no estan agrupados
	 * 
	 */
	private static boolean validarGruposMiles(String numeroAEvaluar) {
		DecimalFormatSymbols simbolos = new java.text.DecimalFormatSymbols();
		char charSimboloMiles = simbolos.getGroupingSeparator();
		String simboloMiles = String.valueOf(charSimboloMiles);
		char charSimboloDecimales = simbolos.getDecimalSeparator();
		String simboloDecimales = String.valueOf(charSimboloDecimales);
		if (StringUtils.contains(numeroAEvaluar, simboloMiles)) {
			// tengo que validar que a la derecha de cada simboloMiles
			// exista tres digitos.
			while (numeroAEvaluar.length() > 0) {
				int posicionSimboloMiles = StringUtils.indexOf(numeroAEvaluar, simboloMiles);
				if (posicionSimboloMiles > 0) {
					String posibleGrupoMiles = StringUtils.mid(numeroAEvaluar, posicionSimboloMiles + 1, 3);
					if (StringUtils.contains(posibleGrupoMiles, simboloMiles)) {
						return false;
					} else {
						numeroAEvaluar = StringUtils.substringAfter(numeroAEvaluar, simboloMiles);
					}
				} else {
					numeroAEvaluar = StringUtils.substringBefore(numeroAEvaluar, simboloDecimales);
					if (numeroAEvaluar.length() != 3) {
						return false;
					}
					return true;
				}
			}
		}
		return true;
	}

	public static boolean isInteger(String numero) {
		Integer numeroEntero = getIntegerFromString(numero);
		if (numeroEntero == null) {
			return false;
		}
		return true;
	}

	public static boolean isNotInt(String numero) {
		return !isInteger(numero);
	}

	public static boolean isNotDouble(String numero) {
		return !isDouble(numero);
	}

	public static boolean isDouble(String numero) {
		Double numeroDouble = getDoubleFromString(numero);
		if (numeroDouble == null) {
			return false;
		}
		return true;
	}

	public static boolean isBaseDouble(String numero) {
		if(numero==null || "".equals(numero))
			return false;
		Double numeroDouble = getDoubleFromBaseString(numero);
		if (numeroDouble == null) {
			return false;
		}
		return true;
	}

	public static Integer getIntegerFromString(String numeroAsString) {
		Integer numeroDevuelto = null;
		DecimalFormatSymbols simbolos = new java.text.DecimalFormatSymbols();
		char charSimboloDecimales = simbolos.getDecimalSeparator();
		if (StringUtils.isNotBlank(numeroAsString) && isNumberValid(numeroAsString)) {
			// es un numero valido, pero necesitamos saber si tiene parte
			// decimal
			if (!StringUtils.contains(numeroAsString, charSimboloDecimales)) {
				NumberFormat parseador = NumberFormat.getInstance();
				try {
					Number numero = parseador.parse(numeroAsString);
					numeroDevuelto = numero.intValue();
				} catch (ParseException e) {
					return null;
				}
			}
		}
		return numeroDevuelto;
	}

	/**
	 * Este metodo se mantuvo para no afectar a las demas clases que lo usaban
	 */
	public static Integer getIntFromString(String numeroAsString) {
		return getIntegerFromString(numeroAsString);
	}

	/**
	 * Este metodo se mantuvo para no afectar a las demas clases que lo usaban
	 */
	public static boolean isInt(String numero) {
		return isInteger(numero);
	}

	public static Double getDoubleFromString(String numeroAsString) {
		Double numeroDevuelto = null;
		if (StringUtils.isNotBlank(numeroAsString) && isNumberValid(numeroAsString)) {
			NumberFormat parseador = NumberFormat.getInstance();
			try {
				Number numero = parseador.parse(numeroAsString);
				numeroDevuelto = numero.doubleValue();
			} catch (ParseException e) {
				return null;
			}
		}
		return numeroDevuelto;
	}

	public static int cantidadDigitosDecimales(Double numero) {
		String numeroAsString = getDoubleAsString(numero);
		return cantidadDigitosDecimales(numeroAsString);
	}

	private static int cantidadDigitosDecimales(String numero) {
		DecimalFormatSymbols simbolos = new java.text.DecimalFormatSymbols();
		char charSimboloDecimales = simbolos.getDecimalSeparator();
		String simboloDecimales = String.valueOf(charSimboloDecimales);
		String parteDecimal = StringUtils.substringAfter(numero, simboloDecimales);
		if (StringUtils.isBlank(parteDecimal)) {
			return 0;
		}
		return parteDecimal.length();
	}
	
	public static int cantidadDigitosEnteros(Double numero) {
		String numeroAsString = getDoubleAsString(numero);
		return cantidadDigitosEnteros(numeroAsString);
	}
	
	private static int cantidadDigitosEnteros(String numero) {
		DecimalFormatSymbols simbolos = new java.text.DecimalFormatSymbols();
		char charSimboloDecimales = simbolos.getDecimalSeparator();
		String simboloDecimales = String.valueOf(charSimboloDecimales);
		String parteEntera = StringUtils.substringBefore(numero, simboloDecimales);
		if (StringUtils.isBlank(parteEntera)) {
			return 0;
		}
		return parteEntera.length();
	}

	// METODOS PARA RECUPERAR UN NUMERO EN BASE A UN STRING.
	// ESTA USANDO EL SISTEMA EUROPEO POR HARDCODE, NO OBTENIDO POR LOCALE

	// recoge el formato Java SI no utiliza el formato europeo (con comas)
	public static double getDoubleFromBaseString(String numeroAsString) {
		if (numeroAsString == null)
			return getDoubleFromString(numeroAsString);
		int pos = numeroAsString.indexOf(",");
		// si tiene varios ',' indican los miles
		// si tiene un ',' esta en formato europeo
		if (pos != -1 && numeroAsString.lastIndexOf(",") != pos)
			numeroAsString = StringUtils.replace(numeroAsString, ",", "");
		else if (pos != -1)
			return getDoubleFromString(numeroAsString);

		pos = numeroAsString.indexOf(".");
		// si tiene varios '.' indican los miles o un exceso de decimales
		if (pos != -1 && numeroAsString.lastIndexOf(".") != pos)
			try {
				return getDoubleFromString(numeroAsString);
			} catch (NullPointerException e) {
				return 0;
			}
		try {
			return Double.parseDouble(numeroAsString);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static String redondearDecimales(String numeroARedondear, int precisionDecimal, boolean isCompletarCerosParteDecimal) {
		String redondeo = numeroARedondear;
		if (numeroARedondear != null) {
			if (numeroARedondear.indexOf(",") > 0) {
				numeroARedondear = numeroARedondear.replace(",", ".");
			}
			try {
				Double dARedondear = Double.parseDouble(numeroARedondear);
				redondeo = getDoubleAsFormatString(dARedondear, precisionDecimal, isCompletarCerosParteDecimal);
			} catch (NumberFormatException nfe) {
				redondeo = numeroARedondear;
			}
		}
		return redondeo;
	}

	@Deprecated
	public static Double getStringAsDouble(String sNumero) {
		Double numero = null;
		if (sNumero != null) {
			if (sNumero.indexOf(",") > 0) {
				sNumero = sNumero.replace(",", ".");
			}
			try {
				numero = Double.parseDouble(sNumero);
			} catch (NumberFormatException nfe) {
				numero = null;
			}
		}
		return numero;
	}
	
	public static boolean isNumberZero(String sNumero){
		Double numero = null;
		if (sNumero != null) {
			if (sNumero.indexOf(",") > 0) {
				sNumero = sNumero.replace(",", ".");
			}
			try {
				numero = Double.parseDouble(sNumero);
			} catch (NumberFormatException nfe) {
				numero = null;
			}
		}
		return (numero != null && numero == 0);
	}
	
	public static String getImporteFormateado(String centimos){
		String euros = "";
						
		double dcent = getDoubleFromBaseString(centimos);
		euros = getDoubleAsFormatString(dcent/100);
		
		return euros;
	}


	/****************************************************************************************************/
	/****		funciones traducidas de euro.inc												*********/
	/****************************************************************************************************/
	public static String Redondear(String importe, Integer decimales)
	{
		
		Integer intValor;
		Double exponente=Math.pow(10.00,Double.valueOf(decimales));
		Double producto=new Float(importe)*exponente;
		
		String strProducto=producto.toString();
		int coma=strProducto.lastIndexOf(".");
		if (coma!=-1)
		{
			String valorRedondeo=strProducto.substring(coma+1,1);
			Float fltRedondeo = new Float(valorRedondeo);
			Float valor;
			if (fltRedondeo>=5)
				valor=fltRedondeo+1;
			else
				valor=fltRedondeo;
			
			intValor=valor.intValue();
			
		}
		else
			intValor=producto.intValue();
			
		importe=(intValor/exponente)+"";
		
		coma=importe.lastIndexOf(".");
		if (coma==-1)
			importe=importe+".0000000000";
		coma=importe.lastIndexOf(".");
		importe=importe.substring(0,coma+decimales+1);
		return (importe);
	}
	public static String toEuro(String importe){
		return toEuro(importe, null, null);
	}
	
	public static String toEuro(String importe, Integer decimales, Integer formato){
		String importe_f = "";
		String tmp = "";
		String punto = ".";
		String frac="";
		
		/**** Redondea a los decimales que se le pasen como parametro. 	***/
		/**** Por defecto no redondea -> decimales=-1 					***/
		if (decimales != null && decimales!=0)	
			decimales=2;	

		if (formato != null && formato!=0)
			formato=1;
		
		if (importe == null || importe.length() == 0 || "null".equals(importe))
			return "";

		
		String signo="";
		if(importe.startsWith("-"))	//signo negativo
		{
			signo = "-";
			importe = importe.substring(1, importe.length());
		}
		
		/*** Pasamos centimos de Euro a Euros ***/
		Integer operacionImporte = new Integer(importe)/100;
		importe = operacionImporte.toString();
		
		int coma=importe.lastIndexOf(".");
		
		/*** SOLO PARA CUNDO TRABAJAMOS EN PESETAS. Ponemos decimales para indicar 	***/
		/*** que se ha cambiado el codigo para adaptarlo al euro. Si no se le 		***/
		/*** nada por defecto metera dos ceros.										***/
		if(coma==-1)
		{
			importe=importe+".0000000000";
			coma=importe.lastIndexOf(".");
			if (decimales==-1)
				importe=importe.substring(0,coma+3);
			else
				importe=importe.substring(0,coma+decimales+1);
			
		}
		
		if (decimales!=-1)
			importe=Redondear(importe,decimales)+"";
			
		coma=importe.lastIndexOf(".");
		if (decimales==0)
		{
			frac="";
			importe=importe.substring(0,coma);
		}
		
		else
		{
			/**** Ponemos ceros por la derecha si es necesario ***/
			frac="," + importe.substring(coma+1,importe.length());
			for (int i=frac.length();i<decimales+1;i++)
				frac=frac+"0";
			/*** Caso en que solo tenemos un decimal. Lo mostraremos con dos decimales ***/
			if (frac.length()<3 && decimales==-1)
				frac=frac+"0";	
			importe=importe.substring(0,coma);
		}
		
		
		
		if (formato==1)
		{
		
			Integer longitud = importe.length();
			Integer resto = longitud % 3;
			if ("null".equals(importe)  || longitud == 0)
				return signo + importe + frac;
			else
			{
				if (importe.length()==3 || importe.length() < 3)
					return signo + importe + frac;
				else
				{
					if (resto != 0)
					{
						importe_f = importe.substring(0 ,resto);
						importe_f = importe_f.concat(punto);
					}
					Integer cociente = (longitud - resto) / 3;
					Integer pos_ini = resto;
					for (int i = 0; i < cociente  ; i++)
					{
						tmp = importe.substring(pos_ini ,pos_ini + 3);
						if (i != 0)
						{
							tmp = punto.concat(tmp);
							importe_f = importe_f.concat(tmp);
						}
						else
						{
							importe_f = importe_f.concat(tmp);
						}
						pos_ini = pos_ini + 3 ;
					}
					return signo + importe_f + frac;
				}
			}
		}else
			return signo + importe + frac;

	}
}
