package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.dao.UserDao;
import vn.iotstar.entity.User;

public class UserDaoImpl implements UserDao {

    @Override
    public User get(String username) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<User> query = enma.createQuery("SELECT u FROM User u WHERE u.userName = :username",
                    User.class);
            query.setParameter("username", username);
            List<User> result = query.setMaxResults(1).getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            enma.close();
        }
    }

    @Override
    public void insert(User user) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(user);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new RuntimeException("Không thể thêm người dùng", e);
        } finally {
            enma.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<User> query = enma.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            List<User> result = query.setMaxResults(1).getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            enma.close();
        }
    }

    @Override
    public User findByPhone(String phone) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<User> query = enma.createQuery("SELECT u FROM User u WHERE u.phone = :phone", User.class);
            query.setParameter("phone", phone);
            List<User> result = query.setMaxResults(1).getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            enma.close();
        }
    }

    @Override
    public void update(User user) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(user);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new RuntimeException("Không thể cập nhật người dùng", e);
        } finally {
            enma.close();
        }
    }

    @Override
    public boolean checkExistEmail(String email) {
        return exists("SELECT count(u) FROM User u WHERE u.email = :v", email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return exists("SELECT count(u) FROM User u WHERE u.userName = :v", username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return exists("SELECT count(u) FROM User u WHERE u.phone = :v", phone);
    }

    @Override
    public long count() {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            return enma.createQuery("SELECT count(u) FROM User u", Long.class).getSingleResult();
        } finally {
            enma.close();
        }
    }

    private boolean exists(String jpql, String value) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<Long> query = enma.createQuery(jpql, Long.class);
            query.setParameter("v", value);
            return query.getSingleResult() > 0;
        } finally {
            enma.close();
        }
    }
}
