<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang chủ</title>
</head>
<body>
<div class="container mt-4">
    <h2>Trang chủ</h2>
    <c:choose>
        <c:when test="${sessionScope.account != null}">
            <p>Xin chào, <strong><c:out value="${sessionScope.account.fullName}" /></strong>
               (username: <c:out value="${sessionScope.account.userName}" />, role: ${sessionScope.account.roleid}).</p>
        </c:when>
        <c:otherwise>
            <p>Bạn chưa đăng nhập.</p>
        </c:otherwise>
    </c:choose>

    <h4 class="mt-4 mb-3">10 video mới nhất</h4>
    <div class="row">
        <c:set var="showActions" value="false" />
        <c:forEach items="${videos}" var="v">
            <%@ include file="/views/common/video-card.jspf" %>
        </c:forEach>
        <c:if test="${empty videos}">
            <div class="col-12 text-muted">Chưa có video nào.</div>
        </c:if>
    </div>
</div>
</body>
</html>
