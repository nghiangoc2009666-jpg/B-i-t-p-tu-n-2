<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sitemesh" uri="http://www.sitemesh.org/sitemesh-ext" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/> | Quản trị</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/site.css">
    <sitemesh:write property="head"/>
</head>
<body class="bg-body-tertiary">
<div class="d-flex min-vh-100">
    <aside class="sidebar text-white p-3">
        <a class="d-block text-white text-decoration-none fs-5 fw-bold mb-4" href="${pageContext.request.contextPath}/home"><i class="bi bi-shop me-2"></i>Shopping JPA</a>
        <ul class="nav nav-pills flex-column gap-1">
            <li><a class="nav-link text-white" href="${pageContext.request.contextPath}/home"><i class="bi bi-house me-2"></i>Trang chủ</a></li>
            <c:if test="${sessionScope.account.roleid == 1}">
                <li><a class="nav-link text-white" href="${pageContext.request.contextPath}/admin/home"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a></li>
                <li><a class="nav-link text-white" href="${pageContext.request.contextPath}/admin/categories"><i class="bi bi-tags me-2"></i>Danh mục</a></li>
            </c:if>
            <li><a class="nav-link text-white" href="${pageContext.request.contextPath}/video"><i class="bi bi-collection-play me-2"></i>Video của tôi</a></li>
            <li><a class="nav-link text-white" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>Hồ sơ cá nhân</a></li>
        </ul>
        <hr>
        <a class="nav-link text-white-50" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a>
    </aside>
    <div class="flex-grow-1 d-flex flex-column">
        <header class="bg-white border-bottom px-4 py-2 d-flex justify-content-end align-items-center gap-2">
            <c:if test="${not empty sessionScope.account.avatar}"><img src="<c:out value='${sessionScope.account.avatar}'/>" class="avatar-sm" alt=""></c:if>
            <span class="text-muted small">Xin chào, <strong><c:out value="${sessionScope.account.fullName}"/></strong></span>
        </header>
        <main class="p-4 flex-grow-1"><sitemesh:write property="body"/></main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/site.js"></script>
</body>
</html>
