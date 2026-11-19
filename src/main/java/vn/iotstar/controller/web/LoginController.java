package vn.iotstar.controller.web;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Đã đăng nhập (Session) -> sang trang điều hướng
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        // 2. Chưa có session nhưng có Cookie "nhớ tôi" -> tự đăng nhập
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
                    User user = service.get(cookie.getValue());
                    if (user != null && user.isActive()) {
                        session = req.getSession(true);
                        session.setAttribute(Constant.SESSION_ACCOUNT, user);
                        session.setAttribute(Constant.SESSION_USERNAME, user.getUserName());
                        resp.sendRedirect(req.getContextPath() + "/waiting");
                        return;
                    }
                }
            }
        }
        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean isRememberMe = "on".equals(req.getParameter("remember"));

        Map<String, String> errors = ValidationUtil.errors();
        ValidationUtil.put(errors, "username", ValidationUtil.required(username, "Vui lòng nhập tài khoản"));
        ValidationUtil.put(errors, "password", ValidationUtil.required(password, "Vui lòng nhập mật khẩu"));
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }

        User user = service.login(username.trim(), password);
        if (user != null && !user.isActive()) {
            // Đúng mật khẩu nhưng chưa kích hoạt bằng OTP
            req.setAttribute("alert", "Tài khoản chưa được kích hoạt. Hãy nhập mã OTP đã gửi qua email.");
            req.setAttribute("inactiveUser", user.getUserName());
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
        } else if (user != null) {
            // Đăng nhập bằng Session
            HttpSession session = req.getSession(true);
            req.changeSessionId();
            session.setAttribute(Constant.SESSION_ACCOUNT, user);
            session.setAttribute(Constant.SESSION_USERNAME, user.getUserName());
            // Đăng nhập bằng Cookie (nhớ tôi)
            if (isRememberMe) {
                saveRememberMe(resp, user.getUserName());
            }
            resp.sendRedirect(req.getContextPath() + "/waiting");
        } else {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không đúng");
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
        }
    }

    private void saveRememberMe(HttpServletResponse response, String username) {
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, username);
        cookie.setMaxAge(Constant.COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
