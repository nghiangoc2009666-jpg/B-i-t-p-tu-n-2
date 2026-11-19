package vn.iotstar.controller.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.User;
import vn.iotstar.service.OtpResult;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.CloudinaryUtil;
import vn.iotstar.util.Constant;
import vn.iotstar.util.MailException;
import vn.iotstar.util.ValidationUtil;

/**
 * Profile: sửa họ tên, số điện thoại, email, ảnh đại diện (upload Cloudinary bằng multipart).
 * Nếu đổi email: gửi OTP tới email MỚI; nhập đúng OTP thì toàn bộ thay đổi mới được lưu.
 */
@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5L * 1024 * 1024, maxRequestSize = 6L * 1024 * 1024)
@WebServlet(urlPatterns = { "/profile", "/profile/verify" })
public class ProfileController extends HttpServlet {
    private static final String VIEW = "/views/profile.jsp";
    private static final String VERIFY = "/views/profile-verify.jsp";
    private static final String PENDING = "pendingProfile";

    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = reload(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (req.getServletPath().equals("/profile/verify")) {
            if (pending(req) == null) {
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }
            req.getRequestDispatcher(VERIFY).forward(req, resp);
            return;
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = reload(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (req.getServletPath().equals("/profile/verify")) {
            verify(req, resp, user);
        } else {
            save(req, resp, user);
        }
    }

    // ------------------------------------------------------------------ lưu hồ sơ
    private void save(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String fullName = trim(req.getParameter("fullname"));
        String phone = trim(req.getParameter("phone"));
        String email = trim(req.getParameter("email"));

        Map<String, String> errors = ValidationUtil.errors();
        ValidationUtil.put(errors, "fullname", ValidationUtil.fullName(fullName));
        ValidationUtil.put(errors, "phone", ValidationUtil.phone(phone));
        ValidationUtil.put(errors, "email", ValidationUtil.email(email));
        if (!errors.containsKey("phone") && service.isPhoneTaken(phone, user.getId())) {
            errors.put("phone", "Số điện thoại đã được dùng bởi tài khoản khác");
        }
        if (!errors.containsKey("email") && service.isEmailTaken(email, user.getId())) {
            errors.put("email", "Email đã được dùng bởi tài khoản khác");
        }

        String avatar = user.getAvatar();
        if (errors.isEmpty()) {
            try {
                Part part = req.getPart("avatar");
                String url = CloudinaryUtil.uploadImage(part, "shopping/avatars"); // null nếu không chọn ảnh
                if (url != null) {
                    avatar = url;
                }
            } catch (IOException | IllegalStateException e) {
                errors.put("avatar", e.getMessage() == null ? "Upload ảnh thất bại" : e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }

        boolean emailChanged = !email.equalsIgnoreCase(user.getEmail());
        if (!emailChanged) {
            setSessionUser(req, service.updateProfile(user.getUserName(), fullName, phone, avatar));
            resp.sendRedirect(req.getContextPath() + "/profile?updated=true");
            return;
        }

        // Đổi email: giữ thay đổi trong session, gửi OTP tới email mới
        try {
            OtpResult r = service.sendEmailChangeOtp(user.getUserName(), email);
            if (r == OtpResult.TOO_SOON) {
                errors.put("email", "Vừa gửi mã xong, vui lòng đợi " + Constant.OTP_RESEND_SECONDS + " giây rồi thử lại");
            }
        } catch (MailException e) {
            e.printStackTrace();
            errors.put("email", e.getMessage());
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }
        Map<String, String> pending = new HashMap<>();
        pending.put("fullname", fullName);
        pending.put("phone", phone);
        pending.put("email", email);
        if (avatar != null) {
            pending.put("avatar", avatar);
        }
        req.getSession().setAttribute(PENDING, pending);
        resp.sendRedirect(req.getContextPath() + "/profile/verify?sent=true");
    }

    // ------------------------------------------------------------------ xác nhận OTP
    private void verify(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Map<String, String> p = pending(req);
        if (p == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        if ("resend".equals(req.getParameter("action"))) {
            try {
                OtpResult r = service.sendEmailChangeOtp(user.getUserName(), p.get("email"));
                if (r == OtpResult.OK) {
                    req.setAttribute("info", "Đã gửi lại mã OTP tới " + p.get("email"));
                } else {
                    req.setAttribute("alert", "Vui lòng đợi " + Constant.OTP_RESEND_SECONDS + " giây trước khi gửi lại mã.");
                }
            } catch (MailException e) {
                req.setAttribute("alert", e.getMessage());
            }
            req.getRequestDispatcher(VERIFY).forward(req, resp);
            return;
        }
        if ("cancel".equals(req.getParameter("action"))) {
            req.getSession().removeAttribute(PENDING);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String otp = trim(req.getParameter("otp"));
        String otpError = ValidationUtil.otp(otp);
        if (otpError != null) {
            req.setAttribute("errors", Map.of("otp", otpError));
            req.getRequestDispatcher(VERIFY).forward(req, resp);
            return;
        }
        OtpResult r = service.confirmProfileUpdate(user.getUserName(), otp, p.get("fullname"), p.get("phone"),
                p.get("email"), p.get("avatar"));
        switch (r) {
        case OK -> {
            req.getSession().removeAttribute(PENDING);
            setSessionUser(req, service.get(user.getUserName()));
            resp.sendRedirect(req.getContextPath() + "/profile?updated=true");
            return;
        }
        case EXPIRED -> req.setAttribute("alert", "Mã OTP đã hết hạn. Hãy bấm \"Gửi lại mã\".");
        case TOO_MANY -> req.setAttribute("alert", "Bạn nhập sai quá nhiều lần. Hãy bấm \"Gửi lại mã\".");
        case CONFLICT -> req.setAttribute("alert", "Email hoặc số điện thoại vừa bị tài khoản khác sử dụng.");
        default -> req.setAttribute("alert", "Mã OTP không đúng.");
        }
        req.getRequestDispatcher(VERIFY).forward(req, resp);
    }

    // ------------------------------------------------------------------ helpers
    /** Nạp lại user từ DB để hiển thị dữ liệu mới nhất. */
    private User reload(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        User current = session == null ? null : (User) session.getAttribute(Constant.SESSION_ACCOUNT);
        if (current == null) {
            return null;
        }
        User fresh = service.get(current.getUserName());
        if (fresh != null) {
            session.setAttribute(Constant.SESSION_ACCOUNT, fresh);
        }
        return fresh;
    }

    private void setSessionUser(HttpServletRequest req, User user) {
        if (user != null) {
            req.getSession().setAttribute(Constant.SESSION_ACCOUNT, user);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> pending(HttpServletRequest req) {
        return (Map<String, String>) req.getSession().getAttribute(PENDING);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
