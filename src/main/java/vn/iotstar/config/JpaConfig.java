package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaConfig {
    public static final String UNIT_NAME = "jpa-hibernate-mssql";

    // EntityManagerFactory rất tốn tài nguyên -> chỉ tạo 1 lần, dùng chung cả ứng dụng
    private static EntityManagerFactory factory;

    private static synchronized EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(UNIT_NAME);
        }
        return factory;
    }

    /** Mỗi lần gọi trả về 1 EntityManager mới; nơi gọi phải đóng (close) sau khi dùng. */
    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    public static synchronized void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
