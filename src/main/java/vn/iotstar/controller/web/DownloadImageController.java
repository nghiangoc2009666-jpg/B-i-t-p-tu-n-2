package vn.iotstar.controller.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

/** Trả về file đã upload (ảnh hoặc video) trong Constant.DIR, có hỗ trợ Range để tua video. */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/image", "/media" }) // /image?fname=abc.png , /media?fname=abc.mp4
public class DownloadImageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getParameter("fname");
        if (fileName == null || fileName.isBlank()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file;
        try {
            // getFileName(): chặn kiểu tấn công đường dẫn ../
            file = new File(Constant.DIR, Paths.get(fileName).getFileName().toString());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String mime = getServletContext().getMimeType(file.getName());
        resp.setContentType(mime != null ? mime : "application/octet-stream");
        resp.setHeader("Accept-Ranges", "bytes");

        long length = file.length();
        long start = 0;
        long end = length - 1;
        String range = req.getHeader("Range");
        if (range != null && range.startsWith("bytes=")) {
            try {
                String[] p = range.substring(6).split("-", 2);
                if (p[0].isEmpty()) { // bytes=-N : N byte cuối
                    start = Math.max(0, length - Long.parseLong(p[1]));
                } else {
                    start = Long.parseLong(p[0]);
                    if (p.length > 1 && !p[1].isEmpty()) {
                        end = Math.min(Long.parseLong(p[1]), length - 1);
                    }
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                start = length; // ép về nhánh 416
            }
            if (start > end || start >= length) {
                resp.setHeader("Content-Range", "bytes */" + length);
                resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
        }

        long remaining = end - start + 1;
        resp.setContentLengthLong(remaining);
        try (RandomAccessFile raf = new RandomAccessFile(file, "r"); OutputStream out = resp.getOutputStream()) {
            raf.seek(start);
            byte[] buf = new byte[8192];
            int n;
            while (remaining > 0 && (n = raf.read(buf, 0, (int) Math.min(buf.length, remaining))) > 0) {
                out.write(buf, 0, n);
                remaining -= n;
            }
        } catch (IOException e) {
            // trình duyệt ngắt kết nối giữa chừng khi tua/đóng video: bỏ qua
        }
    }
}
