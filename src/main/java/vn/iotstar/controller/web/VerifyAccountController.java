package vn.iotstar.controller.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.OtpResult;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.MailException;

/** Kích hoạt tài khoản bằng OTP gửi qua email. */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/verify-account")
public class VerifyAccountController extends HttpServlet {
    private static final String VIEW = "/views/verify-account.jsp";
    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("u");
        if (username == null || username.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }
        req.setAttribute("verifyUser", username.trim());
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("u") == null ? "" : req.getParameter("u").trim();
        String action = req.getParameter("action");
        req.setAttribute("verifyUser", username);

        if ("resend".equals(action)) {
            try {
                OtpResult r = service.sendActivationOtp(username);
                switch (r) {
                case OK -> req.setAttribute("info", "Đã gửi lại mã OTP. Hãy kiểm tra email của bạn.");
                case TOO_SOON -> req.setAttribute("alert",
                        "Vui lòng đợi " + Constant.OTP_RESEND_SECONDS + " giây trước khi gửi lại mã.");
                case ALREADY_ACTIVE -> req.setAttribute("info", "Tài khoản đã được kích hoạt, hãy đăng nhập.");
                default -> req.setAttribute("alert", "Không tìm thấy tài khoản.");
                }
            } catch (MailException e) {
                e.printStackTrace();
                req.setAttribute("alert", e.getMessage());
            }
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }

        String otp = req.getParameter("otp") == null ? "" : req.getParameter("otp").trim();
        if (!otp.matches("\\d{6}")) {
            req.setAttribute("alert", "Mã OTP gồm 6 chữ số.");
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }
        OtpResult r = service.activate(username, otp);
        switch (r) {
        case OK, ALREADY_ACTIVE -> {
            resp.sendRedirect(req.getContextPath() + "/login?activated=true");
            return;
        }
        case EXPIRED -> req.setAttribute("alert", "Mã OTP đã hết hạn. Hãy bấm \"Gửi lại mã\".");
        case TOO_MANY -> req.setAttribute("alert", "Bạn nhập sai quá nhiều lần. Hãy bấm \"Gửi lại mã\" để nhận mã mới.");
        case NOT_FOUND -> req.setAttribute("alert", "Không tìm thấy tài khoản.");
        default -> req.setAttribute("alert", "Mã OTP không đúng.");
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
