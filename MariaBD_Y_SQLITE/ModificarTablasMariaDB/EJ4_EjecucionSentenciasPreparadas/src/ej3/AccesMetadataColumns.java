package ej3;

import java.sql.*;
import java.util.Scanner;

public class AccesMetadataColumns {
	static Scanner es = new Scanner(System.in);
	PreparedStatement psInsertar = null;

	public static void main(String[] args) throws SQLException {

		Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/starwars?user=star&password=wars");
		DatabaseMetaData dbmd = con.getMetaData();

		insertarPJ(con);

	}

	public static void informacionPlanet(Connection con) {

		int valor1, valor2; 

		for (int i = 0; i < 3; i++) {

			System.out.println("Dime el diametro 1: ");
			valor1 = es.nextInt();
			System.out.println("Dime el diametro 2: ");
			valor2 = es.nextInt();

			String consulta = "SELECT * FROM planets p WHERE p.diameter between ? and ?; ";
			try {
				PreparedStatement sentencia = con.prepareStatement(consulta);
				sentencia.setInt(1, valor1);
				sentencia.setInt(2, valor2);
				ResultSet rs = sentencia.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();

				// Número de columnas
				int numeroColumnas = rsmd.getColumnCount();

				// Mostramos cada línea
				while (rs.next()) {
					System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
					System.out.println( "ID: "+rs.getInt(1) + " Name: " + rs.getString(2) + " rotation_period: "+ rs.getInt(3)+ " orbital period: "+ rs.getInt(4)+ " diameter: " +rs.getInt(5) +" climate: "+ rs.getString(6)+ " gravity: "+rs.getString(7)+" terrain: " +rs.getString(8) +" population: "+rs.getInt(9)+" surfate_water: "+rs.getLong(10)+ " creation_data: "+ rs.getDate( 11) +" update_data: "+rs.getDate(12) +" url:" +rs.getString(13)  ) ;
					System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
					System.out.println();
				}

				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void insertarPJ(Connection con) throws SQLException {
		//Consulta encargardada de sacar los valores que nos piden
		String consulta = "INSERT INTO characters values(?, ? ,? ,? ,? ,? ,? ,? ,? ,? ,sysdate() ,sysdate() ,null)";
		
		//PreparedStatment que sustituiremos
		PreparedStatement preparedsentencia = con.prepareStatement(consulta);
		
		//Consulta encargada de saber el último id de la base de datos actual
		String consultaUltimaId = "SELECT max(id) from characters";
		//Creamos el statement
		Statement sentencia = con.createStatement();
		//Creamos el ResultSet que ejecuta la sentencia
		ResultSet ultimaID = sentencia.executeQuery(consultaUltimaId);
		//Obtenemos la metadata
		ResultSetMetaData rsmdID = ultimaID.getMetaData();
		//Creamos una variable para en un futuro guardar la ID
		int maxID = 0;
		//Creamos un bucle que recorra la sentencia
		while(ultimaID.next()) {
			//Y guardamos en la variable maxID la ultima id 
			for (int a = 1; a <= rsmdID.getColumnCount(); a++) {
		        maxID = Integer.parseInt(ultimaID.getString(a));
		    }
		}
		//Obtenemos el ID de los planetas 
		String obtenerIDPlanetas = "SELECT id FROM planets WHERE name = ? ";
		
		//Creamos el prepared statment
		PreparedStatement preparedSentencia3 = con.prepareStatement(obtenerIDPlanetas);
		
		//Creamos el array de int donde guardaremos el id de los planetas
		int[] planetas = new int[3];
		
		//Le asignamos el valor Jakku al ?
		preparedSentencia3.setString(1, "Jakku");
		//Ejecutamos la sentencia
		ResultSet p1 = preparedSentencia3.executeQuery();
		//Obtenemos la metadata
		ResultSetMetaData rsmdP1 = p1.getMetaData();
		while(p1.next()) {
			//Guardamos el id en el planeta
			for (int a = 1; a <= rsmdP1.getColumnCount(); a++) {
		        planetas[0] = Integer.parseInt(p1.getString(a));
		    }
		}
		preparedSentencia3.setString(1, "Kamino");
		ResultSet p2 = preparedSentencia3.executeQuery();
		ResultSetMetaData rsmdP2 = p2.getMetaData();
		while(p2.next()) {
			for (int a = 1; a <= rsmdP2.getColumnCount(); a++) {
		        planetas[1] = Integer.parseInt(p2.getString(a));
		    }
		}
		preparedSentencia3.setString(1, "Chandrila");
		ResultSet p3 = preparedSentencia3.executeQuery();
		ResultSetMetaData rsmdP3 = p3.getMetaData();
		while(p3.next()) {
			for (int a = 1; a <= rsmdP3.getColumnCount(); a++) {
		        planetas[2] = Integer.parseInt(p3.getString(a));
		    }
		}
		
			//Insertamos los datos del personaje 1
			maxID++;
			preparedsentencia.setInt(1, maxID);
			preparedsentencia.setString(2, "Rey");
			preparedsentencia.setInt(3, 170);
			preparedsentencia.setInt(4, 54);
			preparedsentencia.setString(5, "Black");
			preparedsentencia.setString(6, "White");
			preparedsentencia.setString(7, "Brown");
			preparedsentencia.setString(8, "15DBY");
			preparedsentencia.setString(9, "Female");
			preparedsentencia.setInt(10, planetas[0]);
			preparedsentencia.executeQuery();
		
			//Insertamos los datos del personaje 2

			maxID++;
			preparedsentencia.setInt(1, maxID);
			preparedsentencia.setString(2, "Finn");
			preparedsentencia.setInt(3, 178);
			preparedsentencia.setInt(4, 73);
			preparedsentencia.setString(5, "Black");
			preparedsentencia.setString(6, "Dark");
			preparedsentencia.setString(7, "Dark");
			preparedsentencia.setString(8, "11DBY");
			preparedsentencia.setString(9, "Male");
			preparedsentencia.setInt(10, planetas[1]);
			preparedsentencia.executeQuery();
		
			//Insertamos los datos del personaje 3
			maxID++;
			preparedsentencia.setInt(1, maxID);
			preparedsentencia.setString(2, "Kylo Ren");
			preparedsentencia.setInt(3, 189);
			preparedsentencia.setInt(4, 89);
			preparedsentencia.setString(5, "Black");
			preparedsentencia.setString(6, "White");
			preparedsentencia.setString(7, "Brown");
			preparedsentencia.setString(8, "5DBY");
			preparedsentencia.setString(9, "Male");
			preparedsentencia.setInt(10, planetas[2]);
			preparedsentencia.executeQuery();

	
		
	}

	public static void muerte(Connection con) {

		int valor1; 

	
			System.out.println("Introduzca la peli: ");
			valor1 = es.nextInt();
			
			String consulta = "SELECT ch.name, (SELECT ch1.name from characters ch1 WHERE ch1.id = de.id_killer) as nameKiller FROM `deaths` de join films fi on fi.id = de.id_film join characters ch on ch.id = de.id_character where fi.id = ?;";
			try {
				PreparedStatement sentencia = con.prepareStatement(consulta);
				sentencia.setInt(1, valor1);
				ResultSet rs = sentencia.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
  
				while (rs.next()) {
					System.out.println("El muerto: " +rs.getString(1) + " / El asesino: " +rs.getString(2));
				}

				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
	}
		

}
