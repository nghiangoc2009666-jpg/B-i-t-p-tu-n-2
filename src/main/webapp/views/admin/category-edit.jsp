<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="hasErr" value="${not empty errors}"/>
<c:set var="isLink" value="${fn:startsWith(cate.images, 'http')}"/>
<html><head><title>Sửa danh mục</title></head>
<body>
<div class="card shadow-sm mx-auto" style="max-width:600px"><div class="card-body p-4">
    <h4 class="mb-3">Sửa danh mục</h4>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>
    <form action="<c:url value='/admin/category/update'/>" method="post" enctype="multipart/form-data" class="needs-validation" novalidate>
        <input type="hidden" name="categoryid" value="${cate.categoryid}">
        <t:field name="categoryname" label="Tên danh mục" value="${hasErr ? param.categoryname : cate.categoryname}" required="true" maxlength="50" msg="Vui lòng nhập tên danh mục (tối đa 50 ký tự)"/>
        <t:field name="images" label="Link ảnh" type="url" value="${hasErr ? param.images : (isLink ? cate.images : '')}" msg="Link phải bắt đầu bằng http:// hoặc https://"/>
        <c:if test="${not empty cate.images}">
            <c:choose>
                <c:when test="${isLink}"><c:set var="imgUrl" value="${cate.images}"/></c:when>
                <c:otherwise><c:url value="/image" var="imgUrl"><c:param name="fname" value="${cate.images}"/></c:url></c:otherwise>
            </c:choose>
            <div class="mb-3"><img height="120" class="rounded border" src="<c:out value='${imgUrl}'/>" alt="images"></div>
        </c:if>
        <div class="mb-3"><label class="form-label" for="images1">Upload ảnh (bỏ trống nếu giữ ảnh cũ)</label>
            <input type="file" class="form-control" id="images1" name="images1" accept="image/*"></div>
        <div class="mb-3"><label class="form-label d-block">Trạng thái</label>
            <div class="form-check form-check-inline"><input class="form-check-input" type="radio" id="ston" name="status" value="1" ${cate.status == 1 ? 'checked' : ''}><label class="form-check-label" for="ston">Hoạt động</label></div>
            <div class="form-check form-check-inline"><input class="form-check-input" type="radio" id="stoff" name="status" value="0" ${cate.status != 1 ? 'checked' : ''}><label class="form-check-label" for="stoff">Khóa</label></div>
        </div>
        <button type="submit" class="btn btn-primary">Cập nhật</button>
        <a class="btn btn-link" href="<c:url value='/admin/categories'/>">Quay lại</a>
    </form>
</div></div>
</body></html>
