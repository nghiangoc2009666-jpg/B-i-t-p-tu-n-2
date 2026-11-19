<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Admin</title>
</head>
<body>
<div class="container mt-5">
    <h2>Trang quản trị (Admin)</h2>
    <p>Xin chào, <strong><c:out value="${sessionScope.account.fullName}" /></strong>.</p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/categories">Quản lý danh mục (Category)</a>
</div>
</body>
</html>
