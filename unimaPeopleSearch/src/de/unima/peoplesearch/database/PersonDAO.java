package de.unima.peoplesearch.database;

import org.hibernate.Session;

import de.unima.peoplesearch.extraction.Person;

public class PersonDAO {

		public static Person savePerson(Person p){
			Session session = HibernateUtil.getSession();
			session.beginTransaction();
			
				session.save(p);
			
			session.getTransaction().commit();
			HibernateUtil.closeSession(session);
			return p;
		}
		
		public static Person deletePerson(Person p){
			Session session = HibernateUtil.getSession();
			session.beginTransaction();
			
				session.delete(p);
			
			session.getTransaction().commit();
			HibernateUtil.closeSession(session);
			return p;
		}
}
