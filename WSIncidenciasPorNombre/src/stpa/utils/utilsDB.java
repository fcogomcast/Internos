package stpa.utils;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import stpa.services.IncidenciasPorNombre.EstadoInfo;
import stpa.services.IncidenciasPorNombre.IncidenciaInfoType;
import stpa.services.IncidenciasPorNombre.ObjectFactory;
import stpa.utils.ConsultaIncidencias.preferencias.Preferencias;
import stpa.utils.exception.IncidenciasNombreException;

public class utilsDB {

	/*
	 * 
	 * El segundo servicio, podemos llamarlo 'eventum' dentro del proyecto
WSInternos también. Tendrá un método (por ej. incidenciasPorNombre), la
select la haríamos sobre el data source 'eventumDS', sería la siguiente
select, que recibe un parámetro que será el nombre y haremos la consulta
(en mayúsculas). El otro dato es constante (1) y el servicio devolverá 
los datos de la select, que se corresponden con las siguientes
descripciones (número, título, descripción, fechaAlta, fechaCierre,
estado).
	 * 
	 * 
	 */
	

	private static String incidenciasNombreQuery="SELECT i.iss_id,i.iss_summary, i.iss_created_date, i.iss_closed_date,s.sta_title,s.sta_id "+
											  "FROM eventum_issue i, eventum_issue_custom_field c, eventum_status s "+
											  "WHERE i.iss_id = c.icf_iss_id "+
											  "AND i.iss_sta_id = s.sta_id "+
											  "AND c.icf_fld_id = ? "+
											  "AND UPPER(icf_value) = ? ";
	
	private static String condicionIdEstado="AND s.sta_id = ? ";	
	private static String orden = " ORDER  BY i.iss_created_date DESC";
	
	//private static String listaEstadosQuery = "SELECT s.sta_id, s.sta_title FROM eventum_status s ORDER BY s.sta_title";
	
	private static String detalleIncidenciaQuery="SELECT i.iss_id,i.iss_summary, i.iss_description, i.iss_created_date, i.iss_closed_date,s.sta_title,s.sta_id "+
											  "FROM eventum_issue i, eventum_issue_custom_field c, eventum_status s "+
											  "WHERE i.iss_id = c.icf_iss_id "+
											  "AND i.iss_sta_id = s.sta_id "+
											  "AND c.icf_fld_id = ? "+
											  "AND i.iss_id = ? ";

	/**
	 * Acesso a datos para incidencias por nombre
	 * @param fecha
	 * @param usuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<IncidenciaInfoType> incidenciasPorNombreQuery (String nombre, String idEstado) throws IncidenciasNombreException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultado = null;
		FileOutputStream myFO = null;
		IncidenciaInfoType fila = null;
		ArrayList<IncidenciaInfoType> incidencias= new ArrayList<IncidenciaInfoType>();
		try {
			Preferencias _pr=Preferencias.getPreferencias();

			//Obtener conexion de base de datos
			Hashtable ht = new Hashtable();
			ht.put( Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory" );
			ht.put( Context.PROVIDER_URL,_pr.getProviderURL());
			Context ctx=null;
			try {
				ctx = new InitialContext( ht );
			}catch (CommunicationException ex){
				throw new Exception(" Preferencia ProviderURL puede ser no valida");
			}
			if (ctx==null)
				throw new Exception("No se ha podido crear el contexto de conexión al PROVIDER_URL");
			javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup(_pr.getDataSource());
			conn = ds.getConnection();
			
			String query = incidenciasNombreQuery;
			if (idEstado != null && !"".equals(idEstado))
				query += condicionIdEstado;
			
			//El orden siempre descendente
			query += orden;
			
			ps = conn.prepareStatement(query);
			ps.setString(1, _pr.getPreferencia1());
			ps.setString(2, nombre);
			if (idEstado != null && !"".equals(idEstado))
				ps.setInt(3,Integer.valueOf(idEstado));
			
			//Ejecutar procedimiento almacenado en base de datos
			ps.execute();

			//Obtener parametro de procedimiento almacenado de salida 
			resultado=ps.getResultSet();
			while (resultado.next()) {
				fila=new ObjectFactory().createIncidenciaInfoType();
				fila.setNumero(resultado.getString(1));
				fila.setTitulo(resultado.getString(2));
				fila.setFechaAlta(resultado.getString(3));
				fila.setFechaCierre(resultado.getString(4));
				fila.setEstado(resultado.getString(5));
				fila.setIdEstado(resultado.getString(6));
				incidencias.add(fila);
			}
		}catch (NumberFormatException e)
		{
			throw new IncidenciasNombreException ("Error al convertir el id de estado a un número.");
		}
		catch (Exception e) {
			throw new IncidenciasNombreException("Se ha producido un error en la consulta de incidencias por nombre: "+ e.getMessage(),e);
		}finally{
				if (ps != null) {
					try {ps.close();} catch (Exception e) {}
				}
				if (resultado!=null)
				{
					try{resultado.close(); } catch (Exception e){}
				}
				if (conn != null) {
					try {conn.close();} catch (Exception e) {}
				}
				if (myFO != null) {
					try {myFO.close();} catch (Exception e) {}
				}
				
		}
		return incidencias;
	}
	/**
	 * Genera una lista de los estados de la incidencias en Eventum.
	 * @return Lista {@link ArrayList} de {@link EstadoInfo} con los estados de las incidencias.
	 * @throws IncidenciasNombreException En caso de producirse un error.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<EstadoInfo> recuperaListaEstados () throws IncidenciasNombreException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultado = null;
		FileOutputStream myFO = null;
		EstadoInfo fila = null;
		ArrayList<EstadoInfo> estados=new ArrayList<EstadoInfo>();
		try {
			Preferencias _pr=Preferencias.getPreferencias();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new java.io.File(_pr.getNombreFicheroEstados()));
			javax.xml.xpath.XPath xpath=javax.xml.xpath.XPathFactory.newInstance().newXPath();
			NodeList nodos = (NodeList) xpath.evaluate("/estados/estado",
					doc, javax.xml.xpath.XPathConstants.NODESET); //Sólo debería haber uno, y sólo haremos caso a uno
			for (int i=0;i<nodos.getLength();i++)
			{
				fila=new ObjectFactory().createEstadoInfo();
				fila.setId(xpath.evaluate("sta_id", nodos.item(i)));
				fila.setNombre(xpath.evaluate("sta_title", nodos.item(i)));
				estados.add(fila);
			}
		}catch (Exception e) {
			throw new IncidenciasNombreException("Se ha producido un error en la consulta de estados: "+ e.getMessage(),e);
		}finally{
				if (ps != null) {
					try {ps.close();} catch (Exception e) {}
				}
				if (resultado!=null)
				{
					try{resultado.close(); } catch (Exception e){}
				}
				if (conn != null) {
					try {conn.close();} catch (Exception e) {}
				}
				if (myFO != null) {
					try {myFO.close();} catch (Exception e) {}
				}
				
		}
		return estados;
	}
	
	/**
	 * Devuelve los datos de una incidencia concreta.
	 * @param idIncidencia Id de la incidencia cuyos datos se quieren devolver.
	 * @return {@link IncidenciaInfoType}
	 * @throws IncidenciasNombreException En caso de fallar algo al recuperar la incidencia
	 */
	@SuppressWarnings("unchecked")
	public static IncidenciaInfoType detalleIncidencia (String idIncidencia) throws IncidenciasNombreException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultado = null;
		FileOutputStream myFO = null;
		IncidenciaInfoType incidencia = null;
		try {
			Preferencias _pr=Preferencias.getPreferencias();

			//Obtener conexion de base de datos
			Hashtable ht = new Hashtable();
			ht.put( Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory" );
			ht.put( Context.PROVIDER_URL,_pr.getProviderURL());
			Context ctx=null;
			try {
				ctx = new InitialContext( ht );
			}catch (CommunicationException ex){
				throw new Exception(" Preferencia ProviderURL puede ser no valida");
			}
			if (ctx==null)
				throw new Exception("No se ha podido crear el contexto de conexión al PROVIDER_URL");
			javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup(_pr.getDataSource());
			conn = ds.getConnection();
			
			String query = detalleIncidenciaQuery;
			
			ps = conn.prepareStatement(query);
			ps.setString(1, _pr.getPreferencia1());
			ps.setString(2, idIncidencia);
			
			//Ejecutar procedimiento almacenado en base de datos
			ps.execute();

			//Obtener parametro de procedimiento almacenado de salida 
			resultado=ps.getResultSet();
			//Devolvemos sólo una fila, el id de incidencia es clave así que a menos que la 
			//consulta se haya construido mal, no es posible que devuelve más datos.
			if (resultado.next()) {
				incidencia=new ObjectFactory().createIncidenciaInfoType();
				incidencia.setNumero(resultado.getString(1));
				incidencia.setTitulo(resultado.getString(2));
				incidencia.setDescripcion(resultado.getString(3));
				incidencia.setFechaAlta(resultado.getString(4));
				incidencia.setFechaCierre(resultado.getString(5));
				incidencia.setEstado(resultado.getString(6));
				incidencia.setIdEstado(resultado.getString(7));
			}
		}catch (Exception e) {
			throw new IncidenciasNombreException("Se ha producido un error en la consulta de detalle de la incidencia: "+ e.getMessage(),e);
		}finally{
				if (ps != null) {
					try {ps.close();} catch (Exception e) {}
				}
				if (resultado!=null)
				{
					try{resultado.close(); } catch (Exception e){}
				}
				if (conn != null) {
					try {conn.close();} catch (Exception e) {}
				}
				if (myFO != null) {
					try {myFO.close();} catch (Exception e) {}
				}
				
		}
		return incidencia;
	}
}