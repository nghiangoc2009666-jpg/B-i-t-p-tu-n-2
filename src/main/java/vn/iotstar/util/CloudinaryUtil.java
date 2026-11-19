package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;

/**
 * Upload ảnh lên Cloudinary. Cấu hình trong src/main/resources/cloudinary.properties
 * (xem cloudinary.properties.example) hoặc biến môi trường CLOUDINARY_CLOUD_NAME /
 * CLOUDINARY_API_KEY / CLOUDINARY_API_SECRET.
 */
public class CloudinaryUtil {
    private static final Set<String> IMAGES = Set.of("png", "jpg", "jpeg", "gif", "webp");
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final Properties CFG = new Properties();

    static {
        try (InputStream in = CloudinaryUtil.class.getClassLoader().getResourceAsStream("cloudinary.properties")) {
            if (in != null) {
                CFG.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String get(String key, String env) {
        String v = System.getenv(env);
        if (v == null || v.isBlank()) {
            v = CFG.getProperty(key, "");
        }
        return v.trim();
    }

    public static boolean isConfigured() {
        String n = get("cloud_name", "CLOUDINARY_CLOUD_NAME");
        String k = get("api_key", "CLOUDINARY_API_KEY");
        String s = get("api_secret", "CLOUDINARY_API_SECRET");
        return !n.isEmpty() && !k.isEmpty() && !s.isEmpty() && !n.contains("your_");
    }

    /** Upload ảnh (multipart Part) lên Cloudinary, trả về secure_url. Null nếu không chọn file. */
    public static String uploadImage(Part part, String folder) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String name = part.getSubmittedFileName() == null ? "" : part.getSubmittedFileName();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!IMAGES.contains(ext) || part.getContentType() == null || !part.getContentType().startsWith("image/")) {
            throw new IOException("Chỉ chấp nhận ảnh có đuôi: " + IMAGES);
        }
        if (part.getSize() > MAX_SIZE) {
            throw new IOException("Ảnh tối đa 5MB");
        }
        if (!isConfigured()) {
            throw new IOException("Chưa cấu hình Cloudinary (file cloudinary.properties)");
        }
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", get("cloud_name", "CLOUDINARY_CLOUD_NAME"),
                "api_key", get("api_key", "CLOUDINARY_API_KEY"),
                "api_secret", get("api_secret", "CLOUDINARY_API_SECRET"),
                "secure", true));
        try (InputStream in = part.getInputStream()) {
            Map<?, ?> result = cloudinary.uploader().upload(in.readAllBytes(),
                    ObjectUtils.asMap("folder", folder, "resource_type", "image"));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Upload Cloudinary thất bại: " + e.getMessage(), e);
        }
    }
}
