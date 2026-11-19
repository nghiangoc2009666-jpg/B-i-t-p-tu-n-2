package vn.iotstar.util;

import java.io.File;

public class Constant {
    /** Thư mục lưu file upload (ảnh, video). Mặc định: <thư mục người dùng>/upload. */
    public static final String DIR = System.getProperty("user.home") + File.separator + "upload";

    public static final String SESSION_ACCOUNT = "account";
    public static final String SESSION_USERNAME = "username";
    public static final String COOKIE_REMEMBER = "username";
    public static final int COOKIE_MAX_AGE = 30 * 60; // 30 phút

    // OTP
    public static final int OTP_MINUTES = 5;          // OTP có hiệu lực 5 phút
    public static final int OTP_MAX_ATTEMPTS = 5;     // nhập sai tối đa 5 lần
    public static final int OTP_RESEND_SECONDS = 60;  // gửi lại mã sau tối thiểu 60 giây
    public static final String OTP_ACTIVATE = "ACTIVATE";
    public static final String OTP_RESET = "RESET";
    public static final String OTP_EMAIL = "EMAIL";   // xác nhận đổi email ở trang profile

    // Video
    public static final int PAGE_SIZE = 6;            // 6 video / trang
    public static final int HOME_LATEST = 10;         // 10 video mới nhất ở trang chủ

    public static class Path {
        public static final String LOGIN = "/views/login.jsp";
        public static final String REGISTER = "/views/register.jsp";
    }
}
