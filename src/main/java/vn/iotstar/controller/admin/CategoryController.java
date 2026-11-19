package vn.iotstar.controller.admin;

import java.io.IOException;
import java.util.Map;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.UploadUtil;
import vn.iotstar.util.ValidationUtil;

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 25)
@WebServlet(urlPatterns = { "/admin/categories", "/admin/category/add", "/admin/category/insert",
        "/admin/category/edit", "/admin/category/update", "/admin/category/delete" })
public class CategoryController extends HttpServlet {
    private static final String LIST = "/views/admin/category-list.jsp";
    private static final String ADD = "/views/admin/category-add.jsp";
    private static final String EDIT = "/views/admin/category-edit.jsp";

    public ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();

        if (path.equals("/admin/categories")) {
            String keyword = req.getParameter("keyword");
            List<Category> list = (keyword != null && !keyword.trim().isEmpty())
                    ? cateService.searchByName(keyword.trim())
                    : cateService.findAll();
            req.setAttribute("listcate", list);
            req.getRequestDispatcher(LIST).forward(req, resp);

        } else if (path.equals("/admin/category/add")) {
            req.getRequestDispatcher(ADD).forward(req, resp);

        } else if (path.equals("/admin/category/edit")) {
            Category category = findByParam(req);
            if (category == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }
            req.setAttribute("cate", category);
            req.getRequestDispatcher(EDIT).forward(req, resp);

        } else if (path.equals("/admin/category/delete")) {
            String error = null;
            try {
                Category category = findByParam(req);
                if (category != null) {
                    cateService.delete(category.getCategoryid());
                    if (isLocalFile(category.getImages())) {
                        UploadUtil.delete(category.getImages());
                    }
                }
            } catch (Exception e) {
                error = e.getMessage();
            }
            String url = req.getContextPath() + "/admin/categories";
            if (error != null) {
                url += "?error=" + java.net.URLEncoder.encode(error, "UTF-8");
            }
            resp.sendRedirect(url);

        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();

        if (path.equals("/admin/category/insert")) {
            String name = trim(req.getParameter("categoryname"));
            String link = trim(req.getParameter("images"));
            int status = parseStatus(req.getParameter("status"));

            Map<String, String> errors = ValidationUtil.errors();
            ValidationUtil.put(errors, "categoryname", ValidationUtil.required(name, "Vui lòng nhập tên danh mục"));
            ValidationUtil.put(errors, "categoryname", ValidationUtil.maxLen(name, 50, "Tên danh mục tối đa 50 ký tự"));
            ValidationUtil.put(errors, "images", ValidationUtil.optionalUrl(link));
            if (errors.isEmpty() && cateService.findByCategoryname(name) != null) {
                errors.put("categoryname", "Tên danh mục đã tồn tại");
            }
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(ADD).forward(req, resp);
                return;
            }

            Category category = new Category();
            category.setCategoryname(name);
            category.setStatus(status);
            try {
                String fname = UploadUtil.save(req.getPart("images1")); // null nếu không chọn file
                if (fname != null) {
                    category.setImages(fname);
                } else if (!link.isEmpty()) {
                    category.setImages(link);
                }
            } catch (IOException e) {
                back(req, resp, ADD, e.getMessage());
                return;
            }
            cateService.insert(category);
            resp.sendRedirect(req.getContextPath() + "/admin/categories");

        } else if (path.equals("/admin/category/update")) {
            Category category;
            try {
                category = cateService.findById(Integer.parseInt(req.getParameter("categoryid")));
            } catch (NumberFormatException e) {
                category = null;
            }
            if (category == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }
            String name = trim(req.getParameter("categoryname"));
            String link = trim(req.getParameter("images"));
            String fileold = category.getImages();

            Map<String, String> errors = ValidationUtil.errors();
            ValidationUtil.put(errors, "categoryname", ValidationUtil.required(name, "Vui lòng nhập tên danh mục"));
            ValidationUtil.put(errors, "categoryname", ValidationUtil.maxLen(name, 50, "Tên danh mục tối đa 50 ký tự"));
            ValidationUtil.put(errors, "images", ValidationUtil.optionalUrl(link));
            Category dup = errors.isEmpty() ? cateService.findByCategoryname(name) : null;
            if (dup != null && dup.getCategoryid() != category.getCategoryid()) {
                errors.put("categoryname", "Tên danh mục đã tồn tại");
            }
            if (!errors.isEmpty()) {
                req.setAttribute("cate", category);
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(EDIT).forward(req, resp);
                return;
            }
            category.setCategoryname(name);
            category.setStatus(parseStatus(req.getParameter("status")));

            try {
                String fname = UploadUtil.save(req.getPart("images1"));
                if (fname != null) {
                    // đã chọn ảnh mới -> xóa file cũ trên thư mục (nếu là file upload)
                    if (isLocalFile(fileold)) {
                        UploadUtil.delete(fileold);
                    }
                    category.setImages(fname);
                } else if (!link.isEmpty()) {
                    category.setImages(link);
                } else {
                    category.setImages(fileold);
                }
            } catch (IOException e) {
                req.setAttribute("cate", category);
                back(req, resp, EDIT, e.getMessage());
                return;
            }
            cateService.update(category);
            resp.sendRedirect(req.getContextPath() + "/admin/categories");

        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    private Category findByParam(HttpServletRequest req) {
        try {
            return cateService.findById(Integer.parseInt(req.getParameter("id")));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void back(HttpServletRequest req, HttpServletResponse resp, String view, String msg)
            throws ServletException, IOException {
        req.setAttribute("alert", msg);
        req.getRequestDispatcher(view).forward(req, resp);
    }

    private boolean isLocalFile(String images) {
        return images != null && !images.isBlank() && !images.startsWith("http");
    }

    private int parseStatus(String s) {
        return "1".equals(s) ? 1 : 0;
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
