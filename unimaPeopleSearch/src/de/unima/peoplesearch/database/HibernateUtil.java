package de.unima.peoplesearch.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {
    public static Session getSession(){
           Session sess = null;
           Configuration config = new Configuration().configure();
           ServiceRegistry serviceRegistry = null;
           StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
           builder.applySettings(config.getProperties());
           serviceRegistry = builder.build();
           SessionFactory sFec = config.buildSessionFactory(serviceRegistry);
           sess = sFec.openSession();
           return sess;
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