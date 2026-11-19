package vn.iotstar.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.util.Constant;

/**
 * /admin/* : role 1. /manager/* : role 1 hoặc 2. /video, /video/* : phải đăng nhập
 * (riêng /video/detail ai cũng xem được).
 */
@WebFilter(urlPatterns = { "/admin/*", "/manager/*", "/video", "/video/*", "/profile", "/profile/*" })
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();

        if (path.equals("/video/detail")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(Constant.SESSION_ACCOUNT);

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        boolean allowed;
        if (path.startsWith("/admin")) {
            allowed = user.getRoleid() == 1;
        } else if (path.startsWith("/manager")) {
            allowed = user.getRoleid() == 1 || user.getRoleid() == 2;
        } else {
            allowed = true; // /video/*: chỉ cần đăng nhập
        }
        if (!allowed) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }
        chain.doFilter(request, response);
    }
}
