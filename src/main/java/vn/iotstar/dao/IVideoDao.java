package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.Video;

public interface IVideoDao {
    void insert(Video video);

    void update(Video video);

    void delete(String videoId) throws Exception;

    Video findById(String videoId);

    /** Các video công khai mới nhất (trang chủ). */
    List<Video> findLatest(int limit);

    /** Video của 1 user, phân trang (page bắt đầu từ 1). */
    List<Video> findByUser(int userId, int page, int pageSize);

    long countByUser(int userId);

    long count();

    void increaseViews(String videoId);
}
