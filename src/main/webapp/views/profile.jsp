<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="u" value="${sessionScope.account}"/>
<c:set var="hasErr" value="${not empty errors}"/>
<html><head><title>Hồ sơ cá nhân</title></head>
<body>
<div class="row justify-content-center"><div class="col-lg-7">
<div class="card shadow-sm"><div class="card-body p-4">
    <h4 class="mb-4"><i class="bi bi-person-gear me-2"></i>Hồ sơ cá nhân</h4>
    <c:if test="${param.updated == 'true'}"><div class="alert alert-success">Cập nhật hồ sơ thành công!</div></c:if>
    <form action="${pageContext.request.contextPath}/profile" method="post" enctype="multipart/form-data" class="needs-validation" novalidate>
        <div class="text-center mb-4">
            <c:set var="avatarSrc" value="${not empty u.avatar ? u.avatar : 'data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%27140%27 height=%27140%27%3E%3Crect width=%27140%27 height=%27140%27 fill=%27%23dee2e6%27/%3E%3C/svg%3E'}"/>
            <img id="avatarPreview" src="<c:out value='${avatarSrc}'/>" class="avatar-lg mb-3" alt="avatar">
            <input type="file" name="avatar" accept="image/png,image/jpeg,image/gif,image/webp" data-preview="avatarPreview"
                   class="form-control ${not empty errors.avatar ? 'is-invalid' : ''}">
            <div class="invalid-feedback"><c:out value="${errors.avatar}"/></div>
            <div class="form-text">Ảnh png/jpg/gif/webp, tối đa 5MB, được lưu trên Cloudinary.</div>
        </div>
        <div class="mb-3"><label class="form-label">Tài khoản</label>
            <input class="form-control" value="<c:out value='${u.userName}'/>" disabled></div>
        <t:field name="fullname" label="Họ tên" icon="card-text" value="${hasErr ? param.fullname : u.fullName}" required="true" minlength="2" maxlength="100" msg="Họ tên từ 2 đến 100 ký tự"/>
        <t:field name="phone" label="Số điện thoại" icon="telephone" value="${hasErr ? param.phone : u.phone}" required="true" pattern="0[0-9]{9}" msg="Gồm 10 chữ số, bắt đầu bằng 0"/>
        <t:field name="email" label="Email" icon="envelope" type="email" value="${hasErr ? param.email : u.email}" required="true" msg="Email không hợp lệ" help="Nếu đổi email, mã OTP sẽ được gửi tới email mới để xác nhận."/>
        <button type="submit" class="btn btn-primary"><i class="bi bi-check2-circle me-1"></i>Lưu thay đổi</button>
    </form>
</div></div>
</div></div>
</body></html>
