package ru.spbstu.ChatService.dbService.executor;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import ru.spbstu.ChatService.dbService.dao.UserDAO;
import ru.spbstu.ChatService.dbService.dataSets.UserDataSet;

import java.sql.Connection;
import java.sql.SQLException;

public class DBServiceImpl {

    private static final String hibernate_show_sql = "true";
    private static final String hibernate_hbm2ddl_auto = "update";

    private final SessionFactory sessionFactory;

    private Configuration getPostgreSQLConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres");
        configuration.setProperty("hibernate.connection.username", "postgres");
        configuration.setProperty("hibernate.connection.password", "alohomora551");
        configuration.setProperty("hibernate.show_sql", hibernate_show_sql);
        configuration.setProperty("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        return configuration;
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public DBServiceImpl() {
        Configuration configuration = getPostgreSQLConfiguration();
        sessionFactory = createSessionFactory(configuration);
    }

    public UserDataSet getUserById(long id) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UserDAO dao = new UserDAO(session);
            UserDataSet dataSet = dao.getById(id);
            session.close();
            return dataSet;
        }

        catch (HibernateException err) {
            throw new DBException(err);
        }
    }

    public UserDataSet getUserByLogin(String login) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UserDAO dao = new UserDAO(session);
            UserDataSet dataSet = dao.getByLogin(login);
            session.close();
            return dataSet;
        }

        catch (HibernateException err) {
            throw new DBException(err);
        }
    }

    public UserDataSet getUserByEmail(String email) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UserDAO dao = new UserDAO(session);
            UserDataSet dataSet = dao.getByEmail(email);
            session.close();
            return dataSet;
        }

        catch (HibernateException err) {
            throw new DBException(err);
        }
    }

    public long addUser(String login, String email, String password) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            UserDAO dao = new UserDAO(session);
            long id = dao.insertUser(login, email, password);
            transaction.commit();
            session.close();
            return id;
        }

        catch (HibernateException err) {
            throw new DBException(err);
        }
    }

    public void printConnectInfo() {
        try {
            SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
            Connection connection = sessionFactoryImpl.getConnectionProvider().getConnection();
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        }

        catch (SQLException err) {
            err.printStackTrace();
        }
    }
}
