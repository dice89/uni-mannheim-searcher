package de.unima.peoplesearch.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {
	
    private static SessionFactory factory;
    static {
        Configuration config = new Configuration().configure();
        ServiceRegistry serviceRegistry = null;
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(config.getProperties());
        serviceRegistry = builder.build();
        factory = config.buildSessionFactory(serviceRegistry);
    }

    public static Session getSession() {
        return factory.openSession();
    }

    // Call this during shutdown
    public static void close() {
        factory.close();
    }

   
    public static void closeSession(Session sess){
           if(sess!=null){
                  try{
                        sess.close();
                  }
                  catch(HibernateException e){
                        //do nothing just silence this exception
                  }
           }
    }
}