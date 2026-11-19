<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Video của tôi</title>
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">Video của tôi <small class="text-muted fs-6">(${total} video)</small></h3>
        <a class="btn btn-success" href="<c:url value='/video/add'/>">+ Thêm video</a>
    </div>

    <c:if test="${not empty param.error}">
        <div class="alert alert-danger"><c:out value="${param.error}" /></div>
    </c:if>

    <div class="row">
        <c:set var="showActions" value="true" />
        <c:forEach items="${videos}" var="v">
            <%@ include file="/views/common/video-card.jspf" %>
        </c:forEach>
        <c:if test="${empty videos}">
            <div class="col-12 text-muted">Bạn chưa có video nào.</div>
        </c:if>
    </div>

    <%-- Phân trang: 6 video / trang --%>
    <c:if test="${totalPages > 1}">
        <nav aria-label="Phân trang">
            <ul class="pagination justify-content-center">
                <li class="page-item ${page <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/video?page=${page - 1}">«</a>
                </li>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${i == page ? 'active' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/video?page=${i}">${i}</a>
                    </li>
                </c:forEach>
                <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/video?page=${page + 1}">»</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>
</body>
</html>
