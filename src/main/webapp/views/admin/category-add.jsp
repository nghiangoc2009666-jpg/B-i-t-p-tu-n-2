<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html><head><title>Thêm danh mục</title></head>
<body>
<div class="card shadow-sm mx-auto" style="max-width:600px"><div class="card-body p-4">
    <h4 class="mb-3">Thêm danh mục</h4>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>
    <form action="<c:url value='/admin/category/insert'/>" method="post" enctype="multipart/form-data" class="needs-validation" novalidate>
        <t:field name="categoryname" label="Tên danh mục" value="${param.categoryname}" required="true" maxlength="50" msg="Vui lòng nhập tên danh mục (tối đa 50 ký tự)"/>
        <t:field name="images" label="Link ảnh" type="url" value="${param.images}" msg="Link phải bắt đầu bằng http:// hoặc https://"/>
        <div class="mb-3"><label class="form-label" for="images1">Upload ảnh</label>
            <input type="file" class="form-control" id="images1" name="images1" accept="image/*"></div>
        <div class="mb-3"><label class="form-label d-block">Trạng thái</label>
            <div class="form-check form-check-inline"><input class="form-check-input" type="radio" id="ston" name="status" value="1" checked><label class="form-check-label" for="ston">Hoạt động</label></div>
            <div class="form-check form-check-inline"><input class="form-check-input" type="radio" id="stoff" name="status" value="0"><label class="form-check-label" for="stoff">Khóa</label></div>
        </div>
        <button type="submit" class="btn btn-primary">Thêm</button>
        <a class="btn btn-link" href="<c:url value='/admin/categories'/>">Quay lại</a>
    </form>
</div></div>
</body></html>
