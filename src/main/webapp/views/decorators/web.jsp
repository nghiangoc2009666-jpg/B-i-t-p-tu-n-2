    <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sitemesh" uri="http://www.sitemesh.org/sitemesh-ext" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/> | Shopping JPA</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/site.css">
    <sitemesh:write property="head"/>
</head>
<body class="d-flex flex-column min-vh-100 bg-body-tertiary">
<nav class="navbar navbar-expand-lg navbar-dark bg-gradient-brand shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home"><i class="bi bi-play-btn-fill me-1"></i>Shopping JPA</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#nav"><span class="navbar-toggler-icon"></span></button>
        <div class="collapse navbar-collapse" id="nav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                <c:if test="${sessionScope.account != null}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/video">Video của tôi</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/video/add">Thêm video</a></li>
                    <c:if test="${sessionScope.account.roleid == 1}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">Quản lý danh mục</a></li>
                    </c:if>
                </c:if>
            </ul>
            <ul class="navbar-nav align-items-lg-center">
                <c:choose>
                    <c:when test="${sessionScope.account == null}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/login">Đăng nhập</a></li>
                        <li class="nav-item"><a class="btn btn-light btn-sm ms-lg-2" href="${pageContext.request.contextPath}/register">Đăng ký</a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" data-bs-toggle="dropdown">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.account.avatar}"><img src="<c:out value='${sessionScope.account.avatar}'/>" class="avatar-sm" alt=""></c:when>
                                    <c:otherwise><i class="bi bi-person-circle fs-4"></i></c:otherwise>
                                </c:choose>
                                <c:out value="${sessionScope.account.fullName}"/>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>Hồ sơ cá nhân</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a></li>
                            </ul>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
<main class="container py-4 flex-grow-1"><sitemesh:write property="body"/></main>
<footer class="bg-dark text-white-50 text-center py-3 small">&copy; 2026 Shopping JPA - HCMUTE</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/site.js"></script>
</body>
</html>
