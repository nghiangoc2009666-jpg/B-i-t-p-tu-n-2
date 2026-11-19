package vn.iotstar.service.impl;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.User;
import vn.iotstar.service.OtpResult;
import vn.iotstar.service.UserService;
import vn.iotstar.util.Constant;
import vn.iotstar.util.MailUtil;
import vn.iotstar.util.PasswordUtil;

public class UserServiceImpl implements UserService {
    private static final SecureRandom RNG = new SecureRandom();
    private final UserDao userDao = new UserDaoImpl();

    // ------------------------------------------------------------ đăng nhập / đăng ký
    @Override
    public User login(String username, String password) {
        User user = this.get(username);
        if (user == null || !PasswordUtil.verify(password, user.getPassWord())) {
            return null;
        }
        // Tài khoản cũ còn lưu mật khẩu thô -> tự động băm lại lần đăng nhập đầu tiên
        if (!PasswordUtil.isHashed(user.getPassWord())) {
            user.setPassWord(PasswordUtil.hash(password));
            userDao.update(user);
        }
        return user;
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Override
    public User getByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public boolean register(String username, String password, String email, String fullname, String phone) {
        if (userDao.checkExistUsername(username)) {
            return false;
        }
        Date date = Date.valueOf(LocalDate.now());
        // roleid = 5: người dùng thường; active = false: chờ kích hoạt bằng OTP
        User user = new User(email, username, fullname, PasswordUtil.hash(password), null, 5, phone, date);
        user.setActive(false);
        userDao.insert(user);
        return true;
    }

    @Override
    public boolean checkExistEmail(String email) {
        return userDao.checkExistEmail(email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return userDao.checkExistUsername(username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return userDao.checkExistPhone(phone);
    }

    // ------------------------------------------------------------ kích hoạt tài khoản bằng OTP
    @Override
    public OtpResult sendActivationOtp(String username) {
        User user = userDao.get(username);
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        if (user.isActive()) {
            return OtpResult.ALREADY_ACTIVE;
        }
        if (tooSoon(user, Constant.OTP_ACTIVATE)) {
            return OtpResult.TOO_SOON;
        }
        String code = issueOtp(user, Constant.OTP_ACTIVATE);
        MailUtil.sendOtp(user.getEmail(), "Mã kích hoạt tài khoản", "Kích hoạt tài khoản Shopping JPA", code);
        return OtpResult.OK;
    }

    @Override
    public OtpResult activate(String username, String otp) {
        User user = userDao.get(username);
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        if (user.isActive()) {
            return OtpResult.ALREADY_ACTIVE;
        }
        OtpResult result = checkOtp(user, Constant.OTP_ACTIVATE, otp);
        if (result == OtpResult.OK) {
            user.setActive(true);
            user.clearOtp();
            userDao.update(user);
        }
        return result;
    }

    // ------------------------------------------------------------ quên mật khẩu
    @Override
    public boolean requestPasswordReset(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return false;
        }
        if (tooSoon(user, Constant.OTP_RESET)) {
            return true; // vừa gửi mã xong, không gửi lại liên tục
        }
        String code = issueOtp(user, Constant.OTP_RESET);
        MailUtil.sendOtp(user.getEmail(), "Mã đặt lại mật khẩu", "Đặt lại mật khẩu Shopping JPA", code);
        return true;
    }

    @Override
    public OtpResult resetPassword(String email, String otp, String newPassword) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        OtpResult result = checkOtp(user, Constant.OTP_RESET, otp);
        if (result == OtpResult.OK) {
            user.setPassWord(PasswordUtil.hash(newPassword));
            user.setActive(true); // đã chứng minh sở hữu email
            user.clearOtp();
            userDao.update(user);
        }
        return result;
    }

    // ------------------------------------------------------------ profile
    @Override
    public boolean isEmailTaken(String email, int exceptUserId) {
        User other = userDao.findByEmail(email);
        return other != null && other.getId() != exceptUserId;
    }

    @Override
    public boolean isPhoneTaken(String phone, int exceptUserId) {
        User other = userDao.findByPhone(phone);
        return other != null && other.getId() != exceptUserId;
    }

    @Override
    public User updateProfile(String username, String fullName, String phone, String avatar) {
        User user = userDao.get(username);
        if (user == null) {
            return null;
        }
        user.setFullName(fullName.trim());
        user.setPhone(phone.trim());
        user.setAvatar(avatar);
        userDao.update(user);
        return userDao.get(username);
    }

    @Override
    public OtpResult sendEmailChangeOtp(String username, String newEmail) {
        User user = userDao.get(username);
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        if (tooSoon(user, Constant.OTP_EMAIL)) {
            return OtpResult.TOO_SOON;
        }
        String code = issueOtp(user, Constant.OTP_EMAIL);
        MailUtil.sendOtp(newEmail, "Mã xác nhận đổi email", "Xác nhận cập nhật hồ sơ Shopping JPA", code);
        return OtpResult.OK;
    }

    @Override
    public OtpResult confirmProfileUpdate(String username, String otp, String fullName, String phone, String email,
            String avatar) {
        User user = userDao.get(username);
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        OtpResult result = checkOtp(user, Constant.OTP_EMAIL, otp);
        if (result != OtpResult.OK) {
            return result;
        }
        if (isEmailTaken(email, user.getId()) || isPhoneTaken(phone, user.getId())) {
            return OtpResult.CONFLICT;
        }
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        user.setPhone(phone.trim());
        user.setAvatar(avatar);
        user.clearOtp();
        userDao.update(user);
        return OtpResult.OK;
    }

    // ------------------------------------------------------------ OTP nội bộ
    /** Sinh OTP 6 số, lưu BẢN BĂM + hạn dùng vào user, trả về mã gốc để gửi email. */
    private String issueOtp(User user, String purpose) {
        String code = String.format("%06d", RNG.nextInt(1_000_000));
        user.setOtpHash(PasswordUtil.hash(code));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(Constant.OTP_MINUTES));
        user.setOtpPurpose(purpose);
        user.setOtpAttempts(0);
        userDao.update(user);
        return code;
    }

    private boolean tooSoon(User user, String purpose) {
        if (!purpose.equals(user.getOtpPurpose()) || user.getOtpExpiry() == null) {
            return false;
        }
        LocalDateTime sentAt = user.getOtpExpiry().minusMinutes(Constant.OTP_MINUTES);
        return LocalDateTime.now().isBefore(sentAt.plusSeconds(Constant.OTP_RESEND_SECONDS));
    }

    private OtpResult checkOtp(User user, String purpose, String code) {
        if (user.getOtpHash() == null || !purpose.equals(user.getOtpPurpose()) || user.getOtpExpiry() == null) {
            return OtpResult.INVALID;
        }
        if (user.getOtpAttempts() >= Constant.OTP_MAX_ATTEMPTS) {
            return OtpResult.TOO_MANY;
        }
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return OtpResult.EXPIRED;
        }
        if (code == null || !PasswordUtil.verify(code, user.getOtpHash())) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            userDao.update(user);
            return OtpResult.INVALID;
        }
        return OtpResult.OK;
    }
}
