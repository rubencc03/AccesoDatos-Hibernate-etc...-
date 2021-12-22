package ej1;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class AccesMetadDataColums {
	
	public static void main(String[] args) throws SQLException 
	{
		String creates = generateCreates();
		//conectarMariaDB(creates);
		doInserts(generatedInserts());
	}

	private static void conectarMariaDB(String sentencias) throws SQLException 
	{

		Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/starwars?user=star&password=wars");
		DatabaseMetaData dbmd = con.getMetaData();
		Statement sentencia = con.createStatement();
		String[] parts = sentencias.split(";");

		parts[1] = parts[1].replace("2", "");
		parts[0] = parts[0].replace("2", "");
		System.out.println(parts[0] + ";");
		System.out.println(parts[1] + ";");
		
	
		//ResultSet rs2 = sentencia.executeQuery(parts[1] + ";");
		ResultSet rs = sentencia.executeQuery(parts[0] + ";");

		
	}

	private static String generateCreates() {

		String create = "";

		try {
			//Conexión con la querida BD
			Connection con = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\ruben\\Desktop\\sql\\sqlite-tools-win32-x86-3360000\\Ejer1.db");	

			//Cogemos los Metadatos que pide el ejercicio
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet datosTablas = dbmd.getTables(null, "%", null, null);

			//Mostramos los metadatos de la tabla
			while (datosTablas.next()) {
				//Empezamos a escribir el create
				create += "CREATE TABLE ";

				//Sacamos el nombre de la tabla
				String nomTbl = datosTablas.getString("TABLE_NAME");
				//Incluimos el nombre de la tabla en el create mas el resto de texto necesario
				create += nomTbl + " (";

				//Conseguimos la información de las columnas
				ResultSet columnas = dbmd.getColumns(null, "%", nomTbl, null);
				while (columnas.next()) {
					//Asignamos el nombre de la columna en una variable
					String nCol = columnas.getString("COLUMN_NAME");
					//Asignamos el tipo de la columna en una variable
					String tipoColumna = columnas.getString("TYPE_NAME");

					//Utilizamos la informacion recaudada para seguir con el create
					create += nCol + " " + tipoColumna + ", ";
				}

				//Al final sobra la coma ,la quitamos con un substr
				create = create.substring(0, create.length() - 1); 
				
				//Texto necesario para la primary key
				create += " PRIMARY KEY (";

				//Sacamos toda la información relacionada conlas claves primarias
				ResultSet pk = dbmd.getPrimaryKeys(datosTablas.getString("TABLE_CAT"),datosTablas.getString("TABLE_SCHEM"), datosTablas.getString("TABLE_NAME"));
				String primaryK = "";
				
				while (pk.next()) {
					//Sacamos el nombre de la columna que es PK
					primaryK += pk.getString("COLUMN_NAME") + ", ";
				}
				//Añadimos las pk al create
				create += primaryK;
				//Eliminamos otra vez lo que sobra
				create = create.substring(0, create.length() - 2); 
				//Continuamos escribiendo el create
				create += "), ";
				
				//Sacamos la información relacionada con las claves ajenas
				ResultSet fk = dbmd.getImportedKeys(datosTablas.getString("TABLE_CAT"),datosTablas.getString("TABLE_SCHEM"), datosTablas.getString("TABLE_NAME"));

				//Mientras haya informacion sobre las fk
				while (fk.next()) {
					//Sacamos el nombre de la clave fk
					String col = fk.getString("FKCOLUMN_NAME");
					//El nombre de la tabla de donde viene
					String table = fk.getString("PKTABLE_NAME");
					//La columna de la que es pk
					String colTable = fk.getString("PKCOLUMN_NAME");

					//Añadimos los datos recaudados al create
					create += " foreign key(" + col + ") references " + table+ "(" + colTable + "), ";
				}

				//Otra vez quitamos la basura sobrante
				create = create.substring(0, create.length() - 2); 
				//Cerramos todo
				create += ");";

				//Salto de linea para el createde la siguiente tabla
				create += "\n";
			}

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return create;
	}
	
	
	private static String generatedInserts() throws SQLException {
	 String insert="insert into Mascota values (";
		Connection con = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\ruben\\Desktop\\sql\\sqlite-tools-win32-x86-3360000\\Ejer1.db");	

		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet datosTablas = dbmd.getTables(null, "%", null, null);
		String sql = "SELECT * FROM Mascota";
		
		Statement sentencia = con.createStatement();
		ResultSet rs = sentencia.executeQuery(sql);
		
		while(rs.next()) {
		//	insert into Alumnos values (000000001,37,'Miguel',963720895,18);
			
			insert+=rs.getInt(1) + ",'" + rs.getString(2) + "','" + rs.getString(3) + "'," + rs.getString(4) + ");\n";
			
			
		
		}
		
		String sql2 = "SELECT * FROM Propietario";
		
		Statement sentencia2 = con.createStatement();
		ResultSet rs2 = sentencia.executeQuery(sql2);

		
		
		
		 insert+="insert into Propietario values (";

		while(rs2.next()) {
			//Insert into sesiones values(1, 1, to_date('22/03/2021 16:00','dd/mm/yyyy hh24:mi'), 'S1', 4.30,null);
			//insert into Propietario values (1,Jose Martinez,Desarrollador software,01/01/1980);
				insert+=rs.getInt(1) + ",'" + rs.getString(2) + "','" + rs.getString(3) + "','" + rs.getString(4) + "'   );\n";
				
				
			
			}
		
		System.out.println(insert);

		return insert;
		
		
	}


	private static void doInserts(String sentencias) throws SQLException{
		
		Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/starwars?user=star&password=wars");
		DatabaseMetaData dbmd = con.getMetaData();
		Statement sentencia = con.createStatement();
		String[] parts = sentencias.split(";");

		parts[1] = parts[1].replace("2", "");
		parts[0] = parts[0].replace("2", "");
		
		System.out.println(parts[0] + ";");
		System.out.println(parts[1] + ";");
		
		//No se por que,al realizar el instert de la fecha peta,lo he probado con el to_date( pero no me dice desde el sql de localhost que no existe,luego lo probe
		//Tal cual como ahora y fue,adjunto prueba en el repositorio github
	
		ResultSet rs2 = sentencia.executeQuery(parts[1] + ";");
		ResultSet rs = sentencia.executeQuery(parts[0] + ";");

	}
}
