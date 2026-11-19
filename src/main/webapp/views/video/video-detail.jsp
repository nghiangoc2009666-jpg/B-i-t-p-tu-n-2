<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title><c:out value="${video.title}" /></title>
</head>
<body>
<div class="container mt-4" style="max-width: 900px;">
    <c:choose>
        <c:when test="${playerType == 'youtube'}">
            <div class="ratio ratio-16x9 mb-3">
                <iframe src="<c:out value='${playerSrc}'/>" title="video" allowfullscreen
                        allow="accelerometer; autoplay; encrypted-media; picture-in-picture"></iframe>
            </div>
        </c:when>
        <c:when test="${playerType == 'video'}">
            <video class="w-100 bg-dark mb-3" controls preload="metadata">
                <source src="<c:out value='${playerSrc}'/>">
                Trình duyệt của bạn không hỗ trợ phát video.
            </video>
        </c:when>
        <c:otherwise>
            <div class="alert alert-warning">Video này chưa có nguồn phát.</div>
        </c:otherwise>
    </c:choose>

    <h3><c:out value="${video.title}" /></h3>
    <div class="text-muted mb-3">
        ${video.views} lượt xem
        <c:if test="${video.createdDate != null}"> · <fmt:formatDate value="${video.createdDate}" pattern="dd/MM/yyyy HH:mm" /></c:if>
        <c:if test="${video.user != null}"> · Đăng bởi <strong><c:out value="${video.user.fullName}" /></strong></c:if>
        <c:if test="${video.category != null}"> · Danh mục: <c:out value="${video.category.categoryname}" /></c:if>
        <c:if test="${video.active != 1}"> · <span class="badge bg-warning text-dark">Đang ẩn</span></c:if>
    </div>
    <p><c:out value="${video.description}" /></p>

    <div class="mb-4">
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/home">← Trang chủ</a>
        <c:if test="${canManage}">
            <c:url var="editUrl" value="/video/edit"><c:param name="id" value="${video.videoId}" /></c:url>
            <c:url var="delUrl" value="/video/delete"><c:param name="id" value="${video.videoId}" /></c:url>
            <a class="btn btn-outline-primary" href="${editUrl}">Sửa</a>
            <a class="btn btn-outline-danger" href="${delUrl}"
               onclick="return confirm('Bạn có chắc muốn xóa video này?');">Xóa</a>
        </c:if>
    </div>
</div>
</body>
</html>
