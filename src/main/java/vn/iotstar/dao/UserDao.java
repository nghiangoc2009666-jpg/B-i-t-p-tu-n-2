package vn.iotstar.dao;

import vn.iotstar.entity.User;

public interface UserDao {
    User get(String username);

    User findByEmail(String email);

    User findByPhone(String phone);

    void insert(User user);

    void update(User user);

    boolean checkExistEmail(String email);

    boolean checkExistUsername(String username);

    boolean checkExistPhone(String phone);

    long count();
}
