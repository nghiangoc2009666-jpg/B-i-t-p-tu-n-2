package vn.iotstar.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import jakarta.servlet.http.Part;

public class UploadUtil {
    private static final Set<String> IMAGES = Set.of("png", "jpg", "jpeg", "gif", "webp");
    private static final Set<String> VIDEOS = Set.of("mp4", "webm", "ogg");

    /** Lưu ảnh upload; trả về tên file mới, hoặc null nếu không chọn file. */
    public static String save(Part part) throws IOException {
        return save(part, IMAGES, "ảnh");
    }

    public static String saveImage(Part part) throws IOException {
        return save(part, IMAGES, "ảnh");
    }

    public static String saveVideo(Part part) throws IOException {
        return save(part, VIDEOS, "video");
    }

    /** Lưu file vào Constant.DIR bằng Part.write (Servlet Multipart). */
    private static String save(Part part, Set<String> allowed, String label) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String submitted = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        int index = submitted.lastIndexOf('.');
        String ext = index >= 0 ? submitted.substring(index + 1).toLowerCase() : "";
        if (!allowed.contains(ext)) {
            throw new IOException("Chỉ chấp nhận " + label + " có đuôi: " + allowed);
        }
        File dir = new File(Constant.DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fname = System.nanoTime() + "." + ext;
        part.write(new File(dir, fname).getAbsolutePath());
        return fname;
    }

    /** Xóa file đã upload (bỏ qua nếu là link http hoặc không tồn tại). */
    public static void delete(String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.startsWith("http")) {
            return;
        }
        File file = new File(Constant.DIR, Paths.get(fileName).getFileName().toString());
        if (file.isFile()) {
            file.delete();
        }
    }
}
