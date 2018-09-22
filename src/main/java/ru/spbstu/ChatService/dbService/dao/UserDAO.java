package ru.spbstu.ChatService.dbService.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.spbstu.ChatService.dbService.dataSets.UserDataSet;

public class UserDAO {

    private Session session;

    public UserDAO(Session session) {
        this.session = session;
    }

    public UserDataSet getById(long id) throws HibernateException {
        return (UserDataSet) session.get(UserDataSet.class, id);
    }

    public UserDataSet getByLogin(String login) throws HibernateException {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return ((UserDataSet)criteria.add(Restrictions.eq("login", login)).uniqueResult());
    }

    public UserDataSet getByEmail(String email) throws HibernateException {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return ((UserDataSet)criteria.add(Restrictions.eq("email", email)).uniqueResult());
    }

    public long getUserById(String login) throws HibernateException {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return ((UserDataSet)criteria.add(Restrictions.eq("login", login)).uniqueResult()).getId();
    }

    public long insertUser(String login, String email, String password) throws HibernateException {
        return (Long)session.save(new UserDataSet(login, email, password));
    }
}
