package vn.iotstar.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.mindrot.jbcrypt.BCrypt;

/** Mã hóa / kiểm tra mật khẩu bằng BCrypt (băm một chiều, có salt ngẫu nhiên). */
public class PasswordUtil {

    public static String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(10));
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith("$2");
    }

    /** Dữ liệu cũ (mật khẩu thô) vẫn so khớp được để tự nâng cấp sang BCrypt khi đăng nhập. */
    public static boolean verify(String raw, String stored) {
        if (raw == null || stored == null) {
            return false;
        }
        if (isHashed(stored)) {
            try {
                return BCrypt.checkpw(raw, stored);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return MessageDigest.isEqual(raw.getBytes(StandardCharsets.UTF_8), stored.getBytes(StandardCharsets.UTF_8));
    }
}
