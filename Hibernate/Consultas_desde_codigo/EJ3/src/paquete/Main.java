package paquete;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class Main {
	public static void main(String[] args) {

		Configuration cfg = new Configuration().configure();
		SessionFactory sessionFactoria = cfg
				.buildSessionFactory(new StandardServiceRegistryBuilder().configure().build());

		Session sesion = sessionFactoria.openSession();

		// ej1(sesion);
		// ej2(sesion);
		 ej3(sesion);
		// ej4(sesion);
		//ej4v2(sesion);
		sesion.close();

	}

	public static void ej1(Session sesion) {

		Query q = sesion.createQuery("from Person where last_name='Potter'");
		System.out.println("Uso de list()");
		List<Person> cl = q.getResultList();
		for (Person c : cl) {
			System.out.println(c.getFirstName() + " " + c.getLastName() + " - " + c.getHouse().getName());
		}

	}

	public static void ej2(Session sesion) {

		// EJERCICIO2
		ScrollableResults sc = sesion
				.createQuery(
						"select p.firstName,p.lastName from Person as p join p.courses_1 as c  group by p.lastName")
				.scroll();
		while (sc.next()) {
			System.out.println(sc.get(1) + ", " + sc.get(0));

		}
		long tAlum = (long) sesion.createQuery("select count(distinct p.id) from Person p join p.courses_1 c")
				.uniqueResult();
		System.out.println("Total de alumnos: " + tAlum);

	}

	public static void ej3(Session sesion) {

		int puntosTotales =0;
		
		List<HousePoints> listaH = sesion.createQuery(
				"from HousePoints h where h.personByReceiver.firstName = ?1 and   h.personByReceiver.lastName = ?2")
				.setParameter(1, "Harry").setParameter(2, "Potter").getResultList();

		for (HousePoints h : listaH) {
			System.out.println(h.getPersonByGiver().getFirstName() + " " + h.getPersonByGiver().getLastName() + " --> "
					+ h.getPoints() + " puntos para " + h.getPersonByReceiver().getFirstName() + " "
					+ h.getPersonByReceiver().getLastName());
		puntosTotales+= h.getPoints();
		}
				
		List<HousePoints> listaH2 = sesion.createQuery(
				"from HousePoints h where h.personByReceiver.firstName = ?1 and   h.personByReceiver.lastName = ?2")
				.setParameter(1, "Ron").setParameter(2, "Weasley").getResultList();

		for (HousePoints h : listaH2){
			System.out.println(h.getPersonByGiver().getFirstName() + " " + h.getPersonByGiver().getLastName() + " --> "
					+ h.getPoints() + " puntos para " + h.getPersonByReceiver().getFirstName() + " "
					+ h.getPersonByReceiver().getLastName());
			puntosTotales+= h.getPoints();
		}
		
		List<HousePoints> listaH3 = sesion.createQuery(
				"from HousePoints h where h.personByReceiver.firstName = ?1 and   h.personByReceiver.lastName = ?2")
				.setParameter(1, "Hermione").setParameter(2, "Granger").getResultList();

		for (HousePoints h : listaH3) {
			System.out.println(h.getPersonByGiver().getFirstName() + " " + h.getPersonByGiver().getLastName() + " --> "
					+ h.getPoints() + " puntos para " + h.getPersonByReceiver().getFirstName() + " "
					+ h.getPersonByReceiver().getLastName());
			puntosTotales+= h.getPoints();
		}
		System.out.println("Los puntos totales son: " +puntosTotales);
	}

	public static void ej4(Session sesion) {

		// EJERCICIO2
		ScrollableResults sc = sesion.createQuery(
				"select p2.firstName,p2.lastName,c.name, p.firstName,p.lastName from Course c inner join  c.person p inner join  c.persons p2 inner join p2.house h where c.name='Potions' and h.name='Gryffindor'")
				.scroll();
		while (sc.next()) {
			System.out.println(
					sc.get(0) + ", " + sc.get(1) + " estudia " + sc.get(2) + " con " + sc.get(3) + " " + sc.get(4));

		}

		long tAlum = (long) sesion.createQuery(
				"select count( p.id) from Course c join c.persons p  join p.house h  where c.name ='Potions' and h.name='Gryffindor' ")
				.uniqueResult();
		System.out.println("Total de alumnos: " + tAlum);

	}

	public static void ej4v2(Session sesion) {

		// EJERCICIO4

		Course c = (Course) sesion.createQuery(
				"select c from Course c   join fetch c.persons p  join fetch c.person p2 where c.name = 'Potions' and p.house.name = 'Gryffindor'")
				.uniqueResult();

		Set<Person> setAlumnos = c.getPersons();

		for (Person alumno : setAlumnos) {
			System.out.println(alumno.getFirstName() + " " + alumno.getLastName() + " estudia " + c.getName() + " con "
					+ c.getPerson().getFirstName() + " " + c.getPerson().getLastName());
		}
	}
}
