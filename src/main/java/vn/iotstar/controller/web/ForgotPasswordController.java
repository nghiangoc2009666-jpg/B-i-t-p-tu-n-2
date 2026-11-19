package vn.iotstar.controller.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.MailException;

/** Quên mật khẩu: nhập email, hệ thống gửi OTP. */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private static final String VIEW = "/views/forgot-password.jsp";
    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim();
        String emailError = vn.iotstar.util.ValidationUtil.email(email);
        if (emailError != null) {
            req.setAttribute("errors", Map.of("email", emailError));
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }
        try {
            // Luôn trả lời giống nhau dù email có tồn tại hay không (tránh lộ danh sách tài khoản)
            service.requestPasswordReset(email);
        } catch (MailException e) {
            e.printStackTrace();
            req.setAttribute("alert", e.getMessage());
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/reset-password?email=" + URLEncoder.encode(email, "UTF-8")
                + "&sent=true");
    }
}
