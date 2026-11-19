<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Danh sách danh mục</title>
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">Danh sách danh mục (JPA)</h3>
        <a class="btn btn-success" href="<c:url value='/admin/category/add'/>">+ Thêm danh mục</a>
    </div>

    <c:if test="${not empty param.error}">
        <div class="alert alert-danger"><c:out value="${param.error}" /></div>
    </c:if>

    <form class="row g-2 mb-3" action="<c:url value='/admin/categories'/>" method="get">
        <div class="col-auto">
            <input type="text" name="keyword" class="form-control" placeholder="Tìm theo tên..."
                   value="<c:out value='${param.keyword}'/>">
        </div>
        <div class="col-auto"><button class="btn btn-outline-secondary" type="submit">Tìm</button></div>
    </form>

    <table class="table table-bordered table-hover align-middle">
        <thead class="table-light">
        <tr>
            <th style="width: 60px">STT</th>
            <th style="width: 220px">Hình ảnh</th>
            <th>Tên danh mục</th>
            <th style="width: 130px">Trạng thái</th>
            <th style="width: 140px">Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${listcate}" var="cate" varStatus="STT">
            <tr>
                <td>${STT.index + 1}</td>
                <td>
                    <c:if test="${not empty cate.images}">
                        <c:choose>
                            <c:when test="${fn:startsWith(cate.images, 'http')}">
                                <c:set var="imgUrl" value="${cate.images}" />
                            </c:when>
                            <c:otherwise>
                                <c:url value="/image" var="imgUrl"><c:param name="fname" value="${cate.images}" /></c:url>
                            </c:otherwise>
                        </c:choose>
                        <img height="100" src="${imgUrl}" alt="images">
                    </c:if>
                </td>
                <td><c:out value="${cate.categoryname}" /></td>
                <td>
                    <c:choose>
                        <c:when test="${cate.status == 1}">Hoạt động</c:when>
                        <c:otherwise>Khóa</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:url value="/admin/category/edit" var="editUrl"><c:param name="id" value="${cate.categoryid}" /></c:url>
                    <c:url value="/admin/category/delete" var="delUrl"><c:param name="id" value="${cate.categoryid}" /></c:url>
                    <a href="${editUrl}">Sửa</a> |
                    <a href="${delUrl}" onclick="return confirm('Bạn có chắc muốn xóa danh mục này?');">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty listcate}">
            <tr><td colspan="5" class="text-center text-muted">Chưa có danh mục nào</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>
