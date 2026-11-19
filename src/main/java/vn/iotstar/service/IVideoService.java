package vn.iotstar.service;

import java.util.List;

import vn.iotstar.entity.Video;

public interface IVideoService {
    /** Tự sinh videoId, ngày tạo, lượt xem = 0. */
    void insert(Video video);

    void update(Video video);

    void delete(String videoId) throws Exception;

    Video findById(String videoId);

    List<Video> findLatest(int limit);

    List<Video> findByUser(int userId, int page, int pageSize);

    long countByUser(int userId);

    long count();

    void increaseViews(String videoId);
}
