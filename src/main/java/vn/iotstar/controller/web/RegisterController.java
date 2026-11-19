package vn.iotstar.controller.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.MailException;
import vn.iotstar.util.ValidationUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {
    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");
        String repassword = req.getParameter("repassword");
        String email = trim(req.getParameter("email"));
        String fullname = trim(req.getParameter("fullname"));
        String phone = trim(req.getParameter("phone"));

        Map<String, String> errors = validate(username, password, repassword, email, fullname, phone);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
            return;
        }

        try {
            boolean isSuccess = service.register(username, password, email, fullname, phone);
            if (isSuccess) {
                // Gửi OTP kích hoạt qua email, rồi chuyển sang trang nhập mã
                String mailError = "";
                try {
                    service.sendActivationOtp(username);
                } catch (MailException me) {
                    me.printStackTrace();
                    mailError = "&mailerror=true";
                }
                resp.sendRedirect(req.getContextPath() + "/verify-account?u="
                        + URLEncoder.encode(username, "UTF-8") + "&registered=true" + mailError);
            } else {
                fail(req, resp, "Tài khoản đã tồn tại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(req, resp, "System error!");
        }
    }

    private Map<String, String> validate(String username, String password, String repassword, String email,
            String fullname, String phone) {
        Map<String, String> e = ValidationUtil.errors();
        ValidationUtil.put(e, "username", ValidationUtil.username(username));
        ValidationUtil.put(e, "fullname", ValidationUtil.fullName(fullname));
        ValidationUtil.put(e, "email", ValidationUtil.email(email));
        ValidationUtil.put(e, "phone", ValidationUtil.phone(phone));
        ValidationUtil.put(e, "password", ValidationUtil.password(password));
        ValidationUtil.put(e, "repassword", ValidationUtil.repassword(password, repassword));
        if (!e.containsKey("email") && service.checkExistEmail(email)) {
            e.put("email", "Email đã tồn tại");
        }
        if (!e.containsKey("username") && service.checkExistUsername(username)) {
            e.put("username", "Tài khoản đã tồn tại");
        }
        if (!e.containsKey("phone") && service.checkExistPhone(phone)) {
            e.put("phone", "Số điện thoại đã tồn tại");
        }
        return e;
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String msg)
            throws ServletException, IOException {
        req.setAttribute("alert", msg);
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
