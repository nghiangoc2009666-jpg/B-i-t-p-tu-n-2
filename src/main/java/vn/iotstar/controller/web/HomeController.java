package vn.iotstar.controller.web;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/home" })
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {

        resp.setContentType("text/html;charset=UTF-8");

        resp.getWriter().println(
            "<h1>HOME CONTROLLER HOAT DONG!</h1>"
        );

        resp.getWriter().println(
            "<p>Servlet da duoc goi thanh cong.</p>"
        );
    }
}