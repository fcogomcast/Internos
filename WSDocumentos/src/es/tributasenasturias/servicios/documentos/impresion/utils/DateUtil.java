package es.tributasenasturias.servicios.documentos.impresion.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	// Esta constante es usada como valor por defecto en algunos portlets
	public static final String FORMATO_RECUPERADO_FORMULARIOS_WEB = "dd/MM/yyyy";

	public static String getFechaAsFormatString(String fechaUHoraAFormatear, String formatoEntrada, String formatoDeSalida) {
		SimpleDateFormat formateador = new SimpleDateFormat(formatoEntrada);
		formateador.setLenient(false);
		String fechaUHoraFormateada = null;
		try {
			Date fechaUHora = formateador.parse(fechaUHoraAFormatear);
			formateador.applyPattern(formatoDeSalida);
			fechaUHoraFormateada = formateador.format(fechaUHora);
		} catch (Exception e) {
			// No se pudo parsear a un Date.Ese string no representa una fecha.
			fechaUHoraFormateada = null;
		}
		return fechaUHoraFormateada;
	}

	public static Date getFechaUHoraAsDate(String fechaUHoraAsString, String formatoEntradaFechaUHora) {
		SimpleDateFormat formateador = new SimpleDateFormat(formatoEntradaFechaUHora);
		formateador.setLenient(false);
		Date fechaUHoraAsDate = null;
		try {
			fechaUHoraAsDate = formateador.parse(fechaUHoraAsString);
		} catch (Exception e) {
			// No se pudo parsear a un Date.Ese string no representa una fecha.
			fechaUHoraAsDate = null;
		}
		return fechaUHoraAsDate;
	}

	public static Date getFechaUHoraAsDate(String fechaUHoraAsString) {
		SimpleDateFormat formateador = new SimpleDateFormat(FORMATO_RECUPERADO_FORMULARIOS_WEB);
		formateador.setLenient(false);
		Date fechaUHoraAsDate = null;
		try {
			fechaUHoraAsDate = formateador.parse(fechaUHoraAsString);
		} catch (Exception e) {
			// No se pudo parsear a un Date.Ese string no representa una fecha.
			fechaUHoraAsDate = null;
		}
		return fechaUHoraAsDate;
	}

	public static Calendar getFechaUHoraAsCalendar(String fechaUHoraAsString, String formatoEntradaFechaUHora) {
		SimpleDateFormat formateador = new SimpleDateFormat(formatoEntradaFechaUHora);
		formateador.setLenient(false);
		Calendar fechaUHoraAsCalendar;
		try {
			Date fechaUHoraAsDate = formateador.parse(fechaUHoraAsString);
			fechaUHoraAsCalendar = Calendar.getInstance();
			fechaUHoraAsCalendar.setTime(fechaUHoraAsDate);
		} catch (Exception e) {
			// No se pudo parsear a un Date.Ese string no representa una fecha.
			fechaUHoraAsCalendar = null;
		}
		return fechaUHoraAsCalendar;
	}

	public static String getTodayAsString(String formatoSalidaFechaUHora) {
		Date today = new Date();
		return getDateAsFormatString(today, formatoSalidaFechaUHora);
	}

	public static String getDateAsFormatString(Date fechaAsDate, String formatoSalidaFechaUHora) {
		SimpleDateFormat formateador = new SimpleDateFormat(formatoSalidaFechaUHora);
		formateador.setLenient(false);
		return formateador.format(fechaAsDate);
	}

	public static boolean isFecha(String fecha) {
		// Como no le indica el formato de la fecha usamos el valor por defecto
		if (DateUtil.getFechaUHoraAsDate(fecha, DateUtil.FORMATO_RECUPERADO_FORMULARIOS_WEB) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isNotFecha(String fecha) {
		return !isFecha(fecha);
	}

	public static boolean isFechaPasada(String fecha) {
		// Como no le indica el formato de la fecha usamos el valor por defecto
		Date fechaAsDate = DateUtil.getFechaUHoraAsDate(fecha, DateUtil.FORMATO_RECUPERADO_FORMULARIOS_WEB);
		Date today = new Date();
		if (fechaAsDate == null || fechaAsDate.compareTo(today) >= 0)
			return false;
		return true;
	}

	public static boolean isFechaPasadaPresente(String fecha) {
		// Como no le indica el formato de la fecha usamos el valor por defecto
		Date fechaAsDate = DateUtil.getFechaUHoraAsDate(fecha, DateUtil.FORMATO_RECUPERADO_FORMULARIOS_WEB);
		Date today = new Date();
		if (fechaAsDate == null || fechaAsDate.compareTo(today) > 0) {
			return false;
		}
		return true;
	}
}
