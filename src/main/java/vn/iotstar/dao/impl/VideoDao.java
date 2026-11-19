package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.dao.IVideoDao;
import vn.iotstar.entity.Video;

public class VideoDao implements IVideoDao {

    @Override
    public void insert(Video video) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(video);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void update(Video video) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(video);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void delete(String videoId) throws Exception {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            Video video = enma.find(Video.class, videoId);
            if (video == null) {
                throw new Exception("Không tìm thấy video");
            }
            enma.remove(video);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public Video findById(String videoId) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            return enma.find(Video.class, videoId);
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findLatest(int limit) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v LEFT JOIN FETCH v.user WHERE v.active = 1 "
                    + "ORDER BY v.createdDate DESC";
            TypedQuery<Video> query = enma.createQuery(jpql, Video.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findByUser(int userId, int page, int pageSize) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v WHERE v.user.id = :uid ORDER BY v.createdDate DESC";
            TypedQuery<Video> query = enma.createQuery(jpql, Video.class);
            query.setParameter("uid", userId);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public long countByUser(int userId) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<Long> query = enma.createQuery("SELECT count(v) FROM Video v WHERE v.user.id = :uid",
                    Long.class);
            query.setParameter("uid", userId);
            return query.getSingleResult();
        } finally {
            enma.close();
        }
    }

    @Override
    public long count() {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            return enma.createQuery("SELECT count(v) FROM Video v", Long.class).getSingleResult();
        } finally {
            enma.close();
        }
    }

    @Override
    public void increaseViews(String videoId) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.createQuery("UPDATE Video v SET v.views = v.views + 1 WHERE v.videoId = :id")
                    .setParameter("id", videoId).executeUpdate();
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }
}
