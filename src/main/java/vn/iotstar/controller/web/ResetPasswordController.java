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

/** Nhập OTP + mật khẩu mới để hoàn tất đặt lại mật khẩu. */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/reset-password")
public class ResetPasswordController extends HttpServlet {
    private static final String VIEW = "/views/reset-password.jsp";
    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim();
        String otp = req.getParameter("otp") == null ? "" : req.getParameter("otp").trim();
        String password = req.getParameter("password");
        String repassword = req.getParameter("repassword");

        java.util.Map<String, String> errors = vn.iotstar.util.ValidationUtil.errors();
        vn.iotstar.util.ValidationUtil.put(errors, "email", vn.iotstar.util.ValidationUtil.email(email));
        vn.iotstar.util.ValidationUtil.put(errors, "otp", vn.iotstar.util.ValidationUtil.otp(otp));
        vn.iotstar.util.ValidationUtil.put(errors, "password", vn.iotstar.util.ValidationUtil.password(password));
        vn.iotstar.util.ValidationUtil.put(errors, "repassword",
                vn.iotstar.util.ValidationUtil.repassword(password, repassword));

        String error = null;
        if (errors.isEmpty()) {
            OtpResult r = service.resetPassword(email, otp, password);
            switch (r) {
            case OK -> {
                resp.sendRedirect(req.getContextPath() + "/login?reset=true");
                return;
            }
            case EXPIRED -> error = "Mã OTP đã hết hạn. Hãy yêu cầu mã mới.";
            case TOO_MANY -> error = "Bạn nhập sai quá nhiều lần. Hãy yêu cầu mã mới.";
            default -> error = "Mã OTP không đúng hoặc email không hợp lệ.";
            }
        } else {
            req.setAttribute("errors", errors);
        }
        req.setAttribute("alert", error);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
