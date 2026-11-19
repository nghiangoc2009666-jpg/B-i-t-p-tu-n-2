<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Manager</title>
</head>
<body>
<div class="container mt-5">
    <h2>Trang quản lý (Manager)</h2>
    <p>Xin chào, <strong><c:out value="${sessionScope.account.fullName}" /></strong>.</p>
</div>
</body>
</html>
