package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface UserService {
    /** Kiểm tra tài khoản + mật khẩu (BCrypt). Trả về null nếu sai. Không kiểm tra trạng thái kích hoạt. */
    User login(String username, String password);

    User get(String username);

    User getByEmail(String email);

    void insert(User user);

    /** Tạo tài khoản CHƯA kích hoạt, mật khẩu được băm BCrypt. */
    boolean register(String username, String password, String email, String fullname, String phone);

    boolean checkExistEmail(String email);

    boolean checkExistUsername(String username);

    boolean checkExistPhone(String phone);

    /** Tạo OTP kích hoạt và gửi qua email. Ném MailException nếu gửi lỗi. */
    OtpResult sendActivationOtp(String username);

    /** Kiểm tra OTP và kích hoạt tài khoản. */
    OtpResult activate(String username, String otp);

    // ---- Profile
    boolean isEmailTaken(String email, int exceptUserId);

    boolean isPhoneTaken(String phone, int exceptUserId);

    /** Cập nhật họ tên, số điện thoại, avatar (không đổi email). Trả về user mới. */
    User updateProfile(String username, String fullName, String phone, String avatar);

    /** Đổi email: gửi OTP tới EMAIL MỚI. Ném MailException nếu gửi lỗi. */
    OtpResult sendEmailChangeOtp(String username, String newEmail);

    /** Kiểm tra OTP, đúng thì cập nhật toàn bộ hồ sơ gồm email mới. */
    OtpResult confirmProfileUpdate(String username, String otp, String fullName, String phone, String email,
            String avatar);

    /** Quên mật khẩu: gửi OTP tới email nếu có tài khoản. Trả về true nếu email tồn tại. */
    boolean requestPasswordReset(String email);

    /** Kiểm tra OTP và đặt mật khẩu mới (băm BCrypt). */
    OtpResult resetPassword(String email, String otp, String newPassword);
}
