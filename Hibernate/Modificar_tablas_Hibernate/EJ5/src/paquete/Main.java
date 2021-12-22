package paquete;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityTransaction;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class Main {
	public static void main(String[] args) {

		Configuration cfg = new Configuration().configure();
		SessionFactory sessionFactoria = cfg
				.buildSessionFactory(new StandardServiceRegistryBuilder().configure().build());

		Session sesion = sessionFactoria.openSession();

		Transaction tx = sesion.beginTransaction();
		ej4(sesion);

		tx.commit();
		sesion.close();
	}

	public static void crearTablas(Session sesion) {

		EntityTransaction tx = sesion.beginTransaction();
		String sql = "create table Books(\r\n" + "id INTEGER PRIMARY KEY,\r\n" + "Title varchar(300),\r\n"
				+ "Year INTEGER,\r\n" + "Subject INTEGER REFERENCES course(id)\r\n" + ");";
		sesion.createSQLQuery(sql).executeUpdate();
		tx.commit();
	}

	public static void ej1(Session sesion) {

		BufferedReader br;
		// Creamos lista donde guardaremos los libros
		List<Books> listL = new ArrayList<>();
		try {
			// Creamos br para el fichero csv
			br = new BufferedReader(new FileReader("C:\\Users\\ruben\\Downloads\\Harry_Potter_libros.csv"));
			String linea = "";
			int i = 0;
			// Recorremos el br
			while ((linea = br.readLine()) != null) {
				if (i == 0) {
					// Si es la primera linea suma uno para no tenerla en cuenta(son las cabeceras
					// del csv)
					i++;
					continue;
				}
				String[] datosLibros = linea.split(";"); // guardame los datos de un libro separados en un array
				Books book = new Books();// Creamos un libro
				// Modificamos sus datos
				book.setId((i - 1));
				book.setTitle(datosLibros[0]);
				book.setYear(Integer.valueOf(datosLibros[1]));
				// Hacemos un select que saca el id del libro con el nombre que hay en el csv
				Query<Course> q = sesion.createQuery("from Course as c where c.name like '" + datosLibros[2] + "'");
				final List<Course> rs = q.getResultList();// Lista donde guardamos el curso
				Integer id = 0;
				// Bucle que coge la id del cursp
				for (int i2 = 0; i2 < rs.size(); i2++) {
					id = rs.get(i2).getId();
				}
				// Asignamos esa id a la asignatura
				book.setSubject(id);
				// añadimos el libro a la lista
				listL.add(book);
				i++;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Guardamos todos los libros de la lista utilizando un for
		for (int i2 = 0; i2 < listL.size(); i2++) {
			Books book = listL.get(i2);
			sesion.save(book);
		}
		// Mensaje informativo
		System.out.println("Wacho,ya estan tus libros al fin");
	}

	public static void ej2(Session sesion) {

		// Sacamos toda la información sobre los puntos quitados a harry y ron
		ScrollableResults sc = sesion.createQuery("from HousePoints as hp where "
				+ "hp.personByGiver.firstName like 'Severus' and hp.points < 0 and "
				+ "hp.personByReceiver.firstName like 'Harry' and hp.personByReceiver.lastName like 'Potter'"
				+ " or hp.personByReceiver.firstName like 'Ron' and hp.personByReceiver.lastName like 'Weasley'")
				.scroll();
		Integer puntos = 0;

		// Recorremos toda la lista guardan la información
		while (sc.next()) {
			HousePoints hp = (HousePoints) sc.get(0);
			Person alumno = hp.getPersonByGiver();
			Person profesor = hp.getPersonByReceiver();
			Integer puntosActuales = hp.getPoints();
			puntos += puntosActuales;// Sumamos los puntos que quita a una variable total
		}
		System.out.println("Puntos quitados H y R por SS: " + puntos);// Mostramos los puntos antes de hacer nada

		// Simplemente insertamos los campos que hemos cogido antes,claro si ne una
		// sacamos que severus le ha quitado 50 puntos a harry y la volvemos a
		// insertar,le habrá
		// quitado 100
		String hqlInsert = "INSERT INTO HousePoints (personByGiver, personByReceiver, points) "
				+ "select hp.personByGiver, hp.personByReceiver, hp.points from HousePoints as hp where "
				+ "hp.personByGiver.firstName like 'Severus' and hp.points < 0 and "
				+ "hp.personByReceiver.firstName like 'Harry' and hp.personByReceiver.lastName like 'Potter'"
				+ " or hp.personByReceiver.firstName like 'Ron' and hp.personByReceiver.lastName like 'Weasley'";

		sesion.createQuery(hqlInsert).executeUpdate();

		// Ahora sacamos otra vez los datos para confirmar el cambio
		ScrollableResults sc2 = sesion.createQuery("from HousePoints as hp where "
				+ "hp.personByGiver.firstName like 'Severus' and hp.points < 0 and "
				+ "hp.personByReceiver.firstName like 'Harry' and hp.personByReceiver.lastName like 'Potter'"
				+ " or hp.personByReceiver.firstName like 'Ron' and hp.personByReceiver.lastName like 'Weasley'")
				.scroll();
		Integer puntos2 = 0;
		// Realizamos el mismo while para sumar los puntos
		while (sc2.next()) {
			HousePoints hp = (HousePoints) sc2.get(0);
			Person alumno = hp.getPersonByGiver();
			Person profesor = hp.getPersonByReceiver();
			Integer puntosActuales = hp.getPoints();
			puntos2 += puntosActuales;
		}
		// Vemos que somos unos cracks y ha funcionado
		System.out.println("Puntos quitados H y R por SS: " + puntos2);
	}

	public static void ej3(Session sesion) {

		// Guardamos el numero de personas matriculadas en muggle studies en un long
		long num = (long) sesion
				.createQuery(
						"select count(c.person) from Course c inner join c.persons alu where c.name = 'Muggle Studies'")
				.uniqueResult();
		System.out.println(num);// Lo mostramos

		// Guardamos el id de pociones
		List<Integer> listaIdP = sesion
				.createQuery("select p.id from Person p inner join p.courses_1 c where c.name = 'Potions'")
				.getResultList();

		// Guardamos en una lista todas las personas
		List<Person> personas = sesion.createQuery("from Person").getResultList();

		// Sacamos el id del CursoMugle
		int idCursoMugle = (int) sesion.createQuery("select id from Course where name='Muggle Studies'").uniqueResult();

		// Recorremos todas las personas
		for (Person persona : personas) {
			for (int i : listaIdP) {
				if (persona.getId() == i) {
					// Creamos la sentencia con ? para modificarla
					String sql = "INSERT INTO enrollment(person_enrollment,course_enrollment) VALUES (?1, ?2)";
					// La ejecutamos cambiando un ? pir persona y otra por CursoMugle
					sesion.createNativeQuery(sql).setParameter(1, persona).setParameter(2, idCursoMugle)
							.executeUpdate();
				}
			}
		}

		System.out.println(num);// Lo mostramos después de modificar

	}

	public static void ej4(Session sesion) {

		// Extraigo el numero de alumnos que cursan la asignatura Flying
		long num = (long) sesion
				.createQuery("select count(c.person) from Course c inner join c.persons alu where c.name = 'Flying'")
				.uniqueResult();
		System.out.println(num);// Lo muestro
		// Sacamos el id de las personas que cursan Transfiguration
		List<Integer> pIdT = sesion
				.createQuery("select p.id from Person p inner join p.courses_1 c where c.name = 'Transfiguration'")
				.getResultList();
		// Sacamos todas las personas y las pasamos a una litsa
		List<Person> listP = sesion.createQuery("from Person").getResultList();

		// Sacamos el id del curso Flying y la guardamos
		int courseFlying = (int) sesion.createQuery("select id from Course where name='Flying'").uniqueResult();

		// Recorremos la lista de personas
		for (Person p : listP) {
			for (int i : pIdT) {
				if (p.getId() == i) {
					// Creamos la setencia sql con ? para sustituir
					String sql = "INSERT INTO enrollment(person_enrollment,course_enrollment) VALUES (?1, ?2)";
					// Ejecutamos la sentencia
					sesion.createNativeQuery(sql).setParameter(1, p).setParameter(2, courseFlying).executeUpdate();
				}
			}
		}
		num = (long) sesion
				.createQuery("select count(c.person) from Course c inner join c.persons alu where c.name = 'Flying'")
				.uniqueResult();
		System.out.println(num);// Lo muestro
	}

}