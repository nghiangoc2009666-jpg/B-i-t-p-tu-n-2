package vn.iotstar.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Kiểm tra dữ liệu form phía server. Mỗi hàm trả về thông báo lỗi, hoặc null nếu hợp lệ. */
public final class ValidationUtil {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE = Pattern.compile("^0\\d{9}$");
    private static final Pattern OTP = Pattern.compile("^\\d{6}$");

    private ValidationUtil() {
    }

    public static Map<String, String> errors() {
        return new LinkedHashMap<>();
    }

    /** Thêm lỗi vào map nếu msg != null (không ghi đè lỗi đã có). */
    public static void put(Map<String, String> errors, String field, String msg) {
        if (msg != null) {
            errors.putIfAbsent(field, msg);
        }
    }

    public static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String username(String v) {
        if (blank(v)) return "Vui lòng nhập tài khoản";
        return USERNAME.matcher(v).matches() ? null : "Tài khoản gồm chữ, số, _ hoặc . (3 đến 30 ký tự)";
    }

    public static String password(String v) {
        if (v == null || v.isEmpty()) return "Vui lòng nhập mật khẩu";
        if (v.length() < 6 || v.length() > 50) return "Mật khẩu phải từ 6 đến 50 ký tự";
        return null;
    }

    public static String repassword(String pass, String re) {
        if (re == null || re.isEmpty()) return "Vui lòng nhập lại mật khẩu";
        return re.equals(pass) ? null : "Mật khẩu nhập lại không khớp";
    }

    public static String email(String v) {
        if (blank(v)) return "Vui lòng nhập email";
        if (v.length() > 255) return "Email quá dài";
        return EMAIL.matcher(v).matches() ? null : "Email không hợp lệ";
    }

    public static String phone(String v) {
        if (blank(v)) return "Vui lòng nhập số điện thoại";
        return PHONE.matcher(v).matches() ? null : "Số điện thoại gồm 10 chữ số, bắt đầu bằng 0";
    }

    public static String fullName(String v) {
        if (blank(v)) return "Vui lòng nhập họ tên";
        int n = v.trim().length();
        return n < 2 || n > 100 ? "Họ tên từ 2 đến 100 ký tự" : null;
    }

    public static String otp(String v) {
        if (blank(v)) return "Vui lòng nhập mã OTP";
        return OTP.matcher(v).matches() ? null : "Mã OTP gồm đúng 6 chữ số";
    }

    public static String required(String v, String msg) {
        return blank(v) ? msg : null;
    }

    public static String maxLen(String v, int max, String msg) {
        return v != null && v.length() > max ? msg : null;
    }

    /** Rỗng thì hợp lệ; nếu có thì phải bắt đầu bằng http:// hoặc https://. */
    public static String optionalUrl(String v) {
        return blank(v) || v.startsWith("http://") || v.startsWith("https://") ? null
                : "Link phải bắt đầu bằng http:// hoặc https://";
    }
}
