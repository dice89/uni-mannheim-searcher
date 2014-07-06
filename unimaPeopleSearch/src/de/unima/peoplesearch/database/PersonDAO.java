package de.unima.peoplesearch.database;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.unima.peoplesearch.extraction.Person;

public class PersonDAO {

	public static Person savePerson(Person p) {
		Session session = HibernateUtil.getSession();
		session.beginTransaction();
		
		try{
			session.save(p);
			session.getTransaction().commit();
		}catch (Exception e){
			System.out.println("not saved");
		}
	
		HibernateUtil.closeSession(session);
		return p;
	}

	public static List<Person> getPeopleByMail(String mail) {
		Session session = HibernateUtil.getSession();
		Criteria cr = session.createCriteria(Person.class);
		cr.add(Restrictions.eq("email", mail));

		List<Person> results = (List<Person>) cr.list();

		HibernateUtil.closeSession(session);
		return results;
	}
}
