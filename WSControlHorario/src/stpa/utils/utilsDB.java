package stpa.utils;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;

import stpa.utils.exception.ControlHorarioException;
import stpa.utils.ControlHorario.preferencias.Preferencias;

public class utilsDB {

	/*
	 * dentro del proyecto WSInternos, tendría un único método
(por ejemplo 'saldoAcumulado') que ejecutará en un datasource (por ejemplo
'specDS') la siguiente select, donde los parámetros de entrada del método
serán la fecha y el usuario y los valores 35 y 1 serían valores fijos
(aunque mejor tenerlos en las preferencias) y devolverá un único valor
numérico.

	 * 
	 * 
	 */
	
	private static String saldoAcumuladoQuery="SELECT VC_VALORH FROM VALOR_CONTADOR V, USUARIO U " +
											  "WHERE V.EM_ID = U.EM_ID AND V.CN_ID = ? AND V.CN_ACUMULADOR = ? " +
											  "AND V.VC_FECHA = TO_CHAR(SYSDATE,'YYYYMMDD') AND UPPER(U.US_USER) = ? ";
	
	/**
	 * Acesso a datos para saldo acumulado
	 * @param fecha
	 * @param usuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BigInteger saldoAcumuladoQuery (String usuario) throws ControlHorarioException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultado = null;
		FileOutputStream myFO = null;
		BigInteger saldoAcumulado = null;
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

			ps = conn.prepareStatement(saldoAcumuladoQuery);
			ps.setString(1, _pr.getPreferencia1());
			ps.setString(2, _pr.getPreferencia2());
			ps.setString(3, usuario.toUpperCase());
			
			//Ejecutar procedimiento almacenado en base de datos
			ps.execute();

			//Obtener parametro de procedimiento almacenado de salida 
			resultado=ps.getResultSet();
			int saldoTemp=0;
			if (resultado.next()) {
				// Enviar resultado de parametro de salida como respuesta al WebService
				saldoTemp=resultado.getInt(1);
			}
			saldoAcumulado = new BigInteger(String.valueOf(saldoTemp));
		}catch (Exception e) {
			throw new ControlHorarioException("Se ha producido un error en la consulta: "+ e.getMessage(),e);
		}finally{
				if (ps != null) {
					try {ps.close();} catch (Exception e) {}
				}
				if (conn != null) {
					try {conn.close();} catch (Exception e) {}
				}
				if (myFO != null) {
					try {myFO.close();} catch (Exception e) {}
				}
				
		}
		return saldoAcumulado;
	}
}