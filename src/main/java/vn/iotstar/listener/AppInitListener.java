package vn.iotstar.listener;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.PasswordUtil;

/**
 * Khi Tomcat khởi động: Hibernate tự tạo/cập nhật bảng (hbm2ddl.auto=update), rồi nạp dữ liệu mẫu
 * nếu bảng còn trống. Khi tắt: đóng EntityManagerFactory.
 */
@WebListener
public class AppInitListener implements ServletContextListener {
    private static final String SAMPLE_MP4 = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            UserDao userDao = new UserDaoImpl();
            if (userDao.count() == 0) {
                Date today = Date.valueOf(LocalDate.now());
                // Mật khẩu được băm BCrypt trước khi lưu
                userDao.insert(new User("admin@demo.vn", "admin", "Quản trị viên", PasswordUtil.hash("admin123"), null, 1, "0900000001", today));
                userDao.insert(new User("manager@demo.vn", "manager", "Quản lý", PasswordUtil.hash("manager123"), null, 2, "0900000002", today));
                userDao.insert(new User("user@demo.vn", "user", "Người dùng", PasswordUtil.hash("user123"), null, 5, "0900000003", today));
            }
            ICategoryDao cateDao = new CategoryDao();
            if (cateDao.count() == 0) {
                cateDao.insert(new Category("Điện thoại", null, 1));
                cateDao.insert(new Category("Laptop", null, 1));
                cateDao.insert(new Category("Phụ kiện", null, 1));
            }
            // Video mẫu cho tài khoản "user" (7 video để thấy được phân trang 6 video/trang)
            IVideoService videoService = new VideoServiceImpl();
            User sample = userDao.get("user");
            if (sample != null && videoService.countByUser(sample.getId()) == 0) {
                for (int i = 1; i <= 7; i++) {
                    Video v = new Video();
                    v.setTitle("Video mẫu số " + i);
                    v.setDescription("Video mẫu để thử chức năng phân trang và phát video.");
                    v.setVideoUrl(SAMPLE_MP4);
                    v.setActive(1);
                    v.setUser(sample);
                    videoService.insert(v);
                }
            }
        } catch (Exception e) {
            // Không làm sập ứng dụng: xem lỗi kết nối database trong tab Console
            System.err.println("[AppInitListener] Không khởi tạo được JPA/database. Kiểm tra persistence.xml:");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JpaConfig.shutdown();
    }
}
