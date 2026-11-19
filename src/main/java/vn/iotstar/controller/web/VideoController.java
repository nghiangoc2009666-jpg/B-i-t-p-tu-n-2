package vn.iotstar.controller.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.UploadUtil;
import vn.iotstar.util.ValidationUtil;

/**
 * CRUD Videos + danh sách video của user (phân trang) + xem chi tiết/phát video.
 * Upload file bằng Servlet Multipart (@MultipartConfig, Part.write).
 */
@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024L * 1024 * 100, // 100MB / file
        maxRequestSize = 1024L * 1024 * 110) // 110MB / request
@WebServlet(urlPatterns = { "/video", "/video/add", "/video/insert", "/video/edit", "/video/update",
        "/video/delete", "/video/detail" })
public class VideoController extends HttpServlet {
    private static final String LIST = "/views/video/video-list.jsp";
    private static final String FORM = "/views/video/video-form.jsp";
    private static final String DETAIL = "/views/video/video-detail.jsp";
    private static final Pattern YOUTUBE = Pattern.compile("(?:v=|youtu\\.be/|embed/)([A-Za-z0-9_-]{11})");

    private final IVideoService videoService = new VideoServiceImpl();
    private final ICategoryService cateService = new CategoryServiceImpl();

    // ====================================================================== GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        switch (path) {
        case "/video" -> list(req, resp);
        case "/video/add" -> showForm(req, resp, null);
        case "/video/edit" -> {
            Video v = loadManageable(req, resp);
            if (v != null) {
                showForm(req, resp, v);
            }
        }
        case "/video/delete" -> {
            Video v = loadManageable(req, resp);
            if (v != null) {
                delete(req, resp, v);
            }
        }
        case "/video/detail" -> detail(req, resp);
        default -> resp.sendRedirect(req.getContextPath() + "/video");
        }
    }

    /** Danh sách video của user đăng nhập, 6 video / trang. */
    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        long total = videoService.countByUser(user.getId());
        int totalPages = (int) Math.max(1, Math.ceil(total / (double) Constant.PAGE_SIZE));
        int page = parseInt(req.getParameter("page"), 1);
        page = Math.min(Math.max(page, 1), totalPages);

        List<Video> videos = videoService.findByUser(user.getId(), page, Constant.PAGE_SIZE);
        req.setAttribute("videos", videos);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("total", total);
        req.getRequestDispatcher(LIST).forward(req, resp);
    }

    /** Chi tiết + phát video (ai cũng xem được video công khai). */
    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Video video = videoService.findById(req.getParameter("id"));
        User user = currentUser(req);
        if (video == null || (video.getActive() != 1 && !canManage(user, video))) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy video");
            return;
        }
        videoService.increaseViews(video.getVideoId());
        video = videoService.findById(video.getVideoId()); // nạp lại để có lượt xem mới

        String url = video.getVideoUrl();
        if (url != null && !url.isBlank()) {
            if (url.startsWith("http")) {
                Matcher m = YOUTUBE.matcher(url);
                if (url.contains("youtu") && m.find()) {
                    req.setAttribute("playerType", "youtube");
                    req.setAttribute("playerSrc", "https://www.youtube.com/embed/" + m.group(1));
                } else {
                    req.setAttribute("playerType", "video");
                    req.setAttribute("playerSrc", url);
                }
            } else {
                req.setAttribute("playerType", "video");
                req.setAttribute("playerSrc",
                        req.getContextPath() + "/media?fname=" + URLEncoder.encode(url, "UTF-8"));
            }
        }
        req.setAttribute("video", video);
        req.setAttribute("canManage", canManage(user, video));
        req.getRequestDispatcher(DETAIL).forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Video video)
            throws ServletException, IOException {
        req.setAttribute("video", video);
        req.setAttribute("categories", cateService.findAll());
        req.getRequestDispatcher(FORM).forward(req, resp);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp, Video video) throws IOException {
        try {
            videoService.delete(video.getVideoId());
            UploadUtil.delete(video.getPoster());
            UploadUtil.delete(video.getVideoUrl());
            resp.sendRedirect(req.getContextPath() + "/video");
        } catch (Exception e) {
            e.printStackTrace();
            redirectError(req, resp, "Không xóa được video: " + e.getMessage());
        }
    }

    // ====================================================================== POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        if (!path.equals("/video/insert") && !path.equals("/video/update")) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }
        // File vượt giới hạn @MultipartConfig sẽ làm việc đọc request thất bại
        try {
            req.getParts();
        } catch (Exception e) {
            redirectError(req, resp, "Upload thất bại: file quá lớn (tối đa 100MB mỗi file).");
            return;
        }
        boolean isUpdate = path.equals("/video/update");

        Video video;
        if (isUpdate) {
            video = videoService.findById(req.getParameter("videoId"));
            User user = currentUser(req);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
            if (!canManage(user, video)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền sửa video này");
                return;
            }
        } else {
            video = new Video();
            video.setUser(currentUser(req));
        }

        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        String posterLink = trim(req.getParameter("posterLink"));
        String videoLink = trim(req.getParameter("videoLink"));
        int active = "1".equals(req.getParameter("active")) ? 1 : 0;
        Part posterPart = req.getPart("posterFile");
        Part videoPart = req.getPart("videoFile");
        boolean hasVideoFile = videoPart != null && videoPart.getSize() > 0;

        Map<String, String> errors = ValidationUtil.errors();
        ValidationUtil.put(errors, "title", ValidationUtil.required(title, "Tiêu đề không được rỗng"));
        ValidationUtil.put(errors, "title", ValidationUtil.maxLen(title, 500, "Tiêu đề tối đa 500 ký tự"));
        ValidationUtil.put(errors, "description", ValidationUtil.maxLen(description, 500, "Mô tả tối đa 500 ký tự"));
        ValidationUtil.put(errors, "posterLink", ValidationUtil.optionalUrl(posterLink));
        ValidationUtil.put(errors, "videoLink", ValidationUtil.optionalUrl(videoLink));
        if (!isUpdate && !hasVideoFile && videoLink.isEmpty()) {
            errors.putIfAbsent("videoLink", "Hãy chọn file video hoặc nhập link video");
        }
        String error = null;

        String newPoster = null;
        String newVideo = null;
        if (errors.isEmpty()) {
            try {
                newPoster = UploadUtil.saveImage(posterPart);
                newVideo = UploadUtil.saveVideo(videoPart);
            } catch (IOException e) {
                UploadUtil.delete(newPoster); // bỏ file ảnh vừa lưu nếu video lỗi
                error = e.getMessage();
            }
        }
        if (error != null || !errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("alert", error);
            req.setAttribute("categories", cateService.findAll());
            req.setAttribute("video", isUpdate ? video : null);
            req.getRequestDispatcher(FORM).forward(req, resp);
            return;
        }

        // ---- gán dữ liệu
        video.setTitle(title);
        video.setDescription(description.isEmpty() ? null : description);
        video.setActive(active);
        video.setCategory(findCategory(req.getParameter("categoryId")));

        String oldPoster = video.getPoster();
        String oldVideo = video.getVideoUrl();
        if (newPoster != null) {
            video.setPoster(newPoster);
        } else if (!posterLink.isEmpty()) {
            video.setPoster(posterLink);
        }
        if (newVideo != null) {
            video.setVideoUrl(newVideo);
        } else if (!videoLink.isEmpty()) {
            video.setVideoUrl(videoLink);
        }

        if (isUpdate) {
            videoService.update(video);
            // xóa file cũ trên thư mục nếu đã bị thay thế
            if (oldPoster != null && !oldPoster.equals(video.getPoster())) {
                UploadUtil.delete(oldPoster);
            }
            if (oldVideo != null && !oldVideo.equals(video.getVideoUrl())) {
                UploadUtil.delete(oldVideo);
            }
        } else {
            videoService.insert(video);
        }
        resp.sendRedirect(req.getContextPath() + "/video");
    }

    // ====================================================================== helpers
    private User currentUser(HttpServletRequest req) {
        var session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(Constant.SESSION_ACCOUNT);
    }

    /** Chủ video hoặc admin (role 1) mới được sửa/xóa. */
    private boolean canManage(User user, Video video) {
        if (user == null) {
            return false;
        }
        return user.getRoleid() == 1 || (video.getUser() != null && video.getUser().getId() == user.getId());
    }

    private Video loadManageable(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Video video = videoService.findById(req.getParameter("id"));
        if (video == null) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return null;
        }
        if (!canManage(currentUser(req), video)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền với video này");
            return null;
        }
        return video;
    }

    private Category findCategory(String id) {
        int cid = parseInt(id, 0);
        return cid > 0 ? cateService.findById(cid) : null;
    }

    private void redirectError(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/video?error=" + URLEncoder.encode(msg, "UTF-8"));
    }

    private boolean isHttp(String s) {
        return s.startsWith("http://") || s.startsWith("https://");
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
